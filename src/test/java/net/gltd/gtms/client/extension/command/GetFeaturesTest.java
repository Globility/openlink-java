package net.gltd.gtms.client.extension.command;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.command.GetFeatures;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;

public class GetFeaturesTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public GetFeaturesTest() throws JAXBException, XMLStreamException {
		super(Command.class, Note.class, Message.class, IQ.class, IoData.class, GetFeatures.class,
				GetFeatures.GetFeaturesIn.class);
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
	public void testCommand() throws XMLStreamException, JAXBException {
		GetFeatures gi = new GetFeatures();
		gi.getIn().setProfile("leon_office");

		Assert.assertNotNull(gi);

		IQ iq = new IQ(Jid.valueOf("vmstsp.dom"), IQ.Type.GET, gi);

		String xml = marshal(iq);
		Assert.assertNotNull(xml);
		logger.debug(xml);

		// Assert.assertTrue(xml.contains("<iodata xmlns=\"urn:xmpp:tmp:io-data\" type=\"output\">"));

	}
}
