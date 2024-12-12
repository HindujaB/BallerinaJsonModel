package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.object.model.BallerinaPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.BODY;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.LISTENER;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.MATCH;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.MATCH_CASE;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.SERVICE;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.ANY_OR_ERROR;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.DEFAULT_EP;

public class ServiceGenerator {

    private int listenerCount = 0;

    public void generateService(BallerinaPackage.Service service, List<ModuleMemberDeclarationNode> moduleMembers) {
        List<BallerinaPackage.Listener> listeners = service.listeners();
        List<String> listenerVars = new ArrayList<>();
        for (BallerinaPackage.Listener listener : listeners) {
            String listenerDeclaration = generateListenerDeclaration(listener, listenerVars);
            moduleMembers.add(NodeParser.parseModuleMemberDeclaration(listenerDeclaration));
            listenerCount++;
        }
        String serviceDeclaration = generateServiceDeclaration(service, listenerVars);
        moduleMembers.add(NodeParser.parseModuleMemberDeclaration(serviceDeclaration));
    }

    private String generateServiceDeclaration(BallerinaPackage.Service service, List<String> listenerVars) {
        StringBuilder resourceBuilder = new StringBuilder();
        for (BallerinaPackage.Resource resource : service.resources()) {
            String resourceParams = generateResourceParams(resource);
            String resourceReturns = generateResourceReturns(resource);
            String resourceBody = generateResourceBody(resource);
            resourceBuilder.append(BallerinaTemplates.RESOURCE.replace("{METHOD}", resource.method())
                    .replace("{PATH}", resource.path()).replace("{PARAMS}", resourceParams)
                    .replace("{RETURNS}", resourceReturns).replace("{BODY}", resourceBody));
            resourceBuilder.append("\n");
        }
        return SERVICE.replace("{NAME}", service.basePath()).replace("{LISTENERS}",
                String.join(", ", listenerVars)).replace("{RESOURCES}", resourceBuilder.toString());
    }

    private String generateResourceBody(BallerinaPackage.Resource resource) {
        StringBuilder body = new StringBuilder();
        generateStatements(resource.body(), body);
        return body.toString();
    }

    private void generateStatements(List<BallerinaPackage.Statement> statements, StringBuilder body) {
        for (BallerinaPackage.Statement statement : statements) {
            switch (statement.statementType()) {
                case "Statement" -> {
                    BallerinaPackage.BallerinaStatement ballerinaStatement = (BallerinaPackage.BallerinaStatement) statement;
                    body.append(ballerinaStatement.statement()).append("\n");
                }
                case "Match" -> {
                    BallerinaPackage.MatchStatement matchStatement = (BallerinaPackage.MatchStatement) statement;
                    body.append(MATCH
                            .replace("{EXPR}", matchStatement.expression())
                            .replace("{MATCH_CASES}", getMatchCases(matchStatement.patterns())));
                    body.append("\n");
                }
                case "Call" -> {
                    BallerinaPackage.CallStatement callStatement = (BallerinaPackage.CallStatement) statement;
                    body.append(callStatement.functionName()).append("(")
                            .append(String.join(", ", callStatement.parameters())).append(");\n");
                }
                default -> body.append(BODY);
            }
        }
    }

    private String getMatchCases(List<BallerinaPackage.MatchPattern> patterns) {
        StringBuilder body = new StringBuilder();
        for (BallerinaPackage.MatchPattern pattern : patterns) {
            StringBuilder matchBody = new StringBuilder();
            generateStatements(pattern.statements(), matchBody);
            body.append(MATCH_CASE
                    .replace("{PATTERN}", pattern.clause())
                    .replace("{BODY}", matchBody.toString()));
        }
        return body.toString();
    }

    private String generateResourceReturns(BallerinaPackage.Resource resource) {
        return resource.returnType() == null ? ANY_OR_ERROR : resource.returnType();
    }

    private String generateResourceParams(BallerinaPackage.Resource resource) {
        List<BallerinaPackage.Parameter> parameters = resource.parameters();
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            BallerinaPackage.Parameter parameter = parameters.get(i);
            params.append(BallerinaTemplates.PARAM.replace("{ANNOTATION}", parameter.annotations()).replace("{TYPE}", parameter.type()).replace("{NAME}", parameter.name()));
            if (i < parameters.size() - 1) {
                params.append(", ");
            }
        }
        List<String> queryParams = resource.queryParams();
        for (String parameter : queryParams) {
            params.append(BallerinaTemplates.PARAM.replace("{ANNOTATION}", "").replace("{TYPE}", "string").replace("{NAME}", parameter)).append(", ");
        }
        return queryParams.isEmpty() ? params.toString() : params.substring(0, params.length() - 2);
    }

    private String generateListenerDeclaration(BallerinaPackage.Listener listener, List<String> listenerVars) {
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
        String listenerVar = DEFAULT_EP + listenerCount;
        listenerVars.add(listenerVar);
        return LISTENER.replace("{TYPE}", listener.type())
                .replace("{NAME}", listenerVar)
                .replace("{PORT}", port)
                .replace("{CONFIG}", additionalConfig.toString());
    }
}
