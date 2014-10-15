package net.gltd.gtms.client.openlink;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.xml.XmlUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmpp.Jid;
import org.xmpp.XmlTest;
import org.xmpp.stanza.client.IQ;
import org.xmpp.stanza.client.Message;

public class IoDataTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public IoDataTest() throws JAXBException, XMLStreamException {
		super(Command.class, Note.class, Message.class, IQ.class, IoData.class, Profiles.class, Profile.class,
				Action.class);
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
	public void testIoData() throws XMLStreamException, JAXBException {
		int numProfiles = 5;
		int numActions = 3;
		Profiles ps = OpenlinkTestHelper.getProfiles(numProfiles, numActions);
		Assert.assertNotNull(ps);
		Assert.assertTrue(ps.size() == numProfiles);
		for (Profile p : ps) {
			Assert.assertTrue(p.getActions().size() == numActions);
		}

		IoData ioData = new IoData();
		ioData.setType(IoData.IoDataType.OUTPUT);
		ioData.getOut().setExtension(ps);

		Command c = OpenlinkTestHelper.getCommand(OpenlinkNamespaces.NS_OPENLINK_GETPROFILES);
		Assert.assertNotNull(c);
		c.getExtensions().add(ioData);

		Assert.assertNotNull(ioData);
		Assert.assertNotNull(ioData.getOut());
		Assert.assertTrue(ioData.getOut().getExtension(Profiles.class).size() == numProfiles);

		Message m = new Message(Jid.valueOf("leon@example.com"));
		m.getExtensions().add(c);

		String xml = marshal(m);
		Assert.assertNotNull(xml);
		logger.debug(XmlUtil.formatXml(xml));

	}

}
