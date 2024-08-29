package com.viktorger.zulip_client.app.core.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.viktorger.zulip_client.app.R

class MessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val defaultImageIndent = context.resources.getDimension(R.dimen.default_message_image_indent)
    private val defaultMessageBodyIndent = context.resources.getDimension(R.dimen.default_message_body_indent)
    private val defaultPaddingHorizontal = context.resources.getDimension(R.dimen.default_message_padding_horizontal)
    private val defaultPaddingVertical = context.resources.getDimension(R.dimen.default_message_padding_vertical)

    private var messagePaddingVertical: Int = defaultPaddingVertical.toInt()
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    private var messagePaddingHorizontal: Int = defaultPaddingHorizontal.toInt()
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    private var imageIndent: Int = defaultImageIndent.toInt()
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }
    private var messageBodyIndent: Int = defaultMessageBodyIndent.toInt()
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    private val messageBg: View
        get() = getChildAt(0)

    val avatar: ImageView
        get() = getChildAt(1) as ImageView

    private val _username: TextView
        get() = getChildAt(2) as TextView

    val message: TextView
        get() = getChildAt(3) as TextView

    val reactions: FlexBoxLayout
        get() = getChildAt(4) as FlexBoxLayout

    var username: String
        get() = _username.text.toString()
        set(value) {
            _username.text = value
        }

    init {
        inflate(context, R.layout.layout_message, this)

        context.withStyledAttributes(attrs, R.styleable.MessageLayout) {
            avatar.setImageResource(
                getResourceId(
                    R.styleable.MessageLayout_avatar,
                    R.drawable.avatar
                )
            )
            _username.text = getString(R.styleable.MessageLayout_username)
                ?: ""
            message.text = getString(R.styleable.MessageLayout_message)
                ?: ""
            imageIndent = getDimension(R.styleable.MessageLayout_image_indent, defaultImageIndent).toInt()
            messageBodyIndent = getDimension(
                R.styleable.MessageLayout_message_body_indent, defaultMessageBodyIndent
            ).toInt()
            messagePaddingVertical = getDimension(
                R.styleable.MessageLayout_message_padding_vertical, defaultPaddingVertical
            ).toInt()
            messagePaddingHorizontal = getDimension(
                R.styleable.MessageLayout_message_padding_horizontal, defaultPaddingHorizontal
            ).toInt()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(
            avatar,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )
        val rightToAvatar = avatar.measuredWidth + imageIndent
        measureChildWithMargins(
            _username,
            widthMeasureSpec,
            rightToAvatar + messagePaddingHorizontal * 2,
            heightMeasureSpec,
            0
        )
        measureChildWithMargins(
            message,
            widthMeasureSpec,
            rightToAvatar + messagePaddingHorizontal * 2,
            heightMeasureSpec,
            0
        )
        val heightWithoutReactions = getHeightWithoutReactions()
        measureChildWithMargins(
            reactions,
            widthMeasureSpec,
            rightToAvatar,
            heightMeasureSpec,
            heightWithoutReactions
        )

        messageBg.layoutParams.width = maxOf(message.measuredWidth, _username.measuredWidth) +
                messagePaddingHorizontal * 2
        messageBg.layoutParams.height = _username.measuredHeight + message.measuredHeight +
                messagePaddingVertical * 2

        measureChildWithMargins(
            messageBg,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        val expectedWidth = avatar.measuredWidth + maxOf(
            messageBg.measuredWidth, reactions.measuredWidth
        ) + imageIndent
        val actualWidth = resolveSize(expectedWidth, widthMeasureSpec)

        val expectedHeight = heightWithoutReactions + reactions.measuredHeight.let {
            if (it > 0)
                it + messageBodyIndent
            else
                it
        }

        val actualHeight = resolveSize(expectedHeight, heightMeasureSpec)

        setMeasuredDimension(actualWidth, actualHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatar.layout(
            0,
            paddingTop,
            avatar.measuredWidth,
            avatar.measuredHeight
        )
        val rightToAvatar = avatar.measuredWidth + imageIndent
        messageBg.layout(
            rightToAvatar,
            0,
            rightToAvatar + messageBg.measuredWidth,
            messageBg.measuredHeight
        )
        val messageBodyStart = rightToAvatar + messagePaddingHorizontal
        _username.layout(
            messageBodyStart,
            messagePaddingVertical,
            _username.measuredWidth + messageBodyStart,
            _username.measuredHeight + messagePaddingVertical
        )

        message.layout(
            messageBodyStart,
            _username.measuredHeight + messagePaddingVertical,
            message.measuredWidth + messageBodyStart,
            _username.measuredHeight + messagePaddingVertical + message.measuredHeight
        )
        val heightWithoutReactions = getHeightWithoutReactions() + messageBodyIndent
        reactions.layout(
            rightToAvatar,
            heightWithoutReactions,
            rightToAvatar + reactions.measuredWidth,
            heightWithoutReactions + reactions.measuredHeight
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    private fun getHeightWithoutReactions() = maxOf(
        avatar.measuredHeight, _username.measuredHeight + message.measuredHeight +
                messagePaddingVertical * 2
    )
}