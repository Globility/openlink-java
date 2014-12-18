package net.gltd.gtms.client.extension.openlink.callstatus;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.client.openlink.OpenlinkTestHelper;
import net.gltd.gtms.extension.openlink.callstatus.Call;
import net.gltd.gtms.extension.openlink.callstatus.CallFeature;
import net.gltd.gtms.extension.openlink.callstatus.CallStatus;
import net.gltd.gtms.extension.openlink.callstatus.CallerCallee;
import net.gltd.gtms.extension.openlink.callstatus.Participant;
import net.gltd.gtms.extension.openlink.callstatus.action.AddThirdParty;
import net.gltd.gtms.extension.openlink.callstatus.action.AnswerCall;
import net.gltd.gtms.extension.openlink.callstatus.action.ClearCall;
import net.gltd.gtms.extension.openlink.callstatus.action.ClearConnection;
import net.gltd.gtms.extension.openlink.callstatus.action.RemoveThirdParty;
import net.gltd.gtms.extension.openlink.callstatus.action.SendDigits;
import net.gltd.gtms.extension.openlink.callstatus.action.StartVoiceDrop;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.xml.XmlUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xmpp.Jid;
import org.xmpp.XmlTest;
import org.xmpp.stanza.client.Message;

public class CallStatusTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public CallStatusTest() throws JAXBException, XMLStreamException {
		super(Message.class, CallStatus.class, Call.class, CallerCallee.class, CallFeature.class, Participant.class,
				AddThirdParty.class, RemoveThirdParty.class, ClearCall.class, AnswerCall.class, ClearConnection.class,
				SendDigits.class, StartVoiceDrop.class);
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
	public void testCallStatus() throws XMLStreamException, JAXBException {
		CallStatus cs = OpenlinkTestHelper.getCallStatus();
		Assert.assertNotNull(cs);

		Message m = new Message(Jid.valueOf("leon@example.com"));
		m.getExtensions().add(cs);

		String xml = marshal(m);
		Assert.assertNotNull(xml);
		logger.debug(XmlUtil.formatXml(marshal(cs)));

		// Assert.assertTrue(xml.contains("<profiles xmlns=\"" + OpenlinkNamespaces.NS_OPENLINK_PROFILES
		// + "\"><profile id=\""));
	}

}
