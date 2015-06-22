package net.gltd.gtms.client.extension.gtx.privatedata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.profiler.gtx.profile.GtxProfile;
import net.gltd.gtms.profiler.gtx.profile.GtxSystem;
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
import net.gltd.gtms.extension.openlink.features.voicemessage.VoiceMessage;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.gtms.extension.openlink.properties.Property;
import net.gltd.gtms.client.TestUtil;


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

public class GtxProfileTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public GtxProfileTest() throws JAXBException, XMLStreamException {
		super(Property.class, Headers.class, Header.class, Event.class, Command.class, Note.class, Message.class, IQ.class, IoData.class,
				Profiles.class, Profile.class, Action.class, Interests.class, Interest.class, Features.class, Feature.class, CallStatus.class,
				Call.class, CallerCallee.class, CallFeature.class, Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class,
				ClearCall.class, ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class,
				HoldCall.class, IntercomTransfer.class, JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class,
				RetrieveCall.class, SendDigit.class, SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class,
				StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class, DeviceStatus.class, DeviceStatusFeature.class, VoiceMessage.class,
				Callback.class, Callback.Active.class, Dtmf.class, 
				net.gltd.gtms.profiler.gtx.profile.Feature.class, GtxProfile.class,
				GtxSystem.class, net.gltd.gtms.profiler.gtx.profile.Profile.class, 
				net.gltd.gtms.profiler.gtx.profile.Property.class);
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
		dsf.setId("CallBack");
		ds.getFeatures().add(dsf);

		Callback cb = new Callback();
		cb.setAllocation(true);
		cb.setDestination("6601");

		Callback.Active a = new Callback.Active();
		a.setState(CallbackState.Ringing);
		a.setValue(true);
		cb.setActive(a);

		dsf.setValue(cb);

		String xml = marshal(ds);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		Assert.assertTrue(xml.contains("<devicestatus xmlns=\"http://xmpp.org/protocol/openlink:01:00:00#device-status\">"));
		Assert.assertTrue(xml.contains("<callback xmlns=\"http://xmpp.org/protocol/openlink:01:00:00/features#callback\">"));
		Assert.assertTrue(xml.contains("<allocation>true</allocation>"));
		Assert.assertTrue(xml.contains("<destination>6601</destination>"));
		Assert.assertTrue(xml.contains("<active state=\"Ringing\">true</active>"));
	}

	@Test
	public void testDeviceStatusUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = TestUtil.readFileAsString("ol-devicestatus-callback.xml");

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

	@Test
	public void testGtxProfile() throws XMLStreamException, JAXBException {
		GtxProfile gp = new GtxProfile();
		Assert.assertNotNull(gp);

		gp.setUid("leon");

		gp.updateProperty("randomId", "SOMERANDOMVALUE");

		net.gltd.gtms.profiler.gtx.profile.Profile pr = new net.gltd.gtms.profiler.gtx.profile.Profile();
		pr.setId("office");

		GtxSystem sys = new GtxSystem();
		sys.setId("cisco");
		sys.setType("cisco");
		sys.setCategory("telephony");
		sys.setEnabled(true);

		sys.updateProperty("userId", "brian", true);

		pr.addGtxSystem(sys);

		gp.updateProfile(pr);

		String xml = marshal(gp);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		Assert.assertTrue(xml.contains("<gtx-profile xmlns=\"http://gltd.net/protocol/gtx/profile\" uid=\"leon\">"));
		Assert.assertTrue(xml.contains("<property id=\"randomId\" required=\"false\"><value>SOMERANDOMVALUE</value></property>"));
		Assert.assertTrue(xml
				.contains("<profile id=\"office\"><systems><system id=\"cisco\" type=\"cisco\" category=\"telephony\" enabled=\"true\">"));
		Assert.assertTrue(xml
				.contains("<property id=\"userId\" required=\"true\"><value>brian</value></property></properties></system></systems></profile></profiles></gtx-profile>"));
	}
	
	@Test
	public void testGtxProfileUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = TestUtil.readFileAsString("gtx-profile0.xml");

		GtxProfile gp = unmarshal(xmlIn, GtxProfile.class);
		Assert.assertNotNull(gp);
		Assert.assertEquals("user1", gp.getUid());
		Assert.assertEquals(1, gp.getProfiles().size());
		
		net.gltd.gtms.profiler.gtx.profile.Profile p = gp.getProfile("office");
		Assert.assertNotNull(p);
		
		GtxSystem sys = p.getGtxSystem("speakerbus");
		Assert.assertNotNull(sys);
		Assert.assertEquals("telephony", sys.getCategory());
		Assert.assertEquals("speakerbus", sys.getType());
		Assert.assertEquals("speakerbus", sys.getId());
		Assert.assertEquals(true, sys.isEnabled());
		
		Set<net.gltd.gtms.profiler.gtx.profile.Property> ps = sys.getProperties();
		Assert.assertEquals(2, sys.getProperties().size());
		
		net.gltd.gtms.profiler.gtx.profile.Property p1 = sys.getProperty("extension");
		Assert.assertEquals(false, p1.getRequired());
//		Assert.assertEquals("1234", p1.getValue());
		
		net.gltd.gtms.profiler.gtx.profile.Property p2 = sys.getProperty("deviceId");
		Assert.assertEquals(true, p2.getRequired());
		Assert.assertEquals("10", p2.getValue());
		
		
		String xmlOut = marshal(gp);
		Assert.assertNotNull(xmlOut);
		logger.debug(xmlOut);
	}


}
