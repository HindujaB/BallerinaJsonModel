package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.json.model.BallerinaPackage;

import java.util.List;

public class BallerinaCodeBuilder {

    public void build(BallerinaPackage ballerinaPackage) {
        BallerinaPackage.DefaultPackage pkg = ballerinaPackage.getDefaultPackage();
        List<BallerinaPackage.Module> modules = ballerinaPackage.getModules();
        SyntaxTreeGenerator generator = new SyntaxTreeGenerator();
        for (BallerinaPackage.Module module : modules) {
            SyntaxTree syntaxTree = generator.generateSyntaxTree(module);
            BalSourceWriter writer = new BalSourceWriter(pkg);
            if (isDefaultModule(pkg, module)) {
                writer.writeDefaultModule(syntaxTree);
            } else {
                writer.writeNonDefaultModule(syntaxTree);
            }
        }
    }

    private static boolean isDefaultModule(BallerinaPackage.DefaultPackage pkg, BallerinaPackage.Module module) {
        return pkg.getName().equals(module.getModuleName());
    }
}
