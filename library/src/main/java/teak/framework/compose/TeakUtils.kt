package teak.framework.compose

import androidx.compose.runtime.*
import teak.framework.core.runtime.TeakRuntime

@Composable
fun <Model: Any, Msg: Any> WithTeak(
    init: () -> Pair<Model, List<() -> Msg>>,
    update: (Model, Msg) -> Pair<Model, List<() -> Msg>>,
    content: @Composable (Model, (Msg) -> Unit) -> Unit
){
    var model by remember { mutableStateOf(init().first) }
    var dispatch : (Msg) -> Unit by remember { mutableStateOf({}) }
    remember {
        mutableStateOf(
            TeakRuntime(init, view = { newModel, newDispatch ->
                model = newModel
                dispatch = newDispatch
            }, update)
        )
    }
    content(model, dispatch)
}