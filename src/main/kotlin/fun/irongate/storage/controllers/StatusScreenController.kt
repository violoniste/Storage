package `fun`.irongate.storage.controllers

import `fun`.irongate.storage.DiskAnalyzer
import `fun`.irongate.storage.GlobalParams
import `fun`.irongate.storage.utils.StringUtils
import javafx.fxml.FXML
import javafx.scene.control.ProgressBar
import javafx.scene.control.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class StatusScreenController : ScreenController() {
    @FXML
    private lateinit var labelDiskSpace: Label

    @FXML
    private lateinit var labelStatus: Label

    @FXML
    private lateinit var labelProgress: Label

    @FXML
    private lateinit var progressBarDiskSpace: ProgressBar

    @FXML
    private lateinit var progressBarProgress: ProgressBar

    private var totalSpace: Long = 0
    private var usableSpace: Long = 0
    private var totalFilesCount: Long = 0
    private var totalFilesSize: Long = 0

    override fun initialize() {
        super.initialize()

        checkDisks()
    }

    private fun checkDisks() {
        val storage = File(GlobalParams.storagePath)
        val copy = File(GlobalParams.copyPath)

        if (!storage.exists() || !storage.isDirectory || !copy.exists() || !copy.isDirectory) {
            progressBarDiskSpace.progress = 1.0
            progressBarDiskSpace.style = "-fx-accent: #FF0000"
            labelDiskSpace.text = "Один из дисков отсутствует!!!"
            return
        }

        totalSpace = storage.totalSpace
        usableSpace = storage.usableSpace

        progressBarDiskSpace.progress = 1 - usableSpace.toDouble() / totalSpace.toDouble()
        progressBarDiskSpace.style = "-fx-accent: #00FF00"

        labelDiskSpace.text = "${StringUtils.sizeToString(usableSpace)} из ${StringUtils.sizeToString(totalSpace)} свободно"

        launch(Dispatchers.IO) {
            copyFiles(storage)
        }
    }

    private fun copyFiles(storage: File) {
        labelStatus.text = "Копирование"
        progressBarProgress.progress = 0.0
        progressBarProgress.style = "-fx-accent: #00FF00"

        copyDir(storage)
    }

    private fun copyDir(dir: File) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory)
                copyDir(file)
            else {
                copyFile(file)
                totalFilesCount++
                totalFilesSize += file.length()
            }
        }
    }

    private fun copyFile(file: File) {

    }

    override fun onFrame() {
        super.onFrame()

        labelProgress.text = "Файлов: $totalFilesCount ${StringUtils.sizeToString(totalFilesSize)}"
        val p = totalFilesSize.toDouble() / (totalSpace - usableSpace)
        progressBarProgress.progress = p
        println("StatusScreenController.onFrame() $p")
    }
}