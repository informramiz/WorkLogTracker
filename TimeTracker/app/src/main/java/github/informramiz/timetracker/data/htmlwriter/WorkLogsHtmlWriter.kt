package github.informramiz.timetracker.data.htmlwriter

import github.informramiz.timetracker.data.worklogs.model.WorkLogApiModel
import github.informramiz.timetracker.di.qualifiers.ExternalFilesDirectoryPath
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private data class DayWorkLogSummary(
    val dateInMillis: Long,
    val date: String,
    val totalHoursWorkedOnThatDate: Float = 0f,
    val notes: String = ""
)

private fun DayWorkLogSummary.onNewLog(newLog: WorkLogApiModel): DayWorkLogSummary {
    val hoursSum = totalHoursWorkedOnThatDate + newLog.hoursWorked
    val newNotesText = notes + "<li>${newLog.tasksText}</li>"
    return copy(totalHoursWorkedOnThatDate = hoursSum, notes = newNotesText)
}

private fun DayWorkLogSummary.toHtmlFriendlyString(): String {
    return """
            |<b>Date: $date</b> <br>
            |Total Time: $totalHoursWorkedOnThatDate hours <br>
            |Notes: <ul>$notes</ul>
        """.trimMargin()
}

class WorkLogsHtmlWriter @Inject constructor(
    @ExternalFilesDirectoryPath
    private val externalFilesDirectoryPath: String
) {
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

    fun writeToFileAsHtml(workLogs: List<WorkLogApiModel>): String {
        val htmlText = workLogs.prepareLogsSummaryByDate().toHtmlFriendlyString().wrapIntoHtmlBody()
        val file = File(externalFilesDirectoryPath,  "${System.currentTimeMillis()}.html")
        file.writeText(htmlText)
        return file.absolutePath
    }

    private fun String.wrapIntoHtmlBody(): String {
        return """
            |<html>
                |<head>
                    |<title>Filtered Work Logs</title>
                |</head>
                |<style> .content { align-content: center; margin: auto; } </style>
                    |<body>
                        |<div class="content">$this</div>
                    |</body>
            |</html>""".trimMargin()
    }

    private fun List<DayWorkLogSummary>.toHtmlFriendlyString(): String {
        return joinToString("<br>") { it.toHtmlFriendlyString() }
    }

    private fun List<WorkLogApiModel>.prepareLogsSummaryByDate(): List<DayWorkLogSummary> {
        val summaryByDateMap = mutableMapOf<String, DayWorkLogSummary>()
        for (log in this) {
            val dateString = log.date.toDateString()
            val existingSummary = summaryByDateMap[dateString] ?: DayWorkLogSummary(log.date, dateString)
            summaryByDateMap[dateString] = existingSummary.onNewLog(log)
        }

        return summaryByDateMap.values.sortedByDescending { it.dateInMillis }.toList()
    }

    private fun Long.toDateString(): String {
        return dateFormatter.format(Date(this))
    }
}
