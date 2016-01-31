package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by ciro on 31.01.16.
 */

public class YesNoSerializer extends JsonSerializer<Boolean> {
    public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if(value)
            jgen.writeRawValue("yes");
        else
            jgen.writeRawValue("no");
    }
}
