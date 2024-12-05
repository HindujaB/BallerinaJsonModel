package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.internal.parser.LexerTerminals;
import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.CaptureBindingPatternNode;
import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.LiteralValueToken;
import io.ballerina.compiler.syntax.tree.Minutiae;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypedBindingPatternNode;
import io.ballerina.object.model.BallerinaPackage;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createEmptyMinutiaeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createLiteralValueToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createBasicLiteralNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createBuiltinSimpleNameReferenceNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createCaptureBindingPatternNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createModuleVariableDeclarationNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createTypedBindingPatternNode;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.DECIMAL_INTEGER_LITERAL_TOKEN;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.EQUAL_TOKEN;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.SEMICOLON_TOKEN;

public class Utils {

    public static final MinutiaeList SINGLE_WS_MINUTIAE = getSingleWSMinutiae();

    private static MinutiaeList getSingleWSMinutiae() {
        Minutiae whitespace = AbstractNodeFactory.createWhitespaceMinutiae(" ");
        return AbstractNodeFactory.createMinutiaeList(whitespace);
    }

    public static ImportDeclarationNode getImportDeclarationNode(String orgName, String moduleName) {

        Token importKeyword = AbstractNodeFactory.createIdentifierToken("import", SINGLE_WS_MINUTIAE,
                SINGLE_WS_MINUTIAE);
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        Token slashToken = AbstractNodeFactory.createIdentifierToken("/");
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(orgNameToken, slashToken);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList = AbstractNodeFactory.createSeparatedNodeList(moduleNameToken);
        Token semicolon = AbstractNodeFactory.createIdentifierToken(";");

        return NodeFactory.createImportDeclarationNode(importKeyword, importOrgNameNode, moduleNodeList, null,
                semicolon);
    }

    public static ModuleMemberDeclarationNode getLiteralVariableDeclarationNode(BallerinaPackage.Variable variable) {
        String type = variable.type();
        SyntaxKind typeSyntaxKind = getBuiltinTypeSyntaxKind(type);
        BuiltinSimpleNameReferenceNode typeBindingPattern = createBuiltinSimpleNameReferenceNode(typeSyntaxKind,
                createIdentifierToken(type));
        String name = variable.name();
        CaptureBindingPatternNode bindingPattern = createCaptureBindingPatternNode(createIdentifierToken(name));
        TypedBindingPatternNode bindingPatternNode = createTypedBindingPatternNode(typeBindingPattern, bindingPattern);
        String value = variable.value().toString();
        ExpressionNode expressionNode = createBasicLiteral(typeSyntaxKind, value);
        return createModuleVariableDeclarationNode(null, null, NodeFactory.createEmptyNodeList(),
                bindingPatternNode, createToken(EQUAL_TOKEN), expressionNode, createToken(SEMICOLON_TOKEN));
    }

    private static ExpressionNode createBasicLiteral(SyntaxKind typeSyntaxKind, String value) {
        SyntaxKind nodeKind;
        SyntaxKind token = switch (typeSyntaxKind) {
            case SyntaxKind.BOOLEAN_TYPE_DESC -> {
                nodeKind = SyntaxKind.BOOLEAN_LITERAL;
                yield value.equals("true") ? SyntaxKind.TRUE_KEYWORD : SyntaxKind.FALSE_KEYWORD;
            }
            case SyntaxKind.INT_TYPE_DESC, SyntaxKind.FLOAT_TYPE_DESC, SyntaxKind.DECIMAL_TYPE_DESC,
                 SyntaxKind.BYTE_TYPE_DESC -> {
                nodeKind = SyntaxKind.NUMERIC_LITERAL;
                yield DECIMAL_INTEGER_LITERAL_TOKEN;
            }
            case SyntaxKind.STRING_TYPE_DESC -> {
                nodeKind = SyntaxKind.STRING_LITERAL;
                yield SyntaxKind.STRING_LITERAL_TOKEN;
            }
            default -> {
                nodeKind = SyntaxKind.NIL_LITERAL;
                yield SyntaxKind.NIL_LITERAL;
            }
        };
        LiteralValueToken valueToken = createLiteralValueToken(token, value, createEmptyMinutiaeList(),
                createEmptyMinutiaeList());
        return createBasicLiteralNode(nodeKind, valueToken);
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

    public static ModuleMemberDeclarationNode getServiceDeclarationNode(BallerinaPackage.Service service) {
        return null;
    }
}
