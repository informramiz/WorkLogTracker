package github.informramiz.timetracker.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<VIEW_STATE : ViewState> : ViewModel() {
    private val _viewState = MutableLiveData<VIEW_STATE>().apply { value = initialState() }
    val viewState: LiveData<VIEW_STATE>
        get() = _viewState

    val uiExceptionEvents = SingleLiveEvent<Throwable>()
    val notificationState = SingleLiveEvent<NotificationState>()
    val dialogCommands = SingleLiveEvent<DialogCommand>()
    val navigationCommands = SingleLiveEvent<NavigationDestination>()

    protected fun navigate(presentationDestination: NavigationDestination) {
        navigationCommands.value = presentationDestination
    }

    protected fun navigateBack() {
        navigationCommands.value = GeneralNavigationDestination.Back
    }

    protected fun finishActivity() {
        navigationCommands.value = GeneralNavigationDestination.FinishActivity
    }

    fun updateState(newViewState: VIEW_STATE) {
        _viewState.value = newViewState
    }

    fun updateState(updatedStateBlock: (lastState: VIEW_STATE) -> VIEW_STATE) =
        updateState(updatedStateBlock(currentViewState()))

    fun currentViewState() = viewState.value ?: initialState()

    fun notify(dialogCommand: DialogCommand) {
        dialogCommands.value = dialogCommand
    }

    protected fun notifyLoading(message: String) {
        notificationState.value = NotificationState.Loading(message)
    }

    protected fun notifyFailure(message: String, exception: Throwable? = null) {
        notificationState.value = NotificationState.Failure(message)
    }

    protected fun notifySuccess(message: String) {
        notificationState.value = NotificationState.Success(message)
    }

    protected fun notifyNormal(message: String) {
        notificationState.value = NotificationState.Normal(message)
    }

    fun launchWithErrorHandling(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Throwable) {
                Timber.e(e)
                notifyFailure(e.message ?: "Unknown error occurred")
                onDataLoadFailure()
            }
        }
    }

    protected open fun onDataLoadFailure() {}

    open fun onStart() {}

    abstract fun initialState(): VIEW_STATE
}
