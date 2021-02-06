package github.informramiz.timetracker.domain.authentication.model

import github.informramiz.timetracker.data.authentication.model.UserInfoApiModel

data class UserInfoDomainModel(
    val userId: String,
    val email: String,
    val name: String,
    val role: UserRole
)

fun UserInfoDomainModel.toApiModel() = UserInfoApiModel(
    userId = userId,
    name = name,
    email = email,
    role = role.toApiModel()
)

fun UserInfoApiModel.toDomainModel() = UserInfoDomainModel(
    userId = userId,
    name = name,
    email = email,
    role = role.toDomainModel()
)
