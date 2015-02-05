package net.gltd.gtms.client.extension.openlink.callstatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.openlink.OpenlinkTestHelper;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.callstatus.Call;
import net.gltd.gtms.extension.openlink.callstatus.Call.CallDirection;
import net.gltd.gtms.extension.openlink.callstatus.Call.CallState;
import net.gltd.gtms.extension.openlink.callstatus.CallFeature;
import net.gltd.gtms.extension.openlink.callstatus.CallStatus;
import net.gltd.gtms.extension.openlink.callstatus.CallerCallee;
import net.gltd.gtms.extension.openlink.callstatus.Participant;
import net.gltd.gtms.extension.openlink.callstatus.action.AddThirdParty;
import net.gltd.gtms.extension.openlink.callstatus.action.AnswerCall;
import net.gltd.gtms.extension.openlink.callstatus.action.CallAction;
import net.gltd.gtms.extension.openlink.callstatus.action.ClearCall;
import net.gltd.gtms.extension.openlink.callstatus.action.ClearConnection;
import net.gltd.gtms.extension.openlink.callstatus.action.ConferenceFail;
import net.gltd.gtms.extension.openlink.callstatus.action.ConnectSpeaker;
import net.gltd.gtms.extension.openlink.callstatus.action.ConsultationCall;
import net.gltd.gtms.extension.openlink.callstatus.action.DisconnectSpeaker;
import net.gltd.gtms.extension.openlink.callstatus.action.HoldCall;
import net.gltd.gtms.extension.openlink.callstatus.action.IntercomTransfer;
import net.gltd.gtms.extension.openlink.callstatus.action.JoinCall;
import net.gltd.gtms.extension.openlink.callstatus.action.PrivateCall;
import net.gltd.gtms.extension.openlink.callstatus.action.PublicCall;
import net.gltd.gtms.extension.openlink.callstatus.action.RemoveThirdParty;
import net.gltd.gtms.extension.openlink.callstatus.action.RetrieveCall;
import net.gltd.gtms.extension.openlink.callstatus.action.SendDigit;
import net.gltd.gtms.extension.openlink.callstatus.action.SendDigits;
import net.gltd.gtms.extension.openlink.callstatus.action.SingleStepTransfer;
import net.gltd.gtms.extension.openlink.callstatus.action.StartVoiceDrop;
import net.gltd.gtms.extension.openlink.callstatus.action.StopVoiceDrop;
import net.gltd.gtms.extension.openlink.callstatus.action.TransferCall;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.originatorref.OriginatorRef;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.string.StringUtil;

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
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class CallStatusTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public CallStatusTest() throws JAXBException, XMLStreamException {
		super(OriginatorRef.class, Property.class, Headers.class, Header.class, Event.class, Command.class, Note.class,
				Message.class, IQ.class, IoData.class, Profiles.class, Profile.class, Action.class, Interests.class,
				Interest.class, Features.class, Feature.class, CallStatus.class, Call.class, CallerCallee.class,
				CallFeature.class, Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class,
				ClearCall.class, ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class,
				ConsultationCall.class, DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class,
				JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class, RetrieveCall.class,
				SendDigit.class, SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class,
				StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class);
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
		logger.debug("XML : " + xml);

		Assert.assertTrue(xml
				.contains("<callstatus xmlns=\"http://xmpp.org/protocol/openlink:01:00:00#call-status\" busy=\"false\">"));
		Assert.assertTrue(xml.contains("<state>CallConferenced</state>"));
		Assert.assertTrue(xml.contains("<RemoveThirdParty>"));
		Assert.assertTrue(xml.contains("<ClearCall>"));
	}

	@Test
	public void testCallStatusUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = StringUtil.readFileAsString("ol-callstatus.xml");

		Message message = unmarshal(xmlIn, Message.class);

		String xmlOut = marshal(message);
		Assert.assertNotNull(xmlOut);
		logger.debug(xmlOut);

		Event e = message.getExtension(Event.class);
		Assert.assertNotNull(e);
		List<Item> items = e.getItems();
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());

		Item i = items.get(0);
		Assert.assertTrue(i.getPayload() instanceof CallStatus);
		CallStatus cs = (CallStatus) i.getPayload();

		Assert.assertTrue(cs.getCalls().size() == 2);

		Call c1 = null;
		Call c2 = null;

		for (Call c : cs.getCalls()) {
			Assert.assertNotNull(c);
			Assert.assertNotNull(c.getId());
			if (c.getId().equals("AVA#10585#10000000292#AI50202AV50202-BETTY.BIDDER")) {
				c1 = c;
			} else {
				c2 = c;
			}
		}
		Assert.assertEquals("AI50202AV50202-BETTY.BIDDER", c1.getInterest());
		Assert.assertEquals("AV50202-BETTY.BIDDER", c1.getProfile());

		Assert.assertNotNull(c1.getOriginatorRef());
		Assert.assertEquals(2, c1.getOriginatorRef().size());
		int validOriginatorRefCount = 0;
		for (Property p : c1.getOriginatorRef()) {
			if ("universal-callid".equals(p.getId())) {
				Assert.assertEquals("09999105851418922357", p.getValue());
				validOriginatorRefCount++;
			} else if ("platform-callid".equals(p.getId())) {
				Assert.assertEquals("ABCDEFGHIJKLMNOP1234", p.getValue());
				validOriginatorRefCount++;
			}
		}
		Assert.assertEquals(2, validOriginatorRefCount);

		Assert.assertEquals(CallState.ConnectionCleared, c1.getState());
		Assert.assertEquals(CallDirection.Outgoing, c1.getDirection());

		Assert.assertNotNull(c1.getCaller());
		Assert.assertEquals("50202", c1.getCaller().getName());
		Assert.assertEquals("50202", c1.getCaller().getNumber());

		Assert.assertNotNull(c1.getCalled());
		Assert.assertEquals("50203", c1.getCalled().getNumber());
		Assert.assertEquals("50203", c1.getCalled().getName());

		Assert.assertEquals(0, c1.getDuration());

		Assert.assertEquals(2, c1.getFeatures().size());
		for (CallFeature cf : c1.getFeatures()) {
			Assert.assertTrue(cf.getId().equals("Conference") || cf.getId().equals("Callback"));
			Assert.assertTrue(cf.isValue1());
		}

		Assert.assertEquals(3, c1.getActions().size());
		for (CallAction a : c1.getActions()) {
			Assert.assertTrue(a instanceof CallAction);
			 Assert.assertTrue(a.getId().equals("ClearConnection") || a.getId().equals("ClearCall")
			 || a.getId().equals("AddThirdParty"));
		}

		Assert.assertEquals(2, c1.getParticipants().size());
		for (Participant p : c1.getParticipants()) {
			Assert.assertTrue(p.getExten() != null && p.getExten().length() > 1);
			Assert.assertTrue(p.getJid() != null);
			Assert.assertTrue(p.getType() != null);
			Assert.assertTrue(p.getDirection() != null);
			Assert.assertTrue(p.getExten() != null);
		}

	}
}
