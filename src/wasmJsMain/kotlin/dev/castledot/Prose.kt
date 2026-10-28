package dev.castledot

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.ImageData
import com.mikepenz.markdown.model.ImageTransformer
import com.mikepenz.markdown.model.markdownPadding

private object AssetImages : ImageTransformer {
    @Composable
    override fun transform(link: String): ImageData? =
        rememberAsset(link)?.let { ImageData(it) }
}

// Article text. Own Markdown only: raw HTML is never rendered.
@Composable
fun Prose(text: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    val p = LocalPalette.current
    val font = LocalFont.current
    fun style(size: TextUnit, weight: FontWeight? = null) =
        textStyle(size, p.fg, font, weight)
    val body = style(FontSize.m)
    val colors = DefaultMarkdownColors(
        text = p.fg,
        codeBackground = p.codeBg,
        inlineCodeBackground = p.codeBg,
        dividerColor = p.border,
        tableBackground = p.codeBg,
        highlightText = p.fg,
        highlightBackground = p.codeBg,
    )
    val typography = DefaultMarkdownTypography(
        h1 = style(FontSize.xl, FontWeight.Bold),
        h2 = style(FontSize.l, FontWeight.Bold),
        h3 = style(FontSize.m, FontWeight.Bold),
        h4 = style(FontSize.m, FontWeight.Bold),
        h5 = style(FontSize.m, FontWeight.Bold),
        h6 = style(FontSize.m, FontWeight.Bold),
        text = body,
        code = style(FontSize.s),
        inlineCode = style(FontSize.s).copy(background = p.codeBg),
        quote = body.copy(color = p.muted),
        paragraph = body,
        ordered = body,
        bullet = body,
        list = body,
        textLink = TextLinkStyles(
            SpanStyle(color = p.accent, textDecoration = TextDecoration.Underline),
        ),
        table = style(FontSize.s),
    )
    Markdown(
        content = text,
        colors = colors,
        typography = typography,
        modifier = modifier,
        padding = markdownPadding(block = Space.s2),
        imageTransformer = AssetImages,
        components = markdownComponents(
            codeFence = {
                MarkdownHighlightedCodeFence(
                    it.content, it.node, it.typography.code, showHeader = true,
                )
            },
            codeBlock = {
                MarkdownHighlightedCodeBlock(
                    it.content, it.node, it.typography.code, showHeader = true,
                )
            },
        ),
    )
}
