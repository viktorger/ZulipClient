package com.viktorger.zulip_client.app.core.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.common.sp

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.emojiViewStyle,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var emoji = "\uD83D\uDE35\u200D\uD83D\uDCAB"
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    var count = 0
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    private val textToDraw
        get() = "$emoji $count"

    private val textPaint = TextPaint()

    private val textRect = Rect()

    init {
        context.withStyledAttributes(attrs, R.styleable.EmojiView, defStyleAttr) {
            count = getInt(R.styleable.EmojiView_count, 0)
            emoji = getString(R.styleable.EmojiView_emoji) ?: "\uD83D\uDE35\u200D\uD83D\uDCAB"
            textPaint.textSize = getDimension(R.styleable.EmojiView_fontSize, 16f.sp(context))
            textPaint.color = getColor(R.styleable.EmojiView_textColor, Color.WHITE)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, textRect)
        val textWidth = textRect.left + textRect.right

        val actualWidth = resolveSize(textWidth + paddingLeft + paddingRight, widthMeasureSpec)
        val actualHeight = resolveSize(textRect.height() + paddingTop + paddingBottom, heightMeasureSpec)

        setMeasuredDimension(actualWidth, actualHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val textY = (height + paddingTop - paddingBottom) / 2 - textRect.exactCenterY()
        val textX = (width + paddingStart - paddingEnd) / 2 - textRect.exactCenterX()
        canvas.drawText(textToDraw, textX, textY, textPaint)
    }


}