package net.gltd.gtms.client.openlink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.IntegrationTest;
import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
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
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;
import net.gltd.gtms.profiler.gtx.profile.GtxProfile;
import net.gltd.gtms.profiler.gtx.profile.GtxSystem;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;
import rocks.xmpp.extensions.privatedata.PrivateDataManager;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

@Category(IntegrationTest.class)
public class OpenlinkClientIntegrationTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");
	public static final String CLIENT_PROPERTIES = "client.properties";

	private String username;
	private String domain;
	private String resource;

	private String systemAndDomain;

	private OpenlinkClient client = null;

	private Properties clientProperties;

	public OpenlinkClientIntegrationTest() throws JAXBException, XMLStreamException {
		super(Property.class, net.gltd.gtms.extension.openlink.properties.Property.class, Headers.class, Header.class, Event.class, Command.class,
				Note.class, Message.class, IQ.class, IoData.class, Profiles.class, Profile.class, Action.class, Interests.class, Interest.class,
				Features.class, Feature.class, CallStatus.class, Call.class, CallerCallee.class, CallFeature.class, Participant.class,
				CallAction.class, AddThirdParty.class, AnswerCall.class, ClearCall.class, ClearConnection.class, ConferenceFail.class,
				ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class, JoinCall.class,
				PrivateCall.class, PublicCall.class, RemoveThirdParty.class, RetrieveCall.class, SendDigit.class, SendDigits.class,
				SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class, StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class,

				net.gltd.gtms.profiler.gtx.profile.Feature.class, GtxProfile.class, GtxSystem.class,
				net.gltd.gtms.profiler.gtx.profile.Profile.class, net.gltd.gtms.profiler.gtx.profile.Property.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = TestUtil.initializeConsoleLogger("net.gltd.gtms", TestUtil.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");

		this.clientProperties = TestUtil.getProperties(this.getClass(), CLIENT_PROPERTIES);
		this.username = clientProperties.getProperty("client.xmpp.username");
		this.domain = clientProperties.getProperty("client.xmpp.domain");
		this.resource = clientProperties.getProperty("client.xmpp.resource");

		this.systemAndDomain = clientProperties.getProperty("client.xmpp.system") + "." + this.domain;

		client = new OpenlinkClient(this.username, clientProperties.getProperty("client.xmpp.password"), this.resource, this.domain,
				clientProperties.getProperty("client.xmpp.host"));
		client.setDebug(true);
		client.setSecure(true);
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
		Collection<Profile> profiles = this.client.getProfiles(this.systemAndDomain);
		Assert.assertNotNull(profiles);
		Assert.assertTrue(profiles.size() > 0);
		return profiles.iterator().next();
	}

	public Interest getPrimaryInterest(String to, String profileId) throws Exception {
		Assert.assertTrue(isConnected());
		Collection<Profile> profiles = this.client.getProfiles(this.systemAndDomain);
		Assert.assertNotNull(profiles);
		Assert.assertTrue(profiles.size() > 0);
		for (Profile p : profiles) {
			if (profileId.equals(p.getId())) {
				Collection<Interest> interests = this.client.getInterests(this.systemAndDomain, p);
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
		if (this.username.equals("admin")) {
			try {
				Assert.assertTrue(isConnected());
				Collection<Profile> profiles = this.client.getProfiles(this.systemAndDomain, Jid.valueOf("betty.bidder" + "@" + this.domain));
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
			Collection<Profile> profiles = this.client.getProfiles(this.systemAndDomain);
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
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Collection<Feature> features = this.client.getFeatures(this.systemAndDomain, p);
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
	public void getFeaturesError() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = new Profile();
			p.setId("this_profile_does_not_exist");
			Exception e = null;
			try {
				this.client.getFeatures(this.systemAndDomain, p);
			} catch (XmppException xe) {
				e = xe;
				xe.printStackTrace();
			}
			if (e == null) {
				Assert.fail("Expected XMPPException to be thrown");
			}
			System.out.println("EXCEPTION MESSAGE: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getInterests() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Collection<Interest> interests = this.client.getInterests(this.systemAndDomain, p);
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
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(this.systemAndDomain, p.getId());
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
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(this.systemAndDomain, p.getId());
			Assert.assertNotNull(i);
			Subscription result = this.client.subscribe(i);
			Assert.assertNotNull(result);
			this.client.unsubscribe(i);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getSubscriptions() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(this.systemAndDomain, p.getId());
			Assert.assertNotNull(i);
			Thread.sleep(500);
			Subscription result = this.client.subscribe(i);
			Assert.assertNotNull(result);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
			Assert.assertFalse(subs.isEmpty());
			this.client.unsubscribe(i);
			subs = this.client.getSubscriptions(i);
			logger.debug("SUBSCRIPTIONS: SIZE: " + subs.size());
			// Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents
			// subscriptions from removing correctly
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getSubscriptions2() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(this.systemAndDomain, p.getId());
			Assert.assertNotNull(i);
			Thread.sleep(500);
			// Subscription result = this.client.subscribe(i);
			// Assert.assertNotNull(result);
			Collection<Subscription> subs = this.client.getSubscriptions(i);
			// Assert.assertFalse(subs.isEmpty());
			// this.client.unsubscribe(i);
			// subs = this.client.getSubscriptions(i);
			logger.debug("SUBSCRIPTIONS: SIZE: " + subs.size());
			// Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents
			// subscriptions from removing correctly
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

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
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(this.systemAndDomain, p.getId());
			Assert.assertNotNull(i);
			Collection<Call> calls = this.client.makeCall(this.systemAndDomain, i, clientProperties.getProperty("client.call.destination"), null,
					new HashSet<Property>());
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
			this.client.requestAction(this.systemAndDomain, call, RequestActionAction.ClearConnection, null, null);
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

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
			Profile p = getPrimaryProfile(this.systemAndDomain);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(this.systemAndDomain, p.getId());
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

			Collection<Call> calls = this.client.makeCall(this.systemAndDomain, i, clientProperties.getProperty("client.call.destination"), features,
					originatorReferences);
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
			this.client.requestAction(this.systemAndDomain, call, RequestActionAction.ClearConnection, null, null);
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testApplySiteIdOnSystem() {
		if (this.resource != null && !"".equals(resource)) {

			String system = "avaya.example.com";
			Assert.assertEquals("avaya1.example.com", this.client.applySiteIdOnSystem(system));

			system = "avaya.example";
			Assert.assertEquals("avaya1.example", this.client.applySiteIdOnSystem(system));

			system = "avaya1.example.com";
			Assert.assertEquals("avaya1.example.com", this.client.applySiteIdOnSystem(system));

			system = "avaya1.example";
			Assert.assertEquals("avaya1.example", this.client.applySiteIdOnSystem(system));

			system = "avaya1";
			Assert.assertEquals("avaya1", this.client.applySiteIdOnSystem(system));

			system = "avaya";
			Assert.assertEquals("avaya1", this.client.applySiteIdOnSystem(system));

			system = null;
			Assert.assertEquals(null, this.client.applySiteIdOnSystem(system));

			system = "";
			Assert.assertEquals("", this.client.applySiteIdOnSystem(system));
		}
		logger.error("Can't verify site ID code without a valid resource");
	}

	@Test
	public void getGtxProfile() {
		boolean hasProfile = false;
		boolean hasSystem = false;

		try {
			Assert.assertTrue(this.client.isConnected());
			GtxProfile profile = this.client.getXmppSession().getExtensionManager(PrivateDataManager.class).getData(GtxProfile.class);

			String uid = profile.getUid();
			Collection<net.gltd.gtms.profiler.gtx.profile.Property> gtxProfileProps = profile.getProperties();
			Collection<net.gltd.gtms.profiler.gtx.profile.Profile> profiles = profile.getProfiles();
			if (gtxProfileProps != null) {
				for (net.gltd.gtms.profiler.gtx.profile.Property gtxProfileProp : gtxProfileProps) {
					logger.debug(gtxProfileProp);
				}
			}

			for (net.gltd.gtms.profiler.gtx.profile.Profile p : profiles) {
				String profileId = p.getId();
				if (profileId != null && !"".equals(profileId)) {
					hasProfile = true;
				}

				Collection<net.gltd.gtms.profiler.gtx.profile.Property> profileProps = p.getProperties();
				if (gtxProfileProps != null) {
					for (net.gltd.gtms.profiler.gtx.profile.Property profileProp : profileProps) {
						logger.debug(profileProp);
					}
				}

				for (GtxSystem s : p.getSystems()) {
					String id = s.getId();
					if (id != null && !"".equals(id)) {
						hasSystem = true;
					}
					String category = s.getCategory();
					boolean enabled = s.getEnabled();
					Collection<net.gltd.gtms.profiler.gtx.profile.Property> systemProps = s.getProperties();
					for (net.gltd.gtms.profiler.gtx.profile.Property sysProp : systemProps) {
						logger.debug("profile " + profileId + " system: " + id + " property: " + sysProp.getId() + " : " + sysProp.getValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(hasProfile && hasSystem);
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
