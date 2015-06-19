package net.gltd.gtms.client.openlink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile.Location.Auth.AuthType;
import net.gltd.gtms.extension.openlink.callstatus.Call;
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
import net.gltd.gtms.extension.openlink.command.MakeCall.MakeCallIn.MakeCallFeature;
import net.gltd.gtms.extension.openlink.command.RequestAction.RequestActionAction;
import net.gltd.gtms.extension.openlink.command.SetFeatures;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatusFeature;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Feature.FeatureType;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.features.callback.Callback;
import net.gltd.gtms.extension.openlink.features.dtmf.Dtmf;
import net.gltd.gtms.extension.openlink.features.voicemessage.VoiceMessage;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.gtms.client.TestUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class OpenlinkClientMvmTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	private static final String USERNAME = "gary";
	private static final String PASSWORD = "gary";
	private static final String RESOURCE = "office";

	private static final String DOMAIN = "garyvms.gltd.local";
	private static final String HOST = "garyvms.gltd.local";

	private static final String SYSTEM = "vmstsp";

	private static final String DEFAULT_PROFILE = USERNAME + "_office";

	private static final String SYSTEM_AND_DOMAIN = SYSTEM + "." + DOMAIN;

	private static final String DESTINATION = "662";

	private OpenlinkClient client = null;

	public OpenlinkClientMvmTest() throws JAXBException, XMLStreamException {
		super(Property.class, net.gltd.gtms.extension.openlink.properties.Property.class, Headers.class, Header.class, Event.class, Command.class,
				Note.class, Message.class, IQ.class, IoData.class, Profiles.class, Profile.class, Action.class, Interests.class, Interest.class,
				Features.class, Feature.class, CallStatus.class, Call.class, CallerCallee.class, CallFeature.class, Participant.class,
				CallAction.class, AddThirdParty.class, AnswerCall.class, ClearCall.class, ClearConnection.class, ConferenceFail.class,
				ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class, JoinCall.class,
				PrivateCall.class, PublicCall.class, RemoveThirdParty.class, RetrieveCall.class, SendDigit.class, SendDigits.class,
				SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class, StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class,
				SetFeatures.class,

				DeviceStatus.class, DeviceStatusFeature.class,

				AudioFile.class, AudioFile.Location.class, AudioFile.Location.Auth.class,

				VoiceMessage.class, Callback.class, Callback.Active.class, Dtmf.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = TestUtil.initializeConsoleLogger("net.gltd.gtms", TestUtil.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");
		client = new OpenlinkClient(USERNAME, PASSWORD, RESOURCE, DOMAIN, HOST);
		client.setDebug(true);
		client.addCallListener(this.getCallListener());
		client.connect();
	}

	@After
	public void shutdown() throws Exception {
		logger.debug("SHUTDOWN");
		if (this.client != null) {
			client.disconnect();
		}
		LogManager.shutdown();
	}

	public CallListener getCallListener() {
		return new CallListener() {
			@Override
			public void callEvent(Collection<Call> calls) {
				try {
					logger.debug("CALL EVENT: " + marshal(calls));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	public boolean isConnected() {
		boolean result = this.client != null && this.client.isConnected();
		logger.debug("CONNECTED: " + result);
		return result;
	}

	public Profile getPrimaryProfile(String to) throws Exception {
		Profile result = null;
		Assert.assertTrue(isConnected());
		Collection<Profile> profiles = this.client.getProfiles(SYSTEM_AND_DOMAIN);
		Assert.assertNotNull(profiles);
		Assert.assertTrue(profiles.size() > 0);

		if (DEFAULT_PROFILE != null) {
			for (Profile p : profiles) {
				if (DEFAULT_PROFILE.equals(p.getId())) {
					result = p;
					break;
				}
			}
		} else {
			result = profiles.iterator().next();
		}

		return result;
	}

	public Interest getPrimaryInterest(String to, String profileId) throws Exception {
		Assert.assertTrue(isConnected());
		Collection<Profile> profiles = this.client.getProfiles(SYSTEM_AND_DOMAIN);
		Assert.assertNotNull(profiles);
		Assert.assertTrue(profiles.size() > 0);
		for (Profile p : profiles) {
			if (profileId.equals(p.getId())) {
				Collection<Interest> interests = this.client.getInterests(SYSTEM_AND_DOMAIN, p);
				Assert.assertTrue(interests.size() > 0);
				return interests.iterator().next();
			}
		}
		return null;
	}

	@Test
	public void checkIfConnected() throws Exception {
		Assert.assertTrue(isConnected());
		this.client.disconnect();
		Assert.assertFalse(isConnected());
	}

	@Test
	public void getProfilesAsAdmin() throws Exception {
		if (this.USERNAME.equals("admin")) {
			try {
				Assert.assertTrue(isConnected());
				Collection<Profile> profiles = this.client.getProfiles(SYSTEM_AND_DOMAIN, Jid.valueOf("betty.bidder" + "@" + this.DOMAIN));
				Assert.assertNotNull(profiles);
				Assert.assertTrue(profiles.size() > 0);
				logger.debug(marshal(profiles));
				for (Profile p : profiles) {
					Assert.assertNotNull(p.getId());
					Assert.assertNotNull(p.getDevice());
					logger.debug("PROFILES: " + p.getId() + " " + p.getLabel() + " " + p.getDevice());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}
	}

	@Test
	public void getProfiles() throws Exception {
		try {
			Assert.assertTrue(isConnected());
			Collection<Profile> profiles = this.client.getProfiles(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(profiles);
			Assert.assertTrue(profiles.size() > 0);
			logger.debug(marshal(profiles));
			for (Profile p : profiles) {
				Assert.assertNotNull(p.getId());
				Assert.assertNotNull(p.getDevice());
				logger.debug("PROFILES: " + p.getId() + " " + p.getLabel() + " " + p.getDevice());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getFeatures() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Collection<Feature> features = this.client.getFeatures(SYSTEM_AND_DOMAIN, p);
			Assert.assertTrue(features.size() > 0);
			logger.debug(marshal(features));
			for (Feature f : features) {
				Assert.assertNotNull(f.getId());
				Assert.assertNotNull(f.getLabel());
				Assert.assertNotNull(f.getType());
				logger.debug("FEATURE: " + f.getId() + " " + f.getLabel() + " " + f.getType());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void setFeatures() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Collection<Feature> features = this.client.getFeatures(SYSTEM_AND_DOMAIN, p);
			Feature feature = null;
			for (Feature f : features) {
				if ("CallBack".equals(f.getId())) {
					feature = f;
				}
			}
			this.client.setFeatures(SYSTEM_AND_DOMAIN, p, feature, "true", DESTINATION);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getInterests() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Collection<Interest> interests = this.client.getInterests(SYSTEM_AND_DOMAIN, p);
			Assert.assertTrue(interests.size() > 0);
			logger.debug(marshal(interests));
			for (Interest i : interests) {
				Assert.assertNotNull(i.getId());
				Assert.assertNotNull(i.getLabel());
				Assert.assertNotNull(i.getType());
				logger.debug("INTEREST: " + i.getId() + " " + i.getLabel() + " " + i.getType());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void subscribeInterest() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Subscription result = this.client.subscribe(i);
			Assert.assertNotNull(result);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
			Assert.assertFalse(subs.isEmpty());
			logger.debug("SUBSCRIPTION " + i.getId() + " ID: " + result.getSubId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void unsubscribeInterest() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Subscription result = this.client.subscribe(i);
			Assert.assertNotNull(result);
			this.client.unsubscribe(i);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
			// Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents
			// subscriptions from removing correctly
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getSubscriptions() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Thread.sleep(5000);
			Subscription result = this.client.subscribe(i);
			Assert.assertNotNull(result);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
			Assert.assertFalse(subs.isEmpty());
			this.client.unsubscribe(i);
			subs = this.client.getSubscriptions(i);
			logger.debug("SUBSCRIPTIONS: SIZE: " + subs.size());
			// Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents
			// subscriptions from removing correctly
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	public Set<String> getFeatureIds(FeatureType type, int limit) throws Exception {

		Set<String> result = new HashSet<String>();
		Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
		Assert.assertNotNull(p);

		Set<String> messageIds = new HashSet<String>();
		Collection<Feature> features = this.client.getFeatures(SYSTEM_AND_DOMAIN, p);
		Assert.assertTrue(features.size() > 0);
		logger.debug(marshal(features));
		for (Feature f : features) {
			Assert.assertNotNull(f.getId());
			Assert.assertNotNull(f.getLabel());
			Assert.assertNotNull(f.getType());
			logger.debug("FEATURE: " + f.getId() + " " + f.getLabel() + " " + f.getType());
			if (type.name().equals(f.getType())) {
				messageIds.add(f.getId());
			}
		}

		if (limit > 0) {
			for (String id : messageIds) {
				result.add(id);
				if (result.size() == limit) {
					break;
				}
			}
		} else {
			result = messageIds;
		}

		return result;
	}

	public Set<String> getMessageIds(int limit) throws Exception {
		return this.getFeatureIds(FeatureType.VoiceMessage, limit);
	}

	public Set<String> getPlaylistIds(int limit) throws Exception {
		return this.getFeatureIds(FeatureType.VoiceMessagePlaylist, limit);
	}

	public Set<String> getMessageIds() throws Exception {
		return this.getFeatureIds(FeatureType.VoiceMessage, 0);
	}

	public Set<String> getPlaylistIds() throws Exception {
		return this.getFeatureIds(FeatureType.VoiceMessagePlaylist, 0);
	}

	@Test
	public void saveMessage() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			AudioFile a1 = new AudioFile();
			a1.setCreationDate("2015-02-19 12:11:19.522");
			a1.setLabel("Test Label 2");
			a1.setLifetime("0");
			a1.setMsgLength("47.932");
			a1.setSize("1024");

			net.gltd.gtms.extension.openlink.properties.Property p1 = new net.gltd.gtms.extension.openlink.properties.Property();
			p1.setId("callid");
			p1.setType("system");
			p1.setValue("NICEID123455");

			net.gltd.gtms.extension.openlink.properties.Property p2 = new net.gltd.gtms.extension.openlink.properties.Property();
			p2.setId("comment");
			p2.setType("user");
			p2.setValue("My Comment");

			a1.getProperties().add(p1);
			a1.getProperties().add(p2);

			AudioFile.Location l = new AudioFile.Location();
			l.setUrl("http://vss01/81038909_1_20141120_182223.wav");

			AudioFile.Location.Auth auth = new AudioFile.Location.Auth();
			auth.setType(AuthType.required);
			auth.setUserid("user1");
			auth.setPassword("mypass");
			l.setAuth(auth);

			a1.setLocation(l);

			HashSet<AudioFile> audioFiles = new HashSet<AudioFile>();
			audioFiles.add(a1);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().save(SYSTEM_AND_DOMAIN, p,
					"Random Message Label " + RandomStringUtils.randomAlphanumeric(10), audioFiles);
			Assert.assertEquals(1, messages.size());
			for (VoiceMessage vm : messages) {
				logger.debug("VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getStatus());
				// Assert.assertNotNull(vm.getAction()); // not being returned - filed bug against system
				Assert.assertNotNull(vm.getMsgLength());
				Assert.assertNotNull(vm.getCreationDate());
				Assert.assertEquals(2, vm.getProperties().size());
				for (net.gltd.gtms.extension.openlink.properties.Property tmpP : vm.getProperties()) {
					if ("callid".equals(tmpP.getId())) {
						p1 = tmpP;
					} else if ("comment".equals(tmpP.getId())) {
						p2 = tmpP;
					}
				}
				Assert.assertEquals("system", p1.getType());
				Assert.assertEquals("NICEID123455", p1.getValue());

				Assert.assertEquals("user", p2.getType());
				Assert.assertEquals("My Comment", p2.getValue());
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void recordMessage() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().record(SYSTEM_AND_DOMAIN, p,
					"Random Message Label " + RandomStringUtils.randomAlphanumeric(10));
			for (VoiceMessage vm : messages) {
				logger.debug("VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getStatus());
				Assert.assertNotNull(vm.getAction());
				Assert.assertNotNull(vm.getExten());
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void playbackMessage() {

		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = this.getMessageIds(3);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().playback(SYSTEM_AND_DOMAIN, p, messageIds);

			String extension = null;
			if (messages.size() > 0) {
				extension = messages.iterator().next().getExten();
			}

			logger.debug("PLAYBACK EXTENSION: " + extension);
			Assert.assertTrue(StringUtils.isNumericSpace(extension));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void archiveMessage() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = this.getMessageIds();
			int numMessages = messageIds.size();

			messageIds = this.getMessageIds(2);

			this.client.getVoiceMessageHandler().archive(SYSTEM_AND_DOMAIN, p, messageIds);

			messageIds = this.getMessageIds();
			int numMessagesAfter = messageIds.size();

			Assert.assertEquals(numMessages - 2, numMessagesAfter);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void createPlaylist() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = this.getMessageIds(3);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().create(SYSTEM_AND_DOMAIN, p,
					"Random Playlist Label " + RandomStringUtils.randomAlphanumeric(10), messageIds);

			String playlistId = null;
			if (messages.size() > 0) {
				playlistId = messages.iterator().next().getId();
			}
			logger.debug("PLAYLIST: " + playlistId);
			Assert.assertNotNull(playlistId);

			Set<String> playlistIds = new HashSet<String>();
			playlistIds.add(playlistId);

			Collection<VoiceMessage> playlistMessages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, playlistIds);
			Assert.assertEquals(messageIds.size(), playlistMessages.size());
			for (VoiceMessage vm : playlistMessages) {
				logger.debug("PLAYLIST: " + playlistId + " VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getId());
				Assert.assertNotNull(vm.getLabel());
				Assert.assertNotNull(vm.getMsgLength());
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void queryMessage() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = this.getMessageIds(3);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, messageIds);
			for (VoiceMessage vm : messages) {
				logger.debug("VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getId());
				Assert.assertNotNull(vm.getLabel());
				Assert.assertNotNull(vm.getMsgLength());
			}

			Set<String> playlistIds = this.getPlaylistIds(3);

			for (String pid : playlistIds) {
				Collection<VoiceMessage> playlistMessages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, messageIds);
				for (VoiceMessage vm : playlistMessages) {
					logger.debug("PLAYLIST: " + pid + " VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
					Assert.assertNotNull(vm.getId());
					Assert.assertNotNull(vm.getLabel());
					Assert.assertNotNull(vm.getMsgLength());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void editMessage() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = this.getMessageIds(1);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, messageIds);
			for (VoiceMessage vm : messages) {
				logger.debug("VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getId());
				Assert.assertNotNull(vm.getLabel());
				Assert.assertNotNull(vm.getMsgLength());
			}

			String messageLabel = "Random Message Label " + RandomStringUtils.randomAlphanumeric(10);

			Collection<VoiceMessage> playlistMessages = this.client.getVoiceMessageHandler().edit(SYSTEM_AND_DOMAIN, p, messageLabel, messageIds);

			messages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, messageIds);
			for (VoiceMessage vm : messages) {
				logger.debug("VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getId());
				Assert.assertEquals(messageLabel, vm.getLabel());
				Assert.assertNotNull(vm.getMsgLength());
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void queryRandom() {
		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = new HashSet<String>();
			messageIds.add("PL1033");

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, messageIds);
			for (VoiceMessage vm : messages) {
				logger.debug("VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
				Assert.assertNotNull(vm.getId());
				Assert.assertNotNull(vm.getLabel());
				Assert.assertNotNull(vm.getMsgLength());
			}

			Set<String> playlistIds = this.getPlaylistIds(3);

			for (String pid : playlistIds) {
				Collection<VoiceMessage> playlistMessages = this.client.getVoiceMessageHandler().query(SYSTEM_AND_DOMAIN, p, messageIds);
				for (VoiceMessage vm : playlistMessages) {
					logger.debug("PLAYLIST: " + pid + " VOICEMESSAGE: " + vm.getId() + " " + vm.toString());
					Assert.assertNotNull(vm.getId());
					Assert.assertNotNull(vm.getLabel());
					Assert.assertNotNull(vm.getMsgLength());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void playbackMessageMakeCallHangup() {

		try {
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);

			Set<String> messageIds = this.getMessageIds(3);

			Collection<VoiceMessage> messages = this.client.getVoiceMessageHandler().playback(SYSTEM_AND_DOMAIN, p, messageIds);

			String extension = null;
			if (messages.size() > 0) {
				extension = messages.iterator().next().getExten();
			}

			logger.debug("PLAYBACK EXTENSION: " + extension);
			Assert.assertTrue(StringUtils.isNumericSpace(extension));

			this.client.addCallListener(new CallListener() {
				@Override
				public void callEvent(Collection<Call> calls) {
					for (Call c : calls) {
						Assert.assertNotNull(c.getId());
						logger.debug("CALL EV: " + c.getId() + c);
					}
				}
			});

			this.client.addDeviceListener(new DeviceListener() {
				@Override
				public void deviceEvent(String profile, Collection<DeviceStatusFeature> features) {
					for (DeviceStatusFeature f : features) {
						Assert.assertNotNull(f.getId());
						logger.debug("DEVICE EV: " + f.getId() + f);
					}
				}
			});

			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Collection<Call> calls = this.client.makeCall(SYSTEM_AND_DOMAIN, i, extension, null, new HashSet<Property>());

			Thread.sleep(1000);
			Assert.assertNotNull(calls);
			Assert.assertTrue(calls.size() > 0);

			for (Call c : calls) {
				Assert.assertNotNull(c.getId());
				logger.debug("CALL: " + c.getId() + c);
				logger.debug("CALL : " + c.getId() + marshal(c));
				for (Property p2 : c.getOriginatorRef()) {
					logger.debug("CALL PROPERTY: ID: " + p2.getId() + " : " + p2.getValue());
				}
			}
			Call call = calls.iterator().next();
			Thread.sleep(10000);

			this.client.requestAction(SYSTEM_AND_DOMAIN, call, RequestActionAction.ClearConnection, null, null);
			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void makeCallAndRequestActionSimple() {
		try {
			this.client.addCallListener(new CallListener() {
				@Override
				public void callEvent(Collection<Call> calls) {
					for (Call c : calls) {
						Assert.assertNotNull(c.getId());
						logger.debug("CALL EV: " + c.getId() + c);
						for (Property p2 : c.getOriginatorRef()) {
							logger.debug("CALL PROPERTY: ID: " + p2.getId() + " : " + p2.getValue());
						}
						try {
							logger.debug("CALL EV: " + c.getId() + marshal(c));
						} catch (Exception e) {
							e.printStackTrace();
							Assert.fail(e.getMessage());
						}
					}
				}
			});
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Collection<Call> calls = this.client.makeCall(SYSTEM_AND_DOMAIN, i, DESTINATION, null, new HashSet<Property>());
			Thread.sleep(1000);
			Assert.assertNotNull(calls);
			Assert.assertTrue(calls.size() > 0);
			for (Call c : calls) {
				Assert.assertNotNull(c.getId());
				logger.debug("CALL: " + c.getId() + c);
				logger.debug("CALL : " + c.getId() + marshal(c));
				for (Property p2 : c.getOriginatorRef()) {
					logger.debug("CALL PROPERTY: ID: " + p2.getId() + " : " + p2.getValue());
				}
			}
			Call call = calls.iterator().next();
			Thread.sleep(8000);
			this.client.requestAction(SYSTEM_AND_DOMAIN, call, RequestActionAction.ClearConnection, null, null);
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void makeCallAndRequestActionFull() {
		try {
			this.client.addCallListener(new CallListener() {
				@Override
				public void callEvent(Collection<Call> calls) {
					for (Call c : calls) {
						Assert.assertNotNull(c.getId());
						logger.debug("CALL EV: " + c.getId() + c);
						// Assert.assertEquals(1, c.getFeatures().size());
						// for (CallFeature cf : c.getFeatures()) {
						// Assert.assertTrue(cf.getId().equals("CALLBACK"));
						// Assert.assertTrue(cf.isValue1());
						// }

						Assert.assertTrue(c.getOriginatorRef().size() > 0);
						int validOriginatorRefCount = 0;
						for (Property p : c.getOriginatorRef()) {
							logger.debug("CALL PROPERTY: ID: " + p.getId() + " : " + p.getValue());
							if ("dummy-id".equals(p.getId())) {
								Assert.assertEquals("dummy-value-ABC-1234", p.getValue());
								validOriginatorRefCount++;
							}
						}
						Assert.assertEquals(1, validOriginatorRefCount);

						if (CallState.ConnectionCleared != c.getState()) {
							Assert.assertTrue(c.getActions().size() > 0);
							for (CallAction a : c.getActions()) {
								Assert.assertTrue(a instanceof CallAction);
								Assert.assertTrue(a.getId().equals("ClearConnection") || a.getId().equals("ClearCall")
										|| a.getId().equals("AnswerCall"));
							}
						}

						Assert.assertTrue(c.getParticipants().size() > 0);
						for (Participant p : c.getParticipants()) {
							Assert.assertTrue(p.getJid() != null);
							Assert.assertTrue(p.getTimestamp() != null);
							Assert.assertTrue(p.getDirection() != null);
							// Assert.assertTrue(p.getType() != null); // avaya plugin doesn't send inactive for
							// non-active parties at the moment
						}

						for (Property p2 : c.getOriginatorRef()) {
							logger.debug("CALL PROPERTY: ID: " + p2.getId() + " : " + p2.getValue());
						}
						Assert.assertTrue(c.getOriginatorRef().size() > 0);
						for (Property p : c.getOriginatorRef()) {
							Assert.assertNotNull(p.getId());
							Assert.assertNotNull(p.getValue());
						}

						try {
							logger.debug("CALL EV: " + c.getId() + marshal(c));
						} catch (Exception e) {
							e.printStackTrace();
							Assert.fail(e.getMessage());
						}
					}
				}
			});
			subscribeInterest();
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);

			Set<MakeCallFeature> features = new HashSet<MakeCallFeature>();
			MakeCallFeature feature = new MakeCallFeature();
			feature.setId("CALLBACK");
			feature.setValue1("true");
			features.add(feature);

			Set<Property> originatorReferences = new HashSet<Property>();
			Property p1 = new Property();
			p1.setId("dummy-id");
			p1.setValue("dummy-value-ABC-1234");
			originatorReferences.add(p1);

			Collection<Call> calls = this.client.makeCall(SYSTEM_AND_DOMAIN, i, DESTINATION, features, originatorReferences);
			Thread.sleep(1000);
			Assert.assertNotNull(calls);
			Assert.assertTrue(calls.size() > 0);
			for (Call c : calls) {
				Assert.assertNotNull(c.getId());
				logger.debug("CALL: " + c.getId() + c);
				logger.debug("CALL : " + c.getId() + marshal(c));
				for (Property p2 : c.getOriginatorRef()) {
					logger.debug("CALL PROPERTY: ID: " + p2.getId() + " : " + p2.getValue());
				}
			}
			Call call = calls.iterator().next();
			Thread.sleep(8000);
			this.client.requestAction(SYSTEM_AND_DOMAIN, call, RequestActionAction.ClearConnection, null, null);
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void debugTrace() {
		this.subscribeInterest();
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}
	}

}
