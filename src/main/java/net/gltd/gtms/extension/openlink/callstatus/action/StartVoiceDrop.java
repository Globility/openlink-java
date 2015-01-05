package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StartVoiceDrop")
public class StartVoiceDrop extends CallAction {
	@Override
	public String getId() {
		return "StartVoiceDrop";
	}
}
