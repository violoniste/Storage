package `fun`.irongate.storage

import java.io.File

class DiskAnalyzer {
    companion object {
        var total = 0

        fun checkDisk(path: String) {
            println("DiskAnalyzer.checkDisk() $path")
            val root = File(path)

            println("DiskAnalyzer.checkDisk() ${root.totalSpace}")
            println("DiskAnalyzer.checkDisk() ${root.usableSpace}")


        }

//        private fun calcFiles(dir: File) {
//            println("DiskAnalyzer.calcFiles() $dir -------->")
//            val files = dir.listFiles() ?: return
//
//            files.forEach {
//                if (it.isDirectory) {
//                    calcFiles(it)
//                }
//                else {
//                    total++
//                    println("DiskAnalyzer.calcFiles() $total $it")
//                }
//            }
//        }
    }
}