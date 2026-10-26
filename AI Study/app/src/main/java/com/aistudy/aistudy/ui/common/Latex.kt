package com.aistudy.aistudy.ui.common

/**
 * Chuyen LaTeX don gian sang Unicode de doc duoc tren dien thoai ma khong can
 * them dependency nang (giu quy uoc D-1/D-5 cua project).
 *
 * Muc tieu khong phai la trinh render LaTeX day du: cac cong thuc pho thong
 * trong cau tra loi cua Gemini (phan so, can, luy thua, chi so duoi, ky hieu
 * Hy Lap, gioi han, tong, tich phan) duoc hien thi de doc. Phan so duoc ve
 * dang xep tang o `MarkdownText`; o day chi tra ve dang chu du phong.
 */
internal fun latexToUnicode(rawLatex: String): String {
    val latex = normaliseBareCommands(repairLatexControlChars(rawLatex))
    val out = StringBuilder()
    var i = 0
    while (i < latex.length) {
        val ch = latex[i]
        when {
            ch == '\\' -> {
                val (name, afterName) = readLatexCommand(latex, i)
                i = afterName
                when (name) {
                    "frac", "dfrac", "tfrac" -> {
                        val (numerator, afterNumerator) = readLatexArgument(latex, i)
                        val (denominator, afterDenominator) = readLatexArgument(latex, afterNumerator)
                        i = afterDenominator
                        out.append(
                            wrapLatexFraction(
                                latexToUnicode(numerator),
                                latexToUnicode(denominator)
                            )
                        )
                    }

                    "sqrt" -> {
                        var cursor = i
                        var degree: String? = null
                        if (cursor < latex.length && latex[cursor] == '[') {
                            val close = latex.indexOf(']', cursor)
                            if (close > cursor) {
                                degree = latex.substring(cursor + 1, close)
                                cursor = close + 1
                            }
                        }
                        val (radicand, afterRadicand) = readLatexArgument(latex, cursor)
                        i = afterRadicand
                        val symbol = when (degree?.trim()) {
                            "3" -> "\u221B"
                            "4" -> "\u221C"
                            else -> "\u221A"
                        }
                        val inner = latexToUnicode(radicand)
                        out.append(symbol)
                        if (needsLatexParentheses(inner)) out.append('(').append(inner).append(')')
                        else out.append(inner)
                    }

                    "text", "textrm", "mathrm", "mathit", "mathbf", "mathcal",
                    "mathbb", "mathsf", "mathtt", "operatorname", "mbox" -> {
                        val (content, next) = readLatexArgument(latex, i)
                        i = next
                        out.append(latexToUnicode(content))
                    }

                    "overline", "bar" -> {
                        val (content, next) = readLatexArgument(latex, i)
                        i = next
                        out.append(applyCombining(latexToUnicode(content), '\u0305'))
                    }

                    "vec" -> {
                        val (content, next) = readLatexArgument(latex, i)
                        i = next
                        out.append(applyCombining(latexToUnicode(content), '\u20D7'))
                    }

                    "hat", "widehat" -> {
                        val (content, next) = readLatexArgument(latex, i)
                        i = next
                        out.append(applyCombining(latexToUnicode(content), '\u0302'))
                    }

                    "tilde", "widetilde" -> {
                        val (content, next) = readLatexArgument(latex, i)
                        i = next
                        out.append(applyCombining(latexToUnicode(content), '\u0303'))
                    }

                    "dot" -> {
                        val (content, next) = readLatexArgument(latex, i)
                        i = next
                        out.append(applyCombining(latexToUnicode(content), '\u0307'))
                    }

                    "color" -> {
                        // \color{red}{x} -> bo mau, giu noi dung o nhom ke tiep.
                        val (_, next) = readLatexArgument(latex, i)
                        i = next
                    }

                    "left", "right", "big", "Big", "bigg", "Bigg",
                    "bigl", "bigr", "Bigl", "Bigr", "middle" -> {
                        // \left. va \right. la dau phan cach vo hinh.
                        if (i < latex.length && latex[i] == '.') i++
                    }

                    "begin", "end" -> {
                        val (_, next) = readLatexArgument(latex, i)
                        i = next
                    }

                    "\\" -> out.append('\n')
                    "," -> out.append(' ')
                    ";" -> out.append(' ')
                    ":" -> out.append(' ')
                    "!" -> Unit
                    " " -> out.append(' ')
                    "quad" -> out.append("  ")
                    "qquad" -> out.append("    ")
                    "{" -> out.append('{')
                    "}" -> out.append('}')
                    "%" -> out.append('%')
                    "$" -> out.append('$')
                    "_" -> out.append('_')
                    "&" -> out.append('&')
                    "#" -> out.append('#')
                    else -> {
                        val mapped = LATEX_COMMANDS[name]
                        when {
                            mapped != null -> out.append(mapped)
                            name.isNotEmpty() -> out.append(name)
                        }
                    }
                }
            }

            ch == '^' -> {
                val (argument, next) = readLatexArgument(latex, i + 1)
                i = next
                out.append(toSuperscript(latexToUnicode(argument)))
            }

            ch == '_' -> {
                val (argument, next) = readLatexArgument(latex, i + 1)
                i = next
                out.append(toSubscript(latexToUnicode(argument)))
            }

            ch == '{' || ch == '}' -> i++
            ch == '~' -> {
                out.append('\u00A0')
                i++
            }
            ch == '&' -> {
                out.append("  ")
                i++
            }
            ch == '\'' -> {
                out.append('\u2032')
                i++
            }
            else -> {
                out.append(ch)
                i++
            }
        }
    }
    return cleanLatex(out.toString())
}

