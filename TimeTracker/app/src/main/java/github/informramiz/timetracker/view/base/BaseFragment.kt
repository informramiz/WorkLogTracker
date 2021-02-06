package github.informramiz.timetracker.view.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import github.informramiz.timetracker.MainDirections
import github.informramiz.timetracker.extensions.showToast
import github.informramiz.timetracker.presentation.base.*

abstract class BaseFragment<VIEW_STATE : ViewState> : Fragment() {
    abstract val viewModel: BaseViewModel<VIEW_STATE>
    protected val navController: NavController by lazy { findNavController() }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, ::renderViewState)
        viewModel.dialogCommands.observe(viewLifecycleOwner, ::renderDialog)
        viewModel.navigationCommands.observe(viewLifecycleOwner, ::handleNavigation)
        viewModel.uiExceptionEvents.observe(viewLifecycleOwner, ::renderErrorLayout)
        viewModel.notificationState.observe(viewLifecycleOwner, ::renderNotification)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    open fun renderViewState(viewState: VIEW_STATE) {}

    open fun renderDialog(dialogCommand: DialogCommand) {}

    private fun handleNavigation(destination: NavigationDestination) {
        when (destination) {
            is GeneralNavigationDestination.Back -> navController.navigateUp()
            is GeneralNavigationDestination.FinishActivity -> requireActivity().finish()
            is GeneralNavigationDestination.RelaunchActivity -> requireActivity().recreate()
            is GeneralNavigationDestination.LoginScreen -> navController.navigate(MainDirections.actionGlobalLoginFragment())
            else -> navigateToDestination(destination)
        }
    }

    open fun navigateToDestination(destination: NavigationDestination) {}

    protected open fun renderErrorLayout(throwable: Throwable) {
        showToast(throwable.localizedMessage ?: "Unknown error occurred")
    }

    protected fun renderNotification(notificationState: NotificationState) {
        // TODO: Different UIs can be used here for different states but I am going to stick to simple
        showToast(notificationState.message)
    }
}
