
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for anonymous complex type.
 * 
 * The following schema fragment specifies the expected content contained within this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder =
{
        "ref"
})
@XmlRootElement(name = "getUomDataExchangeRequest")
public class GetUomDataExchangeRequest
{

    private String ref;

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *         possible object is
     *         {@link String }
     * 
     */
    public String getRef()
    {
        return this.ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *            allowed object is
     *            {@link String }
     * 
     */
    public void setRef(String value)
    {
        this.ref = value;
    }

}
