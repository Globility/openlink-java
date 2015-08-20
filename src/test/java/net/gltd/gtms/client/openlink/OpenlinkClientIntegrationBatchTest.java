package net.gltd.gtms.client.openlink;

import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

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
import net.gltd.gtms.profiler.gtx.profile.GtxProfile;
import net.gltd.gtms.profiler.gtx.profile.GtxSystem;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

public class OpenlinkClientIntegrationBatchTest extends XmlTest {

	protected static Logger logger = Logger.getLogger("net.gltd.gtms");
	public static final String CLIENT_PROPERTIES = "clientbatch.properties";

	private String username;
	private String domain;

	private String systemAndDomain;

	private HashMap<String, OpenlinkClient> clients = new HashMap<String, OpenlinkClient>();;

	private HashMap<String, Interest> interests = new HashMap<String, Interest>();;

	private Properties clientProperties;

	private int maxUsers = 0;
	private int startIndex = 0;

	public OpenlinkClientIntegrationBatchTest() throws JAXBException, XMLStreamException {
		super(Property2.class, Property.class, net.gltd.gtms.extension.openlink.properties.Property.class, Headers.class, Header.class, Event.class,
				Command.class, Note.class, Message.class, IQ.class, IoData.class, Profiles.class, Profile.class, Action.class, Interests.class,
				Interest.class, Features.class, Feature.class, CallStatus.class, Call.class, CallerCallee.class, CallFeature.class,
				Participant.class, CallAction.class, AddThirdParty.class, AnswerCall.class, ClearCall.class, ClearConnection.class,
				ConferenceFail.class, ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class,
				JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class, RetrieveCall.class, SendDigit.class, SendDigits.class,
				SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class, StartVoiceDrop.class, StopVoiceDrop.class, TransferCall.class,

				net.gltd.gtms.profiler.gtx.profile.Feature.class, GtxProfile.class, GtxSystem.class,
				net.gltd.gtms.profiler.gtx.profile.Profile.class, net.gltd.gtms.profiler.gtx.profile.Property.class);
	}

	public void initialize() throws Exception {
		logger = TestUtil.initializeConsoleLogger("net.gltd.gtms", TestUtil.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");

		this.clientProperties = TestUtil.getProperties(this.getClass(), CLIENT_PROPERTIES);
		this.username = clientProperties.getProperty("client.xmpp.username");
		this.domain = clientProperties.getProperty("client.xmpp.domain");

		this.systemAndDomain = clientProperties.getProperty("client.xmpp.system") + "." + this.domain;
		this.maxUsers = Integer.valueOf(clientProperties.getProperty("client.maxusers"));
		this.startIndex = Integer.valueOf(clientProperties.getProperty("client.startindex"));
		for (int i = this.startIndex; i < startIndex + maxUsers; i++) {
			OpenlinkClient client = new OpenlinkClient(this.username + i, clientProperties.getProperty("client.xmpp.password") + i,
					clientProperties.getProperty("client.xmpp.resource"), this.domain, clientProperties.getProperty("client.xmpp.host"));
			client.setDebug(true);
			client.connect();
			this.clients.put(this.username + i, client);
			logger.debug("CLIENT " + client.getBareJid() + " CONNECTING");
		}
	}

	public void shutdown() {
		logger.debug("SHUTDOWN");
		for (OpenlinkClient client : this.clients.values()) {
			if (client != null) {
				if (interests.get(client.getBareJid()) != null) {
					logger.debug("CLIENT " + client.getBareJid() + " UNSUBSCRIBE");
					try {
						client.unsubscribe(interests.get(client.getBareJid()));
					} catch (XmppException e) {
						e.printStackTrace();
					}
				}
				logger.debug("CLIENT " + client.getBareJid() + " DISCONNECTING");
				client.disconnect();
			}
		}
		LogManager.shutdown();
	}

	public boolean isConnected(OpenlinkClient client) {
		boolean result = client != null && client.isConnected();
		return result;
	}

	public Profile getPrimaryProfile(OpenlinkClient client, String to) throws Exception {
		Profile result = null;
		Collection<Profile> profiles = client.getProfiles(this.systemAndDomain);
		if (profiles != null && profiles.size() > 0) {
			for (Profile p : profiles) {
				if (p.isDefaultProfile()) {
					result = p;
				}
			}
		}
		return result;
	}

	public Interest getPrimaryInterest(OpenlinkClient client, String to, Profile profile) throws Exception {
		Interest result = null;
		Collection<Interest> interests = client.getInterests(this.systemAndDomain, profile);
		if (interests != null && interests.size() > 0) {
			for (Interest i : interests) {
				if (i.isDefaultInterest()) {
					result = i;
				}
			}
		}
		return result;
	}

	public HashMap<String, OpenlinkClient> getClients() {
		return clients;
	}

	public void setClients(HashMap<String, OpenlinkClient> clients) {
		this.clients = clients;
	}

	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					shutdown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("Shut Down Hook Attached.");
	}

