package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestParser {

    @Parameter(names = "-in", description = "request's file path")
    private String fileName;

    @Parameter(names = "-t", description = "request's type")
    private String type;

    @Parameter(names = "-k", description = "key")
    private String key;

    @Parameter(names = "-v", description = "value")
    private String value;

    public String getFileName() {
        return fileName;
    }

    public static String commandLineRequestToJson(String[] args) {

        RequestParser requestParser = new RequestParser();
        String jsonRequest;
        JCommander.newBuilder()
                .addObject(requestParser)
                .build()
                .parse(args);

        if (requestParser.fileName == null) {
            Gson gson = new Gson();
            jsonRequest = gson.toJson(requestParser);
        }
        else {
            jsonRequest = getJsonRequestFromFile(requestParser.getFileName());
        }

        return jsonRequest;
    }

    private static String getJsonRequestFromFile(String fileName) {

        String jsonRequest = null;

        try {
            jsonRequest = readFileAsString(fileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonRequest == null) {
            throw new RuntimeException("Database file is empty");
        }

        return jsonRequest;
    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src", "client", "data", fileName)));
    }
}
