package com.ge.predix.solsvc.simulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ge.predix.solsvc.simulator.types.DataSimulatorResponse;
import com.ge.predix.solsvc.simulator.types.SimulatorDataNode;

/**
 * 
 * @author predix -
 */
@RestController
public class DataSimulatorService {
	private static Logger        log       = LoggerFactory.getLogger(DataSimulatorService.class);
	
	/**
	 * @param dataNodes -
	 * @return -
	 */
	@SuppressWarnings("nls")
    @Autowired
	@RequestMapping(value = "/changesimulatorconfig", method = RequestMethod.POST)
    public @ResponseBody DataSimulatorResponse changeSimulatorConfig(@RequestBody List<SimulatorDataNode> dataNodes)
    {
		DataSimulatorResponse response = new DataSimulatorResponse();
        try{
        	if (dataNodes != null) {
        		ScheduledDataExchangeSimulator.setNodeList(dataNodes);
        	}
        	ObjectMapper mapper = new ObjectMapper();
        	mapper.enable(SerializationFeature.INDENT_OUTPUT);
        	log.info("changesimulatorconfig dataNodes= " + mapper.writeValueAsString(dataNodes));
        }
        catch (Throwable e)
        {
            log.error("changesimulatorconfig failed",e);
            response.setIsError(Boolean.TRUE);
            response.setErrorMessage(e.getMessage());
        }
        return response;
    }
	
	/**
	 * @return -
	 */
	@RequestMapping(value = "/getsimulatorconfig", method = RequestMethod.GET)
	public @ResponseBody List<SimulatorDataNode> listExistingConfig() {
		return ScheduledDataExchangeSimulator.getNodeList();
	}
	/**
	 * @return -
	 */
	@SuppressWarnings("nls")
    @RequestMapping(value = "/simulatealarm", method = RequestMethod.GET)
	public @ResponseBody String simulateAlarm() {
		for (SimulatorDataNode node: ScheduledDataExchangeSimulator.getNodeList()) {
			node.setSimulateAlarm(Boolean.TRUE);
		}
		return "Alarm Simulation started";
	}
}
