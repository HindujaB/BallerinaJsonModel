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

    public record Variable(String name, String type, Object value) {

    }

    public record Service(String basePath, List<Listener> listeners, List<Resource> resources) {

    }

    public record Listener(String type, Map<String, String> config) {

    }

    public record Resource(String resourceName, String method, String path, List<Parameter> parameters,
                           List<BodyStatement> body, List<String> queryParams, String returnType) {
    }

    public record Parameter(String name, String type) {

    }

    public record ReturnType(String type, List<String> types) {

    }

    public record BodyStatement(String statementType, Value value) {

    }

    public record Value(String type, Map<String, Object> content) {

    }
}
