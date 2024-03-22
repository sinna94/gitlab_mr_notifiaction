package me.chung.notification.messagesender.slack

import com.slack.api.model.block.HeaderBlock
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.model.block.composition.PlainTextObject
import me.chung.gitlab.MergeRequest
import me.chung.gitlab.Reviewer
import me.chung.notification.messagesender.EmojiGenerator
import java.time.LocalDate

class LayoutBlockBuilder(
    private val emojiGenerator: EmojiGenerator
) {

    fun build(mergeRequestList: List<MergeRequest>): List<LayoutBlock> {
        return mergeRequestList.flatMap { mergeRequest ->
            listOf(
                buildHeaderBlock(mergeRequest.title), buildContentsBlock(mergeRequest)
            )
        }
    }

    private fun buildHeaderBlock(title: String): HeaderBlock =
        HeaderBlock.builder().text(PlainTextObject.builder().text(title).build()).build()

    private fun buildContentsBlock(
        mergeRequest: MergeRequest
    ): SectionBlock {
        val (_, createdAt, webUrl, assignee) = mergeRequest
        val reviewerNames = mergeRequest.getJoinedReviewerNames()

        return SectionBlock.builder().text(buildMarkdownTextObject(webUrl))
            .fields(
                listOfNotNull(
                    buildAssigneeField(assignee),
                    buildReviewersField(reviewerNames),
                    buildCreatedAtField(createdAt)
                )
            ).build()
    }

    private fun buildAssigneeField(assignee: Reviewer?): MarkdownTextObject? {
        return assignee?.name?.let {
            buildMarkdownTextObject("담당자: $it")
        }
    }

    private fun buildReviewersField(reviewerNames: String?): MarkdownTextObject? {
        return reviewerNames?.let {
            buildMarkdownTextObject("리뷰어: $it")
        }
    }

    private fun buildCreatedAtField(createdAt: LocalDate): MarkdownTextObject {
        return buildMarkdownTextObject(
            "생성일: ${createdAt.format(MergeRequest.format)} ${
                emojiGenerator.generateByDateDiff(
                    LocalDate.now(),
                    createdAt,
                )
            }"
        )
    }

    private fun buildMarkdownTextObject(text: String): MarkdownTextObject =
        MarkdownTextObject.builder().text(text).build()
}