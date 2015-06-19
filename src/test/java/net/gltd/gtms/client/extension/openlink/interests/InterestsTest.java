package net.gltd.gtms.client.extension.openlink.interests;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.client.openlink.OpenlinkTestHelper;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.profiles.Action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.Message;

public class InterestsTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public InterestsTest() throws JAXBException, XMLStreamException {
		super(Message.class, Interests.class, Interest.class, Action.class);
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
	public void testInterests() throws XMLStreamException, JAXBException {
		int numInterests = 5;
		Interests is = OpenlinkTestHelper.getInterests(numInterests);
		Assert.assertNotNull(is);
		Assert.assertTrue(is.size() == numInterests);
		for (Interest i : is) {
			Assert.assertNotNull(i);
			Assert.assertNotNull(i.getId());
			Assert.assertNotNull(i.getLabel());
			Assert.assertNotNull(i.getType());
			Assert.assertNotNull(i.getValue());
		}

		Message m = new Message(Jid.valueOf("leon@example.com"));
		m.getExtensions().add(is);

		String xml = marshal(m);
		Assert.assertNotNull(xml);
		logger.debug(marshal(is));

		Assert.assertTrue(xml.contains("<interests xmlns=\"" + OpenlinkNamespaces.NS_OPENLINK_INTERESTS
				+ "\"><interest id=\""));
	}
}
