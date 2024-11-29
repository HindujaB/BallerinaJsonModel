package io.ballerina.json.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.json.model.BallerinaPackage;

import java.util.List;
import java.util.Map;

public class ServiceGenerator {

    public ModuleMemberDeclarationNode generateService(BallerinaPackage.Service service) {
        List<BallerinaPackage.Listener> listeners = service.getListeners();
        for (BallerinaPackage.Listener listener : listeners) {
            String type = listener.getType();
            if (!type.startsWith("http")) {
                continue;
            }
            if (listener.getConfig() != null) {
                Map<String, String> config = listener.getConfig();
                Object port = config.get("port");

            }
        }
        return Utils.getServiceDeclarationNode(service);
    }
}
