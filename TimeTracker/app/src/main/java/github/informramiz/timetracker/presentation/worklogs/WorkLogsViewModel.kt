package github.informramiz.timetracker.presentation.worklogs

import androidx.hilt.lifecycle.ViewModelInject
import github.informramiz.timetracker.BuildConfig
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.worklogs.WorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.AdminWorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel
import github.informramiz.timetracker.presentation.base.BaseViewModel
import github.informramiz.timetracker.presentation.base.GeneralNavigationDestination
import github.informramiz.timetracker.presentation.settings.toDomainModel
import github.informramiz.timetracker.presentation.settings.toUiModel
import java.text.SimpleDateFormat
import java.util.*

class WorkLogsViewModel @ViewModelInject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val workLogsRepository: WorkLogsRepository,
    private val adminWorkLogsRepository: AdminWorkLogsRepository
) : BaseViewModel<WorkLogsViewState>() {

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    override fun initialState() = WorkLogsViewState(
        preferredWorkingHours = null,
        workLogs = emptyList(),
        filter = null,
        isRefreshing = false,
        isLoading = true
    )

    fun onStart(userId: String?) {
        loadData(userId)
    }

    private fun loadData(userId: String?) {
        launchWithErrorHandling {
            if (!authenticationRepository.isUserLoggedIn()) {
                navigate(GeneralNavigationDestination.LoginScreen)
            } else {
                fetchUserLogs(userId)
            }
        }
    }

    private suspend fun fetchUserLogs(userId: String?) {
        val preferredHoursCount = if (userId == null) {
            authenticationRepository.getPreferredWorkingHoursPerDay()
        } else {
            adminWorkLogsRepository.getUserPreferredWorkingHours(userId)
        }

        val logs = if (userId == null) {
            workLogsRepository.getWorkLogs()
        } else {
            adminWorkLogsRepository.getWorkLogs(userId)
        }

        updateStateWithLogs(logs, preferredHoursCount)
    }

    private fun updateStateWithLogs(logs: List<WorkLogDomainModel>, preferredHoursCount: Float?) {
        updateState { lastState ->
            val accumulatedDayHours = accumulateHoursByDay(logs)
            val logsUiModels = logs.sortedByDescending { it.date }.map {
                it.toUiModel(accumulatedDayHours[it.date.toDateString()] ?: 0f, preferredHoursCount)
            }
            lastState.copy(
                preferredWorkingHours = preferredHoursCount,
                workLogs = logsUiModels,
                filter = lastState.filter?.onNewItems(logsUiModels),
                isLoading = false,
                isRefreshing = false
            )
        }
    }

    private fun accumulateHoursByDay(logs: List<WorkLogDomainModel>): Map<String, Float> {
        val dayHoursMap = mutableMapOf<String, Float>()
        for (log in logs) {
            val currentHours = dayHoursMap[log.date.toDateString()] ?: 0f
            dayHoursMap[log.date.toDateString()] = currentHours + log.hoursWorked
        }
        return dayHoursMap
    }

    private fun Long.toDateString(): String {
        return dateFormatter.format(Date(this))
    }

    override fun onDataLoadFailure() {
        super.onDataLoadFailure()
        updateState { lastState -> lastState.copy(isRefreshing = false, isLoading = false) }
    }

    fun onCreateWorkLogFabClickAction(userId: String?) {
        navigate(WorkLogsNavigationDestination.WorkLogScreen(userId, null))
    }

    fun onSwipeToRefreshAction(userId: String?) {
        updateState { lastState -> lastState.copy(isRefreshing = true) }
        loadData(userId)
        updateState { lastState -> lastState.copy(isRefreshing = false) }
    }

    fun onWorkLogItemClickAction(userId: String?, workLogId: String) {
        navigate(WorkLogsNavigationDestination.WorkLogScreen(userId, workLogId))
    }

    fun onFilterActionClick() {
        notify(WorkLogsDialogCommand.DateRangePicker)
    }

    fun onFilterDateRangeSelectedAction(startDate: Long, endDate: Long) {
        launchWithErrorHandling {
            val filteredItems = currentViewState().workLogs.filter { it.date in startDate..endDate }
            updateState { lastState ->
                lastState.copy(
                    filter = DateFilterUiModel(
                        startDate,
                        endDate,
                        filteredItems
                    )
                )
            }
        }
    }

    fun onClearDateFilterClickAction() {
        updateState { lastState -> lastState.copy(filter = null) }
    }

    fun onExportFilteredListAction() {
        BuildConfig.DEBUG
        launchWithErrorHandling {
            val filteredItems =
                currentViewState().filter?.filteredItems ?: return@launchWithErrorHandling
            updateState { lastState -> lastState.copy(isLoading = true) }
            val filePath =
                workLogsRepository.writeLogsAsHtml(filteredItems.map { it.toDomainModel() })
            updateState { lastState -> lastState.copy(isLoading = false) }
            notify(WorkLogsDialogCommand.ShareFile(filePath))
        }
    }
}
