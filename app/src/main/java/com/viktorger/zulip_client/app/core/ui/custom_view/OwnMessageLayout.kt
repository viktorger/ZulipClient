package com.viktorger.zulip_client.app.core.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.viktorger.zulip_client.app.R

class OwnMessageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val defaultMessageBodyIndent = context.resources.getDimension(R.dimen.default_message_body_indent)

    val message: TextView
        get() = getChildAt(0) as TextView

    val reactions: FlexBoxLayout
        get() = getChildAt(1) as FlexBoxLayout

    private var messageBodyIndent: Int = defaultMessageBodyIndent.toInt()
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.OwnMessageLayout) {
            messageBodyIndent = getDimension(
                R.styleable.OwnMessageLayout_own_message_body_indent,
                defaultMessageBodyIndent
            ).toInt()
        }
        inflate(context, R.layout.layout_own_message, this)
        orientation = VERTICAL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (reactions.childCount == 0) {
            (reactions.layoutParams as MarginLayoutParams).topMargin = 0
        } else {
            (reactions.layoutParams as MarginLayoutParams).topMargin = messageBodyIndent
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

}