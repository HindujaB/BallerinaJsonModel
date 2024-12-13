package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.object.model.BallerinaPackage;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createModulePartNode;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.LISTENER;

public class SyntaxTreeGenerator {

    public SyntaxTreeGenerator() {
    }

    public SyntaxTree generateSyntaxTree(BallerinaPackage.Module module) {
        Token eofToken = createIdentifierToken("");
        List<ImportDeclarationNode> imports = generateImports(module);
        List<ModuleMemberDeclarationNode> moduleMembers = generateModuleMembers(module);
        ModulePartNode modulePartNode = createModulePartNode(createNodeList(imports),
                createNodeList(moduleMembers), eofToken);
        TextDocument textDocument = TextDocuments.from("");
        return SyntaxTree.from(textDocument).modifyWith(modulePartNode);
    }

    private List<ModuleMemberDeclarationNode> generateModuleMembers(BallerinaPackage.Module module) {
        List<ModuleMemberDeclarationNode> moduleMembers = new ArrayList<>();
        if (!module.variables().isEmpty()) {
            List<BallerinaPackage.Variable> variables = module.variables();
            for (BallerinaPackage.Variable variable : variables) {
                Object value = variable.expression();
                if (value != null) {
                    moduleMembers.add(Utils.getLiteralVariableDeclarationNode(variable));
                }
            }
        }

        for (BallerinaPackage.Listener listener : module.listeners()) {
            String listenerDeclaration = generateListenerDeclaration(listener);
            moduleMembers.add(NodeParser.parseModuleMemberDeclaration(listenerDeclaration));
        }

        if (!module.services().isEmpty()) {
            ServiceGenerator serviceGenerator = new ServiceGenerator();
            for (BallerinaPackage.Service service : module.services()) {
                serviceGenerator.generateService(service, moduleMembers);
            }
        }
        return moduleMembers;
    }

    private String generateListenerDeclaration(BallerinaPackage.Listener listener) {
        String port = listener.config().get("port");
        StringBuilder additionalConfig = new StringBuilder();
        for (Map.Entry<String, String> entry : listener.config().entrySet()) {
            if (!"port".equals(entry.getKey())) {
                additionalConfig.append(entry.getKey()).append(" : \"").append(entry.getValue()).append("\", ");
            }
        }
        if (!additionalConfig.isEmpty()) {
            additionalConfig.setLength(additionalConfig.length() - 2);
        }
        return LISTENER.replace("{TYPE}", listener.type())
                .replace("{NAME}", listener.name())
                .replace("{PORT}", port)
                .replace("{CONFIG}", additionalConfig.toString());
    }

    private List<ImportDeclarationNode> generateImports(BallerinaPackage.Module module) {
        List<ImportDeclarationNode> imports = new ArrayList<>();
        for (BallerinaPackage.Import importNode : module.imports()) {
            String importStr = BallerinaTemplates.IMPORT.replace("{ORG}", importNode.org())
                    .replace("{MODULE}", importNode.module());
            imports.add(NodeParser.parseImportDeclaration(importStr));
        }
        return imports;
    }

}
