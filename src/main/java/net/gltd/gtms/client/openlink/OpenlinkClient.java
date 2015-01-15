package net.gltd.gtms.client.openlink;

import java.io.IOException;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.xml.bind.JAXBException;

import net.gltd.gtms.client.openlink.logging.LogFormatter;
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
import net.gltd.gtms.extension.openlink.command.GetFeatures;
import net.gltd.gtms.extension.openlink.command.GetInterests;
import net.gltd.gtms.extension.openlink.command.GetProfiles;
import net.gltd.gtms.extension.openlink.command.MakeCall;
import net.gltd.gtms.extension.openlink.command.MakeCall.MakeCallIn.MakeCallFeature;
import net.gltd.gtms.extension.openlink.command.RequestAction;
import net.gltd.gtms.extension.openlink.command.RequestAction.RequestActionAction;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;

import org.apache.log4j.Logger;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.roster.RosterEvent;
import rocks.xmpp.core.roster.RosterListener;
import rocks.xmpp.core.session.SessionStatusEvent;
import rocks.xmpp.core.session.SessionStatusListener;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.context.extensions.ExtensionContext;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.MessageListener;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.PresenceListener;
import rocks.xmpp.core.stanza.model.StanzaException;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Presence;
import rocks.xmpp.extensions.pubsub.PubSubManager;
import rocks.xmpp.extensions.pubsub.PubSubNode;
import rocks.xmpp.extensions.pubsub.PubSubService;
import rocks.xmpp.extensions.pubsub.model.Item;
import rocks.xmpp.extensions.pubsub.model.Subscription;
import rocks.xmpp.extensions.pubsub.model.event.Event;
import rocks.xmpp.extensions.shim.model.Header;
import rocks.xmpp.extensions.shim.model.Headers;

/**
 * The OpenlinkClient has all the XMPP specific logic for Openlink functionality including profiles, interests, features, make-call and request
 * action.
 * 
 * In addition it exposes helpers for pubsub functionality such as subscribe/unsubscribe and get the list of subscriptions.
 */
public class OpenlinkClient {
	private static final Logger logger = Logger.getLogger(OpenlinkClient.class);

	public static final int PORT = 5222;

	private final String username;
	private final String password;
	private final String resource;

	private final String domain;
	private final String host;

	private boolean debug = false;

	private XmppSession xmppSession;
	private Jid jid;

	private Collection<CallListener> callListeners = new ArrayList<CallListener>();;

	public OpenlinkClient(String username, String password, String resource, String domain, String host) {
		this.username = username;
		this.password = password;
		this.resource = resource;
		this.domain = domain;
		this.host = host;
		if (resource == null) {
			this.jid = Jid.valueOf(username + "@" + domain);
		} else {
			this.jid = Jid.valueOf(username + "@" + domain + "/" + resource);
		}
		logger.debug("Client Initialized with JID: " + this.getFullJid());
	}

	protected XmppSessionConfiguration getSessionConfiguration() {

		XmppSessionConfiguration.Builder builder = XmppSessionConfiguration.builder().context(
				new ExtensionContext(

				Command.class, Note.class, IoData.class,

				Headers.class, Header.class,

				Event.class,

				CallAction.class, AddThirdParty.class, AnswerCall.class, ClearCall.class, ClearConnection.class, ConferenceFail.class,
						ConnectSpeaker.class, ConsultationCall.class, DisconnectSpeaker.class, HoldCall.class, IntercomTransfer.class,
						JoinCall.class, PrivateCall.class, PublicCall.class, RemoveThirdParty.class, RetrieveCall.class, SendDigit.class,
						SendDigits.class, SingleStepTransfer.class, RemoveThirdParty.class, SendDigits.class, StartVoiceDrop.class,
						StopVoiceDrop.class, TransferCall.class,

						Call.class, CallerCallee.class, CallFeature.class, CallStatus.class, Participant.class, Property.class, GetFeatures.class,
						GetFeatures.GetFeaturesIn.class, GetInterests.class, GetInterests.GetInterestsIn.class, MakeCall.class,
						MakeCall.MakeCallIn.class, MakeCall.MakeCallIn.MakeCallFeature.class,

						RequestAction.class, RequestAction.RequestActionIn.class,

						GetProfiles.class, GetProfiles.GetProfilesIn.class,

						Feature.class, Features.class, Interest.class, Interests.class, Action.class, Profile.class, Profiles.class));

		if (isDebug()) {
			builder.debugger(ConsoleDebugger.class);
			logger.debug("CONSOLE LOGGING: ENABLED");
		}

		return builder.build();
	}

