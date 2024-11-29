package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.json.model.BallerinaPackage;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.util.ArrayList;
import java.util.List;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createModulePartNode;

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
        if (!module.getVariables().isEmpty()) {
            List<BallerinaPackage.Variable> variables = module.getVariables();
            for (BallerinaPackage.Variable variable : variables) {
                Object value = variable.getValue();
                if (value != null) {
                    moduleMembers.add(Utils.getLiteralVariableDeclarationNode(variable));
                }
            }
        }

//        if (!module.getServices().isEmpty()) {
//            List<BallerinaPackage.Service> services = module.getServices();
//            for (BallerinaPackage.Service service : services) {
//                ServiceGenerator serviceGenerator = new ServiceGenerator();
//                moduleMembers.add(serviceGenerator.generateService(service));
//            }
//        }
        return moduleMembers;
    }

    private List<ImportDeclarationNode> generateImports(BallerinaPackage.Module module) {
        List<ImportDeclarationNode> imports = new ArrayList<>();
        List<BallerinaPackage.Import> importList = module.getImports();

        for (BallerinaPackage.Import importNode : importList) {
            ImportDeclarationNode importDeclarationNode = Utils.getImportDeclarationNode(importNode.getOrg(),
                    importNode.getModule());
            imports.add(importDeclarationNode);
        }
        return imports;
    }

}