/** Mot manh cong thuc: chu da doi sang Unicode, hoac phan so de ve xep tang. */
internal sealed interface LatexPiece {
    data class Text(val value: String) : LatexPiece
    data class Fraction(val numerator: String, val denominator: String) : LatexPiece
}

/**
 * Tach `\frac{a}{b}` ra khoi cong thuc de UI ve phan so co gach ngang.
 * Cac phan so long trong tu so/mau so van duoc doi thanh dang `a/b`.
 */
internal fun splitLatexPieces(rawLatex: String): List<LatexPiece> {
    val latex = repairLatexControlChars(rawLatex)
    val pieces = mutableListOf<LatexPiece>()
    val text = StringBuilder()

    fun flushText() {
        if (text.isNotEmpty()) {
            val raw = text.toString()
            val converted = latexToUnicode(raw)
            if (converted.isNotEmpty()) {
                val keepLeading = raw.firstOrNull()?.isWhitespace() == true
                val keepTrailing = raw.lastOrNull()?.isWhitespace() == true
                pieces += LatexPiece.Text(
                    buildString {
                        if (keepLeading) append(' ')
                        append(converted)
                        if (keepTrailing) append(' ')
                    }
                )
            }
            text.clear()
        }
    }

    var i = 0
    while (i < latex.length) {
        if (latex[i] == '\\') {
            val (name, afterName) = readLatexCommand(latex, i)
            if (name == "frac" || name == "dfrac" || name == "tfrac") {
                val (numerator, afterNumerator) = readLatexArgument(latex, afterName)
                val (denominator, afterDenominator) = readLatexArgument(latex, afterNumerator)
                flushText()
                pieces += LatexPiece.Fraction(numerator, denominator)
                i = afterDenominator
                continue
            }
        }
        text.append(latex[i])
        i++
    }
    flushText()
    return pieces
}

private fun readLatexCommand(source: String, start: Int): Pair<String, Int> {
    var i = start + 1
    if (i >= source.length) return "" to i
    if (!source[i].isLetter()) return source[i].toString() to i + 1
    val nameStart = i
    while (i < source.length && source[i].isLetter()) i++
    return source.substring(nameStart, i) to i
}

private fun readLatexArgument(source: String, start: Int): Pair<String, Int> {
    var i = start
    while (i < source.length && source[i].isWhitespace()) i++
    if (i >= source.length) return "" to i

    if (source[i] == '{') {
        var depth = 1
        var j = i + 1
        while (j < source.length) {
            when (source[j]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) {
                        return source.substring(i + 1, j) to (j + 1)
                    }
                }
            }
            j++
        }
        return source.substring(i + 1) to source.length
    }

    if (source[i] == '\\') {
        val (name, afterName) = readLatexCommand(source, i)
        return "\\$name" to afterName
    }

    return source[i].toString() to i + 1
}

