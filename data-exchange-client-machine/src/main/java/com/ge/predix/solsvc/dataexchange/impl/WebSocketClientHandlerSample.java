/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.dataexchange.impl;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles messages received by the client. Messages can be sent
 * from this class or from the container class by using the websocket session
 * object.
 *
 * @author Predix Machine Sample
 */

@ClientEndpoint
public class WebSocketClientHandlerSample
{
    private static Logger _logger = LoggerFactory.getLogger(WebSocketClientHandlerSample.class.getName());

    /**
     * Defines the behavior of the handler when a session is opened. Prints
     * the status to the logger.
     * 
     * @param session The web socket session
     */
    @OnOpen
    public void onOpen(Session session)
    {
        _logger.info("Client: opened... " + session.getId()); //$NON-NLS-1$
    }

    /**
     * Defines the behavior of the handler when a string message is received.
     * Checks the message for validity and logs transmission success or failure
     * 
     * @param message The string message that was received
     * @param session The web socket session
     */
    @OnMessage
    public void onStringMessage(String message, Session session)
    {
        _logger.info("Client: received... " + message + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Defines the behavior of the handler when a byte message is received.
     * Checks the message for validity and logs transmission success or failure
     * 
     * @param message The byte buffer message that was received
     * @param session The web socket session
     */
    @OnMessage
    public void onByteMessage(ByteBuffer message, Session session)
    {
        String result = new String(message.array(), Charset.forName("UTF-8")); //$NON-NLS-1$
        _logger.info("Client: received... " + result + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Defines the behavior of the client message handler when the session
     * is closed.
     * 
     * @param session The web socket session
     * @param closeReason Provides information on the session close including
     *            close reason phrase, code, etc...
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason)
    {
        _logger.info("Client: Session " + session.getId() + " closed because of " + closeReason.toString()); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
