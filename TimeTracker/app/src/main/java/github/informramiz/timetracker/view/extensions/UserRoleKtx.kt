package github.informramiz.timetracker.view.extensions

import github.informramiz.timetracker.R
import github.informramiz.timetracker.domain.authentication.model.UserRole

fun UserRole.toStringResource() = when(this) {
    UserRole.REGULAR -> R.string.role_regular_label
    UserRole.MANAGER -> R.string.role_manager_label
    UserRole.ADMIN -> R.string.role_admin_label
}
