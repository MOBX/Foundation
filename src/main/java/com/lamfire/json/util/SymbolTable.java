package com.lamfire.json.util;

/**
 * Created with IntelliJ IDEA.
 * User: lamfire
 * Date: 14-7-11
 * Time: ÏÂÎç2:37
 * To change this template use File | Settings | File Templates.
 */
public class SymbolTable
{
    public static final int DEFAULT_TABLE_SIZE = 128;
    private final Entry[] buckets;
    private final String[] symbols;
    private final char[][] symbols_char;
    private final int indexMask;

    public SymbolTable()
    {
        this(128);
    }

    public SymbolTable(int tableSize) {
        this.indexMask = (tableSize - 1);
        this.buckets = new Entry[tableSize];
        this.symbols = new String[tableSize];
        this.symbols_char = new char[tableSize][];
    }

    public String addSymbol(String symbol)
    {
        return addSymbol(symbol.toCharArray(), 0, symbol.length(), symbol.hashCode());
    }

    public String addSymbol(char[] buffer, int offset, int len)
    {
        int hash = hash(buffer, offset, len);
        return addSymbol(buffer, offset, len, hash);
    }

    public String addSymbol(char[] buffer, int offset, int len, int hash)
    {
        int bucket = hash & this.indexMask;

        String sym = this.symbols[bucket];

        boolean match = true;

        if (sym != null) {
            if (sym.length() == len) {
                char[] characters = this.symbols_char[bucket];

                for (int i = 0; i < len; i++) {
                    if (buffer[(offset + i)] != characters[i]) {
                        match = false;
                        break;
                    }
                }

                if (match)
                    return sym;
            }
            else {
                match = false;
            }
        }

        for (Entry entry = this.buckets[bucket]; entry != null; entry = entry.next) {
            char[] characters = entry.characters;
            if ((len == characters.length) && (hash == entry.hashCode)) {
                int i = 0;
                while (true) if (i < len) {
                    if (buffer[(offset + i)] != characters[i])
                        break;
                    i++; continue;
                }
                else
                {
                    return entry.symbol;
                }
            }
        }
        Entry entry = new Entry(buffer, offset, len, hash, this.buckets[bucket]);
        this.buckets[bucket] = entry;
        if (match) {
            this.symbols[bucket] = entry.symbol;
            this.symbols_char[bucket] = entry.characters;
        }
        return entry.symbol;
    }

    public static final int hash(char[] buffer, int offset, int len) {
        int h = 0;
        int off = offset;

        for (int i = 0; i < len; i++) {
            h = 31 * h + buffer[(off++)];
        }
        return h;
    }

    protected static final class Entry
    {
        public final String symbol;
        public final int hashCode;
        public final char[] characters;
        public final byte[] bytes;
        public Entry next;

        public Entry(char[] ch, int offset, int length, int hash, Entry next) {
            this.characters = new char[length];
            System.arraycopy(ch, offset, this.characters, 0, length);
            this.symbol = new String(this.characters).intern();
            this.next = next;
            this.hashCode = hash;
            this.bytes = null;
        }
    }
}