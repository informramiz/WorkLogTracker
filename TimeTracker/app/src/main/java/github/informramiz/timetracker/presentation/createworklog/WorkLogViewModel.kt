package github.informramiz.timetracker.presentation.createworklog

import android.content.res.Resources
import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.R
import github.informramiz.timetracker.domain.worklogs.WorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.AdminWorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel
import github.informramiz.timetracker.presentation.base.BaseViewModel

class WorkLogViewModel @ViewModelInject constructor(
    private val workLogsRepository: WorkLogsRepository,
    private val adminWorkLogsRepository: AdminWorkLogsRepository,
    private val resources: Resources
) : BaseViewModel<WorkLogViewState>() {
    override fun initialState() = WorkLogViewState(
        tasksText = "",
        hoursWorked = 1f,
        date = System.currentTimeMillis(),
        isLoading = false
    )

    override fun onDataLoadFailure() {
        super.onDataLoadFailure()
        updateState { lastState -> lastState.copy(isLoading = false) }
    }

    fun loadDetails(userId: String?, workLogId: String?) {
        if (workLogId.isNullOrBlank()) {
            return
        }

        if (isLoggedInUser(userId)) {
            loadWorkLogForLoggedInUser(workLogId)
        } else {
            loadWorkLogForAdmin(userId!!, workLogId)
        }
    }

    private fun loadWorkLogForLoggedInUser(workLogId: String) {
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true) }
            val log = workLogsRepository.getWorkLog(workLogId)
            updateStateWithLog(log)
        }
    }

    private fun loadWorkLogForAdmin(userId: String, workLogId: String) {
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true) }
            val log = adminWorkLogsRepository.getWorkLog(userId, workLogId)
            updateStateWithLog(log)
        }
    }

    private fun updateStateWithLog(log: WorkLogDomainModel) {
        updateState { lastState ->
            lastState.copy(
                tasksText = log.tasksText,
                hoursWorked = log.hoursWorked,
                date = log.date,
                isLoading = false
            )
        }
    }

    fun onHoursWorkedItemSelectedAction(hoursSelected: Float, tasksText: String) {
        updateState { lastState ->
            lastState.copy(
                hoursWorked = hoursSelected,
                tasksText = tasksText
            )
        }
    }

    fun onDateSelectionChangedAction(newDate: Long, tasksText: String) {
        if (newDate > System.currentTimeMillis()) {
            notifyFailure(resources.getString(R.string.work_log_fragment_invalid_date_error_msg))
        } else {
            updateState { lastState -> lastState.copy(date = newDate, tasksText = tasksText) }
        }
    }

    fun onSaveWorkLogClickAction(userId: String?, workLogId: String?, tasksText: String) {
        if (tasksText.isBlank()) {
            notifyFailure(resources.getString(R.string.work_log_fragment_invalid_tasks_text_error_msg))
            return
        }

        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true, tasksText = tasksText) }
            val currentViewState = currentViewState()
            val model = WorkLogDomainModel(
                workLogId = workLogId ?: "",
                tasksText = tasksText,
                hoursWorked = currentViewState.hoursWorked,
                date = currentViewState.date
            )

            if (isLoggedInUser(userId)) {
                saveWorkLogForLoggedInUser(workLogId, model)
            } else {
                saveWorkLogForAdmin(userId!!, workLogId, model)
            }
            updateState { lastState -> lastState.copy(isLoading = false) }
            navigateBack()
        }
    }

    private suspend fun saveWorkLogForLoggedInUser(workLogId: String?, model: WorkLogDomainModel) {
        if (workLogId != null) {
            workLogsRepository.updateWorkLog(model)
        } else {
            workLogsRepository.createWorkLog(model)
        }
    }

    private suspend fun saveWorkLogForAdmin(userId: String, workLogId: String?, model: WorkLogDomainModel) {
        if (workLogId == null) {
            adminWorkLogsRepository.createWorkLog(userId, model)
        } else {
            adminWorkLogsRepository.updateWorkLog(userId, workLogId, model)
        }
    }

    fun onDeleteWorkLogClickAction(userId: String?, workLogId: String?, tasksText: String) {
        if (workLogId == null) {
            notifyFailure(resources.getString(R.string.work_log_fragment_work_log_id_missing_error_msg))
            return
        }

        updateState { lastState -> lastState.copy(tasksText = tasksText) }
        notify(WorkLogDialogCommand.DeleteWorkLogConfirmationDialog(userId, workLogId))
    }

    fun onDeleteWorkLogConfirmationAction(userId: String?, workLogId: String) {
        deleteWorkLog(userId, workLogId)
    }

    private fun deleteWorkLog(userId: String?, workLogId: String) {
        launchWithErrorHandling {
            updateState { lastState -> lastState.copy(isLoading = true) }
            if (isLoggedInUser(userId)) {
                deleteWorkLogForLoggedInUser(workLogId)
            } else {
                deleteWorkLogForAdmin(userId!!, workLogId)
            }
            updateState { lastState -> lastState.copy(isLoading = false) }
            navigateBack()
        }
    }

    private suspend fun deleteWorkLogForLoggedInUser(workLogId: String) {
        workLogsRepository.deleteWorkLog(workLogId)
    }

    private suspend fun deleteWorkLogForAdmin(userId: String, workLogId: String) {
        adminWorkLogsRepository.deleteWorkLog(userId, workLogId)
    }

    private fun isLoggedInUser(userId: String?) = userId == null
}
