package github.informramiz.timetracker.domain.user

import github.informramiz.timetracker.domain.authentication.model.UserInfoDomainModel
import github.informramiz.timetracker.domain.user.model.CreateUserDomainModel
import github.informramiz.timetracker.domain.user.model.UpdateUserRequestDomainModel

interface UserManagementRepository {
    suspend fun createUser(createUserDomainModel: CreateUserDomainModel)
    suspend fun getUsers(): List<UserInfoDomainModel>
    suspend fun getUser(userId: String): UserInfoDomainModel
    suspend fun updateUser(updateUserRequestDomainModel: UpdateUserRequestDomainModel)
    suspend fun deleteUser(userId: String)
}
