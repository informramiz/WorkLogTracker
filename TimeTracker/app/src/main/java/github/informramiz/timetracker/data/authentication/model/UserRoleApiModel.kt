package github.informramiz.timetracker.data.authentication.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRoleApiModel(val value: String) {
    @SerialName("regular")
    REGULAR("regular"),
    @SerialName("manager")
    MANAGER("manager"),
    @SerialName("admin")
    ADMIN("admin")
}
