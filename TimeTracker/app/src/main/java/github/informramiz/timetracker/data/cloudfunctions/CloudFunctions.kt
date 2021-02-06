package github.informramiz.timetracker.data.cloudfunctions

import com.google.firebase.functions.FirebaseFunctions
import github.informramiz.timetracker.data.authentication.model.UserInfoApiModel
import github.informramiz.timetracker.data.cloudfunctions.model.*
import github.informramiz.timetracker.data.db.getHttpsCallableSuspendable
import github.informramiz.timetracker.data.db.getTaskResult
import github.informramiz.timetracker.data.worklogs.model.WorkLogApiModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

private typealias AnyResponse = Map<String, String>

class CloudFunctions(private val firebaseFunctions: FirebaseFunctions) {
    companion object {
        private const val FUNCTION_NAME_CREATE_USER = "createUser"
        private const val FUNCTION_NAME_GET_USERS = "getUsers"
        private const val FUNCTION_NAME_GET_USER = "getUser"
        private const val FUNCTION_NAME_UPDATE_USER = "updateUser"
        private const val FUNCTION_NAME_DELETE_USER = "deleteUser"
        private const val FUNCTION_NAME_GET_WORK_LOG = "getWorkLog"
        private const val FUNCTION_NAME_GET_WORK_LOGS = "getWorkLogs"
        private const val FUNCTION_NAME_UPDATE_WORK_LOG = "updateWorkLog"
        private const val FUNCTION_NAME_DELETE_WORK_LOG = "deleteWorkLog"
        private const val FUNCTION_NAME_CREATE_WORK_LOG = "createWorkLog"
        private const val FUNCTION_NAME_GET_USER_PREFERRED_WORKING_HOURS = "getPreferredWorkingHours"
    }

    suspend fun createUser(createUserApiModel: CreateUserApiModel) {
        firebaseFunctions.getHttpsCallableSuspendable<Any>(
            FUNCTION_NAME_CREATE_USER,
            createUserApiModel.toJsonObject()
        ).getTaskResult()
    }

    suspend fun getUsers(): List<UserInfoApiModel> {
        return firebaseFunctions.callCloudFunctionWithJsonParsing<UsersApiModel>(FUNCTION_NAME_GET_USERS).users
    }

    suspend fun getUser(userId: String): UserInfoApiModel {
        return firebaseFunctions.callCloudFunctionWithJsonParsing(
            FUNCTION_NAME_GET_USER,
            GetUserRequestApiModel(userId)
        )
    }

    suspend fun updateUser(request: UpdateUserRequestApiModel) {
        firebaseFunctions.getHttpsCallableSuspendable<Any>(
            FUNCTION_NAME_UPDATE_USER,
            request.toJsonObject()
        ).getTaskResult()
    }

    suspend fun deleteUser(userId: String) {
        firebaseFunctions.getHttpsCallableSuspendable<Any>(
            FUNCTION_NAME_DELETE_USER,
            DeleteUserRequestApiModel(userId).toJsonObject()
        ).getTaskResult()
    }

    suspend fun getWorkLog(userId: String, workLogId: String): WorkLogApiModel {
        return firebaseFunctions.callCloudFunctionWithJsonParsing(
            FUNCTION_NAME_GET_WORK_LOG,
            GetWorkLogRequestApiModel(userId, workLogId)
        )
    }

    suspend fun getWorkLogs(userId: String): List<WorkLogApiModel> {
        return firebaseFunctions.callCloudFunctionWithJsonParsing<GetWorkLogsRequestApiModel, Map<String, WorkLogApiModel>>(
            FUNCTION_NAME_GET_WORK_LOGS,
            GetWorkLogsRequestApiModel(userId)
        ).values.toList()
    }

    suspend fun updateWorkLog(userId: String, workLogId: String, workLogApiModel: WorkLogApiModel) {
        firebaseFunctions.callCloudFunctionWithJsonParsing<UpdateWorkLogRequestApiModel, AnyResponse>(
            FUNCTION_NAME_UPDATE_WORK_LOG,
            UpdateWorkLogRequestApiModel(
                userId = userId,
                workLogId = workLogId,
                tasksText = workLogApiModel.tasksText,
                hoursWorked = workLogApiModel.hoursWorked,
                date = workLogApiModel.date
            )
        )
    }

    suspend fun deleteWorkLog(userId: String, workLogId: String) {
        firebaseFunctions.callCloudFunctionWithJsonParsing<DeleteWorkLogRequestApiModel, AnyResponse>(
            FUNCTION_NAME_DELETE_WORK_LOG,
            DeleteWorkLogRequestApiModel(userId, workLogId)
        )
    }

    suspend fun createWorkLog(userId: String, workLogApiModel: WorkLogApiModel) {
        firebaseFunctions.callCloudFunctionWithJsonParsing<CreateWorkLogRequestApiModel, AnyResponse>(
            FUNCTION_NAME_CREATE_WORK_LOG,
            CreateWorkLogRequestApiModel(
                userId = userId,
                tasksText = workLogApiModel.tasksText,
                hoursWorked = workLogApiModel.hoursWorked,
                date = workLogApiModel.date
            )
        )
    }

    suspend fun getUserPreferredWorkingHours(userId: String): Float? {
        return firebaseFunctions
            .callCloudFunctionWithJsonParsing<GetUserWorkingHoursRequestApiModel, GetUserWorkingHoursResponseApiModel>(
                FUNCTION_NAME_GET_USER_PREFERRED_WORKING_HOURS,
                GetUserWorkingHoursRequestApiModel(userId)
            ).preferredWorkingHours
    }
}

private suspend inline fun <reified I, reified R> FirebaseFunctions.callCloudFunctionWithJsonParsing(
    functionName: String,
    data: I? = null
): R {
    val result =
        getHttpsCallableSuspendable<String>(functionName, data?.toJsonObject()).getTaskResult()
    return Json.decodeFromString(result)
}

private suspend inline fun <reified R> FirebaseFunctions.callCloudFunctionWithJsonParsing(functionName: String): R {
    return callCloudFunctionWithJsonParsing<Unit, R>(functionName)
}

private inline fun <reified T> T.toJsonObject(): JSONObject {
    return JSONObject(Json.encodeToString(this))
}
