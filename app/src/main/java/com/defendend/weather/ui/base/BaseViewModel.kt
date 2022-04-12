package com.defendend.weather.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.lang.Exception


abstract class BaseViewModel<State : UiState> : ViewModel() {

    private val _state: MutableStateFlow<State> by lazy(LazyThreadSafetyMode.NONE) {
        MutableStateFlow(
            initialState
        )
    }
    private val _event = MutableSharedFlow<UiEvent>(extraBufferCapacity = 16, replay = 16)
    private val _effect = Channel<UiEffect>(capacity = 16)

    val currentState: State
        get() = state.value

    val state: StateFlow<State> by lazy { _state.asStateFlow() }

    val effect: Flow<UiEffect> get() = _effect.receiveAsFlow()

    private val initialState: State by lazy { createInitialState() }
    abstract fun createInitialState(): State

    init {
        observeEvents()
    }

    fun postEvent(event: UiEvent) = safeIoLaunch {
        _event.emit(event)
    }

    protected fun postState(state: State) {
        _state.value = state
    }

    protected suspend fun postEffect(effect: UiEffect) {
        _effect.send(effect)
    }

    protected fun safeLaunch(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        launchBlock: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(dispatcher) {
            launchBlock()
        }
    }

    protected inline fun <T> safeRunCatching(runBlock: () -> T): Result<T> {
        return try {
            val result = runBlock()
            Result.success(result)
        } catch (ex: Exception) {
            if (ex is CancellationException) {
                throw ex
            }
            Result.failure(ex)
        }
    }

    protected inline fun <T, V> Result<T>.flatMap(flatMapBody: (T) -> Result<V>): Result<V> {
        fold(
            onSuccess = { return flatMapBody(it) },
            onFailure = { return Result.failure(it) }
        )
    }

    protected fun safeIoLaunch(launchBlock: suspend CoroutineScope.() -> Unit) {
        safeLaunch(dispatcher = Dispatchers.IO, launchBlock = launchBlock)
    }

    protected fun safeDefaultLaunch(launchBlock: suspend CoroutineScope.() -> Unit) {
        safeLaunch(dispatcher = Dispatchers.Default, launchBlock = launchBlock)
    }

    protected open suspend fun handleEvent(event: UiEvent) = Unit

    private fun observeEvents() {
        safeIoLaunch {
            _event.collect {
                handleEvent(it)
            }
        }
    }


}