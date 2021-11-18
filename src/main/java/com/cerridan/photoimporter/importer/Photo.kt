package com.cerridan.photoimporter.importer

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class Photo private constructor(
    val file: File,
    val metadata: Metadata
) {
    companion object {
        private const val DATE_FORMAT_STRING = "YYYYMMdd.HHmmss"

        fun createSimpleDateFormat(): SimpleDateFormat =
            SimpleDateFormat(DATE_FORMAT_STRING)

        fun create(file: File): Photo? = try {
            Photo(
                file = file,
                metadata = ImageMetadataReader.readMetadata(file)
            )
        } catch (e: ImageProcessingException) {
            null
        }
    }

    val dateTaken: Date by lazy {
        val exifDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
        exifDir?.dateOriginal ?: exifDir?.dateDigitized ?: Date()
    }

    fun getDesiredFilename(dateFormatter: SimpleDateFormat, suffix: Int): String {
        val base = dateFormatter.format(dateTaken)
        return if (suffix == 0) {
            "$base.${file.extension}"
        } else {
            "$base-$suffix.${file.extension}"
        }
    }
}