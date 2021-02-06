package github.informramiz.timetracker.view.edituser

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.R
import github.informramiz.timetracker.common.exhaustive
import github.informramiz.timetracker.databinding.EditUserFragmentBinding
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.DialogCommand
import github.informramiz.timetracker.presentation.edituser.EditUserDialogCommand
import github.informramiz.timetracker.presentation.edituser.EditUserViewModel
import github.informramiz.timetracker.presentation.edituser.EditUserViewState
import github.informramiz.timetracker.view.base.BaseFragment
import github.informramiz.timetracker.view.createuser.RoleArrayAdapter
import github.informramiz.timetracker.view.createuser.RoleUiModel
import github.informramiz.timetracker.view.extensions.setSelectedOption
import github.informramiz.timetracker.view.extensions.stringText
import github.informramiz.timetracker.view.extensions.toStringResource

@AndroidEntryPoint
class EditUserFragment : BaseFragment<EditUserViewState>() {
    override val viewModel: EditUserViewModel by viewModels()
    private lateinit var viewBinding: EditUserFragmentBinding
    private val args: EditUserFragmentArgs by navArgs()
    private var roleItems: List<RoleUiModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadDetails(args.userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return EditUserFragmentBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.roleAutoCompleteTextView.setOnItemClickListener { _, view, position, _ ->
            viewModel.onRoleOptionSelectedAction(roleItems[position].roleType)
        }
    }

    override fun renderViewState(viewState: EditUserViewState) {
        super.renderViewState(viewState)
        roleItems = roleItems(viewState.currentUserRole)
        viewBinding.roleAutoCompleteTextView.setAdapter(
            RoleArrayAdapter(requireContext(), roleItems)
        )
        viewBinding.roleAutoCompleteTextView.setSelectedOption(roleItems.indexOfFirst { it.roleType == viewState.role })
        viewBinding.roleExposedDropdownMenu.isVisible = !viewState.isLoggedInUser
        viewBinding.loader.progressBar.isVisible = viewState.isLoading
        viewBinding.nameEditText.stringText = viewState.name
        viewBinding.emailEditText.stringText = viewState.email
        requireActivity().invalidateOptionsMenu()
    }

    private fun roleItems(currentUserRole: UserRole): List<RoleUiModel> = when (currentUserRole) {
        UserRole.REGULAR -> listOf(
            RoleUiModel(getString(UserRole.REGULAR.toStringResource()), UserRole.REGULAR)
        )
        UserRole.MANAGER -> listOf(
            RoleUiModel(getString(UserRole.REGULAR.toStringResource()), UserRole.REGULAR),
            RoleUiModel(getString(UserRole.MANAGER.toStringResource()), UserRole.MANAGER)
        )
        UserRole.ADMIN -> listOf(
            RoleUiModel(getString(UserRole.REGULAR.toStringResource()), UserRole.REGULAR),
            RoleUiModel(getString(UserRole.MANAGER.toStringResource()), UserRole.MANAGER),
            RoleUiModel(getString(UserRole.ADMIN.toStringResource()), UserRole.ADMIN)
        )
    }

    override fun renderDialog(dialogCommand: DialogCommand) {
        if (dialogCommand !is EditUserDialogCommand) {
            super.renderDialog(dialogCommand)
            return
        }

        when (dialogCommand) {
            is EditUserDialogCommand.DeleteUserConfirmationDialog -> showDeleteUserConfirmationDialog(dialogCommand.userId)
        }.exhaustive
    }

    private fun showDeleteUserConfirmationDialog(userId: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_user_fragment_delete_user_confirmation_dialog_title)
            .setMessage(R.string.edit_user_fragment_delete_user_confirmation_dialog_body)
            .setPositiveButton(R.string.dialog_button_positive) { _, _ ->
                viewModel.onDeleteUserConfirmationAction(userId)
            }
            .setNegativeButton(R.string.dialog_button_negative) { _, _ -> }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_user_fragment_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.edit_user_fragment_action_delete).isVisible = !viewModel.currentViewState().isLoggedInUser
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_user_fragment_action_save -> {
                viewModel.onEditClickAction(
                    args.userId,
                    viewBinding.nameEditText.stringText,
                    viewBinding.emailEditText.stringText,
                )
                true
            }
            R.id.edit_user_fragment_action_delete -> {
                viewModel.onDeleteClickAction(args.userId)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
