package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StopVoiceDrop")
public class StopVoiceDrop extends CallAction {
	@Override
	public String getId() {
		return "StopVoiceDrop";
	}
}
