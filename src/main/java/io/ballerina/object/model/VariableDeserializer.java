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

        BallerinaPackage.Variable variable = new BallerinaPackage.Variable();
        variable.setName(jsonObject.get("name").getAsString());
        variable.setType(jsonObject.get("type").getAsString());

        // Handle value based on its JSON representation
        JsonElement valueElement = jsonObject.get("value");
        if (valueElement.isJsonPrimitive()) {
            if (valueElement.getAsJsonPrimitive().isNumber()) {
                Number numberValue = valueElement.getAsNumber();

                // Decide if it's an int, long, or double
                if (numberValue.longValue() == numberValue.intValue()) {
                    variable.setValue(numberValue.intValue()); // Treat as int if no loss of precision
                } else {
                    variable.setValue(numberValue.longValue()); // Treat as long if larger
                }
            } else {
                variable.setValue(valueElement.getAsString()); // Fallback for non-numeric primitives
            }
        } else {
            variable.setValue(null); // Handle non-primitive values if necessary
        }
        return variable;
    }
}
