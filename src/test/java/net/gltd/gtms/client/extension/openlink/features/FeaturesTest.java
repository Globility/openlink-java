package net.gltd.gtms.client.extension.openlink.features;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.client.openlink.OpenlinkTestHelper;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.xml.XmlUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.Message;

public class FeaturesTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public FeaturesTest() throws JAXBException, XMLStreamException {
		super(Message.class, Features.class, Feature.class, Action.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = GtmsLog.initializeConsoleLogger("net.gltd.gtms", GtmsLog.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");
	}

	@After
	public void shutdown() throws Exception {
		LogManager.shutdown();
	}

	@Test
	public void testFeatures() throws XMLStreamException, JAXBException {
		int numFeatures = 5;
		Features fs = OpenlinkTestHelper.getFeatures(numFeatures);
		Assert.assertNotNull(fs);
		Assert.assertTrue(fs.size() == numFeatures);
		for (Feature f : fs) {
			Assert.assertNotNull(f);
			Assert.assertNotNull(f.getId());
			Assert.assertNotNull(f.getLabel());
			Assert.assertNotNull(f.getType());
		}

		Message m = new Message(Jid.valueOf("leon@example.com"));
		m.getExtensions().add(fs);

		String xml = marshal(m);
		Assert.assertNotNull(xml);
		logger.debug(marshal(fs));

		Assert.assertTrue(xml.contains("<features xmlns=\"" + OpenlinkNamespaces.NS_OPENLINK_FEATURES
				+ "\"><feature id=\""));
	}

}
