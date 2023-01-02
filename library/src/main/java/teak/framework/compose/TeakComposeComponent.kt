package teak.framework.compose

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import teak.framework.core.TeakComponent
import teak.framework.core.TeakComponentContract

class TeakComposeComponent<Model : Any, Msg : Any> {
    private lateinit var component: TeakComponent<Model, Msg>

    @Composable
    fun WithModel(
        createInitializer : () -> TeakComponentContract.Initializer<Model, Msg>,
        createUpdater : () -> TeakComponentContract.Updater<Model, Msg>,
        content: @Composable (Model) -> Unit) {
        var state by remember { mutableStateOf(createInitializer().init().first) }
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(LocalLifecycleOwner.current) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        component =
                            TeakComponent(object : TeakComponentContract.Impl<Model, Msg> {
                                override fun initializer(): TeakComponentContract.Initializer<Model, Msg> = createInitializer()
                                override fun updater(): TeakComponentContract.Updater<Model, Msg> = createUpdater()
                                override fun view(model: Model) {
                                    state = model
                                }
                            })
                        component.onCreate()
                    }
                    Lifecycle.Event.ON_STOP -> {
                        component.onDestroy()
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }
        content(state)
    }

    fun dispatch(msg: Msg) = component.dispatch(msg)
}