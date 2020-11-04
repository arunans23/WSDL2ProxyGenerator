package org.wso2.wsdl;

import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.util.XMLUtils;
import org.apache.synapse.core.axis2.ProxyService;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class WSDLAxis2Parser {
    public static void main(String[] args) throws MalformedURLException, IOException, ParserConfigurationException, SAXException, WSDLException {
        URL wsdlURL = new URL("http://localhost:9000/services/SimpleStockQuoteService?wsdl");
        InputStream in = wsdlURL.openConnection().getInputStream();
        Document doc = XMLUtils.newDocument(in);
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition wsdlDefinition = reader.readWSDL(getBaseURI(wsdlURL
                .toString()), doc);

//        WSDL11ToAxisServiceBuilder wsdlBuilder = WSDL11ToAxisServiceBuilder(defs, "PayPalService", "")
        Map services = wsdlDefinition.getServices();
        Iterator<QName> iterator = services.keySet().iterator();
        while (iterator.hasNext()){
            WSDL11ToAxisServiceBuilder serviceBuilder = new WSDL11ToAxisServiceBuilder(
                    wsdlDefinition, iterator.next(), "SimpleStockQuoteServiceHttpsSoap11Endpoint");
            serviceBuilder.populateService();
            System.out.println("");
        }

        ProxyService proxyService = new ProxyService("StockQuoteProxy");
        proxyService.setTransports(new ArrayList<String>());
        

    }

    private static String getBaseURI(String currentURI) {
        try {
            File file = new File(currentURI);
            if (file.exists()) {
                return file.getCanonicalFile().getParentFile().toURI()
                        .toString();
            }
            String uriFragment = currentURI.substring(0, currentURI
                    .lastIndexOf("/"));
            return uriFragment + (uriFragment.endsWith("/") ? "" : "/");
        } catch (IOException e) {
            return null;
        }
    }
}
