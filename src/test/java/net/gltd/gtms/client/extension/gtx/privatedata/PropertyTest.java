package net.gltd.gtms.client.extension.gtx.privatedata;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.profiler.gtx.profile.GtxProfile;
import net.gltd.gtms.profiler.gtx.profile.GtxSystem;
import net.gltd.gtms.profiler.gtx.profile.Property;
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
import net.gltd.gtms.extension.openlink.features.dtmf.Dtmf;
import net.gltd.gtms.extension.openlink.features.voicemessage.VoiceMessage;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class PropertyTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public PropertyTest() throws JAXBException, XMLStreamException {
		super(Headers.class, Header.class, Event.class, Command.class, Note.class, Message.class, IQ.class, IoData.class, Profiles.class,
				Profile.class, Action.class, Interests.class, Interest.class, Features.class, Feature.class, CallStatus.class, Call.class,
				CallerCallee.class, CallFeature.class, Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class, ClearCall.class,
				ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class, HoldCall.class,
				IntercomTransfer.class, JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class, RetrieveCall.class,
				SendDigit.class, SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class, StartVoiceDrop.class,
				StopVoiceDrop.class, TransferCall.class, DeviceStatus.class, DeviceStatusFeature.class, VoiceMessage.class, Callback.class,
				Callback.Active.class, Dtmf.class, net.gltd.gtms.profiler.gtx.profile.Feature.class, GtxProfile.class, GtxSystem.class,
				net.gltd.gtms.profiler.gtx.profile.Profile.class, Property.class);
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
	public void testPropertyMarshal() throws XMLStreamException, JAXBException {
		Property p = new Property();
		Assert.assertNotNull(p);

		p.setId("deviceId");
		p.setRequired(true);
		p.setValue("leon");

		String xml = marshal(p);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		// Assert.assertTrue(xml.contains(""));

	}

	@Test
	public void testPropertyUnmarshal() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = TestUtil.readFileAsString("gtx-profile-property.xml");

		Property p = unmarshal(xmlIn, Property.class);
		Assert.assertNotNull(p);
		Assert.assertEquals("deviceId", p.getId());
		Assert.assertEquals("leon", p.getValue());
		Assert.assertEquals(true, p.getRequired());
	}

}
