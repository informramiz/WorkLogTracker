package github.informramiz.timetracker.presentation.createuser

import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.R
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.domain.user.UserManagementRepository
import github.informramiz.timetracker.domain.user.model.CreateUserDomainModel
import github.informramiz.timetracker.presentation.base.BaseViewModel
import github.informramiz.timetracker.view.validators.isValidEmail
import github.informramiz.timetracker.view.validators.isValidName
import github.informramiz.timetracker.view.validators.isValidPassword

class CreateUserViewModel @ViewModelInject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userManagementRepository: UserManagementRepository,
    private val resources: Resources
) : BaseViewModel<CreateUserViewState>() {
    override fun initialState() = CreateUserViewState(UserRole.MANAGER, newUserRole = UserRole.REGULAR, isLoading = false)

    override fun onStart() {
        super.onStart()
        launchWithErrorHandling {
            val role = authenticationRepository.currentUser().role
            updateState {lastState -> lastState.copy(currentUserRole = role, isLoading = false) }
        }
    }

    override fun onDataLoadFailure() {
        super.onDataLoadFailure()
        updateState { lastState -> lastState.copy(isLoading = false) }
    }

    fun onRoleOptionSelectedAction(selectedRole: UserRole) {
        updateState { lastState -> lastState.copy(newUserRole = selectedRole) }
    }

    fun onSaveClickAction(name: String, email: String, password: String) {
        if (!name.isValidName()) {
            notifyFailure(resources.getString(R.string.create_user_fragment_error_invalid_name))
        } else if (!email.isValidEmail()) {
            notifyFailure(resources.getString(R.string.create_user_fragment_error_invalid_email))
        } else if (!password.isValidPassword()) {
            notifyFailure(resources.getString(R.string.create_user_fragment_error_invalid_password))
        } else {
            createUser(name, email, password)
        }
    }

    private fun createUser(name: String, email: String, password: String) {
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true) }
            userManagementRepository.createUser(CreateUserDomainModel(name, email, password, currentViewState().newUserRole))
            updateState { lastState -> lastState.copy(isLoading = false) }
            navigateBack()
        }
    }
}
