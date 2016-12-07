package org.psesd.srx.shared.core.io

import java.io._
import java.util.UUID

import org.apache.commons.vfs2._
import org.apache.commons.vfs2.impl.StandardFileSystemManager
import org.apache.commons.vfs2.provider.sftp.{IdentityInfo, SftpFileSystemConfigBuilder}
import org.apache.poi.util.IOUtils
import org.psesd.srx.shared.core.config.SftpConfig
import org.psesd.srx.shared.core.exceptions.ArgumentNullException

/** Provides I/O for files stored in SFTP endpoints.
  *
  * @constructor creates a new `SftpClient` instance.
  * @param sftpConfig the configurable parameters to use to access the SFTP endpoint
  * @return a new `SftpClient` instance.
  * @version 1.0
  * @since 1.0
  * @author Ted Neward (iTrellis, LLC)
  * @author David S. Dennison (iTrellis, LLC)
  * */
class SftpClient(sftpConfig: SftpConfig) {
  if (sftpConfig == null) {
    throw new ArgumentNullException("sftpConfig")
  }

  /** Holds the file system manager.
    *
    * The Apache VFS [[org.apache.commons.vfs2.FileSystemManager]] we use to do the
    * actual SFTP upload/download/etc.
    * */
  val manager = new StandardFileSystemManager()

  /** Writes contents to a remote directory.
    *
    * Write the contents to the given file name at the given remote directory.
    * Write the contents to the given file name at the given remote directory.
    *
    * @param remoteName the name of the file into which to write the contents.
    * @param contents   the byte array (assumed to be in appropriate target format) to write.
    * @return `true` if the write was successful; otherwise `false`.
    */
  def write(remoteName: String, contents: Array[Byte]): Boolean = {

    val sftpUri = "%s/%s".format(sftpConfig.url, remoteName)

    sftp { (manager, opts) =>
      val remoteFile = manager.resolveFile(sftpUri, opts)
      val writer = remoteFile.getContent.getOutputStream()
      writer.write(contents)
      writer.flush()
      true
    }

    val isValid = sftp { (manager, opts) =>
      val remoteFile = manager.resolveFile(sftpUri, opts)
      remoteFile.exists() && remoteFile.getContent.getSize == contents.length
    }

    // RETRY ON 0-BYTES WRITTEN
    if (!isValid) {
      sftp { (manager, opts) =>
        val remoteFile = manager.resolveFile(sftpUri, opts)
        val writer = remoteFile.getContent.getOutputStream()
        writer.write(contents)
        writer.flush()
        true
      }

      sftp { (manager, opts) =>
        val remoteFile = manager.resolveFile(sftpUri, opts)
        remoteFile.exists() && remoteFile.getContent.getSize == contents.length
      }
    } else {
      true
    }
  }

  /** Performs an SFTP write.
    *
    * The workhorse method to do the actual work, ensuring that "manager" is properly
    * initialized, authenticated, and closed as part of the SFTP work being done.
    * Uses the Scala "Loan" pattern. See the write member for an example of how to use
    * this method.
    *
    * @param f the block of work to be done.
    * @return success or failure, as determined by `f` (in other words, `f`'s return value
    *         is passed directly back as part of this invocation).
    */
  def sftp(f: (StandardFileSystemManager, FileSystemOptions) => Boolean): Boolean = {
    try {
      manager.init()

      val tempFile = File.createTempFile(UUID.randomUUID.toString, "")
      tempFile.deleteOnExit
      IOUtils.copy(new ByteArrayInputStream(sftpConfig.getPrivateKey.getBytes), new FileOutputStream(tempFile))

      val identityInfo = new IdentityInfo(tempFile, sftpConfig.password.getBytes)

      val opts = new FileSystemOptions()
      SftpFileSystemConfigBuilder.getInstance().setPreferredAuthentications(opts, "publickey")
      SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no")
      SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true)
      SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10 * 1000)
      SftpFileSystemConfigBuilder.getInstance().setIdentityInfo(opts, identityInfo)

      f(manager, opts)
    } finally {
      // Deliberately allow exceptions to propagate upwards, but clean up
      // on our way out in any event
      manager.close()
    }
  }

  /** Checks if target file exists.
    *
    * Checks for the existence of something at the given path on the SFTP server.
    *
    * @param remotePath the full path name of the file/directory being tested.
    * @return `true` if the object exists; otherwise `false`.
    * */
  def exists(remotePath: String): Boolean = {
    sftp { (manager, opts) =>
      val sftpUri = sftpConfig.url
      val remoteFile = manager.resolveFile(sftpUri, opts)
      remoteFile.exists()
    }
  }
}
