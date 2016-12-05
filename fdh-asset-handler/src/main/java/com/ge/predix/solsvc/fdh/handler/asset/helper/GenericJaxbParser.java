package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;

/**
 * @author PMP Development Team
 * @param <T> Type
 */
public abstract class GenericJaxbParser<T>
{

    private JAXBContext jaxbContext;

    /**
     * @param clazz of Type T
     */
    @SuppressWarnings("nls")
    public GenericJaxbParser(Class<T> clazz)
    {
        try
        {

            this.jaxbContext = JAXBContext.newInstance(clazz);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Unable to initialize JAXBContext", e);
        }
    }

    /**
     * default constructor
     */
    protected GenericJaxbParser()
    {
    }

    /**
     * @param object to marshal
     * @return String of the object marshal
     */
    public String marshal(T object)
    {
       return marshal(object, false);
    }

    /**
     * @param object to marshal
     * @param prettyPrint - if true puts in carriage returns and tabs
     * @return String of the object marshal
     */
    @SuppressWarnings("nls")
    public String marshal(T object, boolean prettyPrint)
    {

        StringWriter writer;
        try
        {
            writer = new StringWriter();
            Marshaller marshaller = this.jaxbContext.createMarshaller();
            if ( prettyPrint )
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, writer);

        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Parser failed to marshal: ", e);
        }
        return writer.toString();
    }
    
    /**
     * @param object object to marshal
     * @param writer write to put the marshal content
     */
    @SuppressWarnings("nls")
    public void marshal(Object object, StreamResult writer)
    {
        try
        {
            Marshaller marshaller = this.jaxbContext.createMarshaller();
            marshaller.marshal(object, writer);

        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Parser failed to marshal " + "moduleConfigRoot ", e);
        }
    }

    /**
     * @param xmlString string to unmarshal
     * @return Object of Type T
     * @throws RuntimeException when error happens in unmarshalling the string
     */
    @SuppressWarnings("nls")
    public T unmarshal(String xmlString)
            throws RuntimeException
    {

        T object = null;
        try
        {
            Unmarshaller unmarshaller = this.jaxbContext.createUnmarshaller();
            object = (T) unmarshaller.unmarshal(new StringReader(xmlString));
        }
        catch (JAXBException e)
        {
            throw new RuntimeException("Parser failed to unmarshal object", e);
        }
        return object;
    }

    /**
     * @return JAXBContext
     */
    protected JAXBContext getJaxbContext()
    {
        return this.jaxbContext;
    }

    /**
     * sets JAXBContext
     * 
     * @param jaxbContext JAXBContext
     */
    protected void setJaxbContext(JAXBContext jaxbContext)
    {
        this.jaxbContext = jaxbContext;
    }
}
