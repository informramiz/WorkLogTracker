package github.informramiz.timetracker.presentation.base

sealed class NotificationState(open val message: String) {
    data class Success(override val message: String) : NotificationState(message)
    data class Loading(override val message: String) : NotificationState(message)
    data class Failure(override val message: String) : NotificationState(message)
    data class Normal(override val message: String) : NotificationState(message)
}
