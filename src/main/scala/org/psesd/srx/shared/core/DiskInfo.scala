package org.psesd.srx.shared.core

import java.io.File

/** Represents runtime info for root disks.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class DiskInfo(root: File) {

  def getAbsolutePath = root.getAbsolutePath
  def getFreeMb = root.getFreeSpace / 1024 / 1024
  def getTotalMb = root.getTotalSpace / 1024 / 1024
  def getUsableMb = root.getUsableSpace / 1024 / 1024

  def toXml = {
    <disk>
      <path>{getAbsolutePath}</path>
      <totalMb>{getTotalMb}</totalMb>
      <freeMb>{getFreeMb}</freeMb>
      <usableMb>{getUsableMb}</usableMb>
    </disk>
  }

}
