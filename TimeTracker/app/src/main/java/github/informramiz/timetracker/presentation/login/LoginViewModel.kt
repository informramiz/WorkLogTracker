package github.informramiz.timetracker.presentation.login

import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import com.google.firebase.auth.FirebaseUser
import github.informramiz.timetracker.R
import github.informramiz.timetracker.presentation.base.BaseViewModel

class LoginViewModel @ViewModelInject constructor(
    private val resources: Resources
): BaseViewModel<LoginViewState>() {
    private var isSignInAlreadyStarted = false
    override fun initialState() = LoginViewState(false, isLoading = true)

    override fun onStart() {
        super.onStart()
        if (!isSignInAlreadyStarted) {
            isSignInAlreadyStarted = true
            navigate(LoginNavigationDestination.SignIn)
        }
    }

    fun onBackPressAction() {
        finishActivity()
    }

    fun onLoginButtonClickAction() {
        navigate(LoginNavigationDestination.SignIn)
    }

    fun onLoginResultAction(user: FirebaseUser?) {
        if (user == null) {
            notifyFailure(resources.getString(R.string.login_fragment_login_failed))
            finishActivity()
        } else {
            navigateBack()
        }
    }
}
