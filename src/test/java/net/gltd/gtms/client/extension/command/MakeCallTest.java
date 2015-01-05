package net.gltd.gtms.client.extension.command;

import java.util.HashSet;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;
import net.gltd.gtms.extension.command.Command;
import net.gltd.gtms.extension.command.Note;
import net.gltd.gtms.extension.iodata.IoData;
import net.gltd.gtms.extension.openlink.command.MakeCall;
import net.gltd.gtms.extension.openlink.originatorref.Property;
import net.gltd.util.log.GtmsLog;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.XmlTest;
import rocks.xmpp.core.stanza.model.client.IQ;
import rocks.xmpp.core.stanza.model.client.Message;

public class MakeCallTest extends XmlTest {

	protected Logger logger = Logger.getLogger("net.gltd.gtms");

	public MakeCallTest() throws JAXBException, XMLStreamException {
		super(Command.class, Note.class, Message.class, IQ.class, IoData.class, MakeCall.class,
				MakeCall.MakeCallIn.class, MakeCall.MakeCallIn.MakeCallFeature.class);
	}

	@Before
	public void initialize() throws Exception {
		logger = GtmsLog.initializeConsoleLogger("net.gltd.gtms", GtmsLog.DEFAULT_DEBUG_CONVERSION_PATTERN, "DEBUG");
	}

	@After
	public void shutdown() throws Exception {
		LogManager.shutdown();
	}

	@Test
	public void testCommand() throws XMLStreamException, JAXBException {
		MakeCall mc = new MakeCall();
		mc.getIn().setDestination(RandomStringUtils.randomNumeric(10));
		mc.getIn().setJid(Jid.valueOf("leon@dom"));
		mc.getIn().setInterest("leon_office_vmstsp_default");

		HashSet<Property> props = new HashSet<Property>();
		Property prop1 = new Property();
		prop1.setId("1234");
		prop1.setValue("ABCD");
		props.add(prop1);

		mc.getIn().setOriginatorRef(props);

		MakeCall.MakeCallIn.MakeCallFeature mcf = new MakeCall.MakeCallIn.MakeCallFeature();
		mcf.setId("Callback");
		mcf.setValue1("true");
		mcf.setValue2("6001");
		mc.getIn().getFeatures().add(mcf);

		mcf = new MakeCall.MakeCallIn.MakeCallFeature();
		mcf.setId("CallerId");
		mcf.setValue1("true");
		mcf.setValue2("6001");
		mc.getIn().getFeatures().add(mcf);

		mcf = new MakeCall.MakeCallIn.MakeCallFeature();
		mcf.setId("Conference");
		mcf.setValue1("true");
		mc.getIn().getFeatures().add(mcf);

		Assert.assertNotNull(mc);
		Assert.assertTrue(mc.getIn().getFeatures().size() == 3);
		for (MakeCall.MakeCallIn.MakeCallFeature f : mc.getIn().getFeatures()) {
			Assert.assertNotNull(f.getId());
		}

		IQ iq = new IQ(Jid.valueOf("vmstsp.dom"), IQ.Type.GET, mc);

		String xml = marshal(iq);
		Assert.assertNotNull(xml);
		logger.debug(xml);

		// Assert.assertTrue(xml.contains("<iodata xmlns=\"urn:xmpp:tmp:io-data\" type=\"output\">"));

	}
}
