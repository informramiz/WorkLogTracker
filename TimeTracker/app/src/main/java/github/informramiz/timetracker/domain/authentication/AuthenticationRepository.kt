package github.informramiz.timetracker.domain.authentication

import github.informramiz.timetracker.domain.authentication.model.UpdateCurrentUserRequestDomainModel
import github.informramiz.timetracker.domain.authentication.model.UserInfoDomainModel

interface AuthenticationRepository {
    suspend fun isUserLoggedIn(): Boolean
    suspend fun currentUser(): UserInfoDomainModel
    suspend fun updateUser(request: UpdateCurrentUserRequestDomainModel)
    suspend fun signOut()
    suspend fun getPreferredWorkingHoursPerDay(): Float?
    suspend fun setPreferredWorkingHoursPerDay(workingHours: Float?)
}