	public boolean isDebug() {
		return debug;
	}

	/**
	 * When set disables connection encryption and enables the debugger.
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		if (this.debug) {
			this.initializeLogging();
		}
	}

	/**
	 * Returns true if connection online and authenticated.
	 * 
	 * @return true or false.
	 */
	public boolean isConnected() {
		if (getXmppSession() != null && getXmppSession().getStatus() == XmppSession.Status.AUTHENTICATED) {
			return true;
		}
		return false;
	}

	/**
	 * Get XMPP (Babbler) connection object.
	 */
	public XmppSession getXmppSession() {
		return xmppSession;
	}

	public void setXmppSession(XmppSession xmppSession) {
		this.xmppSession = xmppSession;
	}

	private void initializeLogging() {
		LogManager.getLogManager().reset();

		final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("org.xmpp");
		logger.setLevel(Level.FINE);

		java.util.logging.Logger globalLogger = java.util.logging.Logger.getLogger("");
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.FINE);
		consoleHandler.setFormatter(new LogFormatter());
		globalLogger.addHandler(consoleHandler);
	}

	/**
	 * Gets the JID sans resource.
	 * 
	 * @return bare JID.
	 */
	public String getBareJid() {
		return this.username + "@" + this.domain;
	}

	/**
	 * Gets the full JID.
	 * 
	 * @return full JID.
	 */
	public String getFullJid() {
		return this.username + "@" + this.domain + "/" + this.resource;
	}

	/**
	 * Connect to the server.
	 */
	public void connect() {

		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());

			TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder().hostname(this.host).port(OpenlinkClient.PORT)
					.proxy(Proxy.NO_PROXY).secure(!isDebug()).build();

			xmppSession = new XmppSession(this.domain, getSessionConfiguration(), tcpConfiguration);

			// Listen for presence changes
			xmppSession.addPresenceListener(new PresenceListener() {
				@Override
				public void handlePresence(PresenceEvent e) {
					if (e.isIncoming()) {
						// Handle incoming presence.
					}
				}
			});

			// Listen for session changes
			xmppSession.addSessionStatusListener(new SessionStatusListener() {
				@Override
				public void sessionStatusChanged(SessionStatusEvent e) {
					logger.debug("CONNECTION EV: " + e.getStatus() + " : " + e.getSource() + " : " + e.getException());

				}
			});

			// Listen for messages
			xmppSession.addMessageListener(getCallStatusMessageListener());

			// Listen for roster pushes
			xmppSession.getRosterManager().addRosterListener(new RosterListener() {
				@Override
				public void rosterChanged(RosterEvent e) {

				}
			});

			// Connect
			xmppSession.connect();

			// Login
			xmppSession.login(this.username, this.password, this.resource);

			// Send initial presence
			xmppSession.send(new Presence());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// e.g. UnknownHostException
			e.printStackTrace();
		} catch (FailedLoginException e) {
			// Login failed, due to wrong username/password
			e.printStackTrace();
		} catch (LoginException e) {
			// Login failed, due to wrong username/password
			e.printStackTrace();
		}
	}

	private MessageListener getCallStatusMessageListener() {
		return new MessageListener() {
			@Override
			public void handleMessage(MessageEvent e) {
				logger.debug("MESSAGE EVENT: " + e);
				// Handle outgoing or incoming message

				if (e.isIncoming() && e.getMessage() != null) {

					Event event = e.getMessage().getExtension(Event.class);
					logger.debug("EVENT: " + event);
					if (event != null) {
						for (Item item : event.getItems()) {
							logger.debug("ITEM: " + item);
							if (item.getPayload() instanceof CallStatus) {
								CallStatus callStatus = (CallStatus) item.getPayload();
								logger.debug("CALLSTATUS: " + callStatus.toString());
								for (CallListener listener : callListeners) {
									listener.callEvent(callStatus);
								}

							}
						}
					}
				}
			}
		};
	}

	/**
	 * Returns the list of call listeners.
	 * 
	 * @return call listener.
	 */
	public Collection<CallListener> getCallListeners() {
		return Collections.unmodifiableCollection(callListeners);
	}

	/**
	 * Add a call listener to listen to call events. See {@link CallListener}.
	 * 
	 * @param listener
	 *            call listener.
	 */
	public void addCallListener(CallListener listener) {
		if (listener != null) {
			callListeners.add(listener);
		}
	}

	/**
	 * Remove call listener.
	 * 
	 * @param listener
	 *            call listener.
	 */
	public void removeCallListener(CallListener listener) {
		callListeners.remove(listener);
	}

	/**
	 * Disconnect from the server
	 */
	public void disconnect() {
		logger.debug("DISCONNECT");
		if (xmppSession != null && isConnected()) {
			try {
				xmppSession.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#get-profiles'.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param jid
	 *            if logged on as an admin a user can obtain profiles on behalf of another user by specifying their bare JID (eg. user@example.com).
	 * @return collection of user's profiles.
	 */
	public Collection<Profile> getProfiles(String to, Jid jid) throws XmppException {
		Collection<Profile> result = new ArrayList<Profile>();

		GetProfiles gp = new GetProfiles();
		if (jid == null) {
			gp.getIn().setJid(this.jid);
		} else {
			gp.getIn().setJid(jid);
		}
		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, gp);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			IoData ioData = command.getExtension(IoData.class);
			if (ioData != null && ioData.getOut() != null) {
				Profiles profiles = ioData.getOut().getExtension(Profiles.class);
				if (profiles != null) {
					result = profiles.getProfiles();
				}
			}
		}
		return result;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#get-profiles'.
	 *
	 * @param to
	 *            Openlink XMPP component.
	 * @return collection of user's profiles.
	 */
	public Collection<Profile> getProfiles(String to) throws XmppException {
		return getProfiles(to, null);
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#get-interests'.
	 *
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            User's profile.
	 * @return collection of user's interests.
	 */
	public Collection<Interest> getInterests(String to, Profile profile) throws XmppException {
		Collection<Interest> result = new ArrayList<Interest>();

		GetInterests gi = new GetInterests();
		gi.getIn().setProfile(profile.getId());
		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, gi);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			IoData ioData = command.getExtension(IoData.class);
			if (ioData != null && ioData.getOut() != null) {
				Interests interests = ioData.getOut().getExtension(Interests.class);
				if (interests != null) {
					result = interests.getInterests();
				}
			}
		}
		return result;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#get-features'.
	 *
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            User's profile.
	 * @return collection of features.
	 */
	public Collection<Feature> getFeatures(String to, Profile profile) throws XmppException, JAXBException {
		Collection<Feature> result = new ArrayList<Feature>();

		GetFeatures gf = new GetFeatures();
		gf.getIn().setProfile(profile.getId());
		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, gf);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			IoData ioData = command.getExtension(IoData.class);
			if (ioData != null && ioData.getOut() != null) {
				Features features = ioData.getOut().getExtension(Features.class);
				if (features != null) {
					result = features.getFeatures();
				}
			}
		}
		return result;
	}

	/**
	 * Subscribe to user interest.
	 * 
	 * @param userId
	 *            user id.
	 * @param interest
	 *            user interest.
	 * @return new subscription.
	 */
	public Subscription subscribe(Interest interest) throws XmppException {
		Subscription result = null;
		PubSubService pubSubService = this.getPubSubService();
		if (pubSubService != null) {
			PubSubNode pubSubNode = pubSubService.getNode(interest.getId());
			if (pubSubNode != null) {
				// if (pubSubNode.getSubscriptions().isEmpty()) {
				result = pubSubNode.subscribe();
				// } else {
				// result = pubSubNode.getSubscriptions().iterator().next();
				// }
			}
		}
		return result;
	}

	/**
	 * Unsubscribe from user interest.
	 * 
	 * @param interest
	 *            user interest.
	 */
	public void unsubscribe(Interest interest) throws XmppException {
		PubSubService pubSubService = this.getPubSubService();
		if (pubSubService != null) {
			PubSubNode pubSubNode = pubSubService.getNode(interest.getId());
			if (pubSubNode != null) {
				for (Subscription subscription : pubSubNode.getSubscriptions()) {
					logger.debug("UNSUBSCRIBE: ID: " + pubSubNode.getId() + " SUBID: " + subscription.getSubId());
					try {
						pubSubNode.unsubscribe(subscription.getSubId());
					} catch (StanzaException sex) {
						logger.error("UNSUBSCRIBE: ID: " + pubSubNode.getId() + " SUBID: " + subscription.getSubId() + " FAILED: " + sex.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Implements 'http://www.xmpp.org/extensions/xep-0060.html#entity-subscriptions'.
	 *
	 * @param interest
	 *            the Openlink interest/pubsub node.
	 * @return collection of subscriptions.
	 */
	public Collection<Subscription> getSubscriptions(Interest interest) throws XmppException {
		Collection<Subscription> result = new ArrayList<Subscription>();
		PubSubService pubSubService = this.getPubSubService();
		if (pubSubService != null) {
			PubSubNode pubSubNode = pubSubService.getNode(interest.getId());
			result = pubSubNode.getSubscriptions();
		}
		return result;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#make-call'.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param interest
	 *            the Openlink interest.
	 * @param destination
	 *            destination to be dialled.
	 * @param features
	 *            features to be associated with the call.
	 * @param originatorRef
	 *            collection to associate with the call.
	 * @return Collection of calls made as a result of the request.
	 */
	public Collection<Call> makeCall(String to, Interest interest, String destination, Set<MakeCallFeature> features, Set<Property> originatorRef)
			throws XmppException {
		Collection<Call> result = new ArrayList<Call>();

		MakeCall mc = new MakeCall();
		mc.getIn().setInterest(interest.getId());
		mc.getIn().setDestination(destination);
		mc.getIn().setJid(this.jid.asBareJid());
		if (features != null) {
			mc.getIn().setFeatures(features);
		}
		if (originatorRef != null) {
			mc.getIn().setOriginatorRef(originatorRef);
		}
		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mc);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			IoData ioData = command.getExtension(IoData.class);
			if (ioData != null && ioData.getOut() != null) {
				CallStatus callStatus = ioData.getOut().getExtension(CallStatus.class);
				if (callStatus != null) {
					result = callStatus.getCalls();
				}
			}
		}
		return result;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#request-action'.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param call
	 *            the Openlink Call object.
	 * @param action
	 *            the RequestAction action to execute.
	 * @param value1
	 *            RequestAction value1.
	 * @param value2
	 *            RequestAction value2.
	 * @return Collection of calls made as a result of the request.
	 */
	public Collection<Call> requestAction(String to, Call call, RequestActionAction action, String value1, String value2) throws XmppException {
		Collection<Call> result = new ArrayList<Call>();

		RequestAction ra = new RequestAction();
		ra.getIn().setCall(call.getId());
		ra.getIn().setAction(action);
		ra.getIn().setInterest(call.getInterest());
		ra.getIn().setValue1(value1);
		ra.getIn().setValue2(value2);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, ra);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			IoData ioData = command.getExtension(IoData.class);
			if (ioData != null && ioData.getOut() != null) {
				CallStatus callStatus = ioData.getOut().getExtension(CallStatus.class);
				if (callStatus != null) {
					result = callStatus.getCalls();
				}
			}
		}

		return result;
	}

	public PubSubService getPubSubService() throws XmppException {
		PubSubManager pubSubManager = xmppSession.getExtensionManager(PubSubManager.class);
		if (pubSubManager != null) {
			if (pubSubManager.getPubSubServices().size() > 0) {
				return pubSubManager.getPubSubServices().iterator().next();
			}
		}
		return null;
	}

}
