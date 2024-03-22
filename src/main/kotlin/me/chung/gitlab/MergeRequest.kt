package me.chung.gitlab

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MergeRequest(
    var title: String,
    var createdAt: LocalDate,
    var webUrl: String,
    var assignee: Reviewer?,
    var reviewers: List<Reviewer>,
) {

    companion object {
        val format: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }

    fun getJoinedReviewerNames(): String? {
        if (reviewers.isEmpty()) {
            return null
        }

        return reviewers.joinToString { it.name }
    }
}

data class Reviewer(
    var name: String,
)
