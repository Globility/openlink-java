package net.gltd.gtms.client.extension.command;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.command.RequestAction;
import net.gltd.gtms.extension.openlink.command.RequestAction.RequestActionAction;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.xml.XmlUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;

public class RequestActionTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public RequestActionTest() throws JAXBException, XMLStreamException {
		super(Command.class, Note.class, Message.class, IQ.class, IoData.class, RequestAction.class,
				RequestAction.RequestActionIn.class);
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
		RequestAction gi = new RequestAction();
		gi.getIn().setInterest("leon_office_vmstsp_default");
		gi.getIn().setAction(RequestActionAction.AddThirdParty);
		gi.getIn().setCall(RandomStringUtils.randomNumeric(12));
		gi.getIn().setValue1("6001");
		gi.getIn().setValue2("somevalue");

		Assert.assertNotNull(gi);

		IQ iq = new IQ(Jid.valueOf("vmstsp.dom"), IQ.Type.GET, gi);

		String xml = marshal(iq);
		Assert.assertNotNull(xml);
		logger.debug(xml);

		// Assert.assertTrue(xml.contains("<iodata xmlns=\"urn:xmpp:tmp:io-data\" type=\"output\">"));

	}
}
