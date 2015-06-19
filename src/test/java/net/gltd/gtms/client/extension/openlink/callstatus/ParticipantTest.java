package net.gltd.gtms.client.extension.openlink.callstatus;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.extension.openlink.callstatus.Call.CallDirection;
import net.gltd.gtms.extension.openlink.callstatus.Participant;
import net.gltd.gtms.extension.openlink.callstatus.Participant.ParticipantType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;

public class ParticipantTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public ParticipantTest() throws JAXBException, XMLStreamException {
		super(Participant.class);
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
	public void testParticipantUnMarshal() throws XMLStreamException, JAXBException {
		String xml = "<participant direction=\"Outgoing\" exten=\"5203\" jid=\"betty.bidder@mas-analec.gltd.net\" timestamp=\"Thu Dec 18 17:04:57 GMT 2014\" type=\"Active\"></participant>";

		Participant p1 = unmarshal(xml, Participant.class);
		Assert.assertEquals(p1.getJid(), Jid.valueOf("betty.bidder@mas-analec.gltd.net"));

		Assert.assertEquals(p1.getDirection(), CallDirection.Outgoing);
		Assert.assertEquals(p1.getTimestamp(), "Thu Dec 18 17:04:57 GMT 2014");
		Assert.assertEquals(p1.getExten(), "5203");
		Assert.assertEquals(p1.getType(), ParticipantType.Active);
	}

	@Test
	public void testParticipantMarshal() throws XMLStreamException, JAXBException {

		Participant p1 = new Participant();
		p1.setJid(Jid.valueOf("betty.bidder@example.com"));
		p1.setDirection(CallDirection.Incoming);
		p1.setExten("52220");
		p1.setType(ParticipantType.Inactive);
		p1.setTimestamp("Thu Dec 18 17:04:57 GMT 2014");
		
		String xml = marshal(p1);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		
		Assert.assertTrue(xml.contains("<participant"));
		Assert.assertTrue(xml.contains("jid=\"betty.bidder@example.com\""));
		Assert.assertTrue(xml.contains("direction=\"Incoming\""));
		Assert.assertTrue(xml.contains("exten=\"52220\""));
		Assert.assertTrue(xml.contains("type=\"Inactive\""));
		Assert.assertTrue(xml.contains("timestamp=\"Thu Dec 18 17:04:57 GMT 2014\""));
		
	}

}
