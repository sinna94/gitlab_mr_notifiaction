package me.chung.notification.messagesender

import me.chung.notification.messagesender.discord.DiscordMessageBuilder
import me.chung.notification.messagesender.discord.DiscordMessageSender
import me.chung.notification.messagesender.slack.LayoutBlockBuilder
import me.chung.notification.messagesender.slack.SlackMessageSender

object MessageSenderFactory {
    fun createMessageSender(vendor: MessageSenderVendor): MessageSender {
        val emojiGenerator = EmojiGenerator()

        return when (vendor) {
            MessageSenderVendor.SLACK -> SlackMessageSender(LayoutBlockBuilder(emojiGenerator))
            MessageSenderVendor.DISCORD -> DiscordMessageSender(DiscordMessageBuilder(emojiGenerator))
        }
    }
}