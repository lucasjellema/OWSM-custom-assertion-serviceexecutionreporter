package nl.amis.wspolicy;

import java.util.Iterator;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSPostman {
    public JMSPostman() {
        super();
    }

    private QueueConnection qcon;
    private QueueSession qsession;

    public void publishMapToJMS(String jmsConnectionFactory, String jmsDestination,
                                 Map<String, String> message) throws javax.jms.JMSException {
        //JMS publication
        try {
            Context jndiContext = null;
            try {
                jndiContext = new InitialContext();
            } catch (NamingException e) {
                System.out.println("Could not create JNDI API " + "context: " + e.toString());
                System.exit(1);
            }
            /*
    * Look up connection factory and destination. If either
    * does not exist, exit. If you look up a
    * TopicConnectionFactory or a QueueConnectionFactory,
    * program behavior is the same.
    */
            QueueConnectionFactory connectionFactory = null;
            Destination dest = null;
            try {
                connectionFactory = (QueueConnectionFactory) jndiContext.lookup(jmsConnectionFactory);
                dest = (Destination) jndiContext.lookup(jmsDestination);
            } catch (Exception e) {
                System.out.println("JNDI API lookup failed: " + e.toString());
                e.printStackTrace();
            }

            // javax.jms.Connection connection = connectionFactory.createConnection();
            qcon = connectionFactory.createQueueConnection();
            qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            //qsender = qsession.createSender((javax.jms.Queue) dest);
            qcon.start();
            MapMessage mm = qsession.createMapMessage();
            
            // copy the contents from the message (a Map object) to the MapMessage mm
            Iterator it = message.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                mm.setString((String) pair.getKey(), (String) pair.getValue());
            }

            MessageProducer producer = qsession.createProducer(dest);
            producer.send(mm);
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
