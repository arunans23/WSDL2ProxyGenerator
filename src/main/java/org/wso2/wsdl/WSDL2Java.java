package org.wso2.wsdl;

import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.net.URL;
import java.util.*;

public class WSDL2Java {

    /**
     * Retrieves the WSDL from the given URL, parse and get the WSDL definitions
     *
     * @param wsdlUrl URL to retrieve WSDL
     * @return parsed wsdl definition
     */
    public static Definition readWSDL(String wsdlUrl)
            throws IOException, ParserConfigurationException, SAXException, WSDLException {
        URL wsdlURL = new URL(wsdlUrl);
        InputStream in = wsdlURL.openConnection().getInputStream();
        Document doc = XMLUtils.newDocument(in);
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", true);
        return reader.readWSDL(getBaseURI(wsdlURL.toString()), doc);
    }

    /**
     * Retrieves ServiceNames from the WSDL definitions
     *
     * @param definition WSDL definitions to analyze
     * @return List of Service names found in the WSDL definition
     */
    public static List<String> getServiceNames(Definition definition) {
        Map map = definition.getAllServices();
        List<QName> serviceQnames = new ArrayList<QName>(map.keySet());
        List<String> serviceNames = new ArrayList<String>();
        for (QName qName : serviceQnames) {
            serviceNames.add(qName.getLocalPart());
        }
        return serviceNames;
    }

    /**
     * Retrieves the SoapActions listed from the Binding Operations
     *
     * @param definitions WSDL definitions to analyze
     * @return List of Operation names found in the WSDL definition
     */
    public static List<String> getBindingSoapActions(Definition definitions) {
        Set<String> soapActions = new HashSet<String>();
        if (definitions.getAllBindings() != null) {
            for (Object bindingObj : definitions.getAllBindings().values()) {
                if (bindingObj instanceof  BindingImpl) {
                    BindingImpl binding = (BindingImpl) bindingObj;
                    for (Object bindingOperation : binding.getBindingOperations()) {
                        if (bindingOperation instanceof BindingOperationImpl) {
                            BindingOperationImpl bindingOp = (BindingOperationImpl) bindingOperation;
                            List extElements = bindingOp.getExtensibilityElements();
                            for (Object operationImpl : extElements) {
                                if (operationImpl instanceof SOAPOperationImpl) {
                                    SOAPOperationImpl soapOperation = (SOAPOperationImpl) operationImpl;
                                    String soapAction = soapOperation.getSoapActionURI();
                                    if (StringUtils.isNotEmpty(soapAction)) {
                                        soapActions.add(soapAction);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<String>(soapActions);
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
