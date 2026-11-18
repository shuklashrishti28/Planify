package com.planify.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlUtilTest {
    @Test
    void escapesHtmlSpecialCharacters() {
        assertEquals("&lt;tag attr=&quot;x&quot;&gt;Tom &amp; Jerry&#x27;s&lt;/tag&gt;",
                HtmlUtil.escape("<tag attr=\"x\">Tom & Jerry's</tag>"));
    }

    @Test
    void nullBecomesAnEmptyStringAndPlainTextIsPreserved() {
        assertEquals("", HtmlUtil.escape(null));
        assertEquals("Planify dashboard", HtmlUtil.escape("Planify dashboard"));
    }
}
