package net.gltd.gtms.client.openlink;

import java.util.Collection;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.callstatus.Call;
import net.gltd.gtms.extension.openlink.callstatus.Call.CallAction;
import net.gltd.gtms.extension.openlink.callstatus.CallStatus;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
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
import org.xmpp.stanza.client.IQ;
import org.xmpp.stanza.client.Message;

public class OpenlinkClientTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	private static final String USERNAME = "leon";
	private static final String PASSWORD = "Pa55w0rd";
	private static final String RESOURCE = "office";

	private static final String DOMAIN = "lokidev.gltd.net";
	private static final String HOST = "lokidev.gltd.net";

	private static final String SYSTEM = "vmstsp";

	private static final String SYSTEM_AND_DOMAIN = SYSTEM + "." + DOMAIN;

	private static final String DESTINATION = "3807";

	private OpenlinkClient client = null;

	public OpenlinkClientTest() throws JAXBException, XMLStreamException {
		super(Command.class, Note.class, Message.class, IQ.class, IoData.class, Profiles.class, Profile.class,
				Action.class, Interests.class, Interest.class, Features.class, Feature.class, CallStatus.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = GtmsLog.initializeConsoleLogger("net.gltd.gtms", GtmsLog.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");
		logger.debug("INIT");
		client = new OpenlinkClient(USERNAME, PASSWORD, RESOURCE, DOMAIN, HOST);
		client.addCallListener(this.getCallListener());
		client.setDebug(true);
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
			Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents subscriptions from removing correctly
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
			Assert.assertTrue(subs.isEmpty()); // Not sure if a bug in Openfire's caching strategy which prevents subscriptions from removing correctly
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void makeCall() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Collection<Call> calls = this.client.makeCall(SYSTEM_AND_DOMAIN, i, DESTINATION, null);
			Assert.assertNotNull(calls);
			Assert.assertTrue(calls.size() > 0);
			for (Call c : calls) {
				Assert.assertNotNull(c.getId());
			}
			Call call = calls.iterator().next();
			logger.debug(XmlUtil.formatXml(marshal(calls)));
			Thread.sleep(10000);

			this.client.requestAction(SYSTEM_AND_DOMAIN, call, CallAction.ClearCall, null, null);
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void requestAction() {
		try {
			Assert.assertTrue(isConnected());
			Profile p = getPrimaryProfile(SYSTEM_AND_DOMAIN);
			Assert.assertNotNull(p);
			Interest i = getPrimaryInterest(SYSTEM_AND_DOMAIN, p.getId());
			Assert.assertNotNull(i);
			Collection<Call> calls = this.client.makeCall(SYSTEM_AND_DOMAIN, i, DESTINATION, null);
			Assert.assertNotNull(calls);
			Assert.assertTrue(calls.size() > 0);
			for (Call c : calls) {
				Assert.assertNotNull(c.getId());
			}
			logger.debug(XmlUtil.formatXml(marshal(calls)));

			Call call = calls.iterator().next();
			Thread.sleep(10000);

			calls = this.client.requestAction(SYSTEM_AND_DOMAIN, call, CallAction.ClearCall, null, null);
			Assert.assertNotNull(calls);
			Assert.assertTrue(calls.size() > 0);
			for (Call c : calls) {
				Assert.assertNotNull(c.getId());
			}
			logger.debug(XmlUtil.formatXml(marshal(calls)));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
