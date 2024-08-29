package com.viktorger.zulip_client.app.core.ui.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import com.viktorger.zulip_client.app.R
import com.viktorger.zulip_client.app.core.common.dp

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.flexBoxLayoutStyle,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val defaultSpacing = 4f.dp(context)

    var spacing: Int = defaultSpacing.toInt()
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.FlexBoxLayout, defStyleAttr) {
            spacing = getDimension(R.styleable.FlexBoxLayout_spacing, defaultSpacing).toInt()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)

        var currentRowWidth = 0
        var maxCalculatedWidth = 0
        var currentHeight = 0
        var maxHeightInRow = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lastValidRowWidth = currentRowWidth
            if (currentRowWidth > 0) {
                currentRowWidth += spacing
            }
            currentRowWidth += child.measuredWidth

            if (child.measuredHeight > maxHeightInRow) {
                maxHeightInRow = child.measuredHeight
            }

            if (currentRowWidth > maxWidth) {
                currentHeight += maxHeightInRow + spacing
                maxHeightInRow = child.measuredHeight

                if (lastValidRowWidth > maxCalculatedWidth) {
                    maxCalculatedWidth = lastValidRowWidth
                }
                currentRowWidth = child.measuredWidth
            }
        }

        // handle last child
        currentHeight += maxHeightInRow
        if (currentRowWidth > maxCalculatedWidth) {
            maxCalculatedWidth = currentRowWidth
        }

        val actualWidth = resolveSize(maxCalculatedWidth, widthMeasureSpec)
        val actualHeight = resolveSize(currentHeight, heightMeasureSpec)

        setMeasuredDimension(actualWidth, actualHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentRowWidth = 0
        var currentHeight = 0
        var maxHeightInRow = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentRowWidth > 0) {
                currentRowWidth += spacing
            }
            currentRowWidth += child.measuredWidth

            if (child.measuredHeight > maxHeightInRow) {
                maxHeightInRow = child.measuredHeight
            }

            if (currentRowWidth > measuredWidth) {
                currentHeight += maxHeightInRow + spacing
                maxHeightInRow = child.measuredHeight
                currentRowWidth = child.measuredWidth
            }
            child.layout(
                currentRowWidth - child.measuredWidth,
                currentHeight,
                currentRowWidth,
                currentHeight + child.measuredHeight
            )
        }
    }
}
