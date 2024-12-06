package io.ballerina.object.syntax.tree.generator;

public class BallerinaTemplates {

    public static final String IMPORT_TEMPLATE = "import {ORG}/{MODULE};";
    public static final String LISTENER_TEMPLATE = "listener {TYPE} {NAME} = new ({PORT}, config = {{CONFIG}});";
    public static final String PARAM_TEMPLATE = "string {NAME}";
    public static final String BODY_TEMPLATE = "\n";
    public static final String RESOURCE_TEMPLATE = "resource function {METHOD} {PATH}({PARAMS}) returns {RETURNS} " +
                                                   "{\n {BODY} \n}";
    public static final String SERVICE_TEMPLATE = "service {NAME} on {LISTENERS} {\n {RESOURCES} \n}";

}
