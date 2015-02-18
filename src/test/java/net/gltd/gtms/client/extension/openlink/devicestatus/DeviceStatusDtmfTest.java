package net.gltd.gtms.client.extension.openlink.devicestatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.callstatus.Call;
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
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatusFeature;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.features.callback.Callback;
import net.gltd.gtms.extension.openlink.features.callback.Callback.CallbackState;
import net.gltd.gtms.extension.openlink.features.dtmf.Dtmf;
import net.gltd.gtms.extension.openlink.features.dtmf.Dtmf.DtmfDirection;
import net.gltd.gtms.extension.openlink.features.voicemessage.VoiceMessage;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.gtms.extension.openlink.properties.Property;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.string.StringUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class DeviceStatusDtmfTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public DeviceStatusDtmfTest() throws JAXBException, XMLStreamException {
		super(Property.class, Headers.class, Header.class, Event.class, Command.class, Note.class, Message.class, IQ.class, IoData.class,
				Profiles.class, Profile.class, Action.class, Interests.class, Interest.class, Features.class, Feature.class, CallStatus.class,
				Call.class, CallerCallee.class, CallFeature.class, Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class,
				ClearCall.class, ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class,
				HoldCall.class, IntercomTransfer.class, JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class,
				RetrieveCall.class, SendDigit.class, SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class,
				StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class, DeviceStatus.class, DeviceStatusFeature.class, VoiceMessage.class,
				Callback.class, Callback.Active.class, Dtmf.class);
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
	public void testDeviceStatus() throws XMLStreamException, JAXBException {
		DeviceStatus ds = new DeviceStatus();
		Assert.assertNotNull(ds);

		ds.setProfile("user_myprofile");

		DeviceStatusFeature dsf = new DeviceStatusFeature();
		dsf.setId("CallBack");
		ds.getFeatures().add(dsf);

		Dtmf d = new Dtmf();
		d.setCallId("blahblah-123");
		d.setValue("#");
		d.setDirection(DtmfDirection.sent);

		Participant p = new Participant();
		p.setExten("1234");
		d.setParticipant(p);

		dsf.setValue(d);

		String xml = marshal(ds);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		Assert.assertTrue(xml.contains("<devicestatus xmlns=\"http://xmpp.org/protocol/openlink:01:00:00#device-status\">"));
		Assert.assertTrue(xml.contains("<dtmf xmlns=\"http://xmpp.org/protocol/openlink:01:00:00/features#dtmf\">"));
		Assert.assertTrue(xml.contains("<callid>blahblah-123</callid>"));
		Assert.assertTrue(xml.contains("<value>#</value>"));
		Assert.assertTrue(xml.contains("<direction>sent</direction>"));
		Assert.assertTrue(xml.contains("<participant exten=\"1234\""));
	}

	@Test
	public void testDeviceStatusUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = StringUtil.readFileAsString("ol-devicestatus-callback.xml");

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
		Assert.assertTrue(i.getPayload() instanceof DeviceStatus);
		DeviceStatus ds = (DeviceStatus) i.getPayload();

		Assert.assertTrue(ds.getFeatures().size() == 1);

		DeviceStatusFeature f = ds.getFeatures().iterator().next();

		Assert.assertEquals("CallBack", f.getId());

		Assert.assertTrue(f.getValue() instanceof Callback);

		Callback cb = (Callback) f.getValue();

		Assert.assertNotNull(cb);

		Assert.assertEquals(true, cb.isAllocation());
		Assert.assertEquals("6004", cb.getDestination());
		Assert.assertEquals(true, cb.getActive().isValue());
		Assert.assertEquals(CallbackState.Ringing, cb.getActive().getState());
	}

}
