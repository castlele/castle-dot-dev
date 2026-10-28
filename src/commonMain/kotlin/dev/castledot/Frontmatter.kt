package dev.castledot

/**
 * A Markdown file split into its frontmatter and body. A field is a String,
 * a List<String> or a List<Map<String, String>>.
 */
class Document(val fields: Map<String, Any>, val body: String)

// Reads the small YAML subset the content files use: `key: value`,
// `key: [a, b]` and block lists of strings or of `key: value` records.
fun parseDocument(raw: String): Document {
    val lines = raw.replace("\r", "").lines()
    val end = lines.drop(1).indexOfFirst { it.trim() == "---" } + 1
    if (lines.firstOrNull()?.trim() != "---" || end == 0) {
        return Document(emptyMap(), raw)
    }
    val fields = mutableMapOf<String, Any>()
    var i = 1
    while (i < end) {
        val line = lines[i++]
        if (line.isBlank() || line.startsWith("#") || line.startsWith(" ")) {
            continue
        }
        val key = line.substringBefore(":").trim()
        val value = line.substringAfter(":").trim()
        if (value.isNotEmpty()) {
            fields[key] = inline(value)
            continue
        }
        val block = lines.subList(i, end).takeWhile {
            it.isBlank() || it.startsWith(" ") || it.startsWith("- ")
        }
        i += block.size
        fields[key] = blockList(block.filter { it.isNotBlank() })
    }
    return Document(fields, lines.drop(end + 1).joinToString("\n").trimStart())
}

private fun unquote(s: String) =
    if (s.length >= 2 && s.first() in "\"'" && s.last() == s.first()) {
        s.substring(1, s.length - 1)
    } else {
        s
    }

private fun inline(value: String): Any =
    if (value.startsWith("[") && value.endsWith("]")) {
        value.substring(1, value.length - 1).split(",")
            .map { it.trim() }.filter { it.isNotEmpty() }.map(::unquote)
    } else {
        unquote(value)
    }

private fun blockList(lines: List<String>): List<Any> {
    val items = mutableListOf<Any>()
    var record: MutableMap<String, String>? = null
    for (line in lines) {
        val text = line.trim()
        if (text.startsWith("- ")) {
            val item = text.removePrefix("- ").trim()
            record = null
            if (": " in item) {
                record = mutableMapOf()
                items += record
                record[item.substringBefore(":").trim()] =
                    unquote(item.substringAfter(":").trim())
            } else {
                items += unquote(item)
            }
        } else {
            record?.set(
                text.substringBefore(":").trim(),
                unquote(text.substringAfter(":").trim()),
            )
        }
    }
    return items
}
