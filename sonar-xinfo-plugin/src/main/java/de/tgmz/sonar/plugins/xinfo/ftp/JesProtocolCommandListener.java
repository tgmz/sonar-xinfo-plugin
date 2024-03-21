/*******************************************************************************
  * Copyright (c) 03.03.2024 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.ftp;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for logging ftp commands.
 */
public class JesProtocolCommandListener implements ProtocolCommandListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(JesProtocolCommandListener.class);

	@Override
	public void protocolCommandSent(ProtocolCommandEvent event) {
        if (LOGGER.isDebugEnabled()) {
        	final String cmd = event.getCommand();
        	
        	if ("PASS".equalsIgnoreCase(cmd) || "USER".equalsIgnoreCase(cmd)) {
        		LOGGER.debug(String.format("%s %s", cmd, "*******"));
        	} else {
        		LOGGER.debug(getPrintableString(event.getMessage()));
        	}
        }
	}

	@Override
	public void protocolReplyReceived(ProtocolCommandEvent event) {
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug(getPrintableString(event.getMessage()));
        }
	}
	
    private String getPrintableString(final String msg) {
        final int pos = msg.indexOf(SocketClient.NETASCII_EOL);
        if (pos > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(msg.substring(0, pos));
            return sb.toString();
        }
        
        return msg;
    }
}
