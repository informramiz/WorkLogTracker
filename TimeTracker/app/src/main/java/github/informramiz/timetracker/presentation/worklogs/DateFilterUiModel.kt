package github.informramiz.timetracker.presentation.worklogs

import github.informramiz.timetracker.presentation.settings.WorkLogUiModel

data class DateFilterUiModel(
    val startDate: Long,
    val endDate: Long,
    val filteredItems: List<WorkLogUiModel>
) {
    fun onNewItems(newItems: List<WorkLogUiModel>): DateFilterUiModel {
        val filteredItems = newItems.filter { it.date in startDate..endDate }
        return copy(filteredItems = filteredItems)
    }
}
