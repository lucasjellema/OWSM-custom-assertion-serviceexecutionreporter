package nl.amis.wspolicy;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;

import javax.jms.JMSException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import javax.json.JsonValue;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import oracle.wsm.common.sdk.IContext;
import oracle.wsm.common.sdk.IMessageContext;
import oracle.wsm.common.sdk.IResult;
import oracle.wsm.common.sdk.Result;
import oracle.wsm.common.sdk.SOAPBindingMessageContext;
import oracle.wsm.common.sdk.WSMException;

import oracle.wsm.policy.model.IAssertion;
import oracle.wsm.policyengine.IExecutionContext;

import org.w3c.dom.Node;

public class SOASuiteServiceExecutionReporter extends CustomAssertion {

    ServiceReporterSettings settings = new ServiceReporterSettings();

    public SOASuiteServiceExecutionReporter() {
        super("[SOASuiteServiceExecutionReporter] ");
    }

    @Override
    public void init(IAssertion iAssertion, IExecutionContext iExecutionContext, IContext iContext) {
        // TODO Implement this method
        super.init(iAssertion, iExecutionContext, iContext);
        settings.setJmsDestination(super.getPolicyBindingProperty("JMSDestination"));
        settings.setJmsConnectionFactory(super.getPolicyBindingProperty("JMSConnectionFactory"));
        settings.setServiceAttribute(super.getPolicyBindingProperty("serviceAttribute"));
        settings.initializeMessageTypesMapFromJson(super.getPolicyBindingProperty("operationsMap"));

    }

    public void test() {
        System.out.println("Testing");

        String json =
            "{\n" + "    \"getFlightDetailsRequest\" : {\n" + "        \"operation\" : \"getFlightDetails\",\n" +
            "        \"oneWay\" : \"false\",\n" + "        \"request\" : {\n" +
            "            \"doReport\" : \"true\",\n" + "            \"payload\" : [\n" + "                {\n" +
            "                    \"name\" : \"carrierCode\",\n" +
            "                    \"xpath\" : \"/soap:Envelope/soap:Body/flig:getFlightDetailsRequest/flig:FlightCode/com:CarrierCode\",\n" +
            "                    \"namespaces\" : [\n" + "                        {\n" +
            "                            \"prefix\" : \"soap\",\n" +
            "                            \"namespace\" : \"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"flig\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/flightservice\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"com\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/common\"\n" +
            "                        }\n" + "                    ]\n" + "                },\n" + "                {\n" +
            "                    \"name\" : \"flightNumber\",\n" +
            "                    \"xpath\" : \"/soap:Envelope/soap:Body/flig:getFlightDetailsRequest/flig:FlightCode/com:FlightNumber\",\n" +
            "                    \"namespaces\" : [\n" + "                        {\n" +
            "                            \"prefix\" : \"soap\",\n" +
            "                            \"namespace\" : \"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"flig\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/flightservice\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"com\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/common\"\n" +
            "                        }\n" + "                    ]\n" + "                }\n" + "            ]\n" +
            "        },\n" + "        \"response\" : {\n" + "            \"doReport\" : \"true\",\n" +
            "            \"payload\" : [\n" + "                {\n" +
            "                    \"name\" : \"flightStatus\",\n" +
            "                    \"xpath\" : \"/soap:Envelope/soap:Body/flig:getFlightDetailsResponse/flig:FlightStatus\",\n" +
            "                    \"namespaces\" : [\n" + "                        {\n" +
            "                            \"prefix\" : \"soap\",\n" +
            "                            \"namespace\" : \"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"flig\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/flightservice\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"com\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/common\"\n" +
            "                        }\n" + "                    ]\n" + "                }\n" + "            ]\n" +
            "        }\n" + "    },\n" + "    \"retrievePassengerListForFlightRequest\" : {\n" +
            "        \"operation\" : \"retrievePassengerListForFlight\",\n" + "        \"oneWay\" : \"false\",\n" +
            "        \"request\" : {\n" + "            \"doReport\" : \"true\",\n" + "            \"payload\" : [\n" +
            "                {\n" + "                    \"name\" : \"carrierCode\",\n" +
            "                    \"xpath\" : \"/soap:Envelope/soap:Body/flig:retrievePassengerListForFlightRequest/flig:FlightCode/com:CarrierCode\",\n" +
            "                    \"namespaces\" : [\n" + "                        {\n" +
            "                            \"prefix\" : \"soap\",\n" +
            "                            \"namespace\" : \"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"flig\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/flightservice\"\n" +
            "                        },\n" + "                        {\n" +
            "                            \"prefix\" : \"com\",\n" +
            "                            \"namespace\" : \"com.flyinghigh/operations/common\"\n" +
            "                        }\n" + "                    ]\n" + "                }\n" + "            ]\n" +
            "        },\n" + "        \"response\" : {\n" + "            \"doReport\" : \"true\"\n" + "        }\n" +
            "    }\n" + "}";
        settings.initializeMessageTypesMapFromJson(json);
        System.out.println(settings.getMessageTypesMap());
        System.out.println(settings.getMessageTypesMap().get("getFlightDetailsRequest").getRequest().getPayload().get(0).getName());
        System.out.println(settings.getMessageTypesMap().get("getFlightDetailsRequest").getRequest().getPayload().get(0).getXpath());
        System.out.println(settings.getMessageTypesMap().get("retrievePassengerListForFlightRequest").getOperationName());
    }

