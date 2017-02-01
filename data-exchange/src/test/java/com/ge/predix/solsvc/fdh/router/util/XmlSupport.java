/*
 * Copyright (c) 2013 General Electric Company. All rights reserved.
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.router.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.predix.solsvc.fdh.handler.asset.helper.FileUtils;

/**
 * Handle adding quality to an MCR
 * 
 * @author 502188153 dtrummell
 */
@SuppressWarnings(
{
    "nls",
})
public class XmlSupport
{
    private static final Logger                   log      = LoggerFactory.getLogger(XmlSupport.class);
    private static HashMap<Class<?>, JAXBContext> contexts = new HashMap<Class<?>, JAXBContext>();

    /**
     * Prevent construction
     */
    private XmlSupport()
    {
    }

    /**
     * @param filename -
     * @return -
     */
    public static final String readFileContent(final String filename)
    {
        if ( filename == null || filename.isEmpty() ) throw new IllegalArgumentException("filename null or empty");

        File f = null;
        String absolutePath = null;
        try
        {
            f = new File(filename);
            absolutePath = f.getAbsolutePath();
        }
        catch (Throwable th)
        {
            throw new IllegalArgumentException("unexpected error reading file " + filename + "; error is "
                    + th.getMessage(), th);
        }

        String msg = null;
        if ( !f.exists() )
            msg = "does not exist";
        else if ( !f.isFile() )
            msg = "not a file";
        else if ( !f.canRead() ) msg = "unreadable";
        if ( msg != null )
        {
            throw new IllegalArgumentException(absolutePath + " is not accessable; " + msg);
        }

        String textContent = null;
        try
        {
            textContent = FileUtils.readFile(absolutePath);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error reading input test data file '" + absolutePath + "', error is: "
                    + e.getMessage(), e);
        }

        if ( textContent == null ) throw new UnsupportedOperationException("testContent null for file " + filename);
        if ( textContent.isEmpty() ) throw new UnsupportedOperationException("testContent empty for file " + filename);

