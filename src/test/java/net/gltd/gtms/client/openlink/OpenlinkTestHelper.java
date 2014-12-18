package net.gltd.gtms.client.openlink;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Command.Status;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.command.Note.NoteType;
import net.gltd.gtms.extension.openlink.callstatus.Call;
import net.gltd.gtms.extension.openlink.callstatus.Call.CallDirection;
import net.gltd.gtms.extension.openlink.callstatus.Call.CallState;
import net.gltd.gtms.extension.openlink.callstatus.CallFeature;
import net.gltd.gtms.extension.openlink.callstatus.CallStatus;
import net.gltd.gtms.extension.openlink.callstatus.CallerCallee;
import net.gltd.gtms.extension.openlink.callstatus.Participant;
import net.gltd.gtms.extension.openlink.callstatus.Participant.ParticipantType;
import net.gltd.gtms.extension.openlink.callstatus.action.AddThirdParty;
import net.gltd.gtms.extension.openlink.callstatus.action.AnswerCall;
import net.gltd.gtms.extension.openlink.callstatus.action.ClearCall;
import net.gltd.gtms.extension.openlink.callstatus.action.ClearConnection;
import net.gltd.gtms.extension.openlink.callstatus.action.RemoveThirdParty;
import net.gltd.gtms.extension.openlink.callstatus.action.SendDigits;
import net.gltd.gtms.extension.openlink.callstatus.action.StartVoiceDrop;
import net.gltd.gtms.extension.openlink.features.Feature;
import net.gltd.gtms.extension.openlink.features.Features;
import net.gltd.gtms.extension.openlink.interests.Interest;
import net.gltd.gtms.extension.openlink.interests.Interests;
import net.gltd.gtms.extension.openlink.profiles.Action;
import net.gltd.gtms.extension.openlink.profiles.Profile;
import net.gltd.gtms.extension.openlink.profiles.Profiles;

import org.apache.commons.lang3.RandomStringUtils;
import org.xmpp.Jid;

public class OpenlinkTestHelper {

	private OpenlinkTestHelper() {
	};

	public static CallStatus getCallStatus() {
		CallStatus callStatus = new CallStatus();
		callStatus.setBusy(false);
		callStatus.getCalls().add(getCall());
		callStatus.getCalls().add(getCall());
		return callStatus;
	}
	
	public static Call getCall() {

		Call call = new Call();
		call.setId(RandomStringUtils.randomNumeric(10));
		call.setInterest("vmstsp_office_default");
		call.setProfile("vmstsp_office");
		call.setDirection(Call.CallDirection.Outgoing);
		call.setState(CallState.CallConferenced);
		call.setChanged("state");

		call.getActions().add(new AddThirdParty());
		call.getActions().add(new RemoveThirdParty());
		call.getActions().add(new ClearCall());
		call.getActions().add(new AnswerCall());
		call.getActions().add(new ClearConnection());
		call.getActions().add(new SendDigits());
		call.getActions().add(new StartVoiceDrop());

		CallerCallee caller = new CallerCallee();
		caller.setName("Brian Broker");
		caller.setNumber("1234567890");
		call.setCaller(caller);

		CallerCallee called = new CallerCallee();
		called.setName("Betty Bidder");
		called.setNumber("4567890123");
		call.setCalled(called);

		call.setDuration(Integer.valueOf(RandomStringUtils.randomNumeric(2)));

		CallFeature cf1 = new CallFeature();
		cf1.setId("Conference");
		cf1.setValue1(true);
		call.getFeatures().add(cf1);

		CallFeature cf2 = new CallFeature();
		cf2.setId("Callback");
		cf2.setValue1(true);
		call.getFeatures().add(cf2);

		Participant p1 = new Participant();
		p1.setDirection(CallDirection.Incoming);
		p1.setExten("1234567890");
		p1.setJid(Jid.valueOf("brian.broker@dom"));
		p1.setType(ParticipantType.Active);
		p1.setTimestamp(new Date().toString());
		call.getParticipants().add(p1);
		
		return call;
	}

	public static Interests getInterests(int numInterests) {
		Interests is = new Interests();
		for (int i = 0; i < numInterests; i++) {
			Interest interest = getInterest();
			is.add(interest);
		}
		return is;
	}

	public static Interest getInterest() {
		Interest i = new Interest();
		i.setId(RandomStringUtils.randomAlphabetic(5));
		i.setType(RandomStringUtils.randomAlphabetic(5));
		i.setValue(RandomStringUtils.randomAlphabetic(5));
		i.setLabel(RandomStringUtils.randomAlphabetic(5));
		return i;
	}

	public static Features getFeatures(int numFeatures) {
		Features fs = new Features();
		for (int i = 0; i < numFeatures; i++) {
			Feature f = getFeature();
			fs.add(f);
		}
		return fs;
	}

	public static Feature getFeature() {
		Feature f = new Feature();
		f.setId(RandomStringUtils.randomAlphabetic(5));
		f.setType(RandomStringUtils.randomAlphabetic(5));
		// f.setValue1(RandomStringUtils.randomAlphabetic(5));
		// f.setValue2(RandomStringUtils.randomAlphabetic(5));
		// f.setValue3(RandomStringUtils.randomAlphabetic(5));
		f.setLabel(RandomStringUtils.randomAlphabetic(5));
		return f;
	}

	public static Command getCommand(String node) {
		Command c = new Command();
		c.setSessionId(RandomStringUtils.randomNumeric(4));
		c.setNode(node);
		c.setStatus(Status.COMPLETED);
		c.setNote(getNote());
		return c;
	}
	
	public static Note getNote(String value, NoteType type) {
		Note n = new Note();
		n.setValue(value);
		n.setType(type);
		return n;
	}
	
	public static Note getNote() {
		return getNote(RandomStringUtils.randomAlphanumeric(20), NoteType.INFO);
	}
	

	public static Action getAction() {
		Action a = new Action();
		a.setId(RandomStringUtils.randomAlphabetic(5));
		a.setLabel(RandomStringUtils.randomAlphabetic(5));
		return a;
	}

	public static Profile getProfile(int numActions) {
		Profile p = new Profile();
		p.setId(RandomStringUtils.randomAlphabetic(5));
		p.setDevice(RandomStringUtils.randomAlphabetic(5));
		p.setDefaultProfile(true);
		p.setOnline(true);
		p.setLabel(RandomStringUtils.randomAlphabetic(5));

		Set<Action> actions = new HashSet<Action>();

		for (int i = 0; i < numActions; i++) {
			Action a = getAction();
			actions.add(a);
		}
		p.setActions(actions);

		return p;
	}

	public static Profiles getProfiles(int numProfiles, int numActions) {
		Profiles ps = new Profiles();
		for (int i = 0; i < numProfiles; i++) {
			Profile p = getProfile(numActions);
			ps.add(p);
		}
		return ps;
	}

}
