package github.informramiz.timetracker.data.worklogs

import github.informramiz.timetracker.domain.worklogs.model.AdminWorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel
import github.informramiz.timetracker.domain.worklogs.model.toApiModel
import github.informramiz.timetracker.domain.worklogs.model.toDomainModel
import javax.inject.Inject

class AdminWorkLogsDataRepository @Inject constructor(private val adminRemoteDataSource: AdminWorkLogsRemoteDataSource) :
    AdminWorkLogsRepository {
    override suspend fun getWorkLog(userId: String, workLogId: String): WorkLogDomainModel {
        return adminRemoteDataSource.getWorkLog(userId, workLogId).toDomainModel()
    }

    override suspend fun getWorkLogs(userId: String): List<WorkLogDomainModel> {
        return adminRemoteDataSource.getWorkLogs(userId).map { it.toDomainModel() }
    }

    override suspend fun updateWorkLog(
        userId: String,
        workLogId: String,
        workLogDomainModel: WorkLogDomainModel
    ) {
        adminRemoteDataSource.updateWorkLog(userId, workLogId, workLogDomainModel.toApiModel())
    }

    override suspend fun deleteWorkLog(userId: String, workLogId: String) {
        adminRemoteDataSource.deleteWorkLog(userId, workLogId)
    }

    override suspend fun createWorkLog(userId: String, workLogDomainModel: WorkLogDomainModel) {
        adminRemoteDataSource.createWorkLog(userId, workLogDomainModel.toApiModel())
    }

    override suspend fun getUserPreferredWorkingHours(userId: String): Float? {
        return adminRemoteDataSource.getUserPreferredWorkingHours(userId)
    }
}