        return textContent;
    }

    /**
     * @param outFileName -
     * @param textData -
     */
    public static final void writeFileContent(final String outFileName, final String textData)
    {
        if ( textData == null || textData.isEmpty() ) throw new IllegalArgumentException("mcrString null or empty");

        if ( outFileName == null || outFileName.isEmpty() )
            throw new IllegalArgumentException("outFileName null or empty");

        String fileName = outFileName;
        if ( outFileName.contains(" ") ) fileName = outFileName.replace(" ", "");
        String path = "./";
        if ( fileName.contains("/") )
        {
            path = fileName.substring(0, fileName.lastIndexOf("/"));
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        File f = new File(path);
        f.mkdirs();

        fileName = outFileName;
        if ( outFileName.contains(" ") ) fileName = outFileName.replace(" ", "");
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try
        {
            fos = new FileOutputStream(fileName);
            pw = new PrintWriter(fos);
            pw.print(textData);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("could not create " + fileName, e);
        }
        finally
        {
            if ( pw != null )
            {
                pw.flush();
                pw.close();
            }

            if ( fos != null ) try
            {
                fos.close();
            }
            catch (IOException ignore)
            {
                // Ignore
            }
        }
    }

    /**
     * @param stringToConvert -
     * @param clazz -
     * @return -
     */
    public static <T> T unmarshal(String stringToConvert, Class<T> clazz)
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            return unmarshal(stringToConvert, jaxbContext);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param stringToConvert -
     * @param jaxbContext -
     * @return -
     */
    public static <T> T unmarshal(String stringToConvert, JAXBContext jaxbContext)
    {
        InputStream is = null;
        try
        {
            is = new ByteArrayInputStream(stringToConvert.toString().getBytes("UTF-8"));
            StreamSource ss = new StreamSource(is);
            Unmarshaller marshaller = jaxbContext.createUnmarshaller();
            @SuppressWarnings("unchecked")
            T theClass = (T) marshaller.unmarshal(ss);
            log.debug("Success...");
            return theClass;
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unable to generate input stream from xml", e);
            throw new RuntimeException("Unable to generate input stream from xml", e);
        }
        catch (JAXBException e)
        {
            log.error("", e);
            throw new RuntimeException("JaxbException", e);
        }
    }

    /**
     * @param object -
     * @return -
     */
    public static <T> String convertToString(T object)
    {
        return marshal(object, false);
    }

    /**
     * @param object -
     * @param prettyPrint -
     * @return -
     */
    public static <T> String convertToString(T object, boolean prettyPrint)
    {
        return marshal(object, prettyPrint);
    }

    /**
     * @param object -
     * @return -
     */
    public static <T> String marshal(T object)
    {
        return marshal(object, false);
    }

    /**
     * @param object -
     * @return -
     */
    public static <T> String marshalNoRootElement(T object)
    {
        return marshalNoRootElement(object, false);
    }

    /**
     * @param object
     *            to marshal
     * @param prettyPrint
     *            - if true puts in carriage returns and tabs
     * @return String of the object marshal
     */
    public static <T> String marshal(T object, boolean prettyPrint)
    {
        try
        {
            JAXBContext jaxbContext = getContext(object);
            return marshal(object, jaxbContext, prettyPrint);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Unable to convert", e);
        }
    }

    /**
     * @param object
     *            to marshal
     * @param prettyPrint
     *            - if true puts in carriage returns and tabs
     * @return String of the object marshal
     */
    public static <T> String marshalNoRootElement(T object, boolean prettyPrint)
    {
        try
        {
            JAXBContext jaxbContext = getContext(object);
            return marshalNoRootElement(object, jaxbContext, prettyPrint);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Unable to convert", e);
        }
    }

    private static <T> JAXBContext getContext(T object)
            throws JAXBException
    {
        JAXBContext jaxbContext;
        if ( contexts.get(object.getClass()) != null )
            jaxbContext = contexts.get(object.getClass());
        else
        {
            jaxbContext = JAXBContext.newInstance(object.getClass());
            contexts.put(object.getClass(), jaxbContext);
        }
        return jaxbContext;
    }

    /**
     * @param object
     *            to marshal
     * @param jaxbContext -
     * @param prettyPrint
     *            - if true puts in carriage returns and tabs
     * @return String of the object marshal
     */
    public static <T> String marshal(T object, JAXBContext jaxbContext, boolean prettyPrint)
    {

        StringWriter writer;
        writer = new StringWriter();
        Marshaller marshaller;
        try
        {
            marshaller = jaxbContext.createMarshaller();
            if ( prettyPrint ) marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, writer);
            return writer.toString();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param object
     *            to marshal
     * @param jaxbContext -
     * @param prettyPrint
     *            - if true puts in carriage returns and tabs
     * @return String of the object marshal
     */
    public static <T> String marshalNoRootElement(T object, JAXBContext jaxbContext, boolean prettyPrint)
    {

        StringWriter writer;
        writer = new StringWriter();
        Marshaller marshaller;
        try
        {
            marshaller = jaxbContext.createMarshaller();
            if ( prettyPrint ) marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            QName qName = new QName(object.getClass().getPackage().getName(), object.getClass().getSimpleName());
            @SuppressWarnings("unchecked")
            JAXBElement<T> root = new JAXBElement<T>(qName, (Class<T>) object.getClass(), object);
            marshaller.marshal(root, writer);
            return writer.toString();
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Parser failed to marshal " + object, e);
        }
    }

    /**
     * e.g. "2012-09-11T10:10:10"
     * 
     * @param sourceDateTime -
     * @param dateTime -
     * @return -
     * @throws ParseException -
     */
    public static XMLGregorianCalendar getXMLDate(String sourceDateTime)
            throws ParseException
    {
        DatatypeFactory f = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dateTime = df.parse(sourceDateTime);
        try
        {
            f = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e)
        {
            throw new RuntimeException("convert to runtime exception", e);
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(dateTime);
        XMLGregorianCalendar xmLGregorianCalendar = f.newXMLGregorianCalendar(cal);
        return xmLGregorianCalendar;
    }
}
