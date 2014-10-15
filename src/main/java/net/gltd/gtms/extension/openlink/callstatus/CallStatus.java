package net.gltd.gtms.extension.openlink.callstatus;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "callstatus")
public class CallStatus extends HashSet<Call> {

	private static final long serialVersionUID = 6710896261104039543L;
	
	@XmlAttribute
	private boolean busy;

	@XmlElement(name = "call")
	public Set<Call> getCalls() {
		return this;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

}
