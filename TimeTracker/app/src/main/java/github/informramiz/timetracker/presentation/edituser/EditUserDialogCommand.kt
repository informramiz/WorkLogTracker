package github.informramiz.timetracker.presentation.edituser

import github.informramiz.timetracker.presentation.base.DialogCommand

sealed class EditUserDialogCommand : DialogCommand {
    data class DeleteUserConfirmationDialog(val userId: String) : EditUserDialogCommand()
}
