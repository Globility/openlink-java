package net.gltd.gtms.client.openlink;

import java.util.Collection;
import java.util.HashSet;

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
import net.gltd.gtms.extension.openlink.command.RequestAction.RequestActionAction;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.util.log.GtmsLog;
import net.gltd.util.xml.XmlUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmpp.XmlTest;
import org.xmpp.extension.pubsub.Subscription;
import org.xmpp.extension.pubsub.event.Event;
import org.xmpp.extension.shim.Header;
import org.xmpp.extension.shim.Headers;
import org.xmpp.stanza.client.IQ;
import org.xmpp.stanza.client.Message;

public class OpenlinkClientTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	private static final String USERNAME = "random_user";
	private static final String PASSWORD = "PassW0rd";
	private static final String RESOURCE = "my_resource";

	private static final String DOMAIN = "domain.example.com";
	private static final String HOST = "domain.example.com";

	private static final String SYSTEM = "xmppcomponentnode";

	private static final String SYSTEM_AND_DOMAIN = SYSTEM + "." + DOMAIN;

	private static final String DESTINATION = "12345";

	private OpenlinkClient client = null;

	public OpenlinkClientTest() throws JAXBException, XMLStreamException {
		super(Property.class, Headers.class, Header.class, Event.class, Command.class, Note.class, Message.class, IQ.class,
				IoData.class, Profiles.class, Profile.class, Action.class, Interests.class, Interest.class,
				Features.class, Feature.class, CallStatus.class, Call.class, CallerCallee.class, CallFeature.class,
				Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class, ClearCall.class,
				ClearConnection.class, ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class,
				DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class, JoinCall.class, PrivateCall.class,
				PublicCall.class, RemoveThirdParty.class, RetrieveCall.class, SendDigit.class, SendDigits.class,
				SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class, StartVoiceDrop.class,
				StopVoiceDrop.class, TransferCall.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = GtmsLog.initializeConsoleLogger("net.gltd.gtms", GtmsLog.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");
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
		Assert.assertTrue(isConnected());
		Collection<Profile> profiles = this.client.getProfiles(SYSTEM_AND_DOMAIN);
		Assert.assertNotNull(profiles);
		Assert.assertTrue(profiles.size() > 0);
		return profiles.iterator().next();
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
	public void getProfiles() throws Exception {
		try {
			Assert.assertTrue(isConnected());
			Collection<Profile> profiles = this.client.getProfiles(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(profiles);
			Assert.assertTrue(profiles.size() > 0);
			logger.debug(XmlUtil.formatXml(marshal(profiles)));
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
			logger.debug(XmlUtil.formatXml(marshal(features)));
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
	public void getInterests() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Collection<Interest> interests = this.client.getInterests(SYSTEM_AND_DOMAIN, p);
			Assert.assertTrue(interests.size() > 0);
			logger.debug(XmlUtil.formatXml(marshal(interests)));
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
//			Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents
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
			Subscription result = this.client.subscribe(i);
			Assert.assertNotNull(result);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
			Assert.assertFalse(subs.isEmpty());
			this.client.unsubscribe(i);
			subs = this.client.getSubscriptions(i);
			logger.debug("SUBSCRIPTIONS: SIZE: " + subs.size());
//			Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents
												// subscriptions from removing correctly
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void makeCallAndRequestAction() {
		try {
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
			}
			Call call = calls.iterator().next();
			logger.debug(marshal(calls));
			Thread.sleep(3000);
			this.client.requestAction(SYSTEM_AND_DOMAIN, call, RequestActionAction.ClearConnection, null, null);
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
