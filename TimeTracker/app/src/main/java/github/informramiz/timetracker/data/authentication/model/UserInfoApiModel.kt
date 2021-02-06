package github.informramiz.timetracker.data.authentication.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoApiModel(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRoleApiModel = UserRoleApiModel.REGULAR
)
