package net.gltd.gtms.client.extension.openlink.profiles;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.client.openlink.OpenlinkTestHelper;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.string.StringUtil;
import net.gltd.util.xml.XmlUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmpp.XmlTest;
import org.xmpp.stanza.client.IQ;

public class ProfilesTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public ProfilesTest() throws JAXBException, XMLStreamException {
		super(IQ.class, Command.class, IoData.class, Profiles.class, Profile.class, Action.class);
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
	public void testProfiles() throws XMLStreamException, JAXBException {
		int numProfiles = 5;
		int numActions = 3;
		Profiles ps = OpenlinkTestHelper.getProfiles(numProfiles, numActions);
		Assert.assertNotNull(ps);
		Assert.assertTrue(ps.size() == numProfiles);
		for (Profile p : ps) {
			Assert.assertTrue(p.getActions().size() == numActions);
		}

		String xml = marshal(ps);
		Assert.assertNotNull(xml);
		logger.debug(XmlUtil.formatXml(marshal(ps)));

		Assert.assertTrue(xml.contains("<profiles xmlns=\"" + OpenlinkNamespaces.NS_OPENLINK_PROFILES
				+ "\"><profile id=\""));
		Assert.assertTrue(xml.contains("<actions><action id=\""));
	}

	@Test
	public void testIoDataMarshalUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException,
			IOException {
		String xmlIn = StringUtil.readFileAsString("ol-getprofiles.xml");

		IQ iq = unmarshal(xmlIn, IQ.class);

		String xmlOut = marshal(iq);
		Assert.assertNotNull(xmlOut);
		logger.debug(XmlUtil.formatXml(marshal(iq)));
		
		Command c = iq.getExtension(Command.class);
		Assert.assertNotNull(c);
		IoData iod = c.getExtension(IoData.class);
		Assert.assertNotNull(iod);
		logger.debug("CLAZZ: " + iod.getOut().getExtension());
		Profiles ps = iod.getOut().getExtension(Profiles.class);
		
		Assert.assertNotNull(ps);

		Assert.assertEquals(2, ps.size());
		
		Profile p1 = ps.getProfileById("gary_office");
		Assert.assertNotNull(p1);
		Assert.assertEquals(10, p1.getActions().size());
		
		Profile p2 = ps.getProfileById("gary_office");
		Assert.assertNotNull(p2);
		Assert.assertEquals(10, p2.getActions().size());

	}

}
