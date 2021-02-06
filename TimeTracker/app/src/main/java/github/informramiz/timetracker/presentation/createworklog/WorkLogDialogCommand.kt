package github.informramiz.timetracker.presentation.createworklog

import github.informramiz.timetracker.presentation.base.DialogCommand

sealed class WorkLogDialogCommand : DialogCommand {
    data class DeleteWorkLogConfirmationDialog(val userId: String?, val workLogId: String) : WorkLogDialogCommand()
}
