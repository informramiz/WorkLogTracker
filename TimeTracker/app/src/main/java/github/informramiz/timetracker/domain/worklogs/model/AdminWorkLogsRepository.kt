package github.informramiz.timetracker.domain.worklogs.model

interface AdminWorkLogsRepository {
    suspend fun getWorkLog(userId: String, workLogId: String): WorkLogDomainModel
    suspend fun getWorkLogs(userId: String): List<WorkLogDomainModel>
    suspend fun updateWorkLog(userId: String, workLogId: String, workLogDomainModel: WorkLogDomainModel)
    suspend fun deleteWorkLog(userId: String, workLogId: String)
    suspend fun createWorkLog(userId: String, workLogDomainModel: WorkLogDomainModel)
    suspend fun getUserPreferredWorkingHours(userId: String): Float?
}
