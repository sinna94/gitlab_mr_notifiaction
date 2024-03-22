package me.chung.notification.messagesender

import me.chung.gitlab.MergeRequest

fun interface MessageSender {
    fun send(mergeRequestList: List<MergeRequest>)
}