package net.gltd.gtms.client.openlink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import net.gltd.gtms.client.TestUtil;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.gtx.privatedata.GtxProfile;
import net.gltd.gtms.extension.gtx.privatedata.GtxSystem;
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
import net.gltd.gtms.extension.openlink.originatorref.Property2;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;

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
import rocks.xmpp.extensions.bookmarks.model.Bookmark;
import rocks.xmpp.extensions.privatedata.PrivateDataManager;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class OpenlinkClientGtxProfileTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	private static final String USERNAME = "leon";
	private static final String PASSWORD = "password";
	private static final String RESOURCE = "office";

	private static final String DOMAIN = "clarabel";
	private static final String HOST = "localhost";

	private static final String SYSTEM = "avaya1";

	private static final String SYSTEM_AND_DOMAIN = SYSTEM + "." + DOMAIN;

	private static final String DESTINATION = "50203";

	private OpenlinkClient client = null;

	public OpenlinkClientGtxProfileTest() throws JAXBException, XMLStreamException {
		super();
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
	public void getGtxProfile() {
		try {
			Thread.sleep(500);
			Assert.assertTrue(this.client.isConnected());
			Thread.sleep(500);
			GtxProfile profile = this.client.getXmppSession().getExtensionManager(PrivateDataManager.class).getData(GtxProfile.class);

			String uid = profile.getUid();
			Collection<net.gltd.gtms.extension.gtx.privatedata.Property> gtxProfileProps = profile.getProperties();
			Collection<net.gltd.gtms.extension.gtx.privatedata.Profile> profiles = profile.getProfiles();
			for (net.gltd.gtms.extension.gtx.privatedata.Property gtxProfileProp : gtxProfileProps) {
				logger.debug(gtxProfileProp);
			}

			for (net.gltd.gtms.extension.gtx.privatedata.Profile p : profiles) {
				String profileId = p.getId();
				Collection<net.gltd.gtms.extension.gtx.privatedata.Property> profileProps = p.getProperties();
				for (net.gltd.gtms.extension.gtx.privatedata.Property profileProp : profileProps) {
					logger.debug(profileProp);
				}

				for (GtxSystem s : p.getSystems()) {
					String id = s.getId();
					String category = s.getCategory();
					boolean enabled = s.getEnabled();
					Collection<net.gltd.gtms.extension.gtx.privatedata.Property> systemProps = s.getProperties();
					for (net.gltd.gtms.extension.gtx.privatedata.Property sysProp : systemProps) {
						logger.debug("profile " + profileId + " system: " + id + " property: " + sysProp.getId() + " : " + sysProp.getValue());
					}
				}
			}

			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void debugTrace() {
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
