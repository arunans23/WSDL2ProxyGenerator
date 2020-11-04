package org.wso2.wsdl;

import com.predic8.wsdl.Definitions;
import org.apache.synapse.config.xml.ProxyServiceSerializer;
import org.apache.synapse.core.axis2.ProxyService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
//        String stockQuote = "StockQuoteProxy";
//        List<String> operationNames = new ArrayList<String>();
//        operationNames.add("urn:getQuote");
//        operationNames.add("urn:marketActivity");

        String wsdlUrl = "file:///Users/arunan/mytmp/SimpleStockQuoteService.xml";

        Definitions definitions = WSDL2Java.parseWSDL(wsdlUrl);
        List<String> serviceNames = WSDL2Java.getServiceNames(definitions);
        List<String> soapActions = WSDL2Java.getBindingSoapActions(definitions);

        ProxyService proxyService = ProxyGenerator.generateProxyWithOperations(serviceNames.get(0), soapActions, wsdlUrl);
        System.out.println(ProxyServiceSerializer.serializeProxy(null, proxyService).toString());
//        ProxyServiceSerializer.serializeProxy(null, proxyService).toString();
    }
}
