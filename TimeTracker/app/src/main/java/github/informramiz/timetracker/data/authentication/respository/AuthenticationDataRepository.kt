package github.informramiz.timetracker.data.authentication.respository

import github.informramiz.timetracker.data.authentication.remote.AuthenticationRemoteDataSource
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UpdateCurrentUserRequestDomainModel
import github.informramiz.timetracker.domain.authentication.model.toApiModel
import github.informramiz.timetracker.domain.authentication.model.toDomainModel
import javax.inject.Inject

class AuthenticationDataRepository @Inject constructor(private val authenticationRemoteDataSource: AuthenticationRemoteDataSource) :
    AuthenticationRepository {
    override suspend fun isUserLoggedIn() = authenticationRemoteDataSource.isUserLoggedIn()

    override suspend fun currentUser() =
        authenticationRemoteDataSource.currentUser().toDomainModel()

    override suspend fun updateUser(request: UpdateCurrentUserRequestDomainModel) {
        authenticationRemoteDataSource.updateCurrentUser(request.toApiModel())
    }

    override suspend fun signOut() {
        authenticationRemoteDataSource.signOut()
    }

    override suspend fun getPreferredWorkingHoursPerDay(): Float? {
        return authenticationRemoteDataSource.preferredWorkingHoursPerDay()
    }

    override suspend fun setPreferredWorkingHoursPerDay(workingHours: Float?) {
        authenticationRemoteDataSource.setPreferredWorkingHoursPerDay(workingHours)
    }
}
