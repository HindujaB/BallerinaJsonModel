package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;

import java.util.ArrayList;
import java.util.List;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createModulePartNode;

public class SyntaxTreeGenerator {

    private BMap jsonObject;

    public SyntaxTreeGenerator(Object json) {
        this.jsonObject = (BMap) json;
    }

    public SyntaxTree generateSyntaxTree(BMap module) {
        Token eofToken = createIdentifierToken("");
        List<ImportDeclarationNode> imports = generateImports(module);
        List<ModuleMemberDeclarationNode> moduleMembers = generateModuleMembers(module);
        ModulePartNode modulePartNode = createModulePartNode(createNodeList(imports),
                createNodeList(moduleMembers), eofToken);
        TextDocument textDocument = TextDocuments.from("");
        return SyntaxTree.from(textDocument).modifyWith(modulePartNode);
    }

    private List<ModuleMemberDeclarationNode> generateModuleMembers(BMap module) {
        List<ModuleMemberDeclarationNode> moduleMembers = new ArrayList<>();
        BString variables = StringUtils.fromString("variables");
        if (module.containsKey(variables)) {
            BArray variableArray = module.getArrayValue(variables);
            for (Object variable : variableArray.getValues()) {
                if (variable == null) {
                    continue;
                }
                BMap variableMap = (BMap) variable;
                if (variableMap.containsKey(StringUtils.fromString("value"))) {
                    moduleMembers.add(Utils.getLiteralVariableDeclarationNode(variableMap));
                }
            }
        }
        return moduleMembers;
    }

    private List<ImportDeclarationNode> generateImports(BMap module) {
        List<ImportDeclarationNode> imports = new ArrayList<>();
        BArray importsArray = module.getArrayValue(StringUtils.fromString("imports"));

        for (Object importNode : importsArray.getValues()) {
            if (importNode == null) {
                continue;
            }
            BMap importMap = (BMap) importNode;
            ImportDeclarationNode importDeclarationNode = Utils.getImportDeclarationNode(
                    importMap.getStringValue(StringUtils.fromString("org")).getValue(),
                    importMap.getStringValue(StringUtils.fromString("module")).getValue());
            imports.add(importDeclarationNode);
        }
        return imports;
    }

}
