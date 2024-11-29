package io.ballerina.json.model;

import java.util.List;
import java.util.Map;

public class BallerinaPackage {
    private DefaultPackage defaultPackage;
    private List<Module> modules;

    public DefaultPackage getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage(DefaultPackage defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public static class DefaultPackage {
        private String org;
        private String name;
        private String version;

        public String getOrg() {
            return org;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }
    }

    public static class Module {
        private String moduleName;
        private List<Import> imports;
        private List<Variable> variables;
        private List<Service> services;

        public String getModuleName() {
            return moduleName;
        }

        public List<Import> getImports() {
            return imports;
        }

        public List<Variable> getVariables() {
            return variables;
        }

        public List<Service> getServices() {
            return services;
        }
    }

    public static class Import {
        private String org;
        private String module;

        public String getOrg() {
            return org;
        }

        public String getModule() {
            return module;
        }
    }

    public static class Variable {
        private String name;
        private String type;
        private Object value; // Use Object for flexibility (e.g., int, String, etc.)

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(Object i) {
            this.value = i;
        }
    }

    public static class Service {
        private String basePath;
        private List<Listener> listeners;
        private List<Resource> resources;

        public List<Listener> getListeners() {
            return listeners;
        }

        public String getBasePath() {
            return basePath;
        }

        public List<Resource> getResources() {
            return resources;
        }
    }

    public static class Listener {
        private String type;
        private Map<String, String> config; // Configurations are key-value pairs

        public String getType() {
            return type;
        }

        public Map<String, String> getConfig() {
            return config;
        }
    }

    public static class Resource {
        private String resourceName;
        private String method;
        private String path;
        private List<Parameter> parameters;
        private ReturnType returnType;
        private List<BodyStatement> body;

        
    }

    public static class Parameter {
        private String name;
        private String type;
    }

    public static class ReturnType {
        private String type;
        private List<String> types; // For union types

        
    }

    public static class BodyStatement {
        private String statementType; // e.g., "return"
        private Value value;

        
    }

    public static class Value {
        private String type; // e.g., "json"
        private Map<String, Object> content; // For storing JSON-like structures

        
    }
}
