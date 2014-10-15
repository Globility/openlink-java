package net.gltd.gtms.extension.openlink.callstatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "call")
@XmlType(propOrder = { "id", "profile", "interest", "changed", "state", "direction", "duration", "caller", "called",
		"participants", "features", "actions" })
public class Call {

	@XmlElement(required = true)
	private String id;

	@XmlElement
	private CallDirection direction;

	@XmlElement
	private CallState state;

	@XmlElement
	private String changed;

	@XmlElement
	private int duration;

	@XmlElement
	private CallerCallee caller;

	@XmlElement
	private CallerCallee called;

	@XmlElementWrapper(name = "actions")
	private Set<CallAction> actions = new HashSet<Call.CallAction>();

	@XmlElementWrapper(name = "features")
	@XmlElement(name = "feature")
	private Set<CallFeature> features = new HashSet<CallFeature>();

	@XmlElementWrapper(name = "participants")
	@XmlElement(name = "participant")
	private Collection<Participant> participants = new ArrayList<Participant>();

	@XmlElement
	private String profile;

	@XmlElement
	private String interest;

	public enum CallDirection {
		Outgoing, Incoming
	}

	public enum CallState {
		CallOriginated, CallDelivered, CallEstablished, CallFailed, CallConferenced, CallBusy, CallHeld, CallHeldElsewhere, CallTransferring, CallTransferred, ConnectionBusy, ConnectionCleared, CallMissed
	}

	public enum CallAction {
		AnswerCall, ClearConference, ClearConnection, ClearCall, ConferenceCall, ConsultationCall, StartVoiceDrop, StopVoiceDrop, HoldCall, PrivateCall, PublicCall, IntercomTransfer, JoinCall, RetrieveCall, TransferCall, SingleStepTransfer, SendDigits, SendDigit, AddThirdParty, RemoveThirdParty, ConnectSpeaker, DisconnectSpeaker
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CallDirection getDirection() {
		return direction;
	}

	public void setDirection(CallDirection direction) {
		this.direction = direction;
	}

	public CallState getState() {
		return state;
	}

	public void setState(CallState state) {
		this.state = state;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public CallerCallee getCaller() {
		return caller;
	}

	public void setCaller(CallerCallee caller) {
		this.caller = caller;
	}

	public CallerCallee getCalled() {
		return called;
	}

	public void setCalled(CallerCallee called) {
		this.called = called;
	}

	public Collection<CallAction> getActions() {
		return actions;
	}

	public void setActions(Set<CallAction> actions) {
		this.actions = actions;
	}

	public Collection<CallFeature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<CallFeature> features) {
		this.features = features;
	}

	public Collection<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(Collection<Participant> participants) {
		this.participants = participants;
	}

	public String getChanged() {
		return changed;
	}

	public void setChanged(String changed) {
		this.changed = changed;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

}
