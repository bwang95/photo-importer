package com.cerridan.photoimporter.ui

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JProgressBar

class ImportProgressDialog : JDialog() {
    private val currentImageLabel = JLabel("")
        .apply { alignmentX = CENTER_ALIGNMENT }
    private val progressLabel = JLabel("")
        .apply { alignmentX = CENTER_ALIGNMENT }
    private val progressBar = JProgressBar(0, 1)
        .apply { alignmentX = CENTER_ALIGNMENT }

    var total: Int = 0
        set (value) {
            field = value
            progressBar.maximum = total
        }

    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(440, 256)
        setLocationRelativeTo(null)

        title = "Importing..."

        add(
            Box(BoxLayout.Y_AXIS).apply {
                alignmentX = CENTER_ALIGNMENT
                alignmentY = CENTER_ALIGNMENT

                add(currentImageLabel)
                add(progressLabel)
                add(progressBar)
            }
        )
    }

    fun setProgress(currentFilename: String, currentNumber: Int) {
        println("ImportProgressDialog: $currentFilename, $currentNumber/$total")
        currentImageLabel.text = "Current image: $currentFilename"
        progressLabel.text = "Importing image $currentNumber/$total"
        progressBar.value = currentNumber
        invalidate()
        repaint()
    }
}