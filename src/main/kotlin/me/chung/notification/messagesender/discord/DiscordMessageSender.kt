package me.chung.notification.messagesender.discord

import me.chung.gitlab.MergeRequest
import me.chung.notification.messagesender.MessageBuilder
import me.chung.notification.messagesender.MessageSender
import me.chung.utils.ObjectMapperBuilder
import me.chung.utils.SystemEnvironmentVariableProvider
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DiscordMessageSender(
    private val discordMessageBuilder: MessageBuilder<DiscordMessageList>
) : MessageSender {

    companion object {
        private val logger = LoggerFactory.getLogger(DiscordMessageSender::class.java)
        private const val BASE_URL = "https://discord.com/api"
    }

    override fun send(mergeRequestList: List<MergeRequest>) {
        val discordMessageList = discordMessageBuilder.build(mergeRequestList)
        sendMessage(discordMessageList)
    }

    private fun sendMessage(discordMessageList: DiscordMessageList) {
        val discordWebhookId = SystemEnvironmentVariableProvider.getEnvVariable("DISCORD_WEBHOOK_ID")
        val discordWebhookToken = SystemEnvironmentVariableProvider.getEnvVariable("DISCORD_WEBHOOK_TOKEN")

        discordMessageList.messageList.forEach { message ->
            requestSendMessage(message, discordWebhookId, discordWebhookToken)
        }
    }

    private fun requestSendMessage(message: String, webhookId: String, webhookToken: String) {
        SystemEnvironmentVariableProvider.getEnvVariableOrNull("DEBUG")?.let {
            println(it)
            if (it == "true") {
                logger.info(message)
                return
            }
        }

        val httpClient = HttpClient.newHttpClient()
        val objectMapper = ObjectMapperBuilder.build()
        val jsonMessage = objectMapper.writeValueAsString(mapOf("content" to message))
        val httpRequest = HttpRequest.newBuilder(URI("$BASE_URL/webhooks/$webhookId/$webhookToken"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonMessage))
            .build()

        val httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        logger.info(httpResponse.body())
    }
}