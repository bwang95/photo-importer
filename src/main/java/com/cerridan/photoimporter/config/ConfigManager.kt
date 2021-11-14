package com.cerridan.photoimporter.config

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

class ConfigManager(
  private val configFilePath: String,
  moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
) {
  private val configAdapter = moshi.adapter(Config::class.java).indent("  ")

  fun read(): Config = File(configFilePath)
    .takeIf(File::canRead)
    ?.readText()
    ?.let(configAdapter::fromJson)
    ?: Config()

  fun write(config: Config): Boolean = try {
    File(configFilePath).writeText(configAdapter.toJson(config))
    true
  } catch (e: Exception) {
    e.printStackTrace()
    false
  }
}
