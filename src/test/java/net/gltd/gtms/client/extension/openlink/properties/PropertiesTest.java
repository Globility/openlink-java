package net.gltd.gtms.client.extension.openlink.properties;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.openlink.properties.Properties;
import net.gltd.gtms.extension.openlink.properties.Property;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.Message;

public class PropertiesTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public PropertiesTest() throws JAXBException, XMLStreamException {
		super(Message.class, Properties.class, Property.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = TestUtil.initializeConsoleLogger("net.gltd.gtms", TestUtil.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");
	}

	@After
	public void shutdown() throws Exception {
		LogManager.shutdown();
	}

	@Test
	public void testProperties() throws XMLStreamException, JAXBException {
		int numFeatures = 5;
		Properties ps = new Properties();
		Assert.assertNotNull(ps);
		Assert.assertTrue(ps.size() == 0);
		
		Property p1 = new Property();
		p1.setId("callId");
		p1.setType("sumType");
		p1.setValue("myVal");
		ps.add(p1);
		
		Property p2 = new Property();
		p2.setId("sysId");
		p2.setType("randomType");
		p2.setValue("myVal2");
		ps.add(p2);
		
		for (Property p : ps) {
			Assert.assertNotNull(p);
			Assert.assertNotNull(p.getId());
			Assert.assertNotNull(p.getValue());
			Assert.assertNotNull(p.getType());
		}

		Message m = new Message(Jid.valueOf("leon@example.com"));
		m.getExtensions().add(ps);

		String xml = marshal(m);
		Assert.assertNotNull(xml);
		logger.debug(marshal(ps));

		Assert.assertTrue(xml.contains("<properties xmlns=\"" + OpenlinkNamespaces.NS_OPENLINK_PROPERTIES + "\"><property id=\""));
		Assert.assertTrue(xml.contains("<property id=\"callId\" type=\"sumType\"><value>myVal</value></property>"));
		Assert.assertTrue(xml.contains("<property id=\"sysId\" type=\"randomType\"><value>myVal2</value></property>"));
	}
	
	@Test
	public void testPropertiesMarshalUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException,
			IOException {
		String xmlIn = TestUtil.readFileAsString("ol-properties.xml");

		Properties ps = unmarshal(xmlIn, Properties.class);

		String xmlOut = marshal(ps);
		Assert.assertNotNull(xmlOut);
		logger.debug(marshal(ps));
		
		Assert.assertNotNull(ps);
		Assert.assertEquals(2, ps.size());
		
		Property p1 = ps.getPropertyById("callId");
		Assert.assertEquals("callId", p1.getId());
		Assert.assertEquals("sumType", p1.getType());
		Assert.assertEquals("myVal", p1.getValue());
		
		Property p2 = ps.getPropertyById("sysId");
		Assert.assertEquals("sysId", p2.getId());
		Assert.assertEquals("randomType", p2.getType());
		Assert.assertEquals("myVal2", p2.getValue());
		

	}


}
