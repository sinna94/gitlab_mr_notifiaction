package me.chung.notification.messagesender.discord

import me.chung.gitlab.MergeRequest
import me.chung.notification.messagesender.MessageBuilder
import me.chung.notification.messagesender.MessageSender
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
        private val logger = LoggerFactory.getLogger(Companion::class.java)
        private const val baseUrl = "https://discord.com/api"
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
            if (it == "true") {
                logger.info(message.toString())
                return
            }
        }

        val httpClient = HttpClient.newHttpClient()
        val httpRequest = HttpRequest.newBuilder(URI("$baseUrl/$webhookId/$webhookToken"))
            .POST(HttpRequest.BodyPublishers.ofString("{\"content\":\"$message\"}"))
            .build()

        val httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        logger.info(httpResponse.body())
    }
}