/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.machinedata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import com.ge.dspmicro.machinegateway.types.PDataNode;

/**
 * 
 * 
 * @author Predix Machine Sample
 */
public class DataExcahngeDataNode extends PDataNode
{

		
    private DataExchangeJsonDataNode node;
        
	/**
	 * @param machineAdapterId -
	 * @param node -
	 */
	public DataExcahngeDataNode(UUID machineAdapterId, DataExchangeJsonDataNode node) {
		super(machineAdapterId, node.getNodeName());
		this.setNode(node);
	}

    /**
     * Node address to uniquely identify the node.
     */
    @Override
    public URI getAddress()
    {
        try
        {
            URI address = new URI("sample.subscription.adapter", null, "localhost", -1, "/" + getName(), null, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return address;
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }

	/**
	 * @return -
	 */
	public DataExchangeJsonDataNode getNode() {
		return this.node;
	}

	/**
	 * @param node -
	 */
	public void setNode(DataExchangeJsonDataNode node) {
		this.node = node;
	}
}
