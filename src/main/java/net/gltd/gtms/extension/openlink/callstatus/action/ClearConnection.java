package net.gltd.gtms.extension.openlink.callstatus.action;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ClearConnection")
public class ClearConnection extends CallAction {
	@Override
	public String getId() {
		return "ClearConnection";
	}
	
}
