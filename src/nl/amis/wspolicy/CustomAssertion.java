package nl.amis.wspolicy;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import oracle.wsm.common.sdk.IContext;
import oracle.wsm.policy.model.IAssertion;
import oracle.wsm.policy.model.IAssertionBindings;
import oracle.wsm.policy.model.IConfig;
import oracle.wsm.policy.model.IPropertySet;
import oracle.wsm.policy.model.impl.SimpleAssertion;
import oracle.wsm.policyengine.IExecutionContext;
import oracle.wsm.policyengine.impl.AssertionExecutor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A base class for OWSM custom assertions that specific classes can extend. It
 * contains common code and variables used accross all custom assertions.
 *
 * All custom assertions must extend the AssertionExecutor class.
 */
public abstract class CustomAssertion extends AssertionExecutor {


    protected final static String PROP_DEBUG = "debugFlag";

    protected final static String DEBUG_START = "===========================================================>>>";
    protected final static String DEBUG_END = "<<<===========================================================";

    protected IAssertion mAssertion = null;
    protected IExecutionContext mEcontext = null;
    protected oracle.wsm.common.sdk.IContext mIcontext = null;

    /**
     * A tag or text to display when printing debug information to identify the
     * content.
     */
    protected String mTag;

    /**
     * Constructor
     */
    public CustomAssertion(String tag) {
        super();
        mTag = tag;
    } // CustomAssertion()

    /**
     * Implemented from parent class
     */
    public void init(IAssertion iAssertion, IExecutionContext iExecutionContext, IContext iContext) {
        mAssertion = iAssertion;
        mEcontext = iExecutionContext;
        mIcontext = iContext;
        //IAssertionBindings bindings = ((SimpleAssertion) (mAssertion)).getBindings();
    } // init()

    /**
     * Implemented from parent class
     */
    public void destroy() {
        // Nothing to do.
    } // destroy()


    public String getPolicyBindingProperty(String propertyName) {
        IAssertionBindings bindings = ((SimpleAssertion) (this.mAssertion)).getBindings();
        IConfig config = bindings.getConfigs().get(0);
        IPropertySet propertyset = config.getPropertySets().get(0);
        String propertyValue = propertyset.getPropertyByName(propertyName).getValue();
        return propertyValue;
    }

    /**
     * A utility method for extracting the node specified by <code>xpathStr</code>
     * (with namespaces defined by <code>namespaces</code>) from
     *<code>payload</code>
     *
     * This method will print an stack trace if their is an exception. If you want
     *to
     * return the exception instead then modify the method appropriately.
     *
     * @param payload the payload
     * @param namespaces the namespaces referenced by <code>xpathStr</code>
     * @param xpathStr an XPath query defining how to extract a node from
     *<code>payload</code>
     * @return
     */
    public static Node getDataNode(Element payload, final HashMap<String, String>

                                   namespaces, String xpathStr) {
        Node node = null;

        try {
            // Create a namespace context based on the namespaces passed in.
            //
            NamespaceContext ctx = new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    return namespaces.get(prefix);
                }
                // Dummy implementation - not used
                public Iterator getPrefixes(String val) {
                    return null;
                }
                // Dummy implemenation - not used
                public String getPrefix(String uri) {
                    return null;
                }
            };
            XPathFactory xpathFact = XPathFactory.newInstance();
            XPath xpath = xpathFact.newXPath();
            xpath.setNamespaceContext(ctx);
            node = (Node) xpath.evaluate(xpathStr, payload, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
            return null;
        }

        return node;
    } // getDataNode()

}