    private boolean debug = true;

    @Override
    public IResult execute(IContext iContext) throws WSMException {
        System.out.println("Reporting for duty: SOASuiteServiceExecutionReporter");
        IResult result = new Result();

        String operationName = "";
        String operationAttribute = null;
        String ecid = "";
        String serviceId = "";
        Map<String, String> payloadValues = new HashMap<String, String>();
        String timestamp = "";


        serviceId = ((IMessageContext) iContext).getServiceID();
        ecid = ((IMessageContext) iContext).getGUID().toString();
        timestamp = (new Date()).getTime() + "";

        String messageType = "";
        messageType = super.getMessageType(iContext);
        Object flowId = ((IMessageContext) iContext).getInvocationStatus().getFlowID();
        // until we find the mapped operationName, let's use messageType as a surrogate
        operationName = messageType;
        MessageTypeConfiguration mtc = null;
        IMessageContext.STAGE stage = ((IMessageContext) iContext).getStage();
        SOAPBody mBody = null;
        SOAPBindingMessageContext smc = ((SOAPBindingMessageContext) iContext);
        try {
            SOAPBody aPBody = smc.getRequestMessage().getSOAPBody();
            mBody = aPBody;
        } catch (SOAPException e) {
        }

        if (debug) {
            if (stage == IMessageContext.STAGE.request) {
                mtc = settings.getMessageTypesMap().get(messageType);
                if (mtc != null) {
                    try {
                        operationName = mtc.getOperationName();
                        // payload can consist of zero, one or more elements
                        if (mtc.getRequest().getPayload() != null) {
                            for (PayloadElement pe : mtc.getRequest().getPayload()) {
                                String inputValue = "";
                                try {
                                    Node inputNode = getDataNode(mBody, pe.getNamespaces(), pe.getXpath());
                                    inputValue = inputNode.getTextContent();
                                } catch (Exception e) {
                                    System.out.println("exception in payload element " + pe.getName() + " - xpath " +
                                                       pe.getXpath() + " exception:" + e.getMessage());
                                }
                                payloadValues.put(pe.name, inputValue);
                            } // for all payload elements
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
            }

        }



        if (settings.doJMSReporting()) {
            System.out.println("Do Report to JMS "+settings.getJmsDestination());
            Map<String, String> message = new HashMap<String, String>();
            message.putAll(payloadValues);
            if (settings.getServiceAttribute() != null) {
                message.put("serviceAttribute", settings.getServiceAttribute());
            }
            message.put("service", serviceId);
            message.put("operation", operationName);
            if (mtc != null && mtc.getOperationAttribute() != null) {
                message.put("operationAttribute", mtc.getOperationAttribute());
            }
            message.put("stage", stage.toString());
            message.put("executionTimestamp", timestamp);
            message.put("ecid", ecid);
            try {
                new JMSPostman().publishMapToJMS(settings.getJmsConnectionFactory(), settings.getJmsDestination(),
                                                 message, "ecid:" + ecid);
            } catch (JMSException e) {
                System.out.println("Failed to publish to JMS: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("DO NOT REPORT to JMS");
        }

        if (stage == IMessageContext.STAGE.request) {
            System.out.println("Service Execution Report");
            System.out.println("========================");
            if (settings.getServiceAttribute() != null) {
                System.out.println("* service attribute: " + settings.getServiceAttribute());
            }
            System.out.println("* service: " + serviceId);
            System.out.println("* operation: " + operationName);
            if (mtc != null && mtc.getOperationAttribute() != null) {
                System.out.println("* operation attribute: " + mtc.getOperationAttribute());
            }
            System.out.println("* stage: " + stage);
            for (Map.Entry<String, String> entry : payloadValues.entrySet()) {
                System.out.println("* payload entry " + entry.getKey() + ":" + entry.getValue());
            }
            System.out.println("* timestamp: " + timestamp);
            System.out.println("* ecid: " + ecid);
            System.out.println("========================");
            System.out.println("");
            result.setStatus(IResult.SUCCEEDED);
            return result;
        }

        if (stage == IMessageContext.STAGE.response) {
            System.out.println("Service Execution Report");
            System.out.println("========================");
            System.out.println("* service: " + serviceId);
            System.out.println("* stage: " + stage);
            System.out.println("* timestamp: " + timestamp);
            System.out.println("* ecid: " + ecid);
            System.out.println("========================");
            System.out.println("");
            result.setStatus(IResult.SUCCEEDED);
            return result;
        }

        return result;
    }

    public static void main(String[] args) {
        SOASuiteServiceExecutionReporter sser = new SOASuiteServiceExecutionReporter();
        sser.test();


    }
}


class ServiceReporterSettings {
    private String jmsDestination = null;
    private String jmsConnectionFactory = null;
    private String serviceAttribute = null;

    public void setServiceAttribute(String serviceAttribute) {
        this.serviceAttribute = serviceAttribute;
    }

    public String getServiceAttribute() {
        return serviceAttribute;
    }
    private Map<String, MessageTypeConfiguration> messageTypesMap = new HashMap<String, MessageTypeConfiguration>();


    public Map<String, MessageTypeConfiguration> getMessageTypesMap() {
        return messageTypesMap;
    }

    public void initializeMessageTypesMapFromJson(String json) {
        try {
            JsonReader rdr = Json.createReader(new StringReader(json));
            System.out.println("reader");
            JsonObject obj = rdr.readObject();
            System.out.println("json obj" + obj);
            //            System.out.println("get object from operations map for " + messageType);
            for (Map.Entry<String, JsonValue> entry : obj.entrySet()) {
                MessageTypeConfiguration mtc = new MessageTypeConfiguration();
                String messageType = entry.getKey();
                messageTypesMap.put(messageType, mtc);


                JsonObject operationObj = obj.getJsonObject(messageType);
                System.out.println("operation object = " + operationObj);

                String operationName = operationObj.getString("operation");
                String operationAttribute = operationObj.getString("operationAttribute");
                mtc.setOperationAttribute(operationAttribute);
                System.out.println("Operation = " + operationName);
                mtc.setOperationName(operationName);
                JsonObject messageObj = operationObj.getJsonObject("request");

                // process definition for request|response|fault into MessageConfiguration
                MessageConfiguration mc = new MessageConfiguration();
                mtc.setRequest(mc);
                if (messageObj != null) {

                    String doReport = messageObj.getString("doReport");

                    JsonArray payload = messageObj.getJsonArray("payload");

                    System.out.println("payload array contains : " + payload.size() + " payloadelements");

                    for (JsonObject ple : payload.getValuesAs(JsonObject.class)) {
                        PayloadElement pe = new PayloadElement();
                        System.out.println("ple" + ple.toString());
                        mc.getPayload().add(pe);
                        String name = ple.getString("name");
                        String xpathStr = ple.getString("xpath");
                        pe.setName(name);
                        pe.setXpath(xpathStr);

                        JsonArray results = ple.getJsonArray("namespaces");
                        System.out.println("results array contains : " + results.size() + " namespaces");
                        for (JsonObject ns : results.getValuesAs(JsonObject.class)) {
                            System.out.println("add namespace: " + ns.getString("prefix") + ":" +
                                               ns.getString("namespace") + "  ;;");

                            pe.getNamespaces().put(ns.getString("prefix"), ns.getString("namespace"));
                        } //for
                    } //for


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }


    public void setJmsDestination(String jmsDestination) {
        this.jmsDestination = jmsDestination;
    }

    public String getJmsDestination() {
        return jmsDestination;
    }

    public void setJmsConnectionFactory(String jmsConnectionFactory) {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }

    public String getJmsConnectionFactory() {
        return jmsConnectionFactory;
    }

    boolean doJMSReporting() {
        Boolean doReport = jmsConnectionFactory != null && jmsConnectionFactory.length() > 1 && jmsDestination != null &&
               jmsDestination.length() > 1;
        System.out.println("Do JMS Reporting = "+doReport);
        return doReport;
    }
}

class MessageTypeConfiguration {
    String operationName;
    String operationAttribute;

    public void setOperationAttribute(String operationAttribute) {
        this.operationAttribute = operationAttribute;
    }

    public String getOperationAttribute() {
        return operationAttribute;
    }
    Boolean oneWay;
    MessageConfiguration request;
    MessageConfiguration response;
    MessageConfiguration fault;

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOneWay(Boolean oneWay) {
        this.oneWay = oneWay;
    }

    public Boolean getOneWay() {
        return oneWay;
    }

    public void setRequest(MessageConfiguration request) {
        this.request = request;
    }

    public MessageConfiguration getRequest() {
        return request;
    }

    public void setResponse(MessageConfiguration response) {
        this.response = response;
    }

    public MessageConfiguration getResponse() {
        return response;
    }

    public void setFault(MessageConfiguration fault) {
        this.fault = fault;
    }

    public MessageConfiguration getFault() {
        return fault;
    }

}
class MessageConfiguration {
    Boolean doReport;
    List<PayloadElement> payload = new ArrayList<PayloadElement>();

    public void setDoReport(Boolean doReport) {
        this.doReport = doReport;
    }

    public Boolean getDoReport() {
        return doReport;
    }

    public void setPayload(List<PayloadElement> payload) {
        this.payload = payload;
    }

    public List<PayloadElement> getPayload() {
        return payload;
    }
}

class PayloadElement {
    String name;
    String xpath;
    HashMap<String, String> namespaces = new HashMap<String, String>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getXpath() {
        return xpath;
    }

    public void setNamespaces(HashMap<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public HashMap<String, String> getNamespaces() {
        return namespaces;
    }

}


