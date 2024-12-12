package io.ballerina.object.model;

import java.util.List;
import java.util.Map;

public record BallerinaPackage(DefaultPackage defaultPackage, List<Module> modules) {

    public record DefaultPackage(String org, String name, String version) {

    }

    public record Module(String moduleName, List<Import> imports, List<Variable> variables, List<Service> services) {

    }

    public record Import(String org, String module) {

    }

    public record Variable(String name, String type, String expression) {

    }

    public record Service(String basePath, List<Listener> listeners, List<Resource> resources) {

    }

    public record Listener(String type, Map<String, String> config) {

    }

    public record Resource(String resourceName, String method, String path, List<Parameter> parameters,
                           List<Statement> body, List<String> queryParams, String returnType) {

    }

    public record Function(String functionName, List<Parameter> parameters, List<Statement> body, String returnType) {

    }

    public record Parameter(String name, String type, String annotations) {

    }

    public interface Statement {

        String statementType();
    }

    public record MatchStatement(String expression, List<MatchPattern> patterns) implements Statement {

        @Override
        public String statementType() {
            return "Match";
        }
    }

    public record MatchPattern(String clause, List<Statement> statements) {

    }

    public record CallStatement(String functionName, List<String> parameters) implements Statement {

        @Override
        public String statementType() {
            return "Call";
        }
    }

    public record BallerinaStatement(String statement) implements Statement {

        @Override
        public String statementType() {
            return "Statement";
        }
    }

}
