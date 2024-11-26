package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.runtime.api.utils.JsonUtils;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BIterator;
import io.ballerina.runtime.api.values.BMap;

import java.io.FileInputStream;
import java.io.IOException;

import static io.ballerina.json.syntax.tree.generator.ModelConstants.DEFAULT_PACKAGE;

public class BallerinaCodeBuilder {

    public void build(String inputPath) {
        try (FileInputStream inputStream = new FileInputStream(inputPath)) {
            BMap jsonObject = (BMap) JsonUtils.parse(inputStream);
            BMap pkg = jsonObject.getMapValue(StringUtils.fromString(DEFAULT_PACKAGE));
            BArray modules = jsonObject.getArrayValue(StringUtils.fromString("modules"));
            BIterator itr = modules.getIterator();
            SyntaxTreeGenerator generator = new SyntaxTreeGenerator(jsonObject);
            while (itr.hasNext()) {
                BMap module = (BMap) itr.next();
                SyntaxTree syntaxTree = generator.generateSyntaxTree(module);
                BalSourceWriter writer = new BalSourceWriter(pkg);
                if (isDefaultModule(pkg, module)) {
                    writer.writeDefaultModule(syntaxTree);
                } else {
                    writer.writeNonDefaultModule(syntaxTree);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }

    }

    private static boolean isDefaultModule(BMap pkg, BMap module) {
        return pkg.getStringValue(StringUtils.fromString("name")).equals(
                module.getStringValue(StringUtils.fromString("moduleName")));
    }
}
