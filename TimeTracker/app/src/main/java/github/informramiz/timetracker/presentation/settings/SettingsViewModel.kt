package github.informramiz.timetracker.presentation.settings

import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.BaseViewModel
import github.informramiz.timetracker.presentation.base.GeneralNavigationDestination
import kotlin.math.log

class SettingsViewModel @ViewModelInject constructor(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel<SettingsViewState>() {
    override fun initialState() = SettingsViewState(
        userId = "",
        name = "",
        email = "",
        userRole = UserRole.REGULAR,
        workingHoursCount = null,
        isLoading = true
    )

    override fun onStart() {
        super.onStart()
        launchWithErrorHandling {
            val userInfo = authenticationRepository.currentUser()
            val workingHoursCount = authenticationRepository.getPreferredWorkingHoursPerDay()
            updateState { lastState ->
                lastState.copy(
                    userId = userInfo.userId,
                    name = userInfo.name,
                    email = userInfo.email,
                    userRole = userInfo.role,
                    workingHoursCount = workingHoursCount,
                    isLoading = false
                )
            }
        }
    }

    fun onEditClickAction() {
        navigate(SettingsNavigationDestination.EditUser(currentViewState().userId))
    }

    fun onSignOutButtonClickAction() {
        notify(SettingsDialogCommand.LogoutConfirmation)
    }

    fun onLogoutConfirmationAction() {
        logout()
    }

    private fun logout() {
        launchWithErrorHandling {
            authenticationRepository.signOut()
            navigate(GeneralNavigationDestination.LoginScreen)
        }
    }

    fun onWorkingHoursViewClickAction() {
        notify(SettingsDialogCommand.SelectWorkingHoursCount)
    }

    fun onWorkingHoursCountSelectedAction(hours: Float?) {
        updateWorkingHours(hours)
    }

    private fun updateWorkingHours(hours: Float?) {
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true) }
            authenticationRepository.setPreferredWorkingHoursPerDay(hours)
            updateState { lastState ->
                lastState.copy(
                    isLoading = false,
                    workingHoursCount = hours
                )
            }
        }
    }

    fun onClearWorkingHoursCountAction() {
        launchWithErrorHandling {
            updateWorkingHours(null)
        }
    }
}
