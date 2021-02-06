package github.informramiz.timetracker.domain.user.model

import github.informramiz.timetracker.data.cloudfunctions.model.CreateUserApiModel
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.domain.authentication.model.toApiModel

data class CreateUserDomainModel(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole
)

fun CreateUserDomainModel.toApiModel() = CreateUserApiModel(
    name = name,
    email = email,
    password = password,
    role = role.toApiModel()
)
