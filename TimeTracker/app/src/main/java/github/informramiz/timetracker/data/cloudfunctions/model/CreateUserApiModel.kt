package github.informramiz.timetracker.data.cloudfunctions.model

import github.informramiz.timetracker.data.authentication.model.UserRoleApiModel
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserApiModel(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRoleApiModel
)
