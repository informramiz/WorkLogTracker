package github.informramiz.timetracker.data.user

import github.informramiz.timetracker.data.user.remote.UserManagementRemoteDataSource
import github.informramiz.timetracker.domain.authentication.model.UserInfoDomainModel
import github.informramiz.timetracker.domain.authentication.model.toDomainModel
import github.informramiz.timetracker.domain.user.UserManagementRepository
import github.informramiz.timetracker.domain.user.model.CreateUserDomainModel
import github.informramiz.timetracker.domain.user.model.UpdateUserRequestDomainModel
import github.informramiz.timetracker.domain.user.model.toApiModel
import javax.inject.Inject

class UserManagementDataRepository @Inject constructor(
    private val userManagementRemoteDataSource: UserManagementRemoteDataSource
) : UserManagementRepository {
    override suspend fun createUser(createUserDomainModel: CreateUserDomainModel) {
        userManagementRemoteDataSource.createUser(createUserDomainModel.toApiModel())
    }

    override suspend fun getUsers(): List<UserInfoDomainModel> {
        return userManagementRemoteDataSource.getUsers().map { it.toDomainModel() }
    }

    override suspend fun getUser(userId: String): UserInfoDomainModel {
        return userManagementRemoteDataSource.getUser(userId).toDomainModel()
    }

    override suspend fun updateUser(updateUserRequestDomainModel: UpdateUserRequestDomainModel) {
        userManagementRemoteDataSource.updateUser(updateUserRequestDomainModel.toApiModel())
    }

    override suspend fun deleteUser(userId: String) {
        userManagementRemoteDataSource.deleteUser(userId)
    }
}
