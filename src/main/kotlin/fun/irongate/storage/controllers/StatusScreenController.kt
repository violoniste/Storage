package `fun`.irongate.storage.controllers

import javafx.fxml.FXML
import javafx.scene.control.ProgressBar

class StatusScreenController : ScreenController() {
    @FXML private lateinit var bar: ProgressBar

    override fun initialize() {
        super.initialize()

        bar.progress = 0.5
    }

    override fun onFrame() {
//        println("StatusScreenController.onFrame() ")
    }
}