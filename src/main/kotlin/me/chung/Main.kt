package me.chung

import me.chung.gitlab.GitLabMergeRequestFetcher
import me.chung.notification.NotificationService
import me.chung.notification.messagesender.MessageSenderFactory
import me.chung.notification.messagesender.MessageSenderVendor
import me.chung.utils.SystemEnvironmentVariableProvider

fun main() {
    val messageSenderVendor = SystemEnvironmentVariableProvider.getEnvVariableOrNull("MESSAGE_SENDER_VENDOR") ?: "SLACK"
    val messageSender = MessageSenderFactory.createMessageSender(MessageSenderVendor.fromString(messageSenderVendor))

    NotificationService(
        GitLabMergeRequestFetcher(),
        messageSender
    ).sendNotification()
}
