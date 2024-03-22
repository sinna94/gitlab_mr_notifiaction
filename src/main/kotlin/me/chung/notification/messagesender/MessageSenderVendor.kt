package me.chung.notification.messagesender

import java.util.*

enum class MessageSenderVendor {
    SLACK,
    DISCORD;

    companion object {
        fun fromString(value: String): MessageSenderVendor {
            return valueOf(value.uppercase(Locale.getDefault()))
        }
    }
}