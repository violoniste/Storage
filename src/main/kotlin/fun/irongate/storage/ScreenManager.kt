package `fun`.irongate.storage

import `fun`.irongate.storage.controllers.ScreenController
import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class ScreenManager : Application() {
    companion object {
        private const val MIN_WIDTH = 1024.0
        private const val MIN_HEIGHT = 768.0
        private const val STATUS_SCREEN_VIEW = "status_screen.fxml"
        var currentScreen: ScreenController? = null
        private var root = StackPane()
        private lateinit var scene: Scene
        private var onFrameTimer: AnimationTimer? = null

        fun onScreenCreated(screen: ScreenController) {
            if (currentScreen != null)
                System.err.println("onScreenCreated: currentScreen != null")
            currentScreen = screen
        }

        @Suppress("SameParameterValue")
        private fun openScreen(srcName: String) {
            Platform.runLater {
                closeScreen()

                val resource = this::class.java.getResource(srcName)
                val content: Pane = FXMLLoader(resource).load()

                root.children.add(content)
            }
        }

        fun openEditorScreen() {
            openScreen(STATUS_SCREEN_VIEW)
        }

        fun onScreenInitialized() {
            onResize()
        }

        private fun closeScreen() {
            currentScreen?.destroy()
            root.children.clear()
            currentScreen = null
        }

        private fun onResize() {
            val currentScreen = currentScreen ?: return
            if (currentScreen.initialized)
                currentScreen.onResize(scene.width, scene.height)
        }
    }

    override fun start(stage: Stage) {
        val scene = Scene(root, MIN_WIDTH, MIN_HEIGHT, false, SceneAntialiasing.BALANCED)
        ScreenManager.scene = scene
        stage.title = "Storage recover"
        stage.scene = scene
        stage.show()

        scene.widthProperty().addListener { _, _, _ -> onResize() }
        scene.heightProperty().addListener { _, _, _ -> onResize() }

        val namedParameters = parameters.named

        println("ScreenManager.start() $namedParameters")

//        ConstantUpdater.updateConstant(namedParameters["ids"], namedParameters["strings"], namedParameters["images"])

        openEditorScreen()

        onFrameTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                currentScreen?.onFrame()
            }
        }
        onFrameTimer?.start()
    }

    override fun stop() {
        closeScreen()
        onFrameTimer?.stop()
    }
}
