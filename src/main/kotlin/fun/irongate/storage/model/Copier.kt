package `fun`.irongate.storage.model

import `fun`.irongate.storage.GlobalParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


object Copier : CoroutineScope {
    override val coroutineContext = Dispatchers.IO

    var status = Status.IDLE
        private set

    var totalProgress = 0.0
        private set

    var fileProgress = 0.0
        private set

    var copiedFilesCount = 0
        private set

    var skippedFilesCount = 0
        private set

    var totalFilesSize = 0L
        private set

    var currentFile = ""
        private set

    private lateinit var storageDir: File
    private lateinit var mirrorDir: File
    private var totalSpace: Long = 0
    private var usableSpace: Long = 0

    fun start() {
        launch(Dispatchers.IO) {
            status = Status.IN_PROGRESS

            storageDir = File(GlobalParams.storagePath)
            mirrorDir = File(GlobalParams.mirrorPath)

            totalSpace = storageDir.totalSpace
            usableSpace = storageDir.usableSpace

            copyDir(storageDir)

            totalProgress = 1.0
            status = Status.DONE
        }
    }

    private fun copyDir(dir: File) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                val mirrorDir = getMirrorFile(file)
                if (!mirrorDir.exists())
                    mirrorDir.mkdirs()

                copyDir(file)
            }
            else {
                copyFile(file)

                totalFilesSize += file.length()
                totalProgress = totalFilesSize.toDouble() / (totalSpace - usableSpace)
            }
        }
    }

    private fun copyFile(file: File) {
        currentFile = file.absolutePath
        val mirrorFile = getMirrorFile(file)

        if (mirrorFile.exists() && mirrorFile.length() == file.length()) {
            skippedFilesCount++
            return
        }

        val inputStream = FileInputStream(file)
        val outputStream = FileOutputStream(mirrorFile)
        try {
            val buffer = ByteArray(1024)
            var length: Int
            fileProgress = 0.0
            val fileLength = file.length()
            var sum = 0L
            while ((inputStream.read(buffer).also { length = it }) > 0) {
                outputStream.write(buffer, 0, length)
                sum += length
                fileProgress = sum.toDouble() / fileLength.toDouble()
            }
            fileProgress = 1.0
        }
        catch (ex: Exception) {
            println("Copier.copyFile() $ex")
        }
        finally {
            inputStream.close()
            outputStream.close()
        }

        copiedFilesCount++
    }

    private fun getMirrorFile(file: File): File {
        val localPath = file.absolutePath.split(GlobalParams.storagePath)[1]
        val destination = GlobalParams.mirrorPath + localPath

        return File(destination)
    }

    enum class Status { IDLE, IN_PROGRESS, DONE }
}