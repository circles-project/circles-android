package org.futo.circles.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doAfterTextChanged
import io.element.android.wysiwyg.view.models.InlineFormat
import org.futo.circles.R
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.visible
import org.futo.circles.databinding.ViewSendMessageBinding
import org.futo.circles.feature.direct.timeline.listeners.SendDmMessageListener


class SendMessageView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding = ViewSendMessageBinding.inflate(LayoutInflater.from(context), this)

    private var sendDmMessageListener: SendDmMessageListener? = null
    private val emptyText = ""

    init {
        isClickable = true
        setBackgroundColor(
            ContextCompat.getColor(
                context,
                org.futo.circles.core.R.color.grey_cool_200
            )
        )
        with(binding) {
            etMessage.doAfterTextChanged { text: Editable? ->
                ivAddImage.setIsVisible(text.isNullOrBlank())
                ivSend.setIsVisible(text?.isNotBlank() == true)
            }
            ivCancelEdit.setOnClickListener {
                etMessage.setText(emptyText)
                lEditActions.gone()
                lSendActions.visible()
            }
        }
    }

    fun setup(listener: SendDmMessageListener) {
        sendDmMessageListener = listener
        with(binding) {
            ivEmoji.setOnClickListener { sendDmMessageListener?.onAddEmojiToMessageClicked() }
            ivAddImage.setOnClickListener { sendDmMessageListener?.onSendMediaButtonClicked() }
            ivSend.setOnClickListener {
                sendDmMessageListener?.onSendTextMessageClicked(etMessage.getMarkdown().trimEnd())
                etMessage.setText(emptyText)
            }
        }
    }

    fun insertEmojiIntoMessage(unicode: String) {
        val selection = binding.etMessage.selectionStart
        binding.etMessage.append(unicode)
        binding.etMessage.setSelection(selection + unicode.length)
    }

    fun setTextForEdit(
        message: String,
        onEditConfirmed: (String) -> Unit
    ) {
        with(binding) {
            lSendActions.gone()
            lEditActions.visible()
            etMessage.setText(message)
            showSoftKeyboard(etMessage)
            ivConfirmEdit.setOnClickListener {
                onEditConfirmed(etMessage.getMarkdown().trimEnd())
                etMessage.setText(emptyText)
                lEditActions.gone()
                lSendActions.visible()
            }
        }
    }

    // Delay is required for keyboard show
    private fun showSoftKeyboard(view: View) {
        view.requestFocus()
        view.postDelayed({
            val imm = getSystemService(context, InputMethodManager::class.java)
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 100L)
    }

    fun setReplyText(message: String) {
        with(binding.etMessage) {
            toggleInlineFormat(InlineFormat.Italic)
            append(context.getString(R.string.in_reply_to_format, message))
            toggleInlineFormat(InlineFormat.Italic)
            append("\n\n")
            showSoftKeyboard(this)
        }
    }

}