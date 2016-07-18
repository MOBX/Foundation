package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.regex.Pattern;

public class PatternSerializer implements ObjectSerializer {

    public final static PatternSerializer instance = new PatternSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }

        Pattern p = (Pattern) object;
        serializer.write(p.pattern());
    }
}
