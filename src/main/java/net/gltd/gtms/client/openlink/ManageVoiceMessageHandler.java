package net.gltd.gtms.client.openlink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.audiofiles.AudioFile;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageAction;
import net.gltd.gtms.extension.openlink.command.ManageVoiceMessage.ManageVoiceMessageFeature;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatus;
import net.gltd.gtms.extension.openlink.devicestatus.DeviceStatusFeature;
import net.gltd.gtms.extension.openlink.features.voicemessage.VoiceMessage;
import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.model.client.IQ;

public class ManageVoiceMessageHandler {

	private XmppSession xmppSession;

	public ManageVoiceMessageHandler(XmppSession xmppSession) {
		this.xmppSession = xmppSession;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#manage-voice-message' Record.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            the Openlink profile.
	 * @param label
	 *            the message label.
	 * @return a collection of messages.
	 * @throws XmppException
	 */
	public Collection<VoiceMessage> record(String to, String profile, String label) throws XmppException {
		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Record);
		mvm.getIn().setLabel(label);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		Collection<VoiceMessage> messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#manage-voice-message' Create.
	 * 
	 * Creates a playlist.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            the Openlink profile.
	 * @param label
	 *            the playlist label.
	 * @param messageIds
	 *            the Message ID/Keys to include in the playlist.
	 * @return a collection of playlists.
	 * @throws XmppException
	 */
	public Collection<VoiceMessage> create(String to, String profile, String label, Set<String> messageIds) throws XmppException {
		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Create);
		mvm.getIn().setLabel(label);

		Set<ManageVoiceMessageFeature> mvmFeatures = new HashSet<ManageVoiceMessage.ManageVoiceMessageFeature>();
		for (String id : messageIds) {
			ManageVoiceMessageFeature mvmFeature = new ManageVoiceMessageFeature();
			mvmFeature.setId(id);
			mvmFeatures.add(mvmFeature);
		}
		mvm.getIn().setFeatures(mvmFeatures);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		Collection<VoiceMessage> messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#manage-voice-message' Query.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            the Openlink profile.
	 * @param messageIds
	 *            the Message ID/Keys.
	 * @return a collection of messages and playlists.
	 * @throws XmppException
	 */
	public Collection<VoiceMessage> query(String to, String profile, Set<String> messageIds) throws XmppException {
		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Query);

		Set<ManageVoiceMessageFeature> mvmFeatures = new HashSet<ManageVoiceMessage.ManageVoiceMessageFeature>();
		for (String id : messageIds) {
			ManageVoiceMessageFeature mvmFeature = new ManageVoiceMessageFeature();
			mvmFeature.setId(id);
			mvmFeatures.add(mvmFeature);
		}
		mvm.getIn().setFeatures(mvmFeatures);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		Collection<VoiceMessage> messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#manage-voice-message' Playback.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            the Openlink profile.
	 * @param messageIds
	 *            the Message ID/Keys.
	 * @return a collection of messages and playlists.
	 * @throws XmppException
	 */
	public Collection<VoiceMessage> playback(String to, String profile, Set<String> messageIds) throws XmppException {
		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Playback);

		Set<ManageVoiceMessageFeature> mvmFeatures = new HashSet<ManageVoiceMessage.ManageVoiceMessageFeature>();
		for (String id : messageIds) {
			ManageVoiceMessageFeature mvmFeature = new ManageVoiceMessageFeature();
			mvmFeature.setId(id);
			mvmFeatures.add(mvmFeature);
		}
		mvm.getIn().setFeatures(mvmFeatures);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		Collection<VoiceMessage> messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	public Collection<VoiceMessage> archive(String to, String profile, Set<String> messageIds) throws XmppException {
		Collection<VoiceMessage> messages = new ArrayList<VoiceMessage>();

		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Archive);

		Set<ManageVoiceMessageFeature> mvmFeatures = new HashSet<ManageVoiceMessage.ManageVoiceMessageFeature>();
		for (String id : messageIds) {
			ManageVoiceMessageFeature mvmFeature = new ManageVoiceMessageFeature();
			mvmFeature.setId(id);
			mvmFeatures.add(mvmFeature);
		}
		mvm.getIn().setFeatures(mvmFeatures);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#manage-voice-message' Playback.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            the Openlink profile.
	 * @param label
	 *            the new message label.
	 * @param messageIds
	 *            the Message ID/Keys.
	 * @return a collection of messages and playlists.
	 * @throws XmppException
	 */
	public Collection<VoiceMessage> edit(String to, String profile, String label, Set<String> messageIds) throws XmppException {
		Collection<VoiceMessage> messages = new ArrayList<VoiceMessage>();

		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Edit);
		mvm.getIn().setLabel(label);

		Set<ManageVoiceMessageFeature> mvmFeatures = new HashSet<ManageVoiceMessage.ManageVoiceMessageFeature>();
		for (String id : messageIds) {
			ManageVoiceMessageFeature mvmFeature = new ManageVoiceMessageFeature();
			mvmFeature.setId(id);
			mvmFeatures.add(mvmFeature);
		}
		mvm.getIn().setFeatures(mvmFeatures);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	/**
	 * Implements 'http://xmpp.org/protocol/openlink:01:00:00#manage-voice-message' Playback.
	 * 
	 * @param to
	 *            Openlink XMPP component.
	 * @param profile
	 *            the Openlink profile.
	 * @param label
	 *            the new message label.
	 * @param audioFiles
	 *            the Audio Files to save.
	 * @return a collection of messages and playlists.
	 * @throws XmppException
	 */
	public Collection<VoiceMessage> save(String to, String profile, String label, Set<AudioFile> audioFiles) throws XmppException {
		Collection<VoiceMessage> messages = new ArrayList<VoiceMessage>();

		ManageVoiceMessage mvm = new ManageVoiceMessage();
		mvm.getIn().setProfile(profile);
		mvm.getIn().setAction(ManageVoiceMessageAction.Save);
		mvm.getIn().setLabel(label);
		mvm.getIn().setAudioFiles(audioFiles);

		IQ iq = new IQ(Jid.valueOf(to), IQ.Type.SET, mvm);
		IQ iqResult = xmppSession.query(iq);

		messages = getVoiceMessagesFromIQ(iqResult);

		return messages;
	}

	private Collection<VoiceMessage> getVoiceMessagesFromIQ(IQ iq) {
		Collection<VoiceMessage> messages = new ArrayList<VoiceMessage>();

		Command command = iq.getExtension(Command.class);
		if (command != null) {
			IoData ioData = command.getExtension(IoData.class);
			if (ioData != null && ioData.getOut() != null) {
				DeviceStatus deviceStatus = ioData.getOut().getExtension(DeviceStatus.class);
				if (deviceStatus != null) {
					for (DeviceStatusFeature dsf : deviceStatus.getFeatures()) {
						if (dsf.getValue() instanceof VoiceMessage) {
							((VoiceMessage) dsf.getValue()).setId(dsf.getId()); // since voicemessage doesn't send id in voicemessage element
							messages.add((VoiceMessage) dsf.getValue());
						}
					}
				}
			}
		}
		return messages;
	}

}
