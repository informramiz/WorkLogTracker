package github.informramiz.timetracker.presentation.edituser

import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.R
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UpdateCurrentUserRequestDomainModel
import github.informramiz.timetracker.domain.authentication.model.UserInfoDomainModel
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.domain.user.UserManagementRepository
import github.informramiz.timetracker.domain.user.model.UpdateUserRequestDomainModel
import github.informramiz.timetracker.presentation.base.BaseViewModel
import github.informramiz.timetracker.view.validators.isValidEmail
import github.informramiz.timetracker.view.validators.isValidName
import github.informramiz.timetracker.view.validators.isValidPassword

class EditUserViewModel @ViewModelInject constructor(
    private val userManagementRepository: UserManagementRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val resources: Resources
) : BaseViewModel<EditUserViewState>() {
    override fun initialState() = EditUserViewState(
        currentUserRole = UserRole.MANAGER,
        role = UserRole.REGULAR,
        name = "",
        email = "",
        isLoggedInUser = true,
        isLoading = true
    )

    override fun onDataLoadFailure() {
        super.onDataLoadFailure()
        updateState { lastState -> lastState.copy(isLoading = false) }
    }

    fun loadDetails(userId: String) {
        launchWithErrorHandling {
            val currentUserInfo = authenticationRepository.currentUser()
            if (currentUserInfo.userId == userId) {
                updateStateWithUserInfo(currentUserInfo, currentUserInfo)
            } else {
                val user = userManagementRepository.getUser(userId)
                updateStateWithUserInfo(currentUserInfo, user)
            }
        }
    }

    private fun updateStateWithUserInfo(currentUserInfo: UserInfoDomainModel, user: UserInfoDomainModel) {
        updateState { lastState ->
            lastState.copy(
                currentUserRole = currentUserInfo.role,
                role = user.role,
                name = user.name,
                email = user.email,
                isLoggedInUser = user.userId == currentUserInfo.userId,
                isLoading = false
            )
        }
    }

    fun onRoleOptionSelectedAction(newRole: UserRole) {
        updateState { lastState -> lastState.copy(role = newRole) }
    }

    fun onEditClickAction(userId: String, name: String, email: String) {
        if (!validateUserData(name, email)) return
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(name = name, email = email, isLoading = true) }
            if (currentViewState().isLoggedInUser) {
                updateCurrentUser(name, email)
            } else {
                updateOtherUser(userId, name, email, currentViewState().role)
            }
            updateState { lastState -> lastState.copy(isLoading = false) }
            navigateBack()
        }
    }

    private fun validateUserData(name: String, email: String): Boolean {
        return when {
            !name.isValidName() -> {
                notifyFailure(resources.getString(R.string.create_user_fragment_error_invalid_name))
                false
            }
            !email.isValidEmail() -> {
                notifyFailure(resources.getString(R.string.create_user_fragment_error_invalid_email))
                false
            }
            else -> {
                true
            }
        }
    }

    private suspend fun updateCurrentUser(name: String, email: String) {
        authenticationRepository.updateUser(UpdateCurrentUserRequestDomainModel(name, email))
    }

    private suspend fun updateOtherUser(userId: String, name: String, email: String, role: UserRole) {
        userManagementRepository.updateUser(UpdateUserRequestDomainModel(userId, name, email, role))
    }

    fun onDeleteClickAction(userId: String) {
        notify(EditUserDialogCommand.DeleteUserConfirmationDialog(userId))
    }

    fun onDeleteUserConfirmationAction(userId: String) {
        deleteUser(userId)
    }

    private fun deleteUser(userId: String) {
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true) }
            userManagementRepository.deleteUser(userId)
            updateState { lastState -> lastState.copy(isLoading = false) }
            navigateBack()
        }
    }
}