	public static void main(String[] args) {
		OpenlinkClientIntegrationBatchTest test = null;
		try {
			test = new OpenlinkClientIntegrationBatchTest();
			test.attachShutDownHook();
			test.initialize();
			for (OpenlinkClient client : test.getClients().values()) {
				client.addCallListener(test.new MyCallListener(client));
			}
			while (true) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean cleanup = true;

	public class MyCallListener implements CallListener {

		private OpenlinkClient client;

		public MyCallListener(OpenlinkClient client) {
			super();
			this.client = client;
			try {
				if (!client.isConnected()) {
					throw new IllegalStateException("CLIENT " + client.getBareJid() + " NOT CONNECTED!");
				}
				Profile p = getPrimaryProfile(client, systemAndDomain);
				if (p == null) {
					throw new IllegalStateException("CLIENT " + client.getBareJid() + " DEFAULT PROFILE NOT FOUND");
				}
				Interest i = getPrimaryInterest(client, systemAndDomain, p);
				if (i == null) {
					throw new IllegalStateException("CLIENT " + client.getBareJid() + " DEFAULT INTEREST NOT FOUND");
				}
				try {
					client.unsubscribe(i);
				} catch (XmppException e) {
					e.printStackTrace();
				}
				Subscription s = client.subscribe(i);
				if (s == null) {
					throw new IllegalStateException("CLIENT " + client.getBareJid() + " SUBSCRIPTION FAILED");
				} else {
					interests.put(client.getBareJid(), i);
				}
				logger.debug("CLIENT " + client.getBareJid() + " MONITORING: " + s.getNode() + " " + s.getSubId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void callEvent(Collection<Call> calls) {
			for (Call c : calls) {
				logger.debug("CLIENT " + client.getBareJid() + " CALL EV: " + c.getId() + ": " + c);
				try {
					if (c.getState() == CallState.CallDelivered) {
						logger.debug("CLIENT " + client.getBareJid() + " ANSWER CALL: " + c.getId() + marshal(c));
						client.requestAction(systemAndDomain, c, RequestActionAction.AnswerCall, null, null);
					}
					if (cleanup) {
						if (c.getState() == CallState.CallHeld) {
							logger.debug("CLIENT " + client.getBareJid() + " CLEANUP CALL: " + c.getId() + marshal(c));
							client.requestAction(systemAndDomain, c, RequestActionAction.RetrieveCall, null, null);
						}
						Thread.sleep(2000);
						if (c.getState() == CallState.CallEstablished || c.getState() == CallState.CallConferenced) {
							logger.debug("CLIENT " + client.getBareJid() + " CLEANUP CALL: " + c.getId() + marshal(c));
							client.requestAction(systemAndDomain, c, RequestActionAction.ClearCall, null, null);
						}
						cleanup = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
