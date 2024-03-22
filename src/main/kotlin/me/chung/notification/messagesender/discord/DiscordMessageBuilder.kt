package me.chung.notification.messagesender.discord

import me.chung.gitlab.MergeRequest
import me.chung.notification.messagesender.EmojiGenerator
import me.chung.notification.messagesender.MessageBuilder
import java.time.LocalDate

class DiscordMessageBuilder(
    private val emojiGenerator: EmojiGenerator,
) : MessageBuilder<DiscordMessageList> {


    override fun build(mergeRequestList: List<MergeRequest>): DiscordMessageList {
        val now = LocalDate.now()

        val messageList = mutableListOf<String>()
        var wholeMessage = ""

        mergeRequestList.forEach { mergeRequest ->
            val message = buildMessage(mergeRequest, now)

            if (wholeMessage.length + message.length > 2000) {
                messageList.add(wholeMessage)
                wholeMessage = ""
            }

            wholeMessage += message
        }

        messageList.add(wholeMessage)
        return DiscordMessageList(messageList)
    }

    private fun buildMessage(mergeRequest: MergeRequest, now: LocalDate): String {
        val (title, createdAt, webUrl, assignee) = mergeRequest

        return """
            \n## $title
            $webUrl
            담당자: ${assignee?.name ?: ""}\t 리뷰어: ${mergeRequest.getJoinedReviewerNames()}
            생성일: $createdAt ${emojiGenerator.generateByDateDiff(now, createdAt)}
        """.trimIndent()
    }
}