package org.psesd.srx.shared.core

import java.io.File

import scala.xml.Node

/** Get system runtime info.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SystemInfo {

  lazy val osArchitecture = System.getProperty("os.arch")
  lazy val osName = System.getProperty("os.name")
  lazy val osVersion = System.getProperty("os.version")

  def getAvailableProcessors = {
    Runtime.getRuntime.availableProcessors()
  }

  def getDisks = {
    File.listRoots.map(root => new DiskInfo(root))
  }

  def getMemoryFreeMb = Runtime.getRuntime.freeMemory() / 1024 / 1024

  def getMemoryMaxMb = Runtime.getRuntime.maxMemory() / 1024 / 1024

  def getMemoryTotalMb = Runtime.getRuntime.totalMemory() / 1024 / 1024

  def getMemoryUsedMb = getMemoryTotalMb - getMemoryFreeMb

  def toXml: Node = {
    <system>
      <os>
        <name>{osName}</name>
        <version>{osVersion}</version>
        <architecture>{osArchitecture}</architecture>
        <availableProcessors>{getAvailableProcessors}</availableProcessors>
      </os>
      <memory>
        <totalMb>{getMemoryTotalMb}</totalMb>
        <usedMb>{getMemoryUsedMb}</usedMb>
        <freeMb>{getMemoryFreeMb}</freeMb>
        <maxMb>{getMemoryMaxMb}</maxMb>
      </memory>
      <disks>{getDisks.map(disk => disk.toXml)}</disks>
    </system>
  }

}
