package io.ballerina.object.model;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class VariableDeserializer implements JsonDeserializer<BallerinaPackage.Variable> {

    @Override
    public BallerinaPackage.Variable deserialize(JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String type = jsonObject.get("type").getAsString();
        Object value;

        // Handle value based on its JSON representation
        JsonElement valueElement = jsonObject.get("value");
        if (valueElement.isJsonPrimitive()) {
            if (valueElement.getAsJsonPrimitive().isNumber()) {
                Number numberValue = valueElement.getAsNumber();

                // Decide if it's an int, long, or double
                if (numberValue.longValue() == numberValue.intValue()) {
                    value = numberValue.intValue(); // Treat as int if no loss of precision
                } else {
                    value = numberValue.longValue(); // Treat as long if larger
                }
            } else {
                value = valueElement.getAsString(); // Fallback for non-numeric primitives
            }
        } else {
            value = null; // Handle non-primitive values if necessary
        }
        return new BallerinaPackage.Variable(name, type, value);
    }
}
