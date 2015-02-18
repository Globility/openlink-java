package net.gltd.gtms.client.extension.openlink.audiofiles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile.Location.Auth.AuthType;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFiles;
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
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageAction;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageFeature;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageIn;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus;
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
import net.gltd.gtms.extension.openlink.properties.Property;
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
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class AudioFilesTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public AudioFilesTest() throws JAXBException, XMLStreamException {
		super(Property.class, Headers.class, Header.class, Event.class, Command.class, Note.class, Message.class, IQ.class, IoData.class,
				Profiles.class, Profile.class, Action.class, Interests.class, Interest.class, Features.class, Feature.class, CallStatus.class,
				Call.class, CallerCallee.class, CallFeature.class, Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class,
				ClearCall.class, ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class,
				HoldCall.class, IntercomTransfer.class, JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class,
				RetrieveCall.class, SendDigit.class, SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class,
				StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class, DeviceStatus.class, DeviceStatusFeature.class, VoiceMessage.class,
				Callback.class, Callback.Active.class, Dtmf.class,

				ManageVoiceMessage.class, ManageVoiceMessageFeature.class, ManageVoiceMessageIn.class,

				Property.class,

				AudioFiles.class, AudioFile.class);
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
	public void testAudioFile() throws XMLStreamException, JAXBException, FileNotFoundException, IOException {
		Collection<VoiceMessage> messages = new ArrayList<VoiceMessage>();

		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile("gary_profile");
		mvm.getIn().setAction(ManageVoiceMessageAction.Save);
		mvm.getIn().setLabel("Head Label");

		AudioFile a1 = new AudioFile();
		a1.setCreationDate("2011-09-05 15:52:22.0");
		a1.setLabel("Sub Label");
		a1.setLifetime("10");
		a1.setMsgLength("42.83");
		a1.setSize("2048");

		Property p1 = new Property();
		p1.setId("callId");
		p1.setType("system");
		p1.setValue("12345");

		Property p2 = new Property();
		p2.setId("comment");
		p2.setType("user");
		p2.setValue("My Comment");

		a1.getProperties().add(p1);
		a1.getProperties().add(p2);

		AudioFile.Location l = new AudioFile.Location();
		l.setUrl("http://www.example.com/myfile.wav");

		AudioFile.Location.Auth auth = new AudioFile.Location.Auth();
		auth.setType(AuthType.required);
		auth.setPassword("mypass");
		auth.setUserid("gary");
		l.setAuth(auth);

		a1.setLocation(l);

		mvm.getIn().getAudioFiles().add(a1);

		IQ iq = new IQ(Jid.valueOf("vmstsp.example.com"), IQ.Type.SET, mvm);

		String xml = marshal(iq);
		Assert.assertNotNull(xml);
		logger.debug("XML : " + xml);

		// Assert.assertTrue(xml.contains("<devicestatus xmlns=\"http://xmpp.org/protocol/openlink:01:00:00#device-status\">"));
		// Assert.assertTrue(xml.contains("<voicemessage xmlns=\"http://xmpp.org/protocol/openlink:01:00:00/features#voice-message\">"));
		// Assert.assertTrue(xml.contains("<status>ok</status>"));
		// Assert.assertTrue(xml.contains("<destination>6004</destination>"));
		// Assert.assertTrue(xml.contains("<action>VoiceDrop</action>"));
		// Assert.assertTrue(xml.contains("<creationdate>2011-09-05 15:52:22.0</creationdate>"));
	}

	@Test
	public void testDeviceStatusUnmarshal3() throws FileNotFoundException, XMLStreamException, JAXBException, IOException {
		String xmlIn = StringUtil.readFileAsString("ol-devicestatus-audiofiles2.xml");

		IQ iq = unmarshal(xmlIn, IQ.class);

		String xmlOut = marshal(iq);
		Assert.assertNotNull(xmlOut);
		logger.debug(xmlOut);

		Command c = iq.getExtension(Command.class);
		Assert.assertNotNull(c);
		Assert.assertFalse(c.getExtensions().isEmpty());
		IoData ioData = c.getExtension(IoData.class);
		Assert.assertNotNull(ioData);
		Assert.assertNotNull(ioData.getOut());

		Assert.assertTrue(ioData.getOut().getExtension(DeviceStatus.class) instanceof DeviceStatus);
		DeviceStatus ds = ioData.getOut().getExtension(DeviceStatus.class);

		Assert.assertEquals("gary_office", ds.getProfile());

		Assert.assertTrue(ds.getFeatures().size() == 1);
		VoiceMessage vm1 = null;

		for (DeviceStatusFeature f : ds.getFeatures()) {
			Assert.assertTrue(f.getValue() instanceof VoiceMessage);
			if ("MK1276".equals(f.getId())) {
				vm1 = (VoiceMessage) f.getValue();
			}
		}
		Assert.assertNotNull(vm1);

		Assert.assertEquals(DeviceStatusStatus.ok, vm1.getStatus());
		Assert.assertEquals("Test Label", vm1.getLabel());
		Assert.assertEquals("47.932", vm1.getMsgLength());
		Assert.assertEquals("2015-02-19 12:11:19.522", vm1.getCreationDate());

		Assert.assertNotNull(vm1.getProperties());

		Property p1 = null;
		Property p2 = null;
		for (Property p : vm1.getProperties()) {
			if ("callid".equals(p.getId())) {
				p1 = p;
			} else {
				p2 = p;
			}
		}

		Assert.assertEquals(2, vm1.getProperties().size());

		Assert.assertNotNull(p1);
		Assert.assertEquals("NICEID1234", p1.getValue());

		Assert.assertNotNull(p2);
		Assert.assertEquals("Comment", p2.getValue());

	}
}
