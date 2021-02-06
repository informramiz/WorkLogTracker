package github.informramiz.timetracker.domain.authentication.model

import github.informramiz.timetracker.data.authentication.model.UserRoleApiModel

enum class UserRole {
    REGULAR,
    MANAGER,
    ADMIN
}

fun UserRole.toApiModel() = when(this) {
    UserRole.REGULAR -> UserRoleApiModel.REGULAR
    UserRole.MANAGER -> UserRoleApiModel.MANAGER
    UserRole.ADMIN -> UserRoleApiModel.ADMIN
}

fun UserRoleApiModel.toDomainModel() = when(this) {
    UserRoleApiModel.REGULAR -> UserRole.REGULAR
    UserRoleApiModel.MANAGER -> UserRole.MANAGER
    UserRoleApiModel.ADMIN -> UserRole.ADMIN
}
