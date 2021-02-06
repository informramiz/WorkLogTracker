package github.informramiz.timetracker.data.worklogs

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import github.informramiz.timetracker.data.db.DatabasePath
import github.informramiz.timetracker.data.db.getTaskResult
import github.informramiz.timetracker.data.extensions.getSingleValue
import github.informramiz.timetracker.data.worklogs.model.WorkLogApiModel
import javax.inject.Inject

class WorkLogsRemoteDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend fun createWorkLog(userId: String, workLogApiModel: WorkLogApiModel) {
        val child =
            firebaseDatabase.getReference(DatabasePath.WorkLogs(userId).absolutePath()).push()
        child.setValue(workLogApiModel.copy(workLogId = child.key!!)).getTaskResult()
    }

    suspend fun updateWorkLog(userId: String, workLogApiModel: WorkLogApiModel) {
        firebaseDatabase.getReference(
            DatabasePath.WorkLog(workLogApiModel.workLogId, userId).absolutePath()
        ).setValue(workLogApiModel).getTaskResult()
    }

    suspend fun deleteWorkLog(userId: String, workLogId: String) {
        firebaseDatabase.getReference(DatabasePath.WorkLog(workLogId, userId).absolutePath())
            .setValue(null)
    }

    suspend fun getWorkLog(userId: String, workLogId: String): WorkLogApiModel {
        val snapshot =
            firebaseDatabase.getReference(DatabasePath.WorkLog(workLogId, userId).absolutePath())
                .getSingleValue()
        return snapshot.getValue<WorkLogApiModel>()!!
    }

    suspend fun getWorkLogs(userId: String): List<WorkLogApiModel> {
        val childReference =
            firebaseDatabase.getReference(DatabasePath.WorkLogs(userId).absolutePath())
        val result = childReference.limitToFirst(1000).get().getTaskResult()
        return result.children.map {
            it.getValue<WorkLogApiModel>()!!
        }
    }
}
