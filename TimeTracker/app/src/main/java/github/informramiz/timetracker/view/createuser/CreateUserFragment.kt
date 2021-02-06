package github.informramiz.timetracker.view.createuser

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.BuildConfig
import github.informramiz.timetracker.R
import github.informramiz.timetracker.databinding.CreateUserFragmentBinding
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.createuser.CreateUserViewModel
import github.informramiz.timetracker.presentation.createuser.CreateUserViewState
import github.informramiz.timetracker.view.base.BaseFragment
import github.informramiz.timetracker.view.extensions.setSelectedOption
import github.informramiz.timetracker.view.extensions.stringText
import github.informramiz.timetracker.view.extensions.toStringResource

@AndroidEntryPoint
class CreateUserFragment : BaseFragment<CreateUserViewState>() {
    override val viewModel: CreateUserViewModel by viewModels()
    private lateinit var viewBinding: CreateUserFragmentBinding
    private var roleItems: List<RoleUiModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return CreateUserFragmentBinding.inflate(inflater, container, false)
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

    override fun renderViewState(viewState: CreateUserViewState) {
        super.renderViewState(viewState)
        roleItems = roleItems(viewState.currentUserRole)
        viewBinding.roleAutoCompleteTextView.setAdapter(
            RoleArrayAdapter(
                requireContext(),
                roleItems
            )
        )
        viewBinding.roleAutoCompleteTextView.setSelectedOption(roleItems.indexOfFirst { it.roleType == viewState.newUserRole })
        viewBinding.loader.progressBar.isVisible = viewState.isLoading
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.create_user_fragment_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.create_user_fragment_action_save).isVisible = !viewModel.currentViewState().isLoading
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_user_fragment_action_save -> {
                viewModel.onSaveClickAction(
                    viewBinding.nameEditText.stringText,
                    viewBinding.emailEditText.stringText,
                    viewBinding.passwordEditText.stringText
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
