package com.cerridan.photoimporter

fun String?.substituteIfBlank(substitute: String): String =
  this?.takeIf(String::isNotBlank) ?: substitute