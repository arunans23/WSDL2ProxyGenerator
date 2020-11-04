package org.wso2.wsdl;

import org.apache.synapse.config.xml.AnonymousListMediator;
import org.apache.synapse.config.xml.SwitchCase;
import org.apache.synapse.core.axis2.ProxyService;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.apache.synapse.mediators.builtin.RespondMediator;
import org.apache.synapse.mediators.filters.SwitchMediator;
import org.apache.synapse.mediators.transform.PayloadFactoryMediator;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.jaxen.JaxenException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ProxyGenerator {
    private static final String PAYLOAD_FACTORY_OPERATION_FORMAT = "<format>Operation %s is not implemented</format>";
    private static final String PAYLOAD_FACTORY_DEFAULT_FORMAT = "<format>Operation not found</format>";
    private static final String XML_TYPE = "xml";
    private static final String SOAP_ACTION_PATH = "$trp:SOAPAction";
    private static final ArrayList<String> TRANSPORT_LIST = new ArrayList<String>(Arrays.asList("http", "https"));

    public static ProxyService generateProxyWithOperations(String proxyServiceName, List<String> operations, String wsdlUrl) {
        ProxyService proxyService = new ProxyService(proxyServiceName);
        proxyService.setTransports(TRANSPORT_LIST);

        //Create Target Sequence for the proxy
        SequenceMediator inlineTargetSeq = new SequenceMediator();
        inlineTargetSeq.addChild(generateSwitchMediator(operations));
        inlineTargetSeq.addChild(new RespondMediator());

        proxyService.setTargetInLineInSequence(inlineTargetSeq);
        try {
            proxyService.setWsdlURI(new URI(wsdlUrl));
        } catch (URISyntaxException e) {
            // TODO: Add proper logger here
            e.printStackTrace();
        }
        return proxyService;
    }

    public static SwitchMediator generateSwitchMediator(List<String> operations) {
        SwitchMediator switchMediator = new SwitchMediator();
        SynapseXPath xpath = null;
        try {
            xpath = new SynapseXPath(SOAP_ACTION_PATH);
        } catch (JaxenException e) {
            // TODO: Add proper logger here
            e.printStackTrace();
        }
        switchMediator.setSource(xpath);

        //Create the switch cases
        for (String operation : operations){
            SwitchCase switchCase = new SwitchCase();
            switchCase.setRegex(Pattern.compile("\"" + operation + "\""));

            AnonymousListMediator anonymousListMediator = new AnonymousListMediator();
            anonymousListMediator.addChild(generateEmptyPayloadFactory(operation));
            switchCase.setCaseMediator(anonymousListMediator);
            switchMediator.addCase(switchCase);
        }

        //create default case for Switch mediator
        SwitchCase defaultSwitchCase = new SwitchCase();
        PayloadFactoryMediator defaultPf = new PayloadFactoryMediator();
        defaultPf.setFormat(PAYLOAD_FACTORY_DEFAULT_FORMAT);
        defaultPf.setType(XML_TYPE);
        switchMediator.setDefaultCase(defaultSwitchCase);

        return switchMediator;
    }

    public static PayloadFactoryMediator generateEmptyPayloadFactory(String operationName) {
        PayloadFactoryMediator pf = new PayloadFactoryMediator();
        pf.setFormat(String.format(PAYLOAD_FACTORY_OPERATION_FORMAT, operationName));
        pf.setType(XML_TYPE);
        return pf;
    }
}
