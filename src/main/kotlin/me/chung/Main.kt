package me.chung

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.slack.api.Slack
import com.slack.api.model.block.HeaderBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val PRIVATE_TOKEN = "PRIVATE-TOKEN"
private const val baseUrl = "https://gitlab.com/api/v4"
private val now = LocalDate.now()

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    val mergeRequests = getMergeRequests()
    if (mergeRequests.isEmpty()) {
        logger.info("no merge requests")
        return
    }
    val layoutBlocks = mergeRequests.flatMap { it.createBlock() }
    sendSlack(layoutBlocks)
}

private fun getMergeRequests(): List<MergeRequest> {
    val gitlabToken = getEnvVariable("GITLAB_TOKEN")
    val gitlabGroupId = getEnvVariable("GITLAB_GROUP_ID")

    val httpClient = HttpClient.newHttpClient()
    val response = httpClient.send(
        HttpRequest.newBuilder(URI("$baseUrl/groups/${gitlabGroupId}/merge_requests?state=opened")).GET()
            .header(PRIVATE_TOKEN, gitlabToken)
            .build(),
        HttpResponse.BodyHandlers.ofString()
    )

    val objectMapper = buildObjectMapper()

    return objectMapper.readValue(response.body(), object : TypeReference<List<MergeRequest>>() {})
}

private fun getEnvVariableOrNull(key: String): String? = System.getenv(key)
private fun getEnvVariable(key: String): String =
    System.getenv(key) ?: throw IllegalStateException("please set $key in environment")

private fun buildObjectMapper(): ObjectMapper {
    return ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .registerModules(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build(),
            JavaTimeModule()
        )
}

private fun sendSlack(blocks: List<LayoutBlock>) {
    getEnvVariableOrNull("DEBUG")?.let {
        if (it == "true") {
            logger.info(blocks.toString())
            return
        }
    }

    val slack = Slack.getInstance()
    val token = getEnvVariable("SLACK_TOKEN")
    val channelId = getEnvVariable("SLACK_CHANNEL_ID")
    slack.methods(token).chatPostMessage { req ->
        req.channel(channelId)
            .text("코드 리뷰 알림")
            .blocks(blocks)
    }
}

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

    fun createBlock(): List<LayoutBlock> {
        return listOf(
            HeaderBlock.builder()
                .text(PlainTextObject.builder().text(title).build()).build(),
            SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(webUrl).build())
                .fields(
                    listOfNotNull(
                        assignee?.name?.let {
                            MarkdownTextObject.builder().text("담당자: $it").build()
                        },
                        reviewers.let {
                            if (reviewers.isNotEmpty()) {
                                MarkdownTextObject.builder().text("리뷰어: ${joinReviewerNames()}").build()
                            } else {
                                null
                            }
                        },
                        MarkdownTextObject.builder().text("생성일: ${createdAt.format(format)} ${getEmoji()}").build(),
                    )
                )
                .build()
        )
    }

    private fun joinReviewerNames(): String {
        return reviewers.joinToString { it.name }
    }

    private fun getEmoji(): String {
        if (now == createdAt) {
            return ":new:"
        }

        // count between now and createdAt
        val diff = (now.toEpochDay() - createdAt.toEpochDay())

        return when {
            diff > 3 -> ":fire:"
            diff > 1 -> ":large_orange_diamond:\t"
            else -> ""
        }
    }
}


data class Reviewer(
    var name: String,
)
