package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;


class CellDatabase {

    private JsonObject data = new JsonObject();

    public JsonElement getCell(JsonElement key) {

        if (key.isJsonPrimitive()) {
            return data.get(key.getAsString());
        }
        else {
            JsonArray keys = key.getAsJsonArray();
            if (keys.size() == 1) {
                return data.get(keys.get(0).getAsString());
            }
            JsonElement current = data;
            for(JsonElement value : keys) {
                current = current.getAsJsonObject().get(value.getAsString());
            }
            return current;
        }
    }

    public void setCell(JsonElement key, JsonElement value) {

        if (key.isJsonPrimitive()) {
            data.add(key.getAsString(), value);
        }
        else {
            JsonArray keys = key.getAsJsonArray();
            if (keys.size() == 1) {
                data.add(keys.get(0).getAsString(), value);
                return;
            }
            else {
                setByComplexKey(keys, value);
            }
        }
    }

    private void setByComplexKey(JsonArray keys, JsonElement value) {

        Map<String, JsonElement> combinations = new HashMap<>();
        JsonElement currentValue = value;

        for (int i = keys.size() - 1; i > 0; i--) {
            String currentKey = keys.get(i).getAsString();
            JsonObject obj = new JsonObject();
            obj.add(currentKey, currentValue);
            combinations.put(currentKey, obj);
            currentValue = combinations.get(currentKey);
        }
        for (JsonElement combination : combinations.values()) {
            System.out.println(combination);
        }

        JsonObject currentObject = data;

        for (int i = 0; i < keys.size() - 1; i++) {
            String currentKey = keys.get(i).getAsString();

          if (currentObject.has(currentKey)) {
              currentObject = currentObject.get(currentKey).getAsJsonObject();
          }
          else {
              currentObject.add(currentKey, combinations.get(currentKey).getAsJsonObject().get(currentKey));
              return;
          }
        }
        currentObject.add(keys.get(keys.size() - 1).getAsString(), value);
    }

    public JsonElement clearCell(JsonElement key) {

        if (key.isJsonPrimitive()) {
            return data.remove(key.getAsString());
        }
        else {
            JsonArray keys = key.getAsJsonArray();
            if (keys.size() == 1) {
                return data.remove(keys.get(0).getAsString());
            }

            JsonElement current = data;
            for (int i = 0; i < keys.size() - 1; i++) {
                current = current.getAsJsonObject().get(keys.get(i).getAsString());
            }
            return current.getAsJsonObject().remove(keys.get(keys.size() - 1).getAsString());
        }
    }
}