package me.chung.notification

import me.chung.gitlab.GitLabMergeRequestFetcher
import me.chung.notification.messagesender.MessageSender
import org.slf4j.LoggerFactory

class NotificationService(
    private val mergeRequestFetcher: GitLabMergeRequestFetcher,
    private val messageSender: MessageSender,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(Companion::class.java)
    }

    fun sendNotification() {
        val mergeRequests = mergeRequestFetcher.fetchMergeRequests()

        if (mergeRequests.isEmpty()) {
            logger.info("no merge requests")
            return
        }

        messageSender.send(mergeRequests)
    }
}