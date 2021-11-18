package com.cerridan.photoimporter.importer

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

class MD5SumCalculator {
    private val messageDigest = MessageDigest.getInstance("MD5")

    fun calculate(file: File): String {
        messageDigest.reset()
        return "%032x".format(BigInteger(1, messageDigest.digest(file.readBytes())))
    }
}