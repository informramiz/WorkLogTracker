package github.informramiz.timetracker.presentation.settings

import github.informramiz.timetracker.presentation.base.DialogCommand

sealed class SettingsDialogCommand : DialogCommand {
    object SelectWorkingHoursCount : SettingsDialogCommand()
    object LogoutConfirmation : SettingsDialogCommand()
}
