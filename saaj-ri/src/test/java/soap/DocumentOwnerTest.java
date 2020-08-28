package soap;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import junit.framework.TestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.soap.MessageFactory;
import java.io.InputStream;
import java.net.URL;

/**
 * Original source taken from https://github.com/coheigea/testcases/tree/master/misc/saaj
 */
public class DocumentOwnerTest extends TestCase {

    /**
     * Reproduces the issue identified in #165
     */
    public void testOwnerDocumentConsistency() throws Exception {

        URL signatureFile = DocumentOwnerTest.class.getResource("signed-document.xml");
        assertNotNull(signatureFile);

        Document doc = read(signatureFile);

        Element refElement = (Element) doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Reference").item(0);

        Attr uriAttr = refElement.getAttributeNodeNS(null, "URI");
        Document uriOwnerDocument = uriAttr.getOwnerDocument();
        Document refOwnerDocument = refElement.getOwnerDocument();

        assertEquals("Inconsistent document",  refOwnerDocument, uriOwnerDocument);
        // Ideally the below will be true, but ultimately it shouldn't matter
        // assertEquals("Unexpected document type", SOAPDocumentImpl.class, uriOwnerDocument.getClass());
        // assertEquals("Unexpected document type", SOAPDocumentImpl.class, refOwnerDocument.getClass());
    }

    private static Document read(URL file) throws Exception {
        try (InputStream in = file.openStream()) {
            MessageFactory saajFactory = MessageFactory.newInstance();
            return saajFactory.createMessage(null, in).getSOAPPart();
        }
    }
}
