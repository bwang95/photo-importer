package com.cerridan.photoimporter.ui

import java.awt.Window
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JFileChooser.CENTER_ALIGNMENT
import javax.swing.JFileChooser.DIRECTORIES_ONLY
import javax.swing.JLabel
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE

object DialogCreator {
  fun showFileChooser(
    defaultFilePath: String,
    title: String
  ): File? {
    val chooser = JFileChooser(File(defaultFilePath)).apply {
      dialogTitle = title
      fileSelectionMode = DIRECTORIES_ONLY
    }
    val result = chooser.showOpenDialog(null)

    return if (result == JFileChooser.APPROVE_OPTION) {
      chooser.selectedFile
    } else {
      null
    }
  }

  @Suppress("CAST_NEVER_SUCCEEDS")
  fun showErrorDialog(
    title: String,
    message: String
  ) {
    val dialog = JDialog(null as? Window, title).apply {
      defaultCloseOperation = DISPOSE_ON_CLOSE
      setSize(256, 128)
      setLocationRelativeTo(null)
    }

    val label = JLabel(message, JLabel.CENTER)
      .apply { alignmentX = CENTER_ALIGNMENT }

    val okButton = JButton(Strings.OKAY)
      .apply { alignmentX = CENTER_ALIGNMENT }
    okButton.addActionListener {
      dialog.dispatchEvent(WindowEvent(dialog, WindowEvent.WINDOW_CLOSING))
    }

    dialog.add(
      Box(BoxLayout.Y_AXIS).apply {
        alignmentX = CENTER_ALIGNMENT
        alignmentY = CENTER_ALIGNMENT

        add(label)
        add(okButton)
      }
    )
    dialog.isVisible = true
  }
}
