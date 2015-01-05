# Implementation of Openlink library for Babbler (Java)

##Description

This application uses [Babbler](https://bitbucket.org/sco0ter/babbler/) to provide an implementation of the [Openlink XMPP API](http://openlink.4ng.net:8080/openlink/xep-xxx-openlink_15-11.xml).

##Components

###OpenlinkJava XMPP Library

The OpenlinkJava library itself is comprised of three main files: `openlink-java.jar, xmpp-core.jar, xmpp-core-client.jar, xmpp-extensions.jar and xmpp-extensions-client.jar`. It extends upon the [Babbler](https://bitbucket.org/sco0ter/babbler/) library to provide full client functionality against an Openlink (XMPP) server.

If managing your own dependencies simply use `openlink-java.jar` otherwise use `openlink-java-jar-with-dependencies.jar`.

More information about Babbler can be found in the [Javadoc](http://sco0ter.bitbucket.org/babbler/apidocs/index.html) and [Babbler General Documentation](http://docs.xmpp.rocks/). But the short summary is to simply drop the files into your application and use  OpenlinkClient.java to invoke your Openlink commands.
 
The `Babbler Java` library is referenced by instantiating `OpenlinkClientTest` with the necessary XMPP parameters and then calling connect.

###Openlink Java Test Examples

The Openlink Java Test Example consists of the file `OpenlinkClientTest.java`. Configure the required XMPP parameters and then run the scenarios to trigger the standard Openlink functionality (getFeatures, getInterests, getProfiles, makeCall etc.).

##Getting started with the Openlink Java Library
If you haven’t already done so, install Maven. 

Run `mvn clean install` in your console to install the dependencies and build the package.

###Layout

The items required to use the application can all be found in the `src` folder. It follows the typical Maven layout.

###Application Arguments

The applications arguments are:
* `setDebug`: true/false - enables debugging 
* `domain`: The XMPP domain.
* `server`: The XMPP server hostname.
* `resource`: Your XMPP resource.
* `username`: Optional username to pre-populate the example app.
* `password`: Optional password to pre-populate the example app.

##Application Guide

Whilst the `OpenlinkClientTest` file is only a guide it follows the standard structure for Babbler client applications.

###Instantiate

	OpenlinkClient client = new OpenlinkClient(USERNAME, PASSWORD, RESOURCE, DOMAIN, HOST);
	
###Connect
	client.connect();
	
###Disconnect

    client.disconnect();

###Connected/Disconnected Events

These can be listened for by adding a rocks.xmpp.core.session.SessionStatusListener object to the XmppSession object retrieved by getXmppSession();￼
	client.getXmppSession().addSessionStatusListener(		new SessionStatusListener() {
			@Override
			public void sessionStatusChanged(SessionStatusEvent e) {...}
	});

###Call Events

A call handler can be registered to simplify the process of receiving pubsub call events. Simply call before connecting:

	CallListener listener = new CallListener();
	client.addCallListener(listener);

The method `callEvent(Collection<Call> calls)` will be called with a list of call objects on a call event change.

##License

`This file is subject to the terms and conditions defined 3in file `LICENSE`, which is part of this source code package.`
