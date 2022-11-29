package org.futo.circles.view.markdown.style_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.databinding.ViewMarkdownStylebarBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.feature.timeline.post.create.PostConfigurationOptionListener
import org.futo.circles.model.MainStyleBarOption
import org.futo.circles.model.StyleBarListItem
import org.futo.circles.view.markdown.TextStyle

class MarkdownStylesBar(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewMarkdownStylebarBinding.inflate(LayoutInflater.from(context), this)

    private val mainOptions = listOf(
        StyleBarListItem(MainStyleBarOption.Media.ordinal, R.drawable.ic_image),
        StyleBarListItem(MainStyleBarOption.Emoji.ordinal, R.drawable.ic_emoji),
        StyleBarListItem(MainStyleBarOption.Mention.ordinal, R.drawable.ic_mention),
        StyleBarListItem(MainStyleBarOption.Link.ordinal, R.drawable.ic_link),
        StyleBarListItem(MainStyleBarOption.TextStyle.ordinal, R.drawable.ic_text)
    )

    private val textStyleOptions = listOf(
        StyleBarListItem(TextStyle.BOLD.ordinal, R.drawable.ic_bold),
        StyleBarListItem(TextStyle.ITALIC.ordinal, R.drawable.ic_italic),
        StyleBarListItem(TextStyle.STRIKE.ordinal, R.drawable.ic_strikethrough),
        StyleBarListItem(TextStyle.UNORDERED_LIST.ordinal, R.drawable.ic_bullet_list),
        StyleBarListItem(TextStyle.ORDERED_LIST.ordinal, R.drawable.ic_number_list),
        StyleBarListItem(TextStyle.TASKS_LIST.ordinal, R.drawable.ic_check_box)
    )

    private val mainOptionsAdapter = OptionsStyleBarAdapter(::onMainOptionSelected)
    private val textStyleOptionsAdapter =
        OptionsStyleBarAdapter(::onTextStyleSelected).apply { submitList(textStyleOptions) }

    private var postConfigurationListener: PostConfigurationOptionListener? = null

    private var isTextOptionsOpened = false

    init {
        setupViews()
    }

    fun setOptionsListener(listener: PostConfigurationOptionListener) {
        postConfigurationListener = listener
    }

    fun showMainOptionsList(isMediaAvailable: Boolean) {
        mainOptionsAdapter.submitList(
            if (isMediaAvailable) mainOptions
            else mainOptions.filter { it.id != MainStyleBarOption.Media.ordinal }
        )
    }

    fun highlightStyle(textStyle: TextStyle?) {
        val styleOrdinal = textStyle?.ordinal ?: -1
        selectTextStyle(styleOrdinal)
    }

    private fun setupViews() {
        setTextOptionsOpened(false)
        binding.ivCancel.setOnClickListener { setTextOptionsOpened(false) }
        binding.rvMainOptions.adapter = mainOptionsAdapter
        binding.rvTextOptions.adapter = textStyleOptionsAdapter
    }

    private fun setTextOptionsOpened(isOpened: Boolean) {
        isTextOptionsOpened = isOpened
        binding.rvMainOptions.setIsVisible(!isTextOptionsOpened)
        binding.textOptionsGroup.setIsVisible(isTextOptionsOpened)
    }

    private fun onMainOptionSelected(id: Int) {
        when (MainStyleBarOption.values()[id]) {
            MainStyleBarOption.Media -> postConfigurationListener?.onUploadMediaClicked()
            MainStyleBarOption.Emoji -> postConfigurationListener?.onEmojiClicked()
            MainStyleBarOption.Mention -> postConfigurationListener?.onMentionClicked()
            MainStyleBarOption.Link -> postConfigurationListener?.onAddLinkClicked()
            MainStyleBarOption.TextStyle -> setTextOptionsOpened(true)
        }
    }

    private fun onTextStyleSelected(id: Int) {
        postConfigurationListener?.onTextStyleSelected(TextStyle.values()[id])
        selectTextStyle(id)
    }

    private fun selectTextStyle(id: Int) {
        textStyleOptionsAdapter.submitList(textStyleOptions.map {
            it.copy(isSelected = it.id == id)
        })
    }
}