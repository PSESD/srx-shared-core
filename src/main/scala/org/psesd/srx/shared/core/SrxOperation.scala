package org.psesd.srx.shared.core

import org.psesd.srx.shared.core.extensions.ExtendedEnumeration

/** Enumeration of supported SRX operations.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
object SrxOperation extends ExtendedEnumeration {
  type SrxOperation = Value
  val Diagnostic = Value("Diagnostic")
  val DiagnosticEnvironment = Value("DiagnosticEnvironment")
  val DiagnosticHeaders = Value("DiagnosticHeaders")
  val Info = Value("Info")
  val Messages = Value("Messages")
  val MessagesGetAll = Value("MessagesGetAll")
  val MessagesGetLatest = Value("MessagesGetLatest")
  val None = Value("")
  val Sre = Value("Sre")
  val SrePost = Value("SrePost")
  val Test = Value("Test")
  val Xsre = Value("Xsre")
  val XsreGet = Value("XsreGet")
  val XsreRefresh = Value("XsreRefresh")
}