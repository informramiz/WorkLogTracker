package github.informramiz.timetracker.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.activity.result.launch
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.R
import github.informramiz.timetracker.databinding.LoginFragmentBinding
import github.informramiz.timetracker.extensions.supportActionBar
import github.informramiz.timetracker.presentation.base.NavigationDestination
import github.informramiz.timetracker.presentation.login.LoginNavigationDestination
import github.informramiz.timetracker.presentation.login.LoginViewModel
import github.informramiz.timetracker.presentation.login.LoginViewState
import github.informramiz.timetracker.view.activityresultcontracts.SignInActivityResultContract
import github.informramiz.timetracker.view.base.BaseFragment

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewState>() {
    private lateinit var viewBinding: LoginFragmentBinding
    override val viewModel: LoginViewModel by viewModels()
    private val progressBar: ProgressBar
        get() = requireView().findViewById(R.id.progress_bar)

    private val signInActivityResultLauncher = registerForActivityResult(SignInActivityResultContract()) { user ->
        viewModel.onLoginResultAction(user)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return LoginFragmentBinding.inflate(inflater, container, false).also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.apply {
            loginFragmentLoginButton.setOnClickListener { viewModel.onLoginButtonClickAction() }
        }
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPressAction()
            isEnabled = false
        }
    }

    override fun renderViewState(viewState: LoginViewState) {
        super.renderViewState(viewState)
        progressBar.isVisible = viewState.isLoading
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        when (destination) {
            is LoginNavigationDestination.SignIn -> signInActivityResultLauncher.launch()
            else -> super.navigateToDestination(destination)
        }
    }
}
