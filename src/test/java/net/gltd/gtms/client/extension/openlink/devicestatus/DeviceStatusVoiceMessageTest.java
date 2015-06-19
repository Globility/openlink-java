package net.gltd.gtms.client.extension.openlink.devicestatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.TestUtil;
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
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus.DeviceStatusAction;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus.DeviceStatusStatus;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatusFeature;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.features.callback.Callback;
import net.gltd.gtms.extension.openlink.features.dtmf.Dtmf;
import net.gltd.gtms.extension.openlink.features.voicemessage.VoiceMessage;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.gtms.extension.openlink.properties.Properties;
import net.gltd.gtms.extension.openlink.properties.Property;

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

public class DeviceStatusVoiceMessageTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public DeviceStatusVoiceMessageTest() throws JAXBException, XMLStreamException {
		super(Property.class, Headers.class, Header.class, Event.class, Command.class, Note.class, Message.class, IQ.class,
				IoData.class, Profiles.class, Profile.class, Action.class, Interests.class, Interest.class, Features.class, Feature.class,
				CallStatus.class, Call.class, CallerCallee.class, CallFeature.class, Participant.class, CallAction.class, AddThirdParty.class,
				AnswerCall.class, ClearCall.class, ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class,
				DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class, JoinCall.class, PrivateCall.class, PublicCall.class,
				RemoveThirdParty.class, RetrieveCall.class, SendDigit.class, SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class,
				SendDigits.class, StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class, DeviceStatus.class, DeviceStatusFeature.class,
				VoiceMessage.class, Callback.class, Callback.Active.class, Dtmf.class,

				Properties.class, net.gltd.gtms.extension.openlink.properties.Property.class);
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
	public void testDeviceStatus() throws XMLStreamException, JAXBException {
		DeviceStatus ds = new DeviceStatus();
		Assert.assertNotNull(ds);

		ds.setProfile("user_myprofile");

		DeviceStatusFeature dsf = new DeviceStatusFeature();
		dsf.setId("MK1001");
		ds.getFeatures().add(dsf);

		VoiceMessage vm = new VoiceMessage();

		vm.setState(VoiceMessage.State.start);
		vm.setExten("970003");
		vm.setBlastid("12345");
		vm.setDestination("6004");
		vm.setStatus(DeviceStatusStatus.ok);
		vm.setAction(DeviceStatusAction.VoiceDrop);
		vm.setPlayProgress("121");
		vm.setCreationDate("2011-09-05 15:52:22.0");
		vm.setMsgLength("133.442");

		dsf.setValue(vm);

		String xml = marshal(ds);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		Assert.assertTrue(xml.contains("<devicestatus xmlns=\"http://xmpp.org/protocol/openlink:01:00:00#device-status\">"));
		Assert.assertTrue(xml.contains("<voicemessage xmlns=\"http://xmpp.org/protocol/openlink:01:00:00/features#voice-message\">"));
		Assert.assertTrue(xml.contains("<status>ok</status>"));
		Assert.assertTrue(xml.contains("<destination>6004</destination>"));
		Assert.assertTrue(xml.contains("<action>VoiceDrop</action>"));
		Assert.assertTrue(xml.contains("<creationdate>2011-09-05 15:52:22.0</creationdate>"));
	}

	@Test
	public void testDeviceStatusUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = TestUtil.readFileAsString("ol-devicestatus-voicemessage.xml");

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

		Assert.assertEquals("MK1016", f.getId());

		Assert.assertTrue(f.getValue() instanceof VoiceMessage);

		VoiceMessage vm = (VoiceMessage) f.getValue();

		Assert.assertNotNull(vm);

