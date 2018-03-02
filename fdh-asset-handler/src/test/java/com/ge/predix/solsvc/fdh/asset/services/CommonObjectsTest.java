/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.asset.services;

import static com.ge.predix.solsvc.fdh.handler.asset.common.DateUtil.ISO_8601_FORMAT;
import static com.ge.predix.solsvc.fdh.handler.asset.common.DateUtil.getDateFromISOString;
import static com.ge.predix.solsvc.fdh.handler.asset.common.DateUtil.getDateFromString;
import static com.ge.predix.solsvc.fdh.handler.asset.common.DateUtil.getISOStringFromDate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.ge.predix.solsvc.bootstrap.ams.dto.Cardinality;
import com.ge.predix.solsvc.bootstrap.ams.dto.Classification;
import com.ge.predix.solsvc.bootstrap.ams.dto.Message;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.asset.common.ClassificationQueryBuilder;
import com.ge.predix.solsvc.fdh.handler.asset.common.Operator;
import com.ge.predix.solsvc.fdh.handler.asset.common.Reading;
import com.ge.predix.solsvc.fdh.handler.asset.common.RequestContext;
import com.ge.predix.solsvc.fdh.handler.asset.utils.AmsHttpClientErrorException;
import com.ge.predix.solsvc.fdh.handler.asset.utils.AmsHttpServerErrorException;

/**
 * 
 * @author predix -
 */
@SuppressWarnings(
{
        "nls"
})
@Ignore
public class CommonObjectsTest extends ServiceTestBase
{
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CommonObjectsTest.class);
    
    @Autowired
    private JsonMapper jsonMapper;

	
    /**
     *  -
     * @throws IOException -
     */
    @Test
    public void testJsonParsing() throws IOException
    {
        this.readTextFromResource("testData/XAutoFleetClassification.json");
        List<Classification> list = this.loadFromResource(Classification.class,
                "testData/XAutoFleetClassification.json");

        String json1 = this.jsonMapper.toJson(list.get(0));
        Classification c = this.jsonMapper.fromJson(json1, Classification.class);
        Assert.assertNotNull(c);
    }

    /**
     *  -
     */
    @Test
    public void testOperator()
    {

        Operator eq = Operator.EQ;
        Operator le = Operator.LE;
        Operator ge = Operator.GE;

        Assert.assertTrue(eq.isValidValue("3.0"));
        Assert.assertTrue(le.isValidValue("*...3.0"));
        Assert.assertTrue(ge.isValidValue("3.0...*"));

        Object oe = eq.getValue("3.0");
        Object ol = le.getValue("*...3.0");
        Object og = ge.getValue("3.0...*");
        Assert.assertTrue(oe.equals("3.0"));
        Assert.assertTrue(ol.equals(3.0));
        Assert.assertTrue(og.equals(3.0));
    }

    /**
     *  -
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testDateUtil()
    {

        Date date = new Date(2012, 1, 1);
        String str = getISOStringFromDate(date.getTime());
        java.util.Date date1 = getDateFromISOString(str);
        getDateFromString(str, ISO_8601_FORMAT);
        Assert.assertTrue(date1.equals(date));
    }

    /**
     *  -
     */
    @Test
    public void testCardinality()
    {
        Cardinality c = new Cardinality(); // ("0..",999,100,"", 0);
        c.setMax("100");
        c.setMin("10");
        Assert.assertTrue(c.getMax() == "100");
        Assert.assertTrue(c.getMin() == "10");
        Assert.assertTrue(!c.toString().isEmpty());
        Assert.assertTrue(c.hashCode() != 0);
    }

    /**
     *  -
     */
    @Test
    public void testMessage()
    {
        Message c = new Message();
        c.setMessage("All good!");
        c.setErrors("No error");
        Assert.assertTrue(c.getMessage() == "All good!");
        Assert.assertTrue(c.getErrors() == "No error");
        Assert.assertTrue(!c.toString().isEmpty());
        Assert.assertTrue(c.hashCode() != 0);
    }

    /**
     *  -
     */
    @Test
    public void testAmsHttpClientErrorException()
    {
        AmsHttpClientErrorException e = new AmsHttpClientErrorException(HttpStatus.BAD_REQUEST, "", "What?",
                Charset.defaultCharset());
        Assert.assertTrue(e.statusCode == HttpStatus.BAD_REQUEST);
        Assert.assertTrue(e.getBody().startsWith("What"));
    }

    /**
     *  -
     */
    @Test
    public void testAmsHttpServerErrorException()
    {
        AmsHttpServerErrorException e = new AmsHttpServerErrorException(HttpStatus.BAD_REQUEST, "", "What?",
                Charset.defaultCharset());
        Assert.assertTrue(e.statusCode == HttpStatus.BAD_REQUEST);
        Assert.assertTrue(e.getBody().startsWith("What"));
    }


    /**
     *  -
     */
    @Test
    public void classificationQuery()
    {
        String uri = "/cl123";
        // case 1 - with uri
        ClassificationQueryBuilder q = new ClassificationQueryBuilder(uri);
        Assert.assertTrue(q.build().equals("/cl123"));
        // case 2 - with name
        q = new ClassificationQueryBuilder();
        q.addNameFilter("EWO100");
        Assert.assertTrue(q.build().equals("?filter=name=EWO100"));
        Assert.assertTrue(q.hashCode() != 0);
        Assert.assertTrue(!q.toString().isEmpty());
    }



    /**
     *  -
     */
    @SuppressWarnings("deprecation")
    @Test
    public void reading()
    {
        Reading q = new Reading();
        Date date = new Date(2013, 01, 01);

        q.setTime(date);
        Assert.assertNotNull(q.getTime());

        q.setTimeUTC("2013-02-01T08:00:00.000Z");
        Assert.assertTrue(q.getTimeUTC().equals("2013-02-01T08:00:00.000Z"));

        q.setValue("123");
        Assert.assertTrue(q.getValue().equals("123"));

        Assert.assertTrue(q.hashCode() != 0);
        Assert.assertTrue(!q.toString().isEmpty());
    }


    /**
     *  -
     */
    @Test
    public void requestContext()
    {
        RequestContext r = new RequestContext();
        RequestContext.put("1", "one");
        Assert.assertTrue(RequestContext.get("1").equals("one"));
        Assert.assertTrue(RequestContext.remove("1").equals("one"));
        RequestContext.clear();
        Assert.assertNotNull(r);
        Assert.assertTrue(r.hashCode() != 0);
    }


    



}
