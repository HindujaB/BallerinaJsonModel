package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.object.model.BallerinaPackage;
import org.ballerinalang.formatter.core.Formatter;
import org.ballerinalang.formatter.core.FormatterException;

import java.nio.file.Path;
import java.util.List;

public class BallerinaCodeBuilder {

    public void build(BallerinaPackage ballerinaPackage, Path outputPath) {
        BallerinaPackage.DefaultPackage pkg = ballerinaPackage.defaultPackage();
        List<BallerinaPackage.Module> modules = ballerinaPackage.modules();
        SyntaxTreeGenerator generator = new SyntaxTreeGenerator();
        for (BallerinaPackage.Module module : modules) {
            SyntaxTree syntaxTree = generator.generateSyntaxTree(module);
            try {
                syntaxTree = Formatter.format(syntaxTree);
            } catch (FormatterException e) {
                throw new RuntimeException(e);
            }
            BalSourceWriter writer = new BalSourceWriter(pkg, outputPath);
            if (isDefaultModule(pkg, module)) {
                writer.writeDefaultModule(syntaxTree);
            } else {
                writer.writeNonDefaultModule(syntaxTree);
            }
        }
    }

    private static boolean isDefaultModule(BallerinaPackage.DefaultPackage pkg, BallerinaPackage.Module module) {
        return pkg.name().equals(module.moduleName());
    }
}
