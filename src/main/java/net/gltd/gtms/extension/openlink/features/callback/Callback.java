package net.gltd.gtms.extension.openlink.features.callback;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "callback")
public class Callback {

	@XmlElement
	private boolean allocation;

	@XmlElement
	private String destination;

	@XmlElement
	private Active active;

	public boolean isAllocation() {
		return allocation;
	}

	public void setAllocation(boolean allocation) {
		this.allocation = allocation;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Active getActive() {
		return active;
	}

	public void setActive(Active active) {
		this.active = active;
	}

	public static class Active {

		@XmlValue
		private boolean value;

		@XmlAttribute
		private CallbackState state;

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public CallbackState getState() {
			return state;
		}

		public void setState(CallbackState state) {
			this.state = state;
		}

	}
	
	public enum CallbackState {
		Onhook, Offhook, Ringing;
	}

}
