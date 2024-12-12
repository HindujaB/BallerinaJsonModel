package io.ballerina.object.syntax.tree.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ballerina.object.model.BallerinaPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        String inputPath = "src/main/resources/sample.json";
        Gson gson = new GsonBuilder()
                .create();
        try {
            BallerinaPackage ballerinaPackage = gson.fromJson(readFile(inputPath), BallerinaPackage.class);
            BallerinaCodeBuilder ballerinaCodeBuilder = new BallerinaCodeBuilder();
            ballerinaCodeBuilder.build(ballerinaPackage, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Reads a file from resources with the given file name.
     *
     * @param fileName Resource name.
     * @return File content.
     */
    public static String readFile(String fileName) throws IOException {
        return Files.readString(Path.of(fileName));
    }

}