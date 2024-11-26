package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.runtime.api.utils.JsonUtils;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BMap;

import java.io.FileInputStream;
import java.io.IOException;

import static io.ballerina.json.syntax.tree.generator.ModelConstants.DEFAULT_PACKAGE;

public class Main {

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/sample.json")) {
            BMap jsonObject = (BMap) JsonUtils.parse(inputStream);
            BMap pkg = jsonObject.getMapValue(StringUtils.fromString(DEFAULT_PACKAGE));
            SyntaxTreeGenerator generator = new SyntaxTreeGenerator(jsonObject);
            SyntaxTree syntaxTree = generator.generateSyntaxTree();
            BalSourceWriter writer = new BalSourceWriter(pkg);
            writer.write(syntaxTree);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}