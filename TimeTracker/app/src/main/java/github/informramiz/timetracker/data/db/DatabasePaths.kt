package github.informramiz.timetracker.data.db

sealed class DatabasePath(private val relativePath: String) {
    abstract fun basePath(): String

    fun absolutePath()= if (relativePath.isNotEmpty()) "${basePath()}/$relativePath" else basePath()

    open class User(val userId: String, relativePath: String = "") : DatabasePath(relativePath) {
        override fun basePath() = "users/$userId"
    }

    open class WorkLogs(userId: String, relativePath: String = "") : User(userId, relativePath) {
        override fun basePath(): String {
            return super.basePath() + "/workLogs"
        }
    }

    class WorkLog(val workLogKey: String, userId: String, relativePath: String = "") : WorkLogs(userId, relativePath) {
        override fun basePath(): String {
            return super.basePath() + "/$workLogKey"
        }
    }

    class PreferredWorkingHoursPerDay(userId: String) : User(userId) {
        override fun basePath(): String {
            return super.basePath() + "/preferredWorkingHoursPerDay"
        }
    }
}
