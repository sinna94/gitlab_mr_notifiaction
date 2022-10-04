package me.chung

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.slack.api.Slack
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.text.SimpleDateFormat
import java.util.*

private const val PRIVATE_TOKEN = "PRIVATE-TOKEN"
private const val baseUrl = "https://gitlab.com/api/v4"

fun main() {
    val mergeRequestsNotification = getMergeRequestsNotification()
    sendSlack(mergeRequestsNotification)
}

private fun getMergeRequestsNotification(): String {
    val gitlabToken = getEnvVariable("GITLAB_TOKEN")
    val gitlabGroupId = getEnvVariable("GITLAB_GROUP_ID")

    val httpClient = HttpClient.newHttpClient()
    val response = httpClient.send(
        HttpRequest.newBuilder(URI("$baseUrl/groups/${gitlabGroupId}/merge_requests?state=opened")).GET()
            .header(PRIVATE_TOKEN, gitlabToken)
            .build(),
        HttpResponse.BodyHandlers.ofString()
    )

    val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )

    val format = SimpleDateFormat("yyyy-MM-dd")
    val mergeRequests = objectMapper.readValue(response.body(), object : TypeReference<List<MergeRequest>>() {})
    val mergeRequestsNotification = mergeRequests.joinToString("\n\n") { mergeRequest ->
        val (title, createdAt, webUrl, assignee, reviewers) = mergeRequest
        val createdAtString = format.format(createdAt)
        val reviewerNames = reviewers.joinToString { it.name }
        "$title : 생성일 $createdAtString (${webUrl}) / ${assignee.name}, $reviewerNames"
    }
    return mergeRequestsNotification
}

private fun getEnvVariable(key: String): String? =
    System.getenv(key) ?: throw IllegalStateException("please set $key in environment")

data class MergeRequest(
    var title: String,
    var createdAt: Date,
    var webUrl: String,
    var assignee: Reviewer,
    var reviewers: List<Reviewer>,
)

data class Reviewer(
    var name: String,
)

private fun sendSlack(message: String) {
    val slack = Slack.getInstance()
    val token = getEnvVariable("SLACK_TOKEN")
    val channelId = getEnvVariable("SLACK_CHANNEL_ID")
    slack.methods(token).chatPostMessage { req ->
        req.channel(channelId)
            .text(message)
    }
}
