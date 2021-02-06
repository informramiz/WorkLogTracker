package github.informramiz.timetracker.data.worklogs

import github.informramiz.timetracker.data.cloudfunctions.CloudFunctions
import github.informramiz.timetracker.data.worklogs.model.WorkLogApiModel
import javax.inject.Inject

class AdminWorkLogsRemoteDataSource @Inject constructor(private val cloudFunctions: CloudFunctions) {
    suspend fun getWorkLog(userId: String, workLogId: String): WorkLogApiModel {
        return cloudFunctions.getWorkLog(userId, workLogId)
    }

    suspend fun getWorkLogs(userId: String): List<WorkLogApiModel> {
        return cloudFunctions.getWorkLogs(userId)
    }

    suspend fun updateWorkLog(userId: String, workLogId: String, workLogApiModel: WorkLogApiModel) {
        cloudFunctions.updateWorkLog(userId, workLogId, workLogApiModel)
    }

    suspend fun deleteWorkLog(userId: String, workLogId: String) {
        cloudFunctions.deleteWorkLog(userId, workLogId)
    }

    suspend fun createWorkLog(userId: String, workLogApiModel: WorkLogApiModel) {
        cloudFunctions.createWorkLog(userId, workLogApiModel)
    }

    suspend fun getUserPreferredWorkingHours(userId: String): Float? {
        return cloudFunctions.getUserPreferredWorkingHours(userId)
    }
}
