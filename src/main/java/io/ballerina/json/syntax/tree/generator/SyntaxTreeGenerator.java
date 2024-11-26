package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.runtime.api.values.BMap;
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

    public SyntaxTree generateSyntaxTree() {
        Token eofToken = createIdentifierToken("");
        List<ImportDeclarationNode> imports = generateImports();
        List<ModuleMemberDeclarationNode> moduleMembers = new ArrayList<>();
        ModulePartNode modulePartNode = createModulePartNode(createNodeList(imports),
                createNodeList(moduleMembers), eofToken);
        TextDocument textDocument = TextDocuments.from("");
        return SyntaxTree.from(textDocument).modifyWith(modulePartNode);
    }

    private List<ImportDeclarationNode> generateImports() {
        return new ArrayList<>();
    }


}
