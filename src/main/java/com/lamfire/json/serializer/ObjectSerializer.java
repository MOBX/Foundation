package com.lamfire.json.serializer;

import java.io.IOException;

public interface ObjectSerializer {

    abstract void write(JSONSerializer serializer, Object object) throws IOException;
}
