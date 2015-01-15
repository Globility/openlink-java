package net.gltd.gtms.extension.openlink.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.gltd.gtms.client.openlink.OpenlinkNamespaces;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.iodata.IoData.IoDataType;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import rocks.xmpp.core.Jid;

@XmlRootElement(name = "command")
public class MakeCall extends Command {

	@XmlTransient
	private IoData iodata;

	@XmlTransient
	private MakeCallIn in;

	public MakeCall() {
		setNode(OpenlinkNamespaces.NS_OPENLINK_MAKECALL);
		setAction(CommandAction.EXECUTE);

		this.in = new MakeCall.MakeCallIn();

		this.iodata = new IoData();
		iodata.setType(IoDataType.INPUT);
		iodata.setIn(in);
		getExtensions().add(iodata);
	}

	public IoData getIodata() {
		return iodata;
	}

	public void setIodata(IoData iodata) {
		this.iodata = iodata;
	}

	public MakeCallIn getIn() {
		return in;
	}

	public void setIn(MakeCall.MakeCallIn in) {
		this.in = in;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "in", namespace = "")
	@XmlType(name = "", propOrder = { "jidAsString", "interest", "destination", "originatorRef", "features" })
	public static class MakeCallIn {
		@XmlTransient
		private Jid jid;

		@XmlElement
		private String interest;

		@XmlElement
		private String destination;

		@XmlElementWrapper(name = "originator-ref")
		@XmlElement(name = "property")
		private Collection<Property> originatorRef;

		@XmlElementWrapper(name = "features")
		@XmlElement(name = "feature")
		private Set<MakeCall.MakeCallIn.MakeCallFeature> features = new HashSet<MakeCall.MakeCallIn.MakeCallFeature>();

		@XmlElement(name = "jid")
		public String getJidAsString() {
			String result = null;
			if (getJid() != null) {
				result = getJid().asBareJid().toString();
			}
			return result;
		}

		public Jid getJid() {
			return jid;
		}

		public void setJid(Jid jid) {
			this.jid = jid;
		}

		public String getInterest() {
			return interest;
		}

		public void setInterest(String interest) {
			this.interest = interest;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}

		public Collection<Property> getOriginatorRef() {
			return originatorRef;
		}

		public void setOriginatorRef(Collection<Property> originatorRef) {
			this.originatorRef = originatorRef;
		}

		public Collection<MakeCall.MakeCallIn.MakeCallFeature> getFeatures() {
			return features;
		}

		public void setFeatures(Set<MakeCall.MakeCallIn.MakeCallFeature> features) {
			this.features = features;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		public static class MakeCallFeature {

			@XmlElement
			private String id;

			@XmlElement
			private String value1;

			@XmlElement
			private String value2;

			@XmlElement
			private String value3;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getValue1() {
				return value1;
			}

			public void setValue1(String value1) {
				this.value1 = value1;
			}

			public String getValue2() {
				return value2;
			}

			public void setValue2(String value2) {
				this.value2 = value2;
			}

			public String getValue3() {
				return value3;
			}

			public void setValue3(String value3) {
				this.value3 = value3;
			}

		}

	}

}
