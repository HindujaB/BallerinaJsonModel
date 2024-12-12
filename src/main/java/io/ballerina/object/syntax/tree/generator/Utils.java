package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.internal.parser.LexerTerminals;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.CaptureBindingPatternNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.TypedBindingPatternNode;
import io.ballerina.object.model.BallerinaPackage;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createBuiltinSimpleNameReferenceNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createCaptureBindingPatternNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createModuleVariableDeclarationNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createTypedBindingPatternNode;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.EQUAL_TOKEN;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.SEMICOLON_TOKEN;

public class Utils {

    public static ModuleMemberDeclarationNode getLiteralVariableDeclarationNode(BallerinaPackage.Variable variable) {
        String type = variable.type();
        SyntaxKind typeSyntaxKind = getBuiltinTypeSyntaxKind(type);
        BuiltinSimpleNameReferenceNode typeBindingPattern = createBuiltinSimpleNameReferenceNode(typeSyntaxKind,
                createIdentifierToken(type));
        String name = variable.name();
        CaptureBindingPatternNode bindingPattern = createCaptureBindingPatternNode(createIdentifierToken(name));
        TypedBindingPatternNode bindingPatternNode = createTypedBindingPatternNode(typeBindingPattern, bindingPattern);
        ExpressionNode expressionNode = NodeParser.parseExpression(variable.expression());
        return createModuleVariableDeclarationNode(null, null, NodeFactory.createEmptyNodeList(),
                bindingPatternNode, createToken(EQUAL_TOKEN), expressionNode, createToken(SEMICOLON_TOKEN));
    }

    private static SyntaxKind getBuiltinTypeSyntaxKind(String typeKeyword) {
        return switch (typeKeyword) {
            case LexerTerminals.INT -> SyntaxKind.INT_TYPE_DESC;
            case LexerTerminals.FLOAT -> SyntaxKind.FLOAT_TYPE_DESC;
            case LexerTerminals.DECIMAL -> SyntaxKind.DECIMAL_TYPE_DESC;
            case LexerTerminals.BOOLEAN -> SyntaxKind.BOOLEAN_TYPE_DESC;
            case LexerTerminals.STRING -> SyntaxKind.STRING_TYPE_DESC;
            case LexerTerminals.BYTE -> SyntaxKind.BYTE_TYPE_DESC;
            case LexerTerminals.JSON -> SyntaxKind.JSON_TYPE_DESC;
            case LexerTerminals.HANDLE -> SyntaxKind.HANDLE_TYPE_DESC;
            case LexerTerminals.ANY -> SyntaxKind.ANY_TYPE_DESC;
            case LexerTerminals.ANYDATA -> SyntaxKind.ANYDATA_TYPE_DESC;
            case LexerTerminals.NEVER -> SyntaxKind.NEVER_TYPE_DESC;
            case LexerTerminals.VAR -> SyntaxKind.VAR_TYPE_DESC;
            case LexerTerminals.READONLY -> SyntaxKind.READONLY_TYPE_DESC;
            case LexerTerminals.NULL -> SyntaxKind.NIL_TYPE_DESC;
            default -> {
                assert false : typeKeyword + " is not a built-in type";
                yield SyntaxKind.TYPE_REFERENCE;
            }
        };
    }

}
