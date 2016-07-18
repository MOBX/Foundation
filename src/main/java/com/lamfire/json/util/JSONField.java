package com.lamfire.json.util;

import java.lang.annotation.*;

import com.lamfire.json.parser.Feature;
import com.lamfire.json.serializer.SerializerFeature;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface JSONField {

    String name() default "";

    String format() default "";

    boolean serialize() default true;

    boolean deserialize() default true;

    SerializerFeature[] serialzeFeatures() default {};

    Feature[] parseFeatures() default {};
}
