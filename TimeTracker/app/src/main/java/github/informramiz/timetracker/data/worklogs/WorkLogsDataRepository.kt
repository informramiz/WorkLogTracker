package github.informramiz.timetracker.data.worklogs

import github.informramiz.timetracker.data.htmlwriter.WorkLogsHtmlWriter
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.worklogs.WorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel
import github.informramiz.timetracker.domain.worklogs.model.toApiModel
import github.informramiz.timetracker.domain.worklogs.model.toDomainModel
import javax.inject.Inject

class WorkLogsDataRepository @Inject constructor(
    private val workLogsRemoteDataSource: WorkLogsRemoteDataSource,
    private val authenticationRepository: AuthenticationRepository,
    private val workLogsHtmlWriter: WorkLogsHtmlWriter
): WorkLogsRepository {
    private suspend fun userId() = authenticationRepository.currentUser().userId

    override suspend fun createWorkLog(workLogDomainModel: WorkLogDomainModel) {
        workLogsRemoteDataSource.createWorkLog(userId(), workLogDomainModel.toApiModel())
    }

    override suspend fun updateWorkLog(workLogDomainModel: WorkLogDomainModel) {
        workLogsRemoteDataSource.updateWorkLog(userId(), workLogDomainModel.toApiModel())
    }

    override suspend fun deleteWorkLog(workLogId: String) {
        workLogsRemoteDataSource.deleteWorkLog(userId(), workLogId)
    }

    override suspend fun getWorkLog(workLogId: String): WorkLogDomainModel {
        return workLogsRemoteDataSource.getWorkLog(userId(), workLogId).toDomainModel()
    }

    override suspend fun getWorkLogs(): List<WorkLogDomainModel> {
        return workLogsRemoteDataSource.getWorkLogs(userId()).map { it.toDomainModel() }
    }

    override suspend fun writeLogsAsHtml(logs: List<WorkLogDomainModel>): String {
        return workLogsHtmlWriter.writeToFileAsHtml(logs.map { it.toApiModel() })
    }
}
