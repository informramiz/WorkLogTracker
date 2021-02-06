package github.informramiz.timetracker.domain.user.model

import github.informramiz.timetracker.data.cloudfunctions.model.UpdateUserRequestApiModel
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.domain.authentication.model.toApiModel

data class UpdateUserRequestDomainModel(
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRole
)

fun UpdateUserRequestDomainModel.toApiModel() = UpdateUserRequestApiModel(
    userId = userId,
    name = name,
    email = email,
    role = role.toApiModel()
)
