
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.ge.predix.entity.vo.unitcode.UnitCodesVO;


/**
 * Java class for anonymous complex type.
 * 
 * The following schema fragment specifies the expected content contained within this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "unitCodeVOMap",
    "dateTime",
    "capacity"
})
@XmlRootElement(name = "getUomDataExchangeResult")
public class GetUomDataExchangeResult {

    @XmlElement(namespace = "http://ge.com/predix/entity/vo/unitcode")
    private UnitCodesVO unitCodeVOMap;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar dateTime;
    private int capacity;

    /**
     * 
     *                             This is the unitCode object populated by the
     *                             orchestration and passed onto RDR to be stored in the cache.
     *                         
     * 
     * @return
     *     possible object is
     *     {@link UnitCodesVO }
     *     
     */
    public UnitCodesVO getUnitCodeVOMap() {
        return this.unitCodeVOMap;
    }

    /**
     * Sets the value of the unitCodeVOMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitCodesVO }
     *     
     */
    public void setUnitCodeVOMap(UnitCodesVO value) {
        this.unitCodeVOMap = value;
    }

    /**
     * Gets the value of the dateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTime() {
        return this.dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }

    /**
     * Gets the value of the capacity property.
     * @return 
     * 
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Sets the value of the capacity property.
     * @param value -
     * 
     */
    public void setCapacity(int value) {
        this.capacity = value;
    }

}
