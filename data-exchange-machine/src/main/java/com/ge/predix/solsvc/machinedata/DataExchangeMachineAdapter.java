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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.IMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionAdapterListener;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterException;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterInfo;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterState;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;
import com.ge.predix.solsvc.dataexchange.api.IDataExchangeConfig;
import com.ge.predix.solsvc.dataexchange.api.IDataExchangeConnector;

import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

/**
 * 
 * @author Predix Machine Sample
 */
@Component(name = DataExchangeMachineAdapter.SERVICE_PID,service={
        ISubscriptionMachineAdapter.class, IMachineAdapter.class
})

public class DataExchangeMachineAdapter
        implements ISubscriptionMachineAdapter
{
    /** Service PID for Sample Machine Adapter */
    public static final String                SERVICE_PID         = "com.ge.predix.solsvc.machinedata.adapter";         //$NON-NLS-1$
    /** The regular expression used to split property values into String array. */
    public final static String                SPLIT_PATTERN       = "\\s*\\|\\s*";                                               //$NON-NLS-1$

    /**
     * 
     */
    public final static String 				  MACHINE_HOME		  = System.getProperty("predix.home.dir"); 					 	 //$NON-NLS-1$
    // Create logger to report errors, warning massages, and info messages (runtime Statistics)
    private static final Logger               _logger             = LoggerFactory
                                                                          .getLogger(DataExchangeMachineAdapter.class);
    private UUID                              uuid                = UUID.randomUUID();
    private Dictionary<String, Object>        props;
    private MachineAdapterInfo                adapterInfo;
    private MachineAdapterState               adapterState;
    private Map<UUID,DataExcahngeDataNode>         dataNodes           = new HashMap<UUID, DataExcahngeDataNode>();

    private IDataExchangeConfig               dataExchangeConfig;
    
    private IDataExchangeConnector 				dataExchange;
    
    /**
     * Data cache for holding latest data updates
     */
    protected Map<UUID, PDataValue>           dataValueCache      = new ConcurrentHashMap<UUID, PDataValue>();
    private Map<UUID, DataExchangeSubscription> dataSubscriptions   = new HashMap<UUID, DataExchangeSubscription>();

    private List<DataExchangeJsonDataNode> 				  configNodes 		  = new ArrayList<DataExchangeJsonDataNode>();

    private DecimalFormat 					  decimalFormat 		= new DecimalFormat("####.##"); //$NON-NLS-1$
    
    /*
     * ###############################################
     * # OSGi service lifecycle management #
     * ###############################################
     */

    /**
     * OSGi component lifecycle activation method
     * 
     * @param ctx component context
     * @throws IOException on fail to load/set configuration properties
     */
    @Activate
    public void activate(ComponentContext ctx)
            throws IOException
    {
       
        _logger.info("Starting sample " + ctx.getBundleContext().getBundle().getSymbolicName()); //$NON-NLS-1$
        
        ObjectMapper mapper = new ObjectMapper();
        File configFile = new File(MACHINE_HOME+File.separator+this.dataExchangeConfig.getNodeConfigFile());
        this.configNodes = mapper.readValue(configFile, new TypeReference<List<DataExchangeJsonDataNode>>()
        {
            //
        });
        _logger.info("Nodes : "+this.configNodes.toString()); //$NON-NLS-1$
        createNodes(this.configNodes);

        this.adapterInfo = new MachineAdapterInfo(this.dataExchangeConfig.getSubscriptionDataAdapterName(),
        		DataExchangeMachineAdapter.SERVICE_PID, this.dataExchangeConfig.getSubscriptionDataAdapterDescription(), ctx
                        .getBundleContext().getBundle().getVersion().toString());

        List<String> subs = Arrays.asList(this.dataExchangeConfig.getDataSubscriptions());
        // Start data subscription and sign up for data updates.
        for (String sub : subs)
        {
            DataExchangeSubscription dataSubscription = new DataExchangeSubscription(this, sub, this.dataExchangeConfig.getSubscriptionUpdateInterval(),
                    new ArrayList<PDataNode>(this.dataNodes.values()),this.dataExchange);
            this.dataSubscriptions.put(dataSubscription.getId(), dataSubscription);
            // Using internal listener, but these subscriptions can be used with Spillway listener also
            //dataSubscription.addDataSubscriptionListener(this.dataUpdateHandler);
            new Thread(dataSubscription).start();
        }
    }

    @SuppressWarnings("unused")
	private String[] parseDataSubscriptions(String key)
    {
    	
        Object objectValue = this.props.get(key);
        _logger.info("Key : "+key+" : "+objectValue); //$NON-NLS-1$ //$NON-NLS-2$
        if ( objectValue == null )
        {
            invalidDataSubscription();
        }else {

	        if ( objectValue instanceof String[] )
	        {
	            if ( ((String[]) objectValue).length == 0 )
	            {
	                invalidDataSubscription();
	            }
	            return (String[]) objectValue;
	        }
	
	        String stringValue = objectValue.toString();
	        if ( stringValue.length() > 0 )
	        {
	            return stringValue.split(SPLIT_PATTERN);
	        }
        }
        invalidDataSubscription();
        return new String[0];
    }

    
    private void invalidDataSubscription()
    {
        // data subscriptions must not be empty.
        String msg = "SampleSubscriptionAdapter.dataSubscriptions.invalid"; //$NON-NLS-1$
        _logger.error(msg);
        throw new MachineAdapterException(msg);
    }

    /**
     * OSGi component lifecycle deactivation method
     * 
     * @param ctx component context
     */
    @Deactivate
    public void deactivate(ComponentContext ctx)
    {
        // Put your clean up code here when container is shutting down
        if ( _logger.isDebugEnabled() )
        {
            _logger.debug("Stopped sample for " + ctx.getBundleContext().getBundle().getSymbolicName()); //$NON-NLS-1$
        }

        Collection<DataExchangeSubscription> values = this.dataSubscriptions.values();
        // Stop random data generation thread.
        for (DataExchangeSubscription sub : values)
        {
            sub.stop();
        }
        this.adapterState = MachineAdapterState.Stopped;
    }

    /**
     * OSGi component lifecycle modified method. Called when
     * the component properties are changed.
     * 
     * @param ctx component context
     */
    @Modified
    public synchronized void modified(ComponentContext ctx)
    {
        // Handle run-time changes to properties.

        this.props = ctx.getProperties();
    }

    /*
     * #######################################
     * # IMachineAdapter interface methods #
     * #######################################
     */

    @Override
	public UUID getId()
    {
        return this.uuid;
    }

    @Override
	public MachineAdapterInfo getInfo()
    {
        return this.adapterInfo;
    }

    @Override
	public MachineAdapterState getState()
    {
        return this.adapterState;
    }

    /*
     * Returns all data nodes. Data nodes are auto-generated at startup.
     */
    @Override
	public List<PDataNode> getNodes()
    {
        return new ArrayList<PDataNode>(this.dataNodes.values());
    }

    /*
     * Reads data from data cache. Data cache always contains latest values.
     */
    @Override
	public PDataValue readData(UUID nodeId)
            throws MachineAdapterException
    {
    	
        //DecimalFormat df = new DecimalFormat("####.##"); //$NON-NLS-1$
        DataExcahngeDataNode node = this.dataNodes.get(nodeId);
    	double fvalue = generateRandomUsageValue(node.getNode());
    	if (node.getNode().getExpression() != null && !"".equals(node.getNode().getExpression())) { //$NON-NLS-1$
    		String expr = node.getNode().getExpression();
    		fvalue = eval(expr.replaceAll("#NODE_VALUE#", Double.toString(fvalue))); //$NON-NLS-1$
    	}
        PEnvelope envelope = new PEnvelope(fvalue);
        DataExchangeDataValue pDataValue = new DataExchangeDataValue(node.getNodeId(), envelope);
        pDataValue.setNodeName(node.getName());
        pDataValue.setAddress(node.getAddress());
        pDataValue.setAssetId(node.getNode().getAssetId());
        // Do not return null.
        return pDataValue;
    }

    /*
     * Writes data value into data cache.
     */
    @Override
	public void writeData(UUID nodeId, PDataValue value)
            throws MachineAdapterException
    {
        if ( this.dataValueCache.containsKey(nodeId) )
        {
            // Put data into cache. The value typically should be written to a device node.
            this.dataValueCache.put(nodeId, value);
        }
    }

    /*
     * ###################################################
     * # ISubscriptionMachineAdapter interface methods #
     * ###################################################
     */

    /*
     * Returns list of all subscriptions.
     */
    @Override
	public List<IDataSubscription> getSubscriptions()
    {
        return new ArrayList<IDataSubscription>(this.dataSubscriptions.values());
    }

    /*
     * Adds new data subscription into the list.
     */
    @Override
	public synchronized UUID addDataSubscription(IDataSubscription subscription)
            throws MachineAdapterException
    {
        if ( subscription == null )
        {
            throw new IllegalArgumentException("Subscription is null"); //$NON-NLS-1$
        }

        List<PDataNode> subscriptionNodes = new ArrayList<PDataNode>();

        // Add new data subscription.
        if ( !this.dataSubscriptions.containsKey(subscription.getId()) )
        {
            // Make sure that new subscription contains valid nodes.
            for (PDataNode node : subscription.getSubscriptionNodes())
            {
                if ( !this.dataNodes.containsKey(node.getNodeId()) )
                {
                    throw new MachineAdapterException("Node doesn't exist for this adapter"); //$NON-NLS-1$
                }

                subscriptionNodes.add(this.dataNodes.get(node.getNodeId()));
            }

            // Create new subscription.
            DataExchangeSubscription newSubscription = new DataExchangeSubscription(this, subscription.getName(),
                    subscription.getUpdateInterval(), subscriptionNodes,this.dataExchange);
            this.dataSubscriptions.put(newSubscription.getId(), newSubscription);
            new Thread(newSubscription).start();
            return newSubscription.getId();
        }

        return null;
    }

    /*
     * Remove data subscription from the list
     */
    @Override
	public synchronized void removeDataSubscription(UUID subscriptionId)
    {
        // Stop subscription, notify all subscribers, and remove subscription
        if ( this.dataSubscriptions.containsKey(subscriptionId) )
        {
            this.dataSubscriptions.get(subscriptionId).stop();
            this.dataSubscriptions.remove(subscriptionId);
        }
    }

    /**
     * get subscription given subscription id.
     */
    @Override
	public IDataSubscription getDataSubscription(UUID subscriptionId)
    {
        if ( this.dataSubscriptions.containsKey(subscriptionId) )
        {
            return this.dataSubscriptions.get(subscriptionId);
        }
        throw new MachineAdapterException("Subscription does not exist"); //$NON-NLS-1$ 
    }

    @Override
	public synchronized void addDataSubscriptionListener(UUID dataSubscriptionId, IDataSubscriptionListener listener)
            throws MachineAdapterException
    {
        if ( this.dataSubscriptions.containsKey(dataSubscriptionId) )
        {
            this.dataSubscriptions.get(dataSubscriptionId).addDataSubscriptionListener(listener);
            return;
        }
        throw new MachineAdapterException("Subscription does not exist"); //$NON-NLS-1$	
    }

    @Override
	public synchronized void removeDataSubscriptionListener(UUID dataSubscriptionId, IDataSubscriptionListener listener)
    {
        if ( this.dataSubscriptions.containsKey(dataSubscriptionId) )
        {
            this.dataSubscriptions.get(dataSubscriptionId).removeDataSubscriptionListener(listener);
        }
    }

    /*
     * #####################################
     * # Private methods #
     * #####################################
     */

    /**
     * Generates random nodes
     * 
     * @param count of nodes
     */
    private void createNodes(List<DataExchangeJsonDataNode> nodes)
    {
    	this.dataNodes.clear();
        for (DataExchangeJsonDataNode jsonNode:nodes)
        {
            DataExcahngeDataNode node = new DataExcahngeDataNode(this.uuid, jsonNode);
            // Create a new node and put it in the cache.
            this.dataNodes.put(node.getNodeId(), node);
            
        }
    }

    // Put data into data cache.
    /**
     * @param values list of values
     */
    protected void putData(List<PDataValue> values)
    {
        for (PDataValue value : values)
        {
            this.dataValueCache.put(value.getNodeId(), value);
        }
    }


	@Override
	public void addSubscriptionAdapterListener(ISubscriptionAdapterListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSubscriptionAdapterListener(ISubscriptionAdapterListener arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private  Double generateRandomUsageValue(DataExchangeJsonDataNode node)
    {
		double start = node.getLowerThreshold();
	 	if (node.getSimulateAlarm()) {
	 		start = node.getUpperThreshold();
	 	}
        return new Double(this.decimalFormat.format(start + Math.random() * (node.getUpperThreshold() - node.getLowerThreshold())));
    }

	/**
	 * @return the configNodes
	 */
	public List<DataExchangeJsonDataNode> getConfigNodes() {
		return this.configNodes;
	}

	/**
	 * @param configNodes the configNodes to set
	 */
	public void setConfigNodes(List<DataExchangeJsonDataNode> configNodes) {
		this.configNodes = configNodes;
		createNodes(this.configNodes);
	}
	
	/**
	 * @param expression -
	 * @return -
	 */
	public double eval(String expression) {
		Expression expr;
		try {
			expr = Parser.parse(expression);
			return expr.evaluate();
		} catch (ParseException e) {
			throw new RuntimeException("Exception when parsing expression",e); //$NON-NLS-1$
		}
	}
	
	/**
	 * @return -
	 */
	public IDataExchangeConnector getDataExchange() {
		return this.dataExchange;
	}

	/**
	 * @param dataExchange -
	 */
	@Reference
	public void setDataExchange(IDataExchangeConnector dataExchange) {
		this.dataExchange = dataExchange;
	}

	/**
	 * @return the dataExchangeConfig
	 */
	public IDataExchangeConfig getDataExchangeConfig() {
		return this.dataExchangeConfig;
	}

	/**
	 * @param dataExchangeConfig the dataExchangeConfig to set
	 */
	@Reference
	public void setDataExchangeConfig(IDataExchangeConfig dataExchangeConfig) {
		this.dataExchangeConfig = dataExchangeConfig;
	}

}
