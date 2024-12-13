package io.ballerina.object.syntax.tree.generator;

public class BallerinaCodeGenerator {

    public static final String BODY = "\n";

    public static String generateImportStatement(String org, String module) {
        return "import " + org + "/" + module + ";";
    }

    public static String generateListenerDeclaration(String type, String name, String port, String config) {
        return "listener " + type + " " + name + " = new (" + port + ", config = {" + config + "});";
    }

    public static String generateParameterDeclaration(String annotation, String type, String name) {
        return annotation + " " + type + " " + name;
    }

    public static String generateResourceFunction(String method, String path, String params, String returns,
                                                  String body) {
        return "resource function " + method + " " + path + "(" + params + ") returns " + returns + " {\n" +
               body + "\n}";
    }

    public static String generateServiceDefinition(String name, String listeners, String resources) {
        return "service " + name + " on " + listeners + " {\n" + resources + "\n}";
    }

    public static String generateMatchExpression(String expr, String matchCases) {
        return "match " + expr + " {\n" + matchCases + "\n}";
    }

    public static String generateMatchCase(String pattern, String body) {
        return pattern + " => {\n" + body + "\n}";
    }
}
