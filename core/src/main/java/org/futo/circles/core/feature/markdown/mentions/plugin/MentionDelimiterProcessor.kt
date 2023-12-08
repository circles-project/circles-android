package org.futo.circles.core.feature.markdown.mentions.plugin

import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

internal class MentionDelimiterProcessor : DelimiterProcessor {

    override fun getOpeningCharacter(): Char {
        return '@'
    }

    override fun getClosingCharacter(): Char {
        return '@'
    }

    override fun getMinLength(): Int {
        return 1
    }

    override fun getDelimiterUse(opener: DelimiterRun, closer: DelimiterRun): Int {
        return if (opener.length() >= minLength && closer.length() >= minLength) minLength
        else 0
    }

    override fun process(opener: Text, closer: Text, delimiterUse: Int) {
        val node: Node = MentionNode()
        var tmp = opener.next
        var next: Node
        while (tmp != null && tmp !== closer) {
            next = tmp.next
            node.appendChild(tmp)
            tmp = next
        }
        opener.insertAfter(node)
    }
}
