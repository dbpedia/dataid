package org.aksw.dataid.datahub.xmlObjects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Created by ciro on 31.01.16.
 */
public class YesNoDeSerializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        if(node.asText().trim().toLowerCase().equals("yes"))
        {
            return true;
        }
        else if(node.asText().trim().toLowerCase().equals("no"))
        {
            return false;
        }
        else
            return null;
    }
}