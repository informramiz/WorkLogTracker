package github.informramiz.timetracker.data.authentication.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import github.informramiz.timetracker.data.authentication.model.UpdateCurrentUserRequestApiModel
import github.informramiz.timetracker.data.authentication.model.UserInfoApiModel
import github.informramiz.timetracker.data.authentication.model.UserRoleApiModel
import github.informramiz.timetracker.data.db.DatabasePath
import github.informramiz.timetracker.data.db.getHttpsCallableSuspendable
import github.informramiz.timetracker.data.db.getTaskResult
import github.informramiz.timetracker.data.extensions.getSingleValue
import com.google.firebase.database.ktx.getValue
import javax.inject.Inject

class AuthenticationRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {
    companion object {
        private const val USER_ROLE_KEY = "role"
    }

    fun isUserLoggedIn() = firebaseAuth.currentUser != null

    suspend fun currentUser(): UserInfoApiModel {
        val currentUser = requireCurrentFirebaseUser()
        return UserInfoApiModel(
            currentUser.uid,
            currentUser.displayName!!,
            currentUser.email!!,
            userRole()
        )
    }

    suspend fun updateCurrentUser(requestApiModel: UpdateCurrentUserRequestApiModel) {
        val currentUser = requireCurrentFirebaseUser()
        currentUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(requestApiModel.name).build()).getTaskResult()
        currentUser.updateEmail(requestApiModel.email).getTaskResult()
    }

    private fun requireCurrentFirebaseUser(): FirebaseUser {
        val currentUser = firebaseAuth.currentUser
        require(currentUser != null) {
            "User must be logged in to get user info"
        }
        return currentUser
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun userRole(): UserRoleApiModel {
        val roleValue = requireCurrentFirebaseUser().getIdToken(true).getTaskResult().claims[USER_ROLE_KEY] as? String
            ?: return UserRoleApiModel.REGULAR
        return mapToUserRole(roleValue)
    }

    suspend fun preferredWorkingHoursPerDay(): Float? {
        if (!isUserLoggedIn()) return null
        return firebaseDatabase.getReference(DatabasePath.PreferredWorkingHoursPerDay(currentUser().userId).absolutePath()).getSingleValue().getValue<Float>()
    }

    suspend fun setPreferredWorkingHoursPerDay(hoursCount: Float?) {
        firebaseDatabase.getReference(DatabasePath.PreferredWorkingHoursPerDay(currentUser().userId).absolutePath()).setValue(hoursCount).getTaskResult()
    }

    private fun mapToUserRole(roleValue: String) = when (roleValue) {
        UserRoleApiModel.ADMIN.value -> UserRoleApiModel.ADMIN
        UserRoleApiModel.MANAGER.value -> UserRoleApiModel.MANAGER
        else -> UserRoleApiModel.REGULAR
    }

    private suspend fun callHelloWorld() {
        val result = FirebaseFunctions.getInstance().getHttpsCallableSuspendable<String>("helloWorld").getTaskResult()
    }
}
