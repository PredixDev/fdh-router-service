package com.ge.predix.solsvc.fdh.router.service.spring;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * An example of creating a Spring Based Rest Service
 * @author predix
 */
@RestController
public class HelloController {

	
    /**
     * 
     */
    public HelloController() {
		super();
	}

	/**
	 * @param echo -
	 * @return -
	 */
	@SuppressWarnings({
            "nls"
    })
    @RequestMapping("/")
    public String index(@RequestParam(value="echo",defaultValue="echo") String echo) {
        return "Greetings from Predix Boot! " + (new Date()) + " echo=" + echo;
    }

}