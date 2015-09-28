package net.gltd.gtms.client.openlink;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.gltd.gtms.client.openlink.logging.LogFormatter;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.CommandNoteTypeErrorException;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile;
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
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageFeature;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageIn;
import net.gltd.gtms.extension.openlink.command.RequestAction;
import net.gltd.gtms.extension.openlink.command.RequestAction.RequestActionAction;
import net.gltd.gtms.extension.openlink.command.SetFeatures;
import net.gltd.gtms.extension.openlink.command.SetFeatures.SetFeaturesIn;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatusFeature;
import net.gltd.gtms.extension.openlink.features.Feature;
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
import net.gltd.gtms.profiler.gtx.profile.GtxProfile;
import net.gltd.gtms.profiler.gtx.profile.GtxSystem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.roster.RosterEvent;
import rocks.xmpp.core.roster.RosterListener;
import rocks.xmpp.core.sasl.AuthenticationException;
import rocks.xmpp.core.session.ConnectionException;
import rocks.xmpp.core.session.NoResponseException;
import rocks.xmpp.core.session.SessionStatusEvent;
import rocks.xmpp.core.session.SessionStatusListener;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.XmppSession.Status;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.context.extensions.ExtensionContext;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.MessageListener;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.PresenceListener;
import rocks.xmpp.core.stanza.StanzaException;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Presence;
import rocks.xmpp.core.stream.StreamErrorException;
import rocks.xmpp.core.stream.StreamNegotiationException;
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
	private boolean isSecure = true;

	private XmppSession xmppSession;
	private Jid jid;

	private Collection<CallListener> callListeners = new ArrayList<CallListener>();
	private Collection<DeviceListener> deviceListeners = new ArrayList<DeviceListener>();

	private ManageVoiceMessageHandler voiceMessageHandler;

	private PrivateDataHandler privateDataHandler;

	private GtxProfile profile;

	/**
	 * Creates the Openlink Client which authenticates with an XMPP server using the given credentials.
	 * 
	 * @param username
	 *            XMPP username.
	 * @param password
	 *            XMPP user password.
	 * @param resource
	 *            OPTIONAL XMPP resource.
	 * @param domain
	 *            the XMPP service domain (eg. example.com).
	 * @param host
	 *            OPTIONAL - the XMPP server hostname or IP.
	 */
	public OpenlinkClient(String username, String password, String resource, String domain, String host) {
		this.username = username;
		this.password = password;
		this.resource = resource;
		this.domain = domain;
		this.host = host;
		if (resource == null || "".equals(resource)) {
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
						GetFeatures.GetFeaturesIn.class, SetFeatures.class, SetFeaturesIn.class, GetInterests.class,
						GetInterests.GetInterestsIn.class, MakeCall.class, MakeCall.MakeCallIn.class, MakeCall.MakeCallIn.MakeCallFeature.class,

						ManageVoiceMessage.class, ManageVoiceMessageIn.class, ManageVoiceMessageFeature.class,

						RequestAction.class, RequestAction.RequestActionIn.class,

						GetProfiles.class, GetProfiles.GetProfilesIn.class,

						Feature.class, Features.class, Interest.class, Interests.class, Action.class, Profile.class, Profiles.class,

						DeviceStatus.class, DeviceStatusFeature.class,

						AudioFile.class, AudioFile.Location.class, AudioFile.Location.Auth.class,

						net.gltd.gtms.extension.openlink.properties.Property.class,

						VoiceMessage.class, Callback.class, Callback.Active.class, Dtmf.class,

						net.gltd.gtms.profiler.gtx.profile.Feature.class, GtxProfile.class, GtxSystem.class,
						net.gltd.gtms.profiler.gtx.profile.Profile.class, net.gltd.gtms.profiler.gtx.profile.Property.class

				));

		if (isDebug()) {
			builder.debugger(ConsoleDebugger.class);
			logger.debug("CONSOLE LOGGING: ENABLED");
		}

		return builder.build();
	}

	public boolean isSecure() {
		return isSecure;
	}

	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
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

	private void setXmppSession(XmppSession xmppSession) {
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
	 * Connects to the XMPP server.
	 *
	 * @throws KeyManagementException
	 *             If the SSL Context fails to initialize.
	 * @throws NoSuchAlgorithmException
	 *             If no such algorithm found by the SSLContext.
	 * @throws ConnectionException
	 *             If a connection error occurred on the transport layer, e.g. the socket could not connect.
	 * @throws StreamNegotiationException
	 *             If any exception occurred during stream feature negotiation.
	 * @throws NoResponseException
	 *             If the server didn't return a response during stream establishment.
	 * @throws IllegalStateException
	 *             If the session is in a wrong state, e.g. closed or already connected.
	 * @throws AuthenticationException
	 *             If the login failed, due to a SASL error reported by the server.
	 * @throws StreamErrorException
	 *             If the server returned a stream error.
	 * @throws StanzaException
	 *             If the server returned a stanza error during resource binding or roster retrieval.
	 * @throws XmppException
	 *             If the login failed, due to any other XMPP exception.
	 */
	public void connect() throws KeyManagementException, NoSuchAlgorithmException, XmppException {

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

		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder().hostname(this.host).port(OpenlinkClient.PORT)
				.proxy(Proxy.NO_PROXY).secure(isSecure()).hostnameVerifier(hostnameVerifier).build();

		this.setXmppSession(new XmppSession(this.domain, getSessionConfiguration(), tcpConfiguration));

		this.voiceMessageHandler = new ManageVoiceMessageHandler(this.getXmppSession());
		this.privateDataHandler = new PrivateDataHandler(this.getXmppSession());

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
				if (Status.AUTHENTICATED == e.getStatus()) {
					try {
						profile = privateDataHandler.getGtxProfile();
					} catch (XmppException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		// Listen for messages
		xmppSession.addMessageListener(getCallStatusMessageListener());
		xmppSession.addMessageListener(getDeviceStatusMessageListener());

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
	}

	public void disableConsoleLogging() {

	}

	public String applySiteIdOnSystem(String system) {
		String node = null;
		String domain = null;
		String siteId = null;

		if (system != null) {
			if (system.contains(".")) {
				node = system.split("\\.", 2)[0];
				domain = system.split("\\.", 2)[1];
			} else {
				node = system;
			}

			if (profile == null) {
				try {
					profile = privateDataHandler.getGtxProfile();
				} catch (XmppException e) {
					e.printStackTrace();
				}
			}

			net.gltd.gtms.profiler.gtx.profile.Profile tmpProfile = profile.getProfile(resource);
			if (tmpProfile != null) {
				/*
				 * commented due to bug in avaya plugin with systemId being incorrect - using type instead (see below) GtxSystem gtxSystem =
				 * tmpProfile.getGtxSystem(node); if (gtxSystem != null) { net.gltd.gtms.profiler.gtx.profile.Property property =
				 * gtxSystem.getProperty(PrivateDataHandler.PROFILE_PROPERTY_SITE_ID); if (property != null) { siteId = property.getValue(); node =
				 * node + siteId; } }
				 */
				Set<GtxSystem> gtxSystems = tmpProfile.getGtxSystemsByType(node);
				if (!gtxSystems.isEmpty()) {
					GtxSystem gtxSystem = gtxSystems.iterator().next();
					net.gltd.gtms.profiler.gtx.profile.Property property = gtxSystem.getProperty(PrivateDataHandler.PROFILE_PROPERTY_SITE_ID);
					if (property != null) {
						siteId = property.getValue();
						node = node + siteId;
					}
				}
			}
			if (system.contains(".")) {
				node = node + "." + domain;
			}
		}

		logger.debug("Site ID for system: " + node + " site: " + siteId);

		return node;
	}

	public ManageVoiceMessageHandler getVoiceMessageHandler() {
		return voiceMessageHandler;
	}

	public PrivateDataHandler getPrivateDataHandler() {
		return privateDataHandler;
	}

	private MessageListener getCallStatusMessageListener() {
		return new MessageListener() {
			@Override
			public void handleMessage(MessageEvent e) {
				// Handle outgoing or incoming message
				if (e.isIncoming() && e.getMessage() != null) {
					Event event = e.getMessage().getExtension(Event.class);
					if (event != null) {
						for (Item item : event.getItems()) {
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

	private MessageListener getDeviceStatusMessageListener() {
		return new MessageListener() {
			@Override
			public void handleMessage(MessageEvent e) {
				// Handle outgoing or incoming message
				if (e.isIncoming() && e.getMessage() != null) {
					Event event = e.getMessage().getExtension(Event.class);
					if (event != null) {
						for (Item item : event.getItems()) {
							if (item.getPayload() instanceof DeviceStatus) {
								DeviceStatus deviceStatus = (DeviceStatus) item.getPayload();
								logger.debug("DEVICESTATUS: " + deviceStatus.toString());
								if (deviceStatus.getFeatures() != null && !deviceStatus.getFeatures().isEmpty()) {
									for (DeviceListener listener : deviceListeners) {
										listener.deviceEvent(deviceStatus.getProfile(), deviceStatus.getFeatures());
									}
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
	 * Returns the list of device listeners.
	 * 
	 * @return device listener.
	 */
	public Collection<DeviceListener> getDeviceListeners() {
		return Collections.unmodifiableCollection(deviceListeners);
	}

	/**
	 * Add a device listener to listen to device events. See {@link DeviceListener}.
	 * 
	 * @param listener
	 *            device listener.
	 */
	public void addDeviceListener(DeviceListener listener) {
		if (listener != null) {
			deviceListeners.add(listener);
		}
	}

	/**
	 * Remove device listener.
	 * 
	 * @param listener
	 *            device listener.
	 */
	public void removeDeviceListener(DeviceListener listener) {
		deviceListeners.remove(listener);
	}

	/**
	 * Disconnect from the server
	 * 
	 * @throws XmppException
	 *             If an exception occurs while closing the connection, e.g. the underlying socket connection.
	 */
	public void disconnect() throws XmppException {
		logger.debug("DISCONNECT");
		if (xmppSession != null && isConnected()) {
			xmppSession.close();
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

		IQ iq = new IQ(Jid.valueOf(applySiteIdOnSystem(to)), IQ.Type.SET, gp);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			validateCommandNoteTypeError(command);
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
		IQ iq = new IQ(Jid.valueOf(applySiteIdOnSystem(to)), IQ.Type.SET, gi);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			validateCommandNoteTypeError(command);
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
	public Collection<Feature> getFeatures(String to, Profile profile) throws XmppException {
		Collection<Feature> result = new ArrayList<Feature>();

		GetFeatures gf = new GetFeatures();
		gf.getIn().setProfile(profile.getId());
		IQ iq = new IQ(Jid.valueOf(applySiteIdOnSystem(to)), IQ.Type.SET, gf);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			validateCommandNoteTypeError(command);
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

	public void validateCommandNoteTypeError(Command command) throws XmppException {
		String noteMessage = OpenlinkClientUtil.getNoteError(command);
		if (!StringUtils.isEmpty(noteMessage)) {
			throw new CommandNoteTypeErrorException(noteMessage);
		}
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#set-features'.
	 *
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            User's profile.
	 * @param feature
	 *            Feature to update.
	 * @param values
	 *            Up to three values representing value1, value2, value3 from the Openlink Set Features command.
	 * 
	 */
	public void setFeatures(String to, Profile profile, Feature feature, String... values) throws XmppException {
		SetFeatures sf = new SetFeatures();
		sf.getIn().setProfile(profile.getId());
		sf.getIn().setFeature(feature.getId());

		if (values.length > 0) {
			sf.getIn().setValue1(values[0]);
		}
		if (values.length > 1) {
			sf.getIn().setValue2(values[1]);
		}
		if (values.length > 2) {
			sf.getIn().setValue3(values[2]);
		}

		IQ iq = new IQ(Jid.valueOf(applySiteIdOnSystem(to)), IQ.Type.SET, sf);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			validateCommandNoteTypeError(command);
			Command.Status status = command.getStatus();
			logger.debug("SET FEATURE STATUS: " + status);
		}
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
		IQ iq = new IQ(Jid.valueOf(applySiteIdOnSystem(to)), IQ.Type.SET, mc);
		IQ iqResult = xmppSession.query(iq);

		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			validateCommandNoteTypeError(command);
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
	 * @param interest
	 *            the Openlink interest.
	 * @param callId
	 *            the Openlink Call ID.
	 * @param action
	 *            the RequestAction action to execute.
	 * @param value1
	 *            RequestAction value1.
	 * @param value2
	 *            RequestAction value2.
	 * @param timeout
	 *            Timeout in milliseconds.
	 * @return Collection of calls made as a result of the request.
	 */
	public Collection<Call> requestAction(String to, String interest, String callId, RequestActionAction action, String value1, String value2,
			long timeout) throws XmppException {
		Collection<Call> result = new ArrayList<Call>();

		RequestAction ra = new RequestAction();
		ra.getIn().setCall(callId);
		ra.getIn().setAction(action);
		ra.getIn().setInterest(interest);
		ra.getIn().setValue1(value1);
		ra.getIn().setValue2(value2);

		IQ iq = new IQ(Jid.valueOf(applySiteIdOnSystem(to)), IQ.Type.SET, ra);
		IQ iqResult;
		if (timeout > 0) {
			iqResult = xmppSession.query(iq, timeout);
		} else {
			iqResult = xmppSession.query(iq);
		}
		Command command = iqResult.getExtension(Command.class);
		if (command != null) {
			validateCommandNoteTypeError(command);
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
		return this.requestAction(to, call.getInterest(), call.getId(), action, value1, value2, 0);
	}

	public Collection<Call> requestAction(String to, String interest, String callId, RequestActionAction action, String value1, String value2)
			throws XmppException {
		return this.requestAction(to, interest, callId, action, value1, value2, 0);
	}

	public Collection<Call> requestAction(String to, Call call, RequestActionAction action, String value1, String value2, long timeout)
			throws XmppException {
		return this.requestAction(to, call.getInterest(), call.getId(), action, value1, value2, timeout);
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
