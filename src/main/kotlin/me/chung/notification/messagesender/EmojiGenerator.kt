package me.chung.notification.messagesender

import java.time.LocalDate

class EmojiGenerator() {

    fun generateByDateDiff(now: LocalDate, date: LocalDate): String {
        if (now == date) {
            return ":new:"
        }

        // count between now and date
        val diff = (now.toEpochDay() - date.toEpochDay())

        return when {
            diff > 3 -> ":fire:"
            diff > 1 -> ":large_orange_diamond:\t"
            else -> ""
        }
    }
}