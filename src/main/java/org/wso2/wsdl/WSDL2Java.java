package org.wso2.wsdl;

import com.predic8.wsdl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WSDL2Java {
    final static WSDLParser parser = new WSDLParser();

    public static Definitions parseWSDL(String wsdlUrl) {
        return parser.parse(wsdlUrl);
    }

    public static Map<String, List<String>> getOperationsMap (String wsdlUrl) {
        Definitions wsdlDefinitions = parseWSDL(wsdlUrl);
        return null;
    }

    public static List<String> getServiceNames(Definitions definitions) {
        List<String> serviceNames = new ArrayList<String>();
        for (Service service : definitions.getServices()) {
            serviceNames.add(service.getName());
        }
        return serviceNames;
    }

    public static List<String> getBindingSoapActions(Definitions definitions) {
        List<String> soapActions = new ArrayList<String>();
        for (Binding binding : definitions.getBindings()) {
            for (BindingOperation bop : binding.getOperations()) {
                if (binding.getBinding() instanceof AbstractSOAPBinding) {
                    String action = bop.getOperation().getSoapAction();
                    if (action != null && !"".equals(action)) {
                        soapActions.add(action);
                    }
                }
            }
        }
        return soapActions;
    }

    public static List<String> getPortTypes(Definitions definitions) {
        List<String> portTypeNames = new ArrayList<String>();
        for (PortType portType : definitions.getPortTypes()) {
            portTypeNames.add(portType.getName());
        }
        return portTypeNames;
    }

    public static List<String> getOperations(Definitions definitions, String portType) {
        List<String> operationNames = new ArrayList<String>();
        for (Operation operation : definitions.getPortType(portType).getOperations()) {
            operationNames.add(operation.getName());
        }
        return operationNames;
    }
}