private fun wrapLatexFraction(numerator: String, denominator: String): String {
    val n = if (needsLatexParentheses(numerator)) "($numerator)" else numerator
    val d = if (needsLatexParentheses(denominator)) "($denominator)" else denominator
    return "$n/$d"
}

private fun needsLatexParentheses(value: String): Boolean {
    val trimmed = value.trim()
    if (trimmed.length <= 1) return false
    if (trimmed.startsWith("(") && trimmed.endsWith(")")) return false
    return true
}

private fun applyCombining(value: String, mark: Char): String {
    if (value.isEmpty()) return value
    return buildString {
        value.forEach { char ->
            append(char)
            if (!char.isWhitespace()) append(mark)
        }
    }
}

private fun toSuperscript(value: String): String {
    if (value.isEmpty()) return ""
    val result = StringBuilder(value.length)
    for (char in value) {
        val mapped = SUPERSCRIPTS[char] ?: return "^($value)"
        result.append(mapped)
    }
    return result.toString()
}

private fun toSubscript(value: String): String {
    if (value.isEmpty()) return ""
    val result = StringBuilder(value.length)
    for (char in value) {
        val mapped = SUBSCRIPTS[char] ?: return "_($value)"
        result.append(mapped)
    }
    return result.toString()
}

private fun cleanLatex(value: String): String {
    val normalised = value.replace('\r', '\n')
    val lines = normalised.split('\n').map { line ->
        line.replace(Regex("[ \\t]+"), " ").trim()
    }
    return lines.joinToString("\n").trim()
}

/**
 * Gemini doi khi viet thieu dau `\` (vi du "2 cdot 3" thay vi "2 \\cdot 3").
 * Chi thay cac lenh nam tron trong tu de khong pha van ban thuong.
 */
private fun normaliseBareCommands(value: String): String {
    var result = value
    BARE_COMMANDS.forEach { (name, symbol) ->
        result = result.replace(Regex("\\b$name\\b"), symbol)
    }
    return result
}

private val BARE_COMMANDS: Map<String, String> = mapOf(
    "cdot" to "\u00B7",
    "times" to "\u00D7",
    "div" to "\u00F7",
    "pm" to "\u00B1",
    "mp" to "\u2213",
    "le" to "\u2264",
    "leq" to "\u2264",
    "ge" to "\u2265",
    "geq" to "\u2265",
    "ne" to "\u2260",
    "neq" to "\u2260",
    "approx" to "\u2248",
    "equiv" to "\u2261",
    "to" to "\u2192",
    "rightarrow" to "\u2192",
    "infty" to "\u221E",
    "alpha" to "\u03B1",
    "beta" to "\u03B2",
    "gamma" to "\u03B3",
    "delta" to "\u03B4",
    "Delta" to "\u0394",
    "theta" to "\u03B8",
    "lambda" to "\u03BB",
    "mu" to "\u03BC",
    "pi" to "\u03C0",
    "sigma" to "\u03C3",
    "omega" to "\u03C9"
)

/**
 * Gemini doi khi tra JSON chua escape dung, khien JSON parser bien `\frac`
 * thanh form-feed + "rac", `\right` thanh CR + "ight"... Ham nay khoi phuc
 * cac ky tu dieu khien do ve lenh LaTeX truoc khi render.
 */
internal fun repairLatexControlChars(value: String): String {
    if (value.none {
            it == '\u000C' || it == '\u0008' || it == '\t' ||
                it == '\r' || it == '\n'
        }
    ) {
        return value
    }
    var result = value
    CONTROL_LATEX_FIXES.forEach { (broken, fixed) ->
        result = result.replace(broken, fixed)
    }
    return result
}

