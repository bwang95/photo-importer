package com.cerridan.photoimporter

import com.cerridan.photoimporter.ui.Strings.CONFIG_FILE_PATH
import com.cerridan.photoimporter.ui.Strings.DEFAULT_DIR_PATH
import com.cerridan.photoimporter.ui.Strings.DEST_DIALOG_CANCELLED
import com.cerridan.photoimporter.ui.Strings.DEST_DIALOG_TITLE
import com.cerridan.photoimporter.ui.Strings.ERROR_DIALOG_TITLE
import com.cerridan.photoimporter.ui.Strings.SOURCE_DIALOG_CANCELLED
import com.cerridan.photoimporter.ui.Strings.SOURCE_DIALOG_TITLE
import com.cerridan.photoimporter.config.ConfigManager
import com.cerridan.photoimporter.importer.PhotoImporter
import com.cerridan.photoimporter.ui.DialogCreator
import javax.swing.UIManager

fun main(args: Array<String>) {
  val configManager = ConfigManager(
    configFilePath = args.firstOrNull()
      .substituteIfBlank(CONFIG_FILE_PATH)
  )

  val config = configManager.read()

  try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  } catch (e: Exception) {
    e.printStackTrace()
  }

  val sourceFolder = DialogCreator.showFileChooser(
    defaultFilePath = config.sourcePath.substituteIfBlank(DEFAULT_DIR_PATH),
    title = SOURCE_DIALOG_TITLE
  )
  if (sourceFolder == null) {
    DialogCreator.showErrorDialog(ERROR_DIALOG_TITLE, SOURCE_DIALOG_CANCELLED)
    return
  }

  val destFolder = DialogCreator.showFileChooser(
    defaultFilePath = config.destinationPath.substituteIfBlank(DEFAULT_DIR_PATH),
    title = DEST_DIALOG_TITLE
  )
  if (destFolder == null) {
    DialogCreator.showErrorDialog(ERROR_DIALOG_TITLE, DEST_DIALOG_CANCELLED)
    return
  }

  val newConfig = config.copy(
    sourcePath = sourceFolder.absolutePath,
    destinationPath = destFolder.absolutePath
  )
  configManager.write(newConfig)

  PhotoImporter(
    source = sourceFolder,
    destination = destFolder
  )
    .import()
}