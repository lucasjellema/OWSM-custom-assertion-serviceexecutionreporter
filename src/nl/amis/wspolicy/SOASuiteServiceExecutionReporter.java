package nl.amis.wspolicy;

import java.io.StringReader;

import java.util.Date;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import oracle.wsm.common.sdk.IContext;
import oracle.wsm.common.sdk.IMessageContext;
import oracle.wsm.common.sdk.IResult;
import oracle.wsm.common.sdk.Result;
import oracle.wsm.common.sdk.SOAPBindingMessageContext;
import oracle.wsm.common.sdk.WSMException;

import org.w3c.dom.Node;

public class SOASuiteServiceExecutionReporter extends CustomAssertion {
    public SOASuiteServiceExecutionReporter() {
        super("[SOASuiteServiceExecutionReporter] ");
    }

    private boolean debug = true;

    @Override
    public IResult execute(IContext iContext) throws WSMException {
        System.out.println("Reporting for duty: SOASuiteServiceExecutionReporter");
        IResult result = new Result();

        String operationName = "";
        String ecid = "";
        String serviceId = "";
        String payloadValue = "";
        String timestamp = "";

        IMessageContext.STAGE stage = ((IMessageContext) iContext).getStage();
        SOAPBody mBody = null;
        SOAPBindingMessageContext smc = ((SOAPBindingMessageContext) iContext);
        try {
            SOAPHeader header = smc.getRequestMessage().getSOAPHeader();
            System.out.println("header " + header);
            SOAPBody aPBody = smc.getRequestMessage().getSOAPBody();
            mBody = aPBody;
            System.out.println("body " + aPBody);
        } catch (SOAPException e) {
        }

        String messageType = "";
        if (debug) {
            System.out.println("stage = " + stage);
            System.out.println(mTag + "Nothing to process in this stage. Returning");
            System.out.println(mTag + DEBUG_END);

            System.out.println("service Url" + ((IMessageContext) iContext).getServiceURL());
            serviceId = ((IMessageContext) iContext).getServiceID();
            System.out.println("service id" + serviceId);
            Object flowId = ((IMessageContext) iContext).getInvocationStatus().getFlowID();
            System.out.println("Get Flow Id " + flowId);
            ecid = ((IMessageContext) iContext).getGUID().toString();
            System.out.println("GUID or ECID = " + ecid);
            System.out.println("Property JMS destination /" + super.getPolicyBindingProperty("JMSDestination"));
            //            System.out.println("Property payload " + super.getPolicyBindingProperty("payloadElementXpath"));
            //            System.out.println("Property messageTypeOperationsMapping " +
            //                               super.getPolicyBindingProperty("messageTypeOperationsMapping"));
            timestamp = (new Date()).getTime() + "";
            System.out.println("Timestamp " + timestamp);
            //            String payloadXpath = super.getPolicyBindingProperty("payloadElementXpath");
            String operationsMap = super.getPolicyBindingProperty("operationsMap");
            // find out message type

            final HashMap<String, String> namespaces = new HashMap<String, String>();
            namespaces.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            System.out.println("start investigating message nodes");
            try {
                Node messageNode = getDataNode(mBody, namespaces, "/soap:Envelope/soap:Body/*[1]");
                messageType = messageNode.getLocalName();
                System.out.println("Message Type = " + messageType);
                System.out.println("messagnode level 2  " + messageNode.getLocalName() + "--" +
                                   messageNode.getNodeName() + " pref" + messageNode.getPrefix());
                //                messageNode = getDataNode(mBody, namespaces, "//soap:Body/*[1]");
                //                System.out.println("messagnode level 3 " + messageNode.getLocalName() + "--" +
                //                                   messageNode.getNodeName() + " pref" + messageNode.getPrefix());
            } catch (Exception e) {
                System.out.println("exception in bepaling messageNode " + e.getMessage());
                e.printStackTrace();
            }

            if (operationsMap != null && operationsMap.length() > 1) {

                System.out.println("start look at operationsMap exploraration: " + operationsMap);

                try {
                    JsonReader rdr = Json.createReader(new StringReader(operationsMap));
                    System.out.println("reader");
                    JsonObject obj = rdr.readObject();
                    System.out.println("json obj" + obj);
                    System.out.println("get object from operations map for " + messageType);
                    JsonObject operationObj = obj.getJsonObject(messageType);
                    System.out.println("operation object = " + operationObj);

                    operationName = operationObj.getString("operation");

                    System.out.println("Operation = " + operationName);
                    String xpathStr = operationObj.getString("xpath");
                    System.out.println("from json - xpathstring for operation " + xpathStr);


                    JsonArray results = operationObj.getJsonArray("namespaces");
                    System.out.println("results array contains : " + results.size() + " namespaces");
                    for (JsonObject ns : results.getValuesAs(JsonObject.class)) {
                        System.out.println("add namespace: " + ns.getString("prefix") + ":" +
                                           ns.getString("namespace") + "  ;;");

                        namespaces.put(ns.getString("prefix"), ns.getString("namespace"));
                    } //for

                    System.out.println("get data node for " + xpathStr + " - in " + mBody.toString());

                    Node inputNode = getDataNode(mBody, namespaces, xpathStr);
                    System.out.println("result from getDataNode " + inputNode.toString());
                    String inputValue = inputNode.getTextContent();
                    System.out.println("READ FROM REQUEST - payload value " + inputValue);
                    payloadValue = inputValue;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }


        }


        System.out.println("Service Execution Report");
        System.out.println("========================");
        System.out.println("* service: " + serviceId);
        System.out.println("* operation: " + operationName);
        System.out.println("* stage: "+stage);
        System.out.println("* payload: " + payloadValue);
        System.out.println("* timestamp: " + timestamp);
        System.out.println("* ecid: " + ecid);
        System.out.println("========================");
        System.out.println("");

        if (stage != IMessageContext.STAGE.request) {
            result.setStatus(IResult.SUCCEEDED);
            return result;
        }

        if (stage != IMessageContext.STAGE.response) {
            result.setStatus(IResult.SUCCEEDED);
            if (debug) {
                System.out.println("stage = " + stage);
                System.out.println(mTag + "Nothing to process in this stage. Returning");
                System.out.println(mTag + DEBUG_END);
            }
            return result;
        }


        return result;
    }
}
