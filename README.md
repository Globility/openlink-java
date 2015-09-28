# Implementation of Openlink library for Babbler (Java)

##Description

This application uses [Babbler](https://bitbucket.org/sco0ter/babbler/) to provide an implementation of the [Openlink XMPP API](http://openlink.4ng.net:8080/openlink/xep-xxx-openlink_15-11.xml).

The OpenlinkJava library itself is comprised of a few JAR files: `openlink-java.jar, xmpp-core.jar, xmpp-core-client.jar, xmpp-extensions.jar and xmpp-extensions-client.jar`. It extends upon the [Babbler](https://bitbucket.org/sco0ter/babbler/) library to provide full client functionality against an Openlink (XMPP) server.

If managing your own dependencies simply use `openlink-java.jar` otherwise use `openlink-java-jar-with-dependencies.jar`.

More information about Babbler can be found in the [Babbler Javadoc](http://sco0ter.bitbucket.org/babbler/apidocs/index.html) and [Babbler General Documentation](http://docs.xmpp.rocks/).

##Layout

The items required to use the application can be found in the `src` folder. It follows the typical Maven layout:

	openlink-java
		pom.xml
		README.md
		src/
			main/
				java/
				resources/
			test/
				java/
				resources/		

##Building

1. If you haven’t already done so, install Maven. 

2. Clone our fork of the Babbler project:

	[https://bitbucket.org/globility/babbler/](https://bitbucket.org/globility/babbler/)

	From within the `babbler` folder run:
	
		mvn clean install
	
	This will install the *babbler* dependencies and build the package.

3. Clone the Openlink Java Library:

	[https://github.com/Globility/openlink-java](https://github.com/Globility/openlink-java)

	From within the `openlink-java` folder run:
	
		mvn clean install

	This will install the *openlink-java* dependencies and build the package.
	
This will leave you with a versioned jar of just the openlink-java classes. As part of the default build you will also find a jar with all dependencies bundled **openlink-java-jar-with-dependencies.jar**.

##Getting started with the Client Examples

The Openlink Java Test Example consists of the file `OpenlinkClientIntegrationTest` for call control and `OpenlinkClientMvmIntegrationTest` for voice blast/drop and playback/record.

They are a good way of quickly getting to grips with using the Openlink library.

To use them configure the required XMPP parameters in `src/test/resources/client.properties` and then run the scenarios to trigger Openlink commands.

###Test Arguments

The test arguments are:

* `client.xmpp.username`: XMPP username
* `client.xmpp.password`: XMPP user password
* `client.xmpp.resource`: **(Optional)** XMPP resource
* `client.xmpp.domain`: XMPP domain (eg. example.com)
* `client.xmpp.host`: **(Optional)** XMPP Server Hostname/IP
* `client.xmpp.system`: Component node eg. avaya1, cisco1, etc.

For example the parameters below will connect `user0@cluster.gltd.local` to `cluster.gltd.local` and perform Openlink operations against `avaya1.cluster.gltd.local`.
	
	client.xmpp.username=user0
	client.xmpp.password=user0
	client.xmpp.resource=
	client.xmpp.domain=cluster.gltd.local
	client.xmpp.host=
	client.xmpp.system=avaya1

Once configured try running `OpenlinkClientIntegrationTest.checkIfConnected()` to verify your credentials are correct.

If this test passes you're ready to proceed with `getProfiles()`, `getInterests()`, `subscribeInterest()`, `makeCall()` etc.

##Getting started with the Client

Whilst the `OpenlinkClientIntegrationTest` file is only a guide it follows the standard structure for Babbler client applications.

###Instantiate

The first step is to create the OpenlinkClient object. The arguments are:

* `username`: XMPP username
* `password`: XMPP user password
* `resource`: **(Optional)** XMPP resource
* `domain`: XMPP domain (eg. example.com)
* `host`: **(Optional)** XMPP Server Hostname/IP

Code:

	OpenlinkClient client = new OpenlinkClient(USERNAME, PASSWORD, RESOURCE, DOMAIN, HOST);

###Debug

To enable logging of XMPP events and debug statements:
	
	client.setDebug(true);
	
###SSL

The XMPP Server certificate will need to be imported to your local Trust Store in order to use SSL if using self-signed certificates. See the section on [SSL Troubleshooting](#heading-ssl-troubleshooting) for more details.

To enable/disable SSL:

	client.setSecure(true);

###Connection Events

These can be listened for by adding a *rocks.xmpp.core.session.SessionStatusListener* object to the *XmppSession* object retrieved by *getXmppSession();*￼
	client.getXmppSession().addSessionStatusListener(		new SessionStatusListener() {
			@Override
			public void sessionStatusChanged(SessionStatusEvent e) {...}
	});
	
A *SessionStatusListener* on *sessionStatusChanged* will receive a *SessionStatusEvent* object with the new *XmppSession.Status*, the old *XmppSession.Status* and an optional Exception.

By default on an unexpected disconnect the client attempts to auto reconnect. See the section on [Client Reconnection](#heading-client-reconnection) for more details.

####Session Status
The following chart illustrates the valid status transitions of *XmppSession.Status*:

	 ┌────── INITIAL
	 │          │
	 │          ▼
	 ├───── CONNECTING ◄────────┐
	 │          │               │
	 │          ▼               │
	 ├───── CONNECTED ───► DISCONNECTED
	 │          │               ▲
	 │          ▼               │
	 ├─── AUTHENTICATING        │
	 │          │               │
	 │          ▼               │
	 ├─── AUTHENTICATED ────────┘
	 │          │
	 │          ▼
	 └─────► CLOSING
	            │
	            ▼
	         CLOSED
	 
**AUTHENTICATED**
The session has authenticated.

**AUTHENTICATING**
The session is currently authenticating with the server.

**CLOSED**
The session is closed.

**CLOSING**
The session is closing.

**CONNECTED**
The session is established with the server, features are negotiated, but we are not yet authenticated.

**CONNECTING**
The session is currently negotiating features.

**DISCONNECTED**
The session has been temporarily disconnected by an exception.

**INITIAL**
The session is in initial state.

###Call Events

A call handler can be registered to simplify the process of receiving pubsub call events. Simply call before connecting:

	CallListener listener = 
		new CallListener() {
			@Override
			public void callEvent(Collection<Call> calls) {
				...
			}
		};
	client.addCallListener(listener);

The method `callEvent(Collection<Call> calls)` will be called with a list of call objects on a call event change.

The collection of `Call` objects represents the call events sent by the XMPP server to the client. Each call object corresponds to an Openlink `<call>...</call>` element as encapsulated by `<callstatus xmlns="http://xmpp.org/protocol/openlink:01:00:00#call-status">`.

As a result `Call` objects will have call id, profile, interest, participant list, feature list, action list, direction, state, caller and callee properties.

**CallDirection** can be `Outgoing` or `Incoming`.

**CallState** can be `CallOriginated`, `CallDelivered`, `CallEstablished`, `CallFailed`, `CallConferenced`, `CallBusy`, `CallHeld`, `CallHeldElsewhere`, `CallTransferring`, `CallTransferred`, `ConnectionBusy`, `ConnectionCleared`, `CallMissed`.

Refer to the Openlink spec and the accompanying Javadoc for further details.

###Device Events

A device status handler can be registered to simplify the process of receiving pubsub device status events. Simply call before connecting:

	DeviceListener listener = 
		new DeviceListener() {
			@Override
			public void deviceEvent(String profile, Collection<DeviceStatusFeature> features) {
				...
			}
		};
	client.addDeviceListener(listener);

The method `deviceEvent(String profile, Collection<DeviceStatusFeature> features)` will be called with a list of call objects on a call event change.

`DeviceStatusFeature.getValue()` returns the feature type carried by the `<device-status>` element. It can for instance be a `Callback`, `Dtmf` or `VoiceMessage` instance.

Refer to the Openlink spec and the accompanying Javadoc for further details.

###Connecting to a Server

Once your session is prepared you are ready to connect to the server:

	try {
		client.connect();
	} catch (Exception e) {
		// ...
	}

A connection to the XMPP server will be attempted. Connecting involves opening the XMPP stream header and negotiating the authentication features offered by the server.

A number of exceptions are thrown by the connect method, these are:

* `KeyManagementException` If the SSL Context fails to initialize.
* `NoSuchAlgorithmException` If no such algorithm found by the SSLContext.
* `ConnectionException` If a connection error occurred on the transport layer, e.g. the socket could not connect.
* `StreamNegotiationException` If any exception occurred during stream feature negotiation.
* `NoResponseException` If the server didn't return a response during stream establishment.
* `IllegalStateException` If the session is in a wrong state, e.g. closed or already connected.
* `AuthenticationException` If the login failed, due to a SASL error reported by the server.
* `StreamErrorException` If the server returned a stream error.
* `StanzaException` If the server returned a stanza error during resource binding or roster retrieval.
* `XmppException` If the login failed, due to any other XMPP exception.
 
###Disconnect from the server

Disconnecting from the server is as simple as calling:

    client.disconnect();

###Client Reconnection<a name="heading-client-reconnection"></a>

The default behaviour of the client is to automatically reconnect if the connection goes down and the user was already authenticated.

Reconnection is not executed if the user is disconencted due to a `<conflict/> stream error from an overlapping resource.

Reconnection is performed X seconds after reconnect where X is between 0 and 60 for the first attempt, 0 and 180 for the second attempt, 0 and 420 for the third attempt, 0 and 900 for the fourth attempt and 0 and 1860 for the fifth attempt.

###SSL Troubleshooting<a name="heading-ssl-troubleshooting"></a>

If you encounter the following error when using SSL with self-signed certificates you will either need to use a trusted certificate or add the self-signed certificate to your keystore:

	Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

To do this with Openfire you will need to use `keytool` on the Openfire and client machines.

On RHEL keytool can be found here:
	
	/usr/java/latest/bin/keytool

1. Export the public RSA certificate from Openfire

	Determine the alias name (password `changeit`):

		keytool -list -keystore /opt/openfire/resources/security/keystore 

	Output:

		Your keystore contains 2 entries

		cluster.gltd.local_rsa, 29-Jun-2015, PrivateKeyEntry, 
		Certificate fingerprint (SHA1): 3A:84:6C:20:A1:2F:E7:12:A6:88:FF:A8:58:89:C6:8E:84:B6:9A:35
		cluster.gltd.local_dsa, 29-Jun-2015, PrivateKeyEntry, 
		Certificate fingerprint (SHA1): 3A:84:6C:20:A1:2F:E7:12:A6:88:FF:A8:58:89:C6:8E:84:B6:9A:35

Export the RSA entry using the corresponding alias eg. cluster.gltd.local_rsa in this case:

	keytool -export -alias cluster.gltd.local_rsa -keystore /opt/openfire/resources/security/keystore -rfc -file public_rsa.crt

2. Grab the file public_rsa.crt and on your client machine import it to your local truststore:

On Mac OS X:

	sudo keytool -importcert -file ~/public_rsa.crt -alias cluster.gltd.local_rsa -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit

###Merging with future releases of Babbler

This library uses **Babbler 0.5**.

The current release of Babbler at the time of writing is 0.6.0 which uses Java 8 and rewrites a fair swathe of the code base. As a result merging it is not advised at this time. Any merging should be done against the [Babbler 0.5 branch](https://bitbucket.org/sco0ter/babbler/branch/0.5).

The main items we customized in our own fork of Babbler are:

* Added `net.gltd.gtms.extension.iodata.IoData` to *openlink-java*
* Commented `rocks.xmpp.extensions.commands.model.Command` and `rocks.xmpp.extensions.commands.model.Note`. They  provide Ad-Hoc Command support however are incomplete. As a result we wrote our own versions `net.gltd.gtms.extension.command.Command` and `net.gltd.gtms.extension.command.Note` which supersedes the Babbler versions. 

Be sure to comment out any references to Babbler's versions of *Note* and *Command* in `rocks.xmpp.core.session.context.extensions.ExtensionContext` otherwise you'll encounter run-time errors due to overlapping JAXB classes.

Besides the pom.xml version being set to 0.5.X-gltd there are no modifications made to the Babbler source code itself so any version from the Babbler 0.5 branch should work without issue.

##License

`This file is subject to the terms and conditions defined in the file `LICENSE`, which is part of this source code package.`

*Version: 2.1.9*