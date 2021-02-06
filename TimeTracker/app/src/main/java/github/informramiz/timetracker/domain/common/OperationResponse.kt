package github.informramiz.timetracker.domain.common

sealed class OperationResponse<T> {
    data class Success<T>(val data: T) : OperationResponse<T>()
    data class Failure(val errorMsg: String) : OperationResponse<Unit>()
}
