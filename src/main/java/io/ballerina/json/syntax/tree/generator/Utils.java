package io.ballerina.json.syntax.tree.generator;

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
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BMap;

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
        MinutiaeList leading = AbstractNodeFactory.createMinutiaeList(whitespace);
        return leading;
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

    public static ModuleMemberDeclarationNode getLiteralVariableDeclarationNode(BMap variableMap) {
        String type = variableMap.getStringValue(StringUtils.fromString("type")).getValue();
        SyntaxKind typeSyntaxKind = getBuiltinTypeSyntaxKind(type);
        BuiltinSimpleNameReferenceNode typeBindingPattern = createBuiltinSimpleNameReferenceNode(typeSyntaxKind,
                createIdentifierToken(type));
        String name = variableMap.getStringValue(StringUtils.fromString("name")).getValue();
        CaptureBindingPatternNode bindingPattern = createCaptureBindingPatternNode(createIdentifierToken(name));
        TypedBindingPatternNode bindingPatternNode = createTypedBindingPatternNode(typeBindingPattern, bindingPattern);
        String value = variableMap.get(StringUtils.fromString("value")).toString();
        ExpressionNode expressionNode = createBasicLiteral(typeSyntaxKind, value);
        return createModuleVariableDeclarationNode(null, null, NodeFactory.createEmptyNodeList(),
                bindingPatternNode, createToken(EQUAL_TOKEN), expressionNode, createToken(SEMICOLON_TOKEN));
    }

    private static ExpressionNode createBasicLiteral(SyntaxKind typeSyntaxKind, String value) {
        SyntaxKind nodeKind;
        SyntaxKind token;
        switch (typeSyntaxKind) {
            case SyntaxKind.NIL_TYPE_DESC:
                nodeKind = SyntaxKind.NIL_LITERAL;
                token = SyntaxKind.NIL_LITERAL;
                break;
            case SyntaxKind.BOOLEAN_TYPE_DESC:
                nodeKind = SyntaxKind.BOOLEAN_LITERAL;
                token = value.equals("true") ? SyntaxKind.TRUE_KEYWORD : SyntaxKind.FALSE_KEYWORD;
                break;
            case SyntaxKind.INT_TYPE_DESC:
            case SyntaxKind.FLOAT_TYPE_DESC:
            case SyntaxKind.DECIMAL_TYPE_DESC:
            case SyntaxKind.BYTE_TYPE_DESC:
                nodeKind = SyntaxKind.NUMERIC_LITERAL;
                token = DECIMAL_INTEGER_LITERAL_TOKEN;
                break;
            case SyntaxKind.STRING_TYPE_DESC:
                nodeKind = SyntaxKind.STRING_LITERAL;
                token = SyntaxKind.STRING_LITERAL_TOKEN;
                break;
            default:
                nodeKind = SyntaxKind.NIL_LITERAL;
                token = SyntaxKind.NIL_LITERAL;
        }
        LiteralValueToken valueToken = createLiteralValueToken(token, value, createEmptyMinutiaeList(),
                createEmptyMinutiaeList());
        return createBasicLiteralNode(nodeKind, valueToken);
    }

    private static SyntaxKind getBuiltinTypeSyntaxKind(String typeKeyword) {
        switch (typeKeyword) {
            case LexerTerminals.INT:
                return SyntaxKind.INT_TYPE_DESC;
            case LexerTerminals.FLOAT:
                return SyntaxKind.FLOAT_TYPE_DESC;
            case LexerTerminals.DECIMAL:
                return SyntaxKind.DECIMAL_TYPE_DESC;
            case LexerTerminals.BOOLEAN:
                return SyntaxKind.BOOLEAN_TYPE_DESC;
            case LexerTerminals.STRING:
                return SyntaxKind.STRING_TYPE_DESC;
            case LexerTerminals.BYTE:
                return SyntaxKind.BYTE_TYPE_DESC;
            case LexerTerminals.JSON:
                return SyntaxKind.JSON_TYPE_DESC;
            case LexerTerminals.HANDLE:
                return SyntaxKind.HANDLE_TYPE_DESC;
            case LexerTerminals.ANY:
                return SyntaxKind.ANY_TYPE_DESC;
            case LexerTerminals.ANYDATA:
                return SyntaxKind.ANYDATA_TYPE_DESC;
            case LexerTerminals.NEVER:
                return SyntaxKind.NEVER_TYPE_DESC;
            case LexerTerminals.VAR:
                return SyntaxKind.VAR_TYPE_DESC;
            case LexerTerminals.READONLY:
                return SyntaxKind.READONLY_TYPE_DESC;
            case LexerTerminals.NULL:
                return SyntaxKind.NIL_TYPE_DESC;
            default:
                assert false : typeKeyword + " is not a built-in type";
                return SyntaxKind.TYPE_REFERENCE;
        }
    }

}
