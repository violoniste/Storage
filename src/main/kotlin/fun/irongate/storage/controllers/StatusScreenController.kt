package `fun`.irongate.storage.controllers

import `fun`.irongate.storage.GlobalParams
import `fun`.irongate.storage.model.Copier
import `fun`.irongate.storage.utils.StringUtils
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import java.io.File

class StatusScreenController : ScreenController() {
    companion object {
        const val RED_PROGRESS_BAR_STYLE = "-fx-accent: #FF0000"
        const val GREEN_PROGRESS_BAR_STYLE = "-fx-accent: #00CC00"
    }

    @FXML
    private lateinit var btnStart: Button

    @FXML
    private lateinit var labelDiskSpace: Label

    @FXML
    private lateinit var labelFileProgress: Label

    @FXML
    private lateinit var labelStatus: Label

    @FXML
    private lateinit var labelTotalProgress: Label

    @FXML
    private lateinit var progressBarDiskSpace: ProgressBar

    @FXML
    private lateinit var progressBarFileProgress: ProgressBar

    @FXML
    private lateinit var progressBarTotalProgress: ProgressBar

    private var totalSpace: Long = 0
    private var usableSpace: Long = 0

    override fun initialize() {
        super.initialize()

        progressBarTotalProgress.style = GREEN_PROGRESS_BAR_STYLE
        progressBarFileProgress.style = GREEN_PROGRESS_BAR_STYLE

        checkDisks()
    }

    private fun checkDisks() {
        val storage = File(GlobalParams.storagePath)
        val mirror = File(GlobalParams.mirrorPath)

        if (!storage.exists() || !storage.isDirectory || !mirror.exists() || !mirror.isDirectory) {
            progressBarDiskSpace.progress = 1.0
            progressBarDiskSpace.style = "-fx-accent: #FF0000"
            labelDiskSpace.text = "Один из дисков отсутствует!!!"
            return
        }

        totalSpace = storage.totalSpace
        usableSpace = storage.usableSpace

        progressBarDiskSpace.progress = 1 - usableSpace.toDouble() / totalSpace.toDouble()
        progressBarDiskSpace.style = GREEN_PROGRESS_BAR_STYLE

        labelDiskSpace.text =
            "${StringUtils.sizeToString(usableSpace)} из ${StringUtils.sizeToString(totalSpace)} свободно"
    }

    override fun onFrame() {
        super.onFrame()

        labelStatus.text = when (Copier.status) {
            Copier.Status.IDLE -> "Ожидание"
            Copier.Status.IN_PROGRESS -> "Копирование"
            Copier.Status.DONE -> "Завершено"
            Copier.Status.INTERRUPTED -> "Прервано"
        }

        btnStart.text = when (Copier.status) {
            Copier.Status.IDLE -> "Запуск копирования"
            Copier.Status.IN_PROGRESS -> "Прервать копирование!"
            Copier.Status.DONE -> "Запуск копирования"
            Copier.Status.INTERRUPTED -> "Запуск копирования"
        }

        progressBarTotalProgress.progress = Copier.totalProgress
        labelTotalProgress.text = "Пропущено: ${Copier.skippedFilesCount} Скопировано: ${Copier.copiedFilesCount} Размером: ${StringUtils.sizeToString(Copier.totalFilesSize)}"

        progressBarFileProgress.progress = Copier.fileProgress
        labelFileProgress.text = Copier.currentFile
    }

    @FXML
    fun onStartClick() {
        if (Copier.status != Copier.Status.IN_PROGRESS)
            Copier.start()
        else
            Copier.stop()
    }
}