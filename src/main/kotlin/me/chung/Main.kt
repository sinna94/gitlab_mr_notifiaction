package me.chung

import me.chung.gitlab.GitLabMergeRequestFetcher
import me.chung.notification.NotificationService
import me.chung.notification.messagesender.EmojiGenerator
import me.chung.notification.messagesender.slack.LayoutBlockBuilder
import me.chung.notification.messagesender.slack.SlackMessageSender

fun main() {
    val notificationService = NotificationService(
        GitLabMergeRequestFetcher(),
        SlackMessageSender(LayoutBlockBuilder(EmojiGenerator()))
    )
    notificationService.sendNotification()
}
