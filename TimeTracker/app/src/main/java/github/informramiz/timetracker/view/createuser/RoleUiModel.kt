package github.informramiz.timetracker.view.createuser

import github.informramiz.timetracker.domain.authentication.model.UserRole

data class RoleUiModel(val title: String, val roleType: UserRole) {
    override fun toString() = title
}