private val CONTROL_LATEX_FIXES = listOf(
    "\u000Dightarrow" to "\\rightarrow",
    "\u000Dight" to "\\right",
    "\u000Crac" to "\\frac",
    "\u000Corall" to "\\forall",
    "\u0009heta" to "\\theta",
    "\u0009imes" to "\\times",
    "\u0009ext" to "\\text",
    "\u0009an" to "\\tan",
    "\u0009au" to "\\tau",
    "\u0009o" to "\\to",
    "\u0008egin" to "\\begin",
    "\u0008eta" to "\\beta",
    "\u0008inom" to "\\binom",
    "\u0008ar" to "\\bar",
    "\u000Aabla" to "\\nabla",
    "\u000Aotin" to "\\notin",
    "\u000Aeq" to "\\neq",
    "\u000Dho" to "\\rho"
)

private val LATEX_COMMANDS: Map<String, String> = mapOf(
    // Hy Lap
    "alpha" to "\u03B1", "beta" to "\u03B2", "gamma" to "\u03B3", "delta" to "\u03B4",
    "epsilon" to "\u03B5", "varepsilon" to "\u03B5", "zeta" to "\u03B6", "eta" to "\u03B7",
    "theta" to "\u03B8", "vartheta" to "\u03D1", "iota" to "\u03B9", "kappa" to "\u03BA",
    "lambda" to "\u03BB", "mu" to "\u03BC", "nu" to "\u03BD", "xi" to "\u03BE",
    "pi" to "\u03C0", "rho" to "\u03C1", "sigma" to "\u03C3", "tau" to "\u03C4",
    "upsilon" to "\u03C5", "phi" to "\u03C6", "varphi" to "\u03C6", "chi" to "\u03C7",
    "psi" to "\u03C8", "omega" to "\u03C9",
    "Gamma" to "\u0393", "Delta" to "\u0394", "Theta" to "\u0398", "Lambda" to "\u039B",
    "Xi" to "\u039E", "Pi" to "\u03A0", "Sigma" to "\u03A3", "Phi" to "\u03A6",
    "Psi" to "\u03A8", "Omega" to "\u03A9",

    // Toan tu / quan he
    "infty" to "\u221E", "partial" to "\u2202", "nabla" to "\u2207",
    "to" to "\u2192", "rightarrow" to "\u2192", "leftarrow" to "\u2190",
    "leftrightarrow" to "\u2194", "Rightarrow" to "\u21D2", "Leftarrow" to "\u21D0",
    "Leftrightarrow" to "\u21D4", "mapsto" to "\u21A6",
    "cdot" to "\u00B7", "times" to "\u00D7", "div" to "\u00F7",
    "pm" to "\u00B1", "mp" to "\u2213", "ast" to "\u2217",
    "le" to "\u2264", "leq" to "\u2264", "ge" to "\u2265", "geq" to "\u2265",
    "ne" to "\u2260", "neq" to "\u2260", "approx" to "\u2248", "equiv" to "\u2261",
    "sim" to "\u223C", "simeq" to "\u2243", "propto" to "\u221D",
    "ll" to "\u226A", "gg" to "\u226B",
    "in" to "\u2208", "notin" to "\u2209", "ni" to "\u220B",
    "subset" to "\u2282", "subseteq" to "\u2286", "supset" to "\u2283",
    "supseteq" to "\u2287", "cup" to "\u222A", "cap" to "\u2229",
    "emptyset" to "\u2205", "varnothing" to "\u2205",
    "forall" to "\u2200", "exists" to "\u2203", "nexists" to "\u2204",
    "therefore" to "\u2234", "because" to "\u2235",
    "angle" to "\u2220", "degree" to "\u00B0", "prime" to "\u2032",
    "perp" to "\u27C2", "parallel" to "\u2225", "cong" to "\u2245",

    // Tong / tich phan / gioi han
    "sum" to "\u2211", "prod" to "\u220F", "int" to "\u222B", "iint" to "\u222C",
    "iiint" to "\u222D", "oint" to "\u222E",
    "lim" to "lim", "limsup" to "lim sup", "liminf" to "lim inf",
    "max" to "max", "min" to "min", "sup" to "sup", "inf" to "inf",
    "sin" to "sin", "cos" to "cos", "tan" to "tan", "cot" to "cot",
    "sec" to "sec", "csc" to "csc", "arcsin" to "arcsin", "arccos" to "arccos",
    "arctan" to "arctan", "sinh" to "sinh", "cosh" to "cosh", "tanh" to "tanh",
    "log" to "log", "ln" to "ln", "exp" to "exp", "det" to "det", "dim" to "dim",

    // Dau / ky hieu khac
    "ldots" to "\u2026", "dots" to "\u2026", "cdots" to "\u22EF",
    "vdots" to "\u22EE", "ddots" to "\u22F1",
    "langle" to "\u27E8", "rangle" to "\u27E9",
    "lfloor" to "\u230A", "rfloor" to "\u230B", "lceil" to "\u2308", "rceil" to "\u2309",
    "dagger" to "\u2020", "ddagger" to "\u2021",
    "star" to "\u2606", "bullet" to "\u2022",
    "Re" to "\u211C", "Im" to "\u2111", "aleph" to "\u2135",
    "hbar" to "\u210F", "ell" to "\u2113",
    "displaystyle" to "", "textstyle" to "", "scriptstyle" to "",
    "limits" to "", "nolimits" to "", "not" to "\u0338",
    "quad" to " ", "qquad" to "  "
)

