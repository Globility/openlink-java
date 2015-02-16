@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(type = Jid.class, value = JidAdapter.class)
@XmlSchema(namespace = net.gltd.gtms.client.openlink.OpenlinkNamespaces.NS_OPENLINK_DEVICESTATUS, elementFormDefault = XmlNsForm.QUALIFIED)
package net.gltd.gtms.extension.openlink.devicestatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import rocks.xmpp.core.Jid;
import rocks.xmpp.core.JidAdapter;

