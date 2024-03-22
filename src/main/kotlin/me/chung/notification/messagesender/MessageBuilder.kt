package me.chung.notification.messagesender

import me.chung.gitlab.MergeRequest

fun interface MessageBuilder<R> {
    fun build(mergeRequestList: List<MergeRequest>): R
}