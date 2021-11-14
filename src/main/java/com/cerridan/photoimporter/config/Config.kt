package com.cerridan.photoimporter.config

import com.squareup.moshi.Json

data class Config(
  @Json(name = "source_directory") val sourcePath: String = "",
  @Json(name = "destination_directory") val destinationPath: String = ""
)
