package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.object.model.BallerinaPackage;

import java.util.List;

import static io.ballerina.object.syntax.tree.generator.BallerinaCodeGenerator.BODY;
import static io.ballerina.object.syntax.tree.generator.ModelConstants.ANY_OR_ERROR;

public class ServiceGenerator {

    public void generateService(BallerinaPackage.Service service, List<ModuleMemberDeclarationNode> moduleMembers) {
        List<String> listeners = service.listenerRefs();
        String serviceDeclaration = generateServiceDeclaration(service, listeners);
        moduleMembers.add(NodeParser.parseModuleMemberDeclaration(serviceDeclaration));
    }

    private String generateServiceDeclaration(BallerinaPackage.Service service, List<String> listenerVars) {
        StringBuilder resourceBuilder = new StringBuilder();
        for (BallerinaPackage.Resource resource : service.resources()) {
            String resourceParams = generateResourceParams(resource);
            String resourceReturns = generateResourceReturns(resource);
            String resourceBody = generateResourceBody(resource);
            resourceBuilder.append(BallerinaCodeGenerator.generateResourceFunction(resource.method(), resource.path(),
                    resourceParams, resourceReturns, resourceBody));
            resourceBuilder.append("\n");
        }
        return BallerinaCodeGenerator.generateServiceDefinition(service.basePath(), String.join(", ",
                listenerVars), resourceBuilder.toString());
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
                    BallerinaPackage.BallerinaStatement ballerinaStatement = (BallerinaPackage.BallerinaStatement)
                            statement;
                    body.append(ballerinaStatement.statement()).append("\n");
                }
                case "Match" -> {
                    BallerinaPackage.MatchStatement matchStatement = (BallerinaPackage.MatchStatement) statement;
                    body.append(BallerinaCodeGenerator.generateMatchExpression(matchStatement.expression(),
                            getMatchCases(matchStatement.patterns())));
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
            body.append(BallerinaCodeGenerator.generateMatchCase(pattern.clause(), matchBody.toString()));
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
            params.append(BallerinaCodeGenerator.generateParameterDeclaration(parameter.annotations(),
                    parameter.type(), parameter.name()));
            if (i < parameters.size() - 1) {
                params.append(", ");
            }
        }
        List<String> queryParams = resource.queryParams();
        for (String parameter : queryParams) {
            params.append(BallerinaCodeGenerator.generateParameterDeclaration("", "string",
                    parameter)).append(", ");
        }
        return queryParams.isEmpty() ? params.toString() : params.substring(0, params.length() - 2);
    }

}
