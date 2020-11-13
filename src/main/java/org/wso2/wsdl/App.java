package org.wso2.wsdl;

import org.apache.synapse.config.xml.ProxyServiceSerializer;
import org.apache.synapse.core.axis2.ProxyService;

import javax.wsdl.Definition;
import java.util.List;

public class App {
    public static void main(String[] args)  {

        String wsdlUrl = "http://localhost:9000/services/SimpleStockQuoteService?wsdl";
        Definition wsdlDefinition = null;
        try {
            wsdlDefinition = WSDL2Java.readWSDL(wsdlUrl);
        } catch (Exception e) {
            System.out.println("Error while reading WSDL from the given location");
            e.printStackTrace();
        }
        List<String> serviceNames = WSDL2Java.getServiceNames(wsdlDefinition);
        List<String> soapActions = WSDL2Java.getBindingSoapActions(wsdlDefinition);

        int serviceNameSelected = 0; //TODO : Need to get this as in input

        ProxyService proxyService = ProxyGenerator.generateProxyWithOperations(serviceNames.get(serviceNameSelected), soapActions, wsdlUrl);
        System.out.println(ProxyServiceSerializer.serializeProxy(null, proxyService).toString());
    }
}
