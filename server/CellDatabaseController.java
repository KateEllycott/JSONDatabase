package server;

import com.google.gson.*;
import netscape.javascript.JSObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CellDatabaseController {

    private static final String RESOURCES = "src/server/data";
    private static final String DATABASE_NAME = "db.json";
    private final CellDatabase database;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public CellDatabaseController(CellDatabase database) {
        this.database = database;
    }

    public Response getCell(String requestJson) {

        Response response = new Response();

        JsonObject requestJsonObject = JsonParser.parseString(requestJson).getAsJsonObject();
        JsonElement key = requestJsonObject.get("key");

        readLock.lock();
        try {
            JsonElement value = database.getCell(key);
            if (value.isJsonNull()) {
                response.setResponse("ERROR");
                response.setReason("No such key");
            }
            else {
                response.setResponse("OK");
                response.setValue(value);
            }
        }
        finally {
            readLock.unlock();
        }
        return response;
    }

    public Response setCell(String requestJson) {
        Response response = new Response();

        JsonObject requestJsonObject = JsonParser.parseString(requestJson).getAsJsonObject();
        JsonElement key = requestJsonObject.get("key");
        JsonElement value = requestJsonObject.get("value");
        System.out.println("value" + value.toString());
        writeLock.lock();
        try {
            System.out.println("Database controller: set cell");
            database.setCell(key, value);
            response.setResponse("OK");
            updateDatabaseFile();
            System.out.println("Database controller: database updated");
        }
        finally {
            writeLock.unlock();
        }
        return response;
    }

    public Response clearCell(String requestJson) {
        Response response = new Response();

        JsonObject requestJsonObject = JsonParser.parseString(requestJson).getAsJsonObject();
        JsonElement key = requestJsonObject.get("key");

        writeLock.lock();
        try {
            JsonElement deleted = database.clearCell(key);
            if (deleted.isJsonNull()) {
                response.setResponse("ERROR");
                response.setReason("No such key");
            }
            else {
                response.setResponse("OK");
            }
        }
        finally {
            writeLock.unlock();
        }
        return response;
    }

    private void updateDatabaseFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(RESOURCES, DATABASE_NAME), StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
            Gson gson = new Gson();
            gson.toJson(database, writer);
        }
        catch (IOException e) {
            System.out.println("Can't open the database file!");
        }
    }

    public void initialiseDatabase() {
        Path pathToFile = Paths.get(RESOURCES, DATABASE_NAME);
        System.out.println(pathToFile);
        if(!Files.exists(pathToFile))  {
            try {
                System.out.println("file does't exist");
                File dirs = new File(RESOURCES);
                dirs.mkdirs();
                File file = new File(RESOURCES, DATABASE_NAME);
                file.createNewFile();
            }
            catch (IOException e) {
                throw new RuntimeException("Can't create the database file", e);
            }

        }
    }
}
