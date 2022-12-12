package org.futo.circles.feature.timeline.post.markdown.mentions.plugin

import org.commonmark.node.CustomNode
import org.commonmark.node.Visitor


class MentionNode : CustomNode() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}