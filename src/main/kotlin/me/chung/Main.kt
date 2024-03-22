package me.chung

import me.chung.gitlab.GitLabMergeRequestFetcher
import me.chung.notification.NotificationService
import me.chung.notification.messagesender.EmojiGenerator
import me.chung.notification.messagesender.MessageSenderFactory
import me.chung.notification.messagesender.MessageSenderVendor
import me.chung.notification.messagesender.slack.LayoutBlockBuilder
import me.chung.notification.messagesender.slack.SlackMessageSender
import me.chung.utils.SystemEnvironmentVariableProvider

fun main() {
    val messageSenderVendor = SystemEnvironmentVariableProvider.getEnvVariable("MESSAGE_SENDER_VENDOR")
    val messageSender = MessageSenderFactory.createMessageSender(MessageSenderVendor.fromString(messageSenderVendor))

    NotificationService(
        GitLabMergeRequestFetcher(),
        messageSender
    ).sendNotification()
}
