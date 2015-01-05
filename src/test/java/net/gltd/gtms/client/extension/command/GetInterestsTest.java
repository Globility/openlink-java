package net.gltd.gtms.client.extension.command;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.command.GetInterests;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.xml.XmlUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;

public class GetInterestsTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public GetInterestsTest() throws JAXBException, XMLStreamException {
		super(Command.class, Note.class, Message.class, IQ.class, IoData.class, GetInterests.class,
				GetInterests.GetInterestsIn.class);
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
	public void testCommand() throws XMLStreamException, JAXBException {
		GetInterests gi = new GetInterests();
		gi.getIn().setProfile("leon_office");

		Assert.assertNotNull(gi);

		IQ iq = new IQ(Jid.valueOf("vmstsp.dom"), IQ.Type.GET, gi);

		String xml = marshal(iq);
		Assert.assertNotNull(xml);
		logger.debug(xml);

		// Assert.assertTrue(xml.contains("<iodata xmlns=\"urn:xmpp:tmp:io-data\" type=\"output\">"));

	}
}
