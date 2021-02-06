package github.informramiz.timetracker.view.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.common.exhaustive
import github.informramiz.timetracker.databinding.UsersFragmentBinding
import github.informramiz.timetracker.presentation.base.DialogCommand
import github.informramiz.timetracker.presentation.base.NavigationDestination
import github.informramiz.timetracker.presentation.users.UsersDialogCommand
import github.informramiz.timetracker.presentation.users.UsersNavigationDestination
import github.informramiz.timetracker.presentation.users.UsersViewModel
import github.informramiz.timetracker.presentation.users.UsersViewState
import github.informramiz.timetracker.view.base.BaseFragment

@AndroidEntryPoint
class UsersFragment : BaseFragment<UsersViewState>() {
    override val viewModel: UsersViewModel by viewModels()
    private lateinit var viewBinding: UsersFragmentBinding
    private val recyclerAdapter = UsersRecyclerAdapter {
        viewModel.onUserItemClickAction(it.userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return UsersFragmentBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerListeners()
        viewBinding.usersList.adapter = recyclerAdapter
    }

    private fun registerListeners() {
        viewBinding.createUserFab.setOnClickListener {
            viewModel.onCreateNewUserFabClickAction()
        }

        viewBinding.swipeToRefreshView.setOnRefreshListener {
            viewModel.onSwipeToRefreshAction()
        }
    }

    override fun renderViewState(viewState: UsersViewState) {
        super.renderViewState(viewState)
        viewBinding.swipeToRefreshView.isRefreshing = viewState.isRefreshing
        viewBinding.loader.progressBar.isVisible = viewState.isLoading
        recyclerAdapter.submitList(viewState.users)
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        if (destination !is UsersNavigationDestination) {
            super.navigateToDestination(destination)
            return
        }

        when (destination) {
            is UsersNavigationDestination.CreateNewUser -> navController.navigate(
                UsersFragmentDirections.actionUsersFragmentToCreateUserFragment()
            )
            is UsersNavigationDestination.EditUser -> navController.navigate(
                UsersFragmentDirections.actionUsersFragmentToEditUserFragment(destination.userId)
            )
            is UsersNavigationDestination.UserWorkLogs -> navController.navigate(
                UsersFragmentDirections.actionUsersFragmentToWorkLogsFragment(destination.userId)
            )
        }.exhaustive
    }

    override fun renderDialog(dialogCommand: DialogCommand) {
        if (dialogCommand !is UsersDialogCommand) {
            super.renderDialog(dialogCommand)
            return
        }

        when (dialogCommand) {
            is UsersDialogCommand.SelectAdminActionDialog -> {
                showSelectAdminActionDialog(dialogCommand)
            }
        }.exhaustive
    }

    private fun showSelectAdminActionDialog(dialogCommand: UsersDialogCommand.SelectAdminActionDialog) {
        val selectActionDialog =
            SelectUserActionBottomSheetDialog(dialogCommand.userId) { selectedAction ->
                when (selectedAction) {
                    is SelectUserActionBottomSheetDialog.UserAction.EditUser -> viewModel.onEditUserActionSelected(
                        selectedAction.userId
                    )
                    is SelectUserActionBottomSheetDialog.UserAction.OpenUserLogs -> viewModel.onOpenUserRecordsActionSelected(
                        selectedAction.userId
                    )
                }.exhaustive
            }
        selectActionDialog.show(childFragmentManager, "SelectAdminAction")
    }
}
