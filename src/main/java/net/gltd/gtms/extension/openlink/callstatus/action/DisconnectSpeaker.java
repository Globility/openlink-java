package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DisconnectSpeaker")
public class DisconnectSpeaker extends CallAction {
	@Override
	public String getId() {
		return "DisconnectSpeaker";
	}
}
