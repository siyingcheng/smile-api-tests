package com.smile.core.reporter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HtmlMarkup {

    public static String bolder(String content) {
        return "<b>" + content + "</b>";
    }

    public static String lineBreak() {
        return "<br />";
    }
}
