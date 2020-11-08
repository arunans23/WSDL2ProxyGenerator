package org.wso2.wsdl;

import com.predic8.wsdl.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WSDL2Java {
    final static WSDLParser parser = new WSDLParser();

    /**
     * Retrieves the WSDL from the given URL, parse and get the WSDL definitions
     *
     * @param wsdlUrl URL to retrieve WSDL
     * @return parsed wsdl definitions
     */
    public static Definitions parseWSDL(String wsdlUrl) {
        return parser.parse(wsdlUrl);
    }

    /**
     * Retrieves ServiceNames from the WSDL definitions
     *
     * @param definitions WSDL definitions to analyze
     * @return List of Service names found in the WSDL definition
     */
    public static List<String> getServiceNames(Definitions definitions) {
        List<String> serviceNames = new ArrayList<String>();
        for (Service service : definitions.getServices()) {
            serviceNames.add(service.getName());
        }
        return serviceNames;
    }

    /**
     * Retrieves the SoapActions listed from the Binding Operations
     *
     * @param definitions WSDL definitions to analyze
     * @return List of Operation names found in the WSDL definition
     */
    public static List<String> getBindingSoapActions(Definitions definitions) {
        Set<String> soapActions = new HashSet<String>();
        for (Binding binding : definitions.getBindings()) {
            for (BindingOperation bop : binding.getOperations()) {
                if (binding.getBinding() instanceof AbstractSOAPBinding) {
                    if (bop.getOperation() != null) {
                        String action = bop.getOperation().getSoapAction();
                        if (StringUtils.isNotEmpty(action)) {
                            soapActions.add(action);
                        }
                    }
                }
            }
        }
        return new ArrayList<String>(soapActions);
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