		Assert.assertEquals(DeviceStatusStatus.ok, vm.getStatus());
		Assert.assertEquals(DeviceStatusAction.VoiceDrop, vm.getAction());
		Assert.assertEquals("121.23274993896", vm.getMsgLength());
		Assert.assertEquals("6004", vm.getDestination());
		Assert.assertEquals("abcd", vm.getCallId());
		Assert.assertEquals(VoiceMessage.State.stop, vm.getState());
		Assert.assertEquals("970003", vm.getExten());
		Assert.assertEquals("2011-09-05 15:52:22.0", vm.getCreationDate());
		Assert.assertEquals("121", vm.getPlayProgress());
	}

	@Test
	public void testDeviceStatusUnmarshal2() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = TestUtil.readFileAsString("ol-devicestatus-voicemessage2.xml");

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

		Assert.assertEquals("MK1016", f.getId());

		Assert.assertTrue(f.getValue() instanceof VoiceMessage);

		VoiceMessage vm = (VoiceMessage) f.getValue();

		Assert.assertNotNull(vm);

		Assert.assertEquals(2, vm.getProperties().size());

		Assert.assertEquals("PL0001", vm.getPlaylist());
		Assert.assertEquals("1", vm.getSequence());
		Assert.assertEquals(DeviceStatusStatus.ok, vm.getStatus());
		Assert.assertEquals("Invitation to watch cricket at the oval", vm.getLabel());
		Assert.assertEquals("mk-101230412123123", vm.getCallId());
		Assert.assertEquals(DeviceStatusAction.Playback, vm.getAction());
		Assert.assertEquals(VoiceMessage.State.start, vm.getState());
		Assert.assertEquals("47.93275", vm.getMsgLength());
		Assert.assertEquals("6004", vm.getDestination());
		Assert.assertEquals("15.969", vm.getPlayProgress());
		Assert.assertEquals("2010-01-01 14:23:27.029", vm.getCreationDate());
		Assert.assertEquals(true, vm.isAmdDetect());
		Assert.assertEquals("6969", vm.getExten());
		Assert.assertEquals("456", vm.getBlastid());
		Assert.assertEquals("123", vm.getBlastItemId());
		Assert.assertEquals("2010-01-01 14:23:27.029", vm.getTimestamp());
		Assert.assertEquals("This is a message", vm.getStatusDescriptor());
	}

	@Test
	public void testDeviceStatusUnmarshal3() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = TestUtil.readFileAsString("ol-devicestatus-voicemessage3.xml");

		IQ iq = unmarshal(xmlIn, IQ.class);

		String xmlOut = marshal(iq);
		Assert.assertNotNull(xmlOut);
		logger.debug(xmlOut);

		Command c = iq.getExtension(Command.class);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.getExtensions().isEmpty());
		IoData ioData = c.getExtension(IoData.class);
		Assert.assertNotNull(ioData);

		Assert.assertTrue(ioData.getOut().getExtension(DeviceStatus.class) instanceof DeviceStatus);

		DeviceStatus ds = ioData.getOut().getExtension(DeviceStatus.class);

		Assert.assertTrue(ds.getFeatures().size() == 4);

		VoiceMessage vm1 = null;
		VoiceMessage vm2 = null;
		VoiceMessage vm3 = null;
		VoiceMessage vm4 = null;

		for (DeviceStatusFeature f : ds.getFeatures()) {
			Assert.assertTrue(f.getValue() instanceof VoiceMessage);
			if ("MK0001".equals(f.getId())) {
				vm1 = (VoiceMessage) f.getValue();
			} else if ("MK0002".equals(f.getId())) {
				vm2 = (VoiceMessage) f.getValue();
			} else if ("MK0003".equals(f.getId())) {
				vm3 = (VoiceMessage) f.getValue();
			} else if ("MK0005".equals(f.getId())) {
				vm4 = (VoiceMessage) f.getValue();
			}
		}

		Assert.assertNotNull(vm1);
		Assert.assertNotNull(vm2);
		Assert.assertNotNull(vm3);
		Assert.assertNotNull(vm4);

		Assert.assertEquals(DeviceStatusStatus.ok, vm1.getStatus());
		Assert.assertEquals("Invitation to watch cricket at the oval", vm1.getLabel());
		Assert.assertEquals("47.93275", vm1.getMsgLength());
		Assert.assertEquals("2010-01-01 14:23:27.029", vm1.getCreationDate());

		Assert.assertEquals("PL0001", vm2.getPlaylist());
		Assert.assertEquals("2", vm2.getSequence());
		Assert.assertEquals(DeviceStatusStatus.ok, vm2.getStatus());
		Assert.assertEquals("Market alert", vm2.getLabel());
		Assert.assertEquals("mk-101230412123123", vm2.getCallId());
		Assert.assertEquals(DeviceStatusAction.Playback, vm2.getAction());
		Assert.assertEquals(VoiceMessage.State.start, vm2.getState());
		Assert.assertEquals("312.123", vm2.getMsgLength());
		Assert.assertEquals("6004", vm2.getDestination());
		Assert.assertEquals("22.969", vm2.getPlayProgress());
		Assert.assertEquals("2010-01-01 09:33:22.019", vm2.getCreationDate());

		Assert.assertEquals("PL0001", vm3.getPlaylist());
		Assert.assertEquals("3", vm3.getSequence());
		Assert.assertEquals(DeviceStatusStatus.ok, vm3.getStatus());
		Assert.assertEquals("Status update", vm3.getLabel());
		Assert.assertEquals("mk-101230412123123", vm3.getCallId());
		Assert.assertEquals(DeviceStatusAction.Playback, vm3.getAction());
		Assert.assertEquals(VoiceMessage.State.pending, vm3.getState());
		Assert.assertEquals("83.123", vm3.getMsgLength());
		Assert.assertEquals("2010-02-02 10:23:27.044", vm3.getCreationDate());

		Assert.assertEquals(DeviceStatusStatus.ok, vm4.getStatus());
		Assert.assertEquals("Status update", vm4.getLabel());
		Assert.assertEquals("83.123", vm4.getMsgLength());
		Assert.assertEquals("2010-02-02 10:23:27.044", vm4.getCreationDate());
	}
}
