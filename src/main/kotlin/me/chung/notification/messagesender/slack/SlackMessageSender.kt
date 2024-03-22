package me.chung.notification.messagesender.slack

import com.slack.api.Slack
import com.slack.api.model.block.LayoutBlock
import me.chung.gitlab.MergeRequest
import me.chung.notification.messagesender.MessageSender
import me.chung.utils.SystemEnvironmentVariableProvider
import org.slf4j.LoggerFactory

class SlackMessageSender(
    private val layoutBlockBuilder: LayoutBlockBuilder
) : MessageSender {

    companion object {
        private val logger = LoggerFactory.getLogger(Companion::class.java)
    }

    override fun send(mergeRequestList: List<MergeRequest>) {
        val layoutBlocks = layoutBlockBuilder.build(mergeRequestList)
        sendMessage(layoutBlocks)
    }

    private fun sendMessage(blocks: List<LayoutBlock>) {
        SystemEnvironmentVariableProvider.getEnvVariableOrNull("DEBUG")?.let {
            if (it == "true") {
                logger.info(blocks.toString())
                return
            }
        }

        val token = SystemEnvironmentVariableProvider.getEnvVariable("SLACK_TOKEN")
        val channelId = SystemEnvironmentVariableProvider.getEnvVariable("SLACK_CHANNEL_ID")
        Slack
            .getInstance()
            .methods(token).chatPostMessage { req ->
                req.channel(channelId)
                    .text("코드 리뷰 알림")
                    .blocks(blocks)
            }
    }
}
