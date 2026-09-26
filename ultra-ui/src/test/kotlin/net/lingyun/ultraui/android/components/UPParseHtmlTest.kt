package net.lingyun.ultraui.android.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for the bounded HTML parser behind [UPParse]. */
class UPParseHtmlTest {
    private fun el(node: UPParseNode) = node as UPParseElement
    private fun txt(node: UPParseNode) = (node as UPParseText).text

    @Test
    fun decodesNamedDecimalAndHexEntities() {
        assertEquals("a<b>&c\u00A0d", UPParseHtml.decodeEntities("a&lt;b&gt;&amp;c&nbsp;d"))
        assertEquals("A", UPParseHtml.decodeEntities("&#65;"))
        assertEquals("A", UPParseHtml.decodeEntities("&#x41;"))
        assertEquals("bad&entity", UPParseHtml.decodeEntities("bad&entity"))
    }

    @Test
    fun parsesNestedElementsWithAttributes() {
        val nodes = upParseHtml("<p class=\"lead\">Hi <b>there</b></p>")
        assertEquals(1, nodes.size)
        val p = el(nodes[0])
        assertEquals("p", p.name)
        assertEquals("lead", p.attrs["class"])
        assertEquals("Hi ", txt(p.children[0]))
        val b = el(p.children[1])
        assertEquals("b", b.name)
        assertEquals("there", txt(b.children[0]))
    }

    @Test
    fun voidAndSelfClosingTagsTakeNoChildren() {
        val nodes = upParseHtml("<div>a<br>b<img src=\"x.png\"/></div>")
        val div = el(nodes[0])
        assertEquals("a", txt(div.children[0]))
        assertEquals("br", el(div.children[1]).name)
        assertTrue(el(div.children[1]).children.isEmpty())
        assertEquals("b", txt(div.children[2]))
        val img = el(div.children[3])
        assertEquals("img", img.name)
        assertEquals("x.png", img.attrs["src"])
    }

    @Test
    fun dropsCommentsAndRawTextTags() {
        val nodes = upParseHtml("<div><!-- hi -->x<script>var a=1<2;</script>y<style>.a{}</style></div>")
        val div = el(nodes[0])
        val text = div.children.filterIsInstance<UPParseText>().joinToString("") { it.text }
        assertEquals("xy", text)
        assertTrue(div.children.none { it is UPParseElement && (it.name == "script" || it.name == "style") })
    }

    @Test
    fun unwindsUnclosedTagsAndIgnoresStrayCloses() {
        // Unclosed tags at EOF are unwound into a well-formed tree.
        val nodes = upParseHtml("<div><b>bold")
        val div = el(nodes[0])
        assertEquals("div", div.name)
        val b = el(div.children[0])
        assertEquals("b", b.name)
        assertEquals("bold", txt(b.children[0]))
        // A stray close tag with no open match is ignored.
        val stray = upParseHtml("hello</span> world")
        assertEquals("hello world", stray.filterIsInstance<UPParseText>().joinToString("") { it.text })
    }

    @Test
    fun unquotedAttributesAndBareBooleans() {
        val nodes = upParseHtml("<a href=http://x.com target=_blank disabled>go</a>")
        val a = el(nodes[0])
        assertEquals("http://x.com", a.attrs["href"])
        assertEquals("_blank", a.attrs["target"])
        assertEquals("", a.attrs["disabled"])
    }
}
