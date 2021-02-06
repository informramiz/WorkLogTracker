package github.informramiz.timetracker.presentation.users

import github.informramiz.timetracker.presentation.base.DialogCommand

sealed class UsersDialogCommand : DialogCommand {
    data class SelectAdminActionDialog(val userId: String) : UsersDialogCommand()
}
