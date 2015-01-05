package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SendDigits")
public class SendDigits extends CallAction {
	@Override
	public String getId() {
		return "SendDigits";
	}
}
