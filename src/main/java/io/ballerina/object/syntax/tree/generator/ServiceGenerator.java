package io.ballerina.object.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.object.model.BallerinaPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.BODY_TEMPLATE;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.LISTENER_TEMPLATE;
import static io.ballerina.object.syntax.tree.generator.BallerinaTemplates.SERVICE_TEMPLATE;
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
            String resourceParams = generateResourceQueryParams(resource);
            String resourceReturns = generateResourceReturns(resource);
            String resourceBody = BODY_TEMPLATE;
            resourceBuilder.append(BallerinaTemplates.RESOURCE_TEMPLATE
                    .replace("{METHOD}", resource.method())
                    .replace("{PATH}", resource.path())
                    .replace("{PARAMS}", resourceParams)
                    .replace("{RETURNS}", resourceReturns)
                    .replace("{BODY}", resourceBody));
            resourceBuilder.append("\n");
        }
        return SERVICE_TEMPLATE.replace("{NAME}", service.basePath())
                .replace("{LISTENERS}", String.join(", ", listenerVars))
                .replace("{RESOURCES}", resourceBuilder.toString());
    }

    private String generateResourceReturns(BallerinaPackage.Resource resource) {
        return resource.returnType() == null ? ANY_OR_ERROR : resource.returnType();
    }

    private String generateResourceQueryParams(BallerinaPackage.Resource resource) {
        StringBuilder params = new StringBuilder();
        List<String> queryParams = resource.queryParams();
        if (queryParams.isEmpty()) {
            return "";
        }
        for (String parameter : queryParams) {
            params.append("string ").append(parameter).append(", ");
        }
        return params.substring(0, params.length() - 2);
    }

    private String generateListenerDeclaration(BallerinaPackage.Listener listener, List<String> listenerVars) {
        String port = listener.config().get("port");
        StringBuilder additionalConfig = new StringBuilder();
        for (Map.Entry<String, String> entry : listener.config().entrySet()) {
            if (!"port".equals(entry.getKey())) {
                additionalConfig.append(entry.getKey())
                        .append(" : \"").append(entry.getValue()).append("\", ");
            }
        }
        if (!additionalConfig.isEmpty()) {
            additionalConfig.setLength(additionalConfig.length() - 2);
        }
        String listenerVar = DEFAULT_EP + listenerCount;
        listenerVars.add(listenerVar);
        return LISTENER_TEMPLATE.replace("{TYPE}", listener.type())
                .replace("{NAME}", listenerVar)
                .replace("{PORT}", port)
                .replace("{CONFIG}", additionalConfig.toString());
    }
}
