package teak.framework.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun <Model: Any, Msg: Any> WithTeak(
    init: () -> Pair<Model, List<() -> Msg>>,
    update: (Model, Msg) -> Pair<Model, List<() -> Msg>>,
    content: @Composable (Model, (Msg) -> Unit) -> Unit
) = TeakWithViewModel(init = init, update = update, view = content)

@Composable
fun <Model: Any, Msg: Any> WithTeak(
    key: String,
    init: () -> Pair<Model, List<() -> Msg>>,
    update: (Model, Msg) -> Pair<Model, List<() -> Msg>>,
    content: @Composable (Model, (Msg) -> Unit) -> Unit
) = TeakWithViewModel(key = key, init = init, update = update, view = content)

@Composable
private fun <Model : Any, Msg> TeakWithViewModel(
    key: String? = null,
    init: () -> Pair<Model, List<() -> Msg>>,
    update: (model: Model, message: Msg) -> Pair<Model, List<() -> Msg>>,
    view: @Composable (Model, (Msg) -> Unit) -> Unit,
    viewModel: TeakViewModel<Model, Msg> = teakViewModel(init, update, key = key)
) {
    ShowWhenTeakCreated(viewModel = viewModel) { model, dispatch ->
        view(model, dispatch)
    }
}

@Composable
private fun <Model : Any, Msg, VM : TeakViewModel<Model, Msg>> ShowWhenTeakCreated(
    viewModel: VM,
    content: @Composable (Model, (Msg) -> Unit) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    uiState.first?.let { model ->
        content(model, uiState.second)
    }
}