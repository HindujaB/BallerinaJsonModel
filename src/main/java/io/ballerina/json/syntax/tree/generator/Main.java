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
        String inputPath = "src/main/resources/sample.json";
        BallerinaCodeBuilder ballerinaCodeBuilder = new BallerinaCodeBuilder();
        ballerinaCodeBuilder.build(inputPath);
    }
}