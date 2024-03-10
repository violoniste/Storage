package `fun`.irongate.storage.controllers

import `fun`.irongate.storage.ScreenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class ScreenController : CoroutineScope {
    override val coroutineContext = Dispatchers.Default

    var initialized = false
        private set

    var destroyed = false
        private set

    init {
        ScreenManager.onScreenCreated(this)
    }

    open fun initialize() {
        initialized = true
        ScreenManager.onScreenInitialized()
    }

    open fun onResize(width: Double, height: Double) {}

    open fun destroy() {
        destroyed = true
    }

    abstract fun onFrame()
}