package com.lamfire.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class EscapeUtils {

    public static String escapeJava(String str) {
        return escapeJavaStyleString(str, false);
    }

    public static void escapeJava(Writer out, String str) throws IOException {
        escapeJavaStyleString(out, str, false);
    }

    public static String escapeJavaScript(String str) {
        return escapeJavaStyleString(str, true);
    }

    public static void escapeJavaScript(Writer out, String str) throws IOException {
        escapeJavaStyleString(out, str, true);
    }

    private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes) {
        if (str == null) return null;
        try {
            StringWriter writer = new StringWriter(str.length() * 2);
            escapeJavaStyleString(writer, str, escapeSingleQuotes);
            return writer.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }

        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            if (ch > '?') out.write("\\u" + hex(ch));
            else if (ch > '?') out.write("\\u0" + hex(ch));
            else if (ch > '') out.write("\\u00" + hex(ch));
            else if (ch < ' ') switch (ch) {
                case '\b':
                    out.write(92);
                    out.write(98);
                    break;
                case '\n':
                    out.write(92);
                    out.write(110);
                    break;
                case '\t':
                    out.write(92);
                    out.write(116);
                    break;
                case '\f':
                    out.write(92);
                    out.write(102);
                    break;
                case '\r':
                    out.write(92);
                    out.write(114);
                    break;
                case '\013':
                default:
                    if (ch > '\017') {
                        out.write("\\u00" + hex(ch));
                        continue;
                    }
                    out.write("\\u000" + hex(ch));

                    break;
            }
            else switch (ch) {
                case '\'':
                    if (escapeSingleQuote) {
                        out.write(92);
                    }
                    out.write(39);
                    break;
                case '"':
                    out.write(92);
                    out.write(34);
                    break;
                case '\\':
                    out.write(92);
                    out.write(92);
                    break;
                default:
                    out.write(ch);
            }
        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public static String unescapeJava(String str) {
        if (str == null) return null;
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() != 4) continue;
                try {
                    int value = Integer.parseInt(unicode.toString(), 16);
                    out.write((char) value);
                    unicode.setLength(0);
                    inUnicode = false;
                    hadSlash = false;
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                }

            } else if (hadSlash) {
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write(92);
                        break;
                    case '\'':
                        out.write(39);
                        break;
                    case '"':
                        out.write(34);
                        break;
                    case 'r':
                        out.write(13);
                        break;
                    case 'f':
                        out.write(12);
                        break;
                    case 't':
                        out.write(9);
                        break;
                    case 'n':
                        out.write(10);
                        break;
                    case 'b':
                        out.write(8);
                        break;
                    case 'u':
                        inUnicode = true;
                        break;
                    default:
                        out.write(ch);
                        break;
                }
            } else if (ch == '\\') {
                hadSlash = true;
            } else {
                out.write(ch);
            }
        }
        if (hadSlash) {
            out.write(92);
        }
    }

    public static String unescapeJavaScript(String str) {
        return unescapeJava(str);
    }

    public static void unescapeJavaScript(Writer out, String str) throws IOException {
        unescapeJava(out, str);
    }

    public static String escapeHtml(String str) {
        if (str == null) return null;
        try {
            StringWriter writer = new StringWriter((int) (str.length() * 1.5D));
            escapeHtml(writer, str);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void escapeHtml(Writer writer, String string) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        Entities.HTML40.escape(writer, string);
    }

    public static String unescapeHtml(String str) {
        if (str == null) return null;
        try {
            StringWriter writer = new StringWriter((int) (str.length() * 1.5D));
            unescapeHtml(writer, str);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void unescapeHtml(Writer writer, String string) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (string == null) {
            return;
        }
        Entities.HTML40.unescape(writer, string);
    }

    public static void escapeXml(Writer writer, String str) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (str == null) {
            return;
        }
        Entities.XML.escape(writer, str);
    }

    public static String escapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.escape(str);
    }

    public static void unescapeXml(Writer writer, String str) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null.");
        }
        if (str == null) {
            return;
        }
        Entities.XML.unescape(writer, str);
    }

    public static String unescapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.unescape(str);
    }

    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }
}
