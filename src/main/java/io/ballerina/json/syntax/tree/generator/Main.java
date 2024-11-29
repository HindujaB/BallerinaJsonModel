package io.ballerina.json.syntax.tree.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ballerina.json.model.BallerinaPackage;
import io.ballerina.json.model.VariableDeserializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String inputPath = "src/main/resources/sample.json";
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BallerinaPackage.Variable.class, new VariableDeserializer())
                .create();
        try {
            BallerinaPackage ballerinaPackage = gson.fromJson(readFile(inputPath), BallerinaPackage.class);
            BallerinaCodeBuilder ballerinaCodeBuilder = new BallerinaCodeBuilder();
            ballerinaCodeBuilder.build(ballerinaPackage);
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