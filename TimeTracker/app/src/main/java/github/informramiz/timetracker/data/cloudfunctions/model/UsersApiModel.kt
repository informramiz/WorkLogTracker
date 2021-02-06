package github.informramiz.timetracker.data.cloudfunctions.model

import github.informramiz.timetracker.data.authentication.model.UserInfoApiModel
import kotlinx.serialization.Serializable

@Serializable
data class UsersApiModel(val users: List<UserInfoApiModel>)
