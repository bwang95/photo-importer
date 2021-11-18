package com.cerridan.photoimporter.importer

import com.cerridan.photoimporter.ui.ImportProgressDialog
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.File
import java.text.SimpleDateFormat
import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue

class PhotoImporter(
  private val source: File,
  private val destination: File,
  private val scheduler: Scheduler = Schedulers.io()
) {
  companion object {
    private val YEAR_FORMAT_STRING = "YYYY"
    private val DATE_FORMAT_STRING = "YYYY-MM-dd"
  }

  private class DirectoryWalker(private val root: File) {
    fun listAllFiles(): List<File> {
      val result = LinkedList<File>()
      val directories = LinkedList<File>()
        .apply { add(root) }

      while (!directories.isEmpty()) {
        (directories.remove().listFiles() ?: emptyArray()).forEach {
          if (it.isDirectory) directories += it
          else result += it
        }
      }

      return result
    }
  }

  private val initFinishedSubject = BehaviorSubject.createDefault(false)
  private val currentImageSubject = BehaviorSubject.createDefault("")
  private val progressSubject = BehaviorSubject.createDefault(0)
  private val queue = ConcurrentLinkedQueue<Photo>()

  private val dialog = ImportProgressDialog()
  private val disposables = CompositeDisposable()

  init {
    Observable.fromCallable { DirectoryWalker(source) }
      .observeOn(scheduler)
      .map { it.listAllFiles().mapNotNull(Photo::create) }
      .subscribe {
        queue.addAll(it)
        dialog.total = queue.size
        initFinishedSubject.onNext(true)
      }
      .let(disposables::add)

    Observable.combineLatest(currentImageSubject, progressSubject, BiFunction(::Pair))
      .subscribe { (currentImage, progress) -> dialog.setProgress(currentImage, progress) }
  }

  fun import() {
    initFinishedSubject.filter { it }
      .observeOn(scheduler)
      .doOnNext { dialog.isVisible = true }
      .map {
        val yearDirectoryFormat = SimpleDateFormat(YEAR_FORMAT_STRING)
        val directoryDateFormat = SimpleDateFormat(DATE_FORMAT_STRING)
        val photoDateFormat = Photo.createSimpleDateFormat()
        val md5Sum = MD5SumCalculator()
        var processed = 0

        photoQueue@while (!queue.isEmpty()) {
          val photo = queue.remove()
          var suffix = 0

          currentImageSubject.onNext(photo.file.name)

          val yearDirectory = File(destination, yearDirectoryFormat.format(photo.dateTaken))
          val desiredDirectory = File(yearDirectory, directoryDateFormat.format(photo.dateTaken))
          var desiredFile = File(desiredDirectory, photo.getDesiredFilename(photoDateFormat, suffix))
          while (desiredFile.exists()) {
            if (md5Sum.calculate(desiredFile) == md5Sum.calculate(photo.file)) {
              continue@photoQueue
            }
            desiredFile = File(desiredDirectory, photo.getDesiredFilename(photoDateFormat, ++suffix))
          }
          println("PhotoImporter: ${desiredFile.path}")
          photo.file.copyTo(desiredFile, false)

          progressSubject.onNext(++processed)
        }
      }
      .blockingFirst()

    dialog.dispose()
  }

  fun dispose() {
    disposables.dispose()
  }
}