package dev.castledot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// The only source of colors, spacing and sizes.
object Space {
    val s1 = 4.dp
    val s2 = 8.dp
    val s3 = 16.dp
    val s4 = 24.dp
    val s5 = 48.dp
}

object FontSize {
    val s = 16.sp
    val m = 20.sp
    val l = 28.sp
    val xl = 40.sp
}

val ContentWidth = 720.dp
val BorderWidth = 2.dp

class Palette(
    val bg: Color,
    val fg: Color,
    val muted: Color,
    val accent: Color,
    val border: Color,
    val codeBg: Color,
)

val Light = Palette(
    bg = Color(0xFFF4F1EA),
    fg = Color(0xFF1B1B1B),
    muted = Color(0xFF6B6B6B),
    accent = Color(0xFFC2410C),
    border = Color(0xFF1B1B1B),
    codeBg = Color(0xFFE9E4D8),
)

val Dark = Palette(
    bg = Color(0xFF121212),
    fg = Color(0xFFE8E6E3),
    muted = Color(0xFF9A9A9A),
    accent = Color(0xFFFF8A5C),
    border = Color(0xFFE8E6E3),
    codeBg = Color(0xFF1F1F1F),
)

val LocalPalette = staticCompositionLocalOf { Light }
val LocalFont = staticCompositionLocalOf<FontFamily> { FontFamily.Default }

fun textStyle(
    size: TextUnit,
    color: Color,
    font: FontFamily,
    weight: FontWeight? = null,
) = TextStyle(
    color = color,
    fontSize = size,
    fontFamily = font,
    fontWeight = weight,
    lineHeight = size * 1.5f,
)

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = FontSize.m,
    color: Color = LocalPalette.current.fg,
    weight: FontWeight? = null,
) = BasicText(text, modifier, textStyle(size, color, LocalFont.current, weight))

@Composable
fun Rule(modifier: Modifier = Modifier) = Box(
    modifier.fillMaxWidth().height(BorderWidth)
        .background(LocalPalette.current.border),
)
