package org.wso2.wsdl;

import com.predic8.wsdl.Definitions;
import org.apache.synapse.config.xml.ProxyServiceSerializer;
import org.apache.synapse.core.axis2.ProxyService;

import java.util.List;

public class App {
    public static void main(String[] args) {

        String wsdlUrl = "file:///Users/arunan/wso2/wso2-axis2/modules/jaxws-integration/test-resources/wsdl/HeadersHandler.wsdl";

        Definitions definitions = WSDL2Java.parseWSDL(wsdlUrl);
        List<String> serviceNames = WSDL2Java.getServiceNames(definitions);
        List<String> soapActions = WSDL2Java.getBindingSoapActions(definitions);

        int serviceNameSelected = 0; //TODO : Need to get this as in input

        ProxyService proxyService = ProxyGenerator.generateProxyWithOperations(serviceNames.get(serviceNameSelected), soapActions, wsdlUrl);
        System.out.println(ProxyServiceSerializer.serializeProxy(null, proxyService).toString());
    }
}
