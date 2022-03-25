package me.linusdev.data.parser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

public class ParseHelper {

    /**
     * Escapes ", \, \n, \f, \r, \t, \b and Characters with matching unicode: ch &lt;= '\u001f' || (ch &gt;= '\u007f' &amp;&amp; ch &lt;= '\u009F') || (ch &gt;= '\u2000' &amp;&amp; ch &lt;= '\u20FF')
     * @param s the string to escape
     * @param str the stringBuilder, the escaped string should be addded to
     */
    protected static void escape(@NotNull String s, @NotNull StringBuilder str) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    str.append("\\\"");
                    break;
                case '\\':
                    str.append("\\\\");
                    break;
                case '\n':
                    str.append("\\n");
                    break;
                case '\f':
                    str.append("\\f");
                    break;
                case '\r':
                    str.append("\\r");
                    break;
                case '\t':
                    str.append("\\t");
                    break;
                case '\b':
                    str.append("\\b");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if (ch <= '\u001F' || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        str.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            str.append('0');
                        }
                        str.append(ss.toUpperCase());
                    } else {
                        str.append(ch);
                    }
            }
        }
    }

    /**
     * Escapes ", \, /, \n, \f, \r, \t, \b and Characters with matching unicode: ch &lt;= '\u001f' || (ch &gt;= '\u007f' &amp;&amp; ch &lt;= '\u009F') || (ch &gt;= '\u2000' &amp;&amp; ch &lt;= '\u20FF')
     * @param s the string to escape
     * @param str the stringBuilder, the escaped string should be addded to
     */
    protected static void escapeWithForwardSlash(@NotNull String s, @NotNull StringBuilder str) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '/':
                    str.append("\\/");
                    break;
                case '"':
                    str.append("\\\"");
                    break;
                case '\\':
                    str.append("\\\\");
                    break;
                case '\n':
                    str.append("\\n");
                    break;
                case '\f':
                    str.append("\\f");
                    break;
                case '\r':
                    str.append("\\r");
                    break;
                case '\t':
                    str.append("\\t");
                    break;
                case '\b':
                    str.append("\\b");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if (ch <= '\u001F' || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        str.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            str.append('0');
                        }
                        str.append(ss.toUpperCase());
                    } else {
                        str.append(ch);
                    }
            }
        }
    }

    /**
     * Escapes ", \, \n, \f, \r, \t, \b and Characters with matching unicode: ch &lt;= '\u001f' || (ch &gt;= '\u007f' &amp;&amp; ch &lt;= '\u009F') || (ch &gt;= '\u2000' &amp;&amp; ch &lt;= '\u20FF')
     * @param s the string to escape
     * @param str the stringBuilder, the escaped string should be addded to
     */
    protected static void escape(@NotNull String s, @NotNull Writer str) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    str.append("\\\"");
                    break;
                case '\\':
                    str.append("\\\\");
                    break;
                case '\n':
                    str.append("\\n");
                    break;
                case '\f':
                    str.append("\\f");
                    break;
                case '\r':
                    str.append("\\r");
                    break;
                case '\t':
                    str.append("\\t");
                    break;
                case '\b':
                    str.append("\\b");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if (ch <= '\u001F' || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        str.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            str.append('0');
                        }
                        str.append(ss.toUpperCase());
                    } else {
                        str.append(ch);
                    }
            }
        }
    }

    /**
     * Escapes ", \, /, \n, \f, \r, \t, \b and Characters with matching unicode: ch &lt;= '\u001f' || (ch &gt;= '\u007f' &amp;&amp; ch &lt;= '\u009F') || (ch &gt;= '\u2000' &amp;&amp; ch &lt;= '\u20FF')
     * @param s the string to escape
     * @param str the stringBuilder, the escaped string should be addded to
     */
    protected static void escapeWithForwardSlash(@NotNull String s, @NotNull Writer str) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '/':
                    str.append("\\/");
                    break;
                case '"':
                    str.append("\\\"");
                    break;
                case '\\':
                    str.append("\\\\");
                    break;
                case '\n':
                    str.append("\\n");
                    break;
                case '\f':
                    str.append("\\f");
                    break;
                case '\r':
                    str.append("\\r");
                    break;
                case '\t':
                    str.append("\\t");
                    break;
                case '\b':
                    str.append("\\b");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if (ch <= '\u001F' || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        str.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            str.append('0');
                        }
                        str.append(ss.toUpperCase());
                    } else {
                        str.append(ch);
                    }
            }
        }
    }

}
