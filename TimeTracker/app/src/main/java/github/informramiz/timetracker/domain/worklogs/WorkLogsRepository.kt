package github.informramiz.timetracker.domain.worklogs

import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel

interface WorkLogsRepository {
    suspend fun createWorkLog(workLogDomainModel: WorkLogDomainModel)
    suspend fun updateWorkLog(workLogDomainModel: WorkLogDomainModel)
    suspend fun deleteWorkLog(workLogId: String)
    suspend fun getWorkLog(workLogId: String): WorkLogDomainModel
    suspend fun getWorkLogs(): List<WorkLogDomainModel>
    suspend fun writeLogsAsHtml(logs: List<WorkLogDomainModel>): String
}
