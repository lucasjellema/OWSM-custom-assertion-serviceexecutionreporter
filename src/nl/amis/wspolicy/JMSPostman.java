package nl.amis.wspolicy;

import java.util.Iterator;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import weblogic.jms.extensions.WLDestination;

public class JMSPostman {
    public JMSPostman() {
        super();
    }

    private QueueConnection qcon;
    private QueueSession qsession;
    private TopicConnection tcon;
    private TopicSession tsession;

    public void publishMapToJMS(String jmsConnectionFactory, String jmsDestination, Map<String, String> message,
                                String correlationId) throws javax.jms.JMSException {
        //JMS publication
        try {
            Context jndiContext = null;
            jndiContext = new InitialContext();

            ConnectionFactory connectionFactory = null;
            Destination dest = null;
            connectionFactory = (ConnectionFactory) jndiContext.lookup(jmsConnectionFactory);
            System.out.println("cf");
            
            dest = (Destination) jndiContext.lookup(jmsDestination);
            System.out.println("Destination "+ dest.getClass().getCanonicalName());
            Session jmsSession=null;
            Boolean isQueue=true;
            if (dest instanceof WLDestination) {
                System.out.println("WLDestination");
                isQueue = ((WLDestination)dest).isQueue();
                System.out.println("is queue? "+isQueue);
            } else {
                if (jmsDestination.toLowerCase().contains("topic")){
                    isQueue=false;
                    System.out.println("does not contain topic in name so must be queue");
                }
            }
            
            if (isQueue) {
                System.out.println("Queue");
                qcon = ((QueueConnectionFactory) connectionFactory).createQueueConnection();
                jmsSession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                qcon.start();
                System.out.println("qcon started");
            } else if (dest instanceof Topic) {
                System.out.println("Topic");
                tcon = ((TopicConnectionFactory) connectionFactory).createTopicConnection();
                jmsSession = tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                tcon.start();
                System.out.println("topic started");
            }
            MapMessage mm=jmsSession.createMapMessage();
            // copy the contents from the message (a Map object) to the MapMessage mm
            Iterator it = message.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                mm.setString((String) pair.getKey(), (String) pair.getValue());
            }
            mm.setJMSCorrelationID(correlationId);
            mm.setJMSType("ServiceExecutionReport");
            System.out.println("MapMsg prepared");
            if (isQueue) {
                System.out.println("Queue");
                MessageProducer producer = ((QueueSession)jmsSession).createProducer((Queue)dest);
                System.out.println("producer");
                producer.send(mm);
                System.out.println("sent");
            } else {
                System.out.println("topic");
                TopicSession topicSession = null;
                        Topic topic = null;
                        TopicPublisher topicPublisher = ((TopicSession)jmsSession).createPublisher((Topic)dest);
                System.out.println("publisher");
                topicPublisher.publish(mm);
                System.out.println("publish");
            }
            
            
        } catch (NamingException e) {
            System.out.println("Could not create JNDI API " + "context: " + e.toString());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("JNDI API lookup failed: " + e.toString());
            e.printStackTrace();
        } finally {
            if (qcon != null) {
                try {
                    qcon.close();
                } catch (JMSException e) {
                }
            }
        }
    
    }
}