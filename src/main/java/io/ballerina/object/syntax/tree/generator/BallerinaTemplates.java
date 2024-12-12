package io.ballerina.object.syntax.tree.generator;

public class BallerinaTemplates {

    public static final String IMPORT = "import {ORG}/{MODULE};";
    public static final String LISTENER = "listener {TYPE} {NAME} = new ({PORT}, config = {{CONFIG}});";
    public static final String PARAM = "{ANNOTATION} {TYPE} {NAME}";
    public static final String BODY = "\n";
    public static final String RESOURCE = """
            resource function {METHOD} {PATH}({PARAMS}) returns {RETURNS} {
             {BODY}\s
            }""";
    public static final String SERVICE = "service {NAME} on {LISTENERS} {\n {RESOURCES} \n}";
    public static final String MATCH = "match {EXPR} {\n  {MATCH_CASES}\n}";
    public static final String MATCH_CASE = "{PATTERN} => {\n{BODY}\n}";

}
