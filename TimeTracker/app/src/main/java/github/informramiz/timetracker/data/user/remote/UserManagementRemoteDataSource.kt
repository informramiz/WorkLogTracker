package github.informramiz.timetracker.data.user.remote

import com.google.firebase.auth.FirebaseAuth
import github.informramiz.timetracker.data.authentication.model.UserInfoApiModel
import github.informramiz.timetracker.data.cloudfunctions.CloudFunctions
import github.informramiz.timetracker.data.cloudfunctions.model.CreateUserApiModel
import github.informramiz.timetracker.data.cloudfunctions.model.UpdateUserRequestApiModel
import javax.inject.Inject

class UserManagementRemoteDataSource @Inject constructor(
    private val cloudFunctions: CloudFunctions,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun createUser(createUserApiModel: CreateUserApiModel) {
        cloudFunctions.createUser(createUserApiModel)
    }

    suspend fun getUsers(): List<UserInfoApiModel> {
        return cloudFunctions.getUsers()
    }

    suspend fun getUser(userId: String): UserInfoApiModel {
        return cloudFunctions.getUser(userId)
    }

    suspend fun updateUser(updateUserRequestApiModel: UpdateUserRequestApiModel) {
        cloudFunctions.updateUser(updateUserRequestApiModel)
    }

    suspend fun deleteUser(userId: String) {
        cloudFunctions.deleteUser(userId)
    }
}