private val SUPERSCRIPTS: Map<Char, Char> = mapOf(
    '0' to '\u2070', '1' to '\u00B9', '2' to '\u00B2', '3' to '\u00B3',
    '4' to '\u2074', '5' to '\u2075', '6' to '\u2076', '7' to '\u2077',
    '8' to '\u2078', '9' to '\u2079',
    '+' to '\u207A', '-' to '\u207B', '=' to '\u207C',
    '(' to '\u207D', ')' to '\u207E', ' ' to ' ',
    'n' to '\u207F', 'i' to '\u2071',
    'a' to '\u1D43', 'b' to '\u1D47', 'c' to '\u1D9C', 'd' to '\u1D48',
    'e' to '\u1D49', 'f' to '\u1DA0', 'g' to '\u1D4D', 'h' to '\u02B0',
    'j' to '\u02B2', 'k' to '\u1D4F', 'l' to '\u02E1', 'm' to '\u1D50',
    'o' to '\u1D52', 'p' to '\u1D56', 'r' to '\u02B3', 's' to '\u02E2',
    't' to '\u1D57', 'u' to '\u1D58', 'v' to '\u1D5B', 'w' to '\u02B7',
    'x' to '\u02E3', 'y' to '\u02B8', 'z' to '\u1DBB',
    'A' to '\u1D2C', 'B' to '\u1D2E', 'D' to '\u1D30', 'E' to '\u1D31',
    'G' to '\u1D33', 'H' to '\u1D34', 'I' to '\u1D35', 'J' to '\u1D36',
    'K' to '\u1D37', 'L' to '\u1D38', 'M' to '\u1D39', 'N' to '\u1D3A',
    'O' to '\u1D3C', 'P' to '\u1D3E', 'R' to '\u1D3F', 'T' to '\u1D40',
    'U' to '\u1D41', 'V' to '\u2C7D', 'W' to '\u1D42'
)

private val SUBSCRIPTS: Map<Char, Char> = mapOf(
    '0' to '\u2080', '1' to '\u2081', '2' to '\u2082', '3' to '\u2083',
    '4' to '\u2084', '5' to '\u2085', '6' to '\u2086', '7' to '\u2087',
    '8' to '\u2088', '9' to '\u2089',
    '+' to '\u208A', '-' to '\u208B', '=' to '\u208C',
    '(' to '\u208D', ')' to '\u208E', ' ' to ' ',
    'a' to '\u2090', 'e' to '\u2091', 'h' to '\u2095', 'i' to '\u1D62',
    'j' to '\u2C7C', 'k' to '\u2096', 'l' to '\u2097', 'm' to '\u2098',
    'n' to '\u2099', 'o' to '\u2092', 'p' to '\u209A', 'r' to '\u1D63',
    's' to '\u209B', 't' to '\u209C', 'u' to '\u1D64', 'v' to '\u1D65',
    'x' to '\u2093'
)
