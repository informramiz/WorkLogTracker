package github.informramiz.timetracker.presentation.worklogs

import github.informramiz.timetracker.presentation.base.DialogCommand

sealed class WorkLogsDialogCommand : DialogCommand {
    object DateRangePicker : WorkLogsDialogCommand()
    data class ShareFile(val filePath: String) : WorkLogsDialogCommand()
}
