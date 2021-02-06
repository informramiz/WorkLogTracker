package github.informramiz.timetracker.view.settings

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.R
import github.informramiz.timetracker.common.exhaustive
import github.informramiz.timetracker.databinding.SettingsFragmentBinding
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.DialogCommand
import github.informramiz.timetracker.presentation.base.NavigationDestination
import github.informramiz.timetracker.presentation.settings.SettingsDialogCommand
import github.informramiz.timetracker.presentation.settings.SettingsNavigationDestination
import github.informramiz.timetracker.presentation.settings.SettingsViewModel
import github.informramiz.timetracker.presentation.settings.SettingsViewState
import github.informramiz.timetracker.view.base.BaseFragment
import github.informramiz.timetracker.view.extensions.toStringResource

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewState>() {
    private lateinit var viewBinding: SettingsFragmentBinding
    override val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return SettingsFragmentBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.signOutButton.setOnClickListener { viewModel.onSignOutButtonClickAction() }
        viewBinding.workingHoursValueTextView.setOnClickListener { viewModel.onWorkingHoursViewClickAction() }
        viewBinding.clearWorkingHoursImageView.setOnClickListener { viewModel.onClearWorkingHoursCountAction() }
    }

    override fun renderViewState(viewState: SettingsViewState) {
        super.renderViewState(viewState)
        viewBinding.nameValueTextView.text = viewState.name
        viewBinding.emailValueTextView.text = viewState.email
        viewBinding.roleValueTextView.setText(viewState.userRole.toStringResource())
        viewBinding.workingHoursValueTextView.text = (viewState.workingHoursCount ?: getString(R.string.settings_fragment_preferred_working_hours_placeholder)).toString()
        viewBinding.clearWorkingHoursImageView.isVisible = viewState.workingHoursCount != null && viewState.userRole == UserRole.REGULAR
        viewBinding.preferredWorkingHoursViewsGroup.isVisible = viewState.userRole == UserRole.REGULAR
        viewBinding.settingsProgressBar.progressBar.isVisible = viewState.isLoading
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        if (destination !is SettingsNavigationDestination) {
            super.navigateToDestination(destination)
            return
        }

        when (destination) {
            is SettingsNavigationDestination.EditUser -> navController.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToEditUserFragment(destination.userId)
            )
        }.exhaustive
    }

    override fun renderDialog(dialogCommand: DialogCommand) {
        if (dialogCommand !is SettingsDialogCommand) {
            super.renderDialog(dialogCommand)
            return
        }

        when (dialogCommand) {
            SettingsDialogCommand.SelectWorkingHoursCount -> showWorkingHoursCountSelectionDialog()
            SettingsDialogCommand.LogoutConfirmation -> showLogoutConfirmationDialog()
        }.exhaustive
    }

    private fun showWorkingHoursCountSelectionDialog() {
        val workingHoursSelectionDialog = SelectPreferredWorkingHoursDialog { hoursCount ->
            viewModel.onWorkingHoursCountSelectedAction(hoursCount)
        }

        workingHoursSelectionDialog.show(childFragmentManager, "SelectWorkingHoursCount")
    }

    private fun showLogoutConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_fragment_logout_confirmation_dialog_title)
            .setMessage(R.string.settings_fragment_logout_confirmation_dialog_body)
            .setPositiveButton(R.string.dialog_button_positive) { _, _ ->
                viewModel.onLogoutConfirmationAction()
            }
            .setNegativeButton(R.string.dialog_button_negative) { _, _ -> }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_fragment_action_edit -> {
                viewModel.onEditClickAction()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
