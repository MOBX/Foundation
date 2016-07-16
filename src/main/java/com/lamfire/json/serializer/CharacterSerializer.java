package com.lamfire.json.serializer;

import java.io.IOException;

public class CharacterSerializer implements ObjectSerializer {

    public final static CharacterSerializer instance = new CharacterSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        Character value = (Character) object;
        if (value == null) {
            out.writeString("");
            return;
        }

        char c = value.charValue();
        if (c == 0) {
            out.writeString("\u0000");
        } else {
            out.writeString(value.toString());
        }
    }

}
