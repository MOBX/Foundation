package com.lamfire.json;

import java.io.IOException;

public interface JSONWriter {

    void write(Appendable out) throws IOException;
}
