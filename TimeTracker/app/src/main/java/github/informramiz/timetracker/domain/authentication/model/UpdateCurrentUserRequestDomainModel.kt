package github.informramiz.timetracker.domain.authentication.model

import github.informramiz.timetracker.data.authentication.model.UpdateCurrentUserRequestApiModel

data class UpdateCurrentUserRequestDomainModel(
    val name: String,
    val email: String
)

fun UpdateCurrentUserRequestDomainModel.toApiModel() = UpdateCurrentUserRequestApiModel(
    name = name,
    email = email
)
