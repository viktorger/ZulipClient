package com.viktorger.zulip_client.app.core.common

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import com.viktorger.zulip_client.app.R

internal fun Float.dp(context: Context): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
)

internal fun Float.sp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics
)

internal fun <T> lazyUnsafe(block: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, block)

internal fun showErrorSnackBar(view: View, action: () -> Unit) {
    Snackbar.make(view, "Load fail", Snackbar.LENGTH_LONG)
        .setAction(R.string.retry) {
            action()
        }
        .show()
}

internal fun getAssociatedColor(text: String, factor: Float = 0.6f): Int {
    val hash = text.hashCode()
    val red = ((hash and 0xFF0000 shr 16) * factor).toInt()
    val green = ((hash and 0x00FF00 shr 8) * factor).toInt()
    val blue = ((hash and 0x0000FF) * factor).toInt()
    return Color.rgb(red, green, blue)
}

private fun String.withoutExcessiveParagraphs() = substring(0, length - 1)

internal fun textFromHtml(htmlString: String) =
    HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        .withoutExcessiveParagraphs()

fun getEmojiByUnicode(unicode: String): String {
    var outputString = ""
    val unicodeList = unicode.split("-")
    unicodeList.forEach {
        outputString += String(Character.toChars(it.hexToInt()))
    }

    return outputString
}

private fun String.hexToInt(): Int = this.toInt(16)

fun toMillis(timestamp: Long): Long = timestamp * 1000

fun toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}
