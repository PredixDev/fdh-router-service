package com.ge.predix.solsvc.fdh.handler.asset.common;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Reading
 * <p>
 * name and uri pair for type ahead (auto-complete) function for UI front-end
 * 
 */
public class Reading
{

    /**
     * time stamp of this reading
     * (Required)
     * 
     */
    @JsonProperty(value = "time")
    private String time;

    /**
     * value of this time series reading
     * (Required)
     * 
     */
    @JsonProperty("value")
    private String value;

    /**
     * time stamp of this reading
     * (Required)
     * @return -
     * 
     */
    @JsonIgnore
    public Date getTime()
    {
        Date date = DateUtil.getDateFromISOString(this.time);
        return date;
    }

    /**
     * @return -
     */
    @JsonIgnore
    public String getTimeUTC()
    {
        return this.time;
    }

    /**
     * time stamp of this reading
     * (Required)
     * @param time -
     * 
     */
    @JsonIgnore
    public void setTime(Date time)
    {
        this.time = DateUtil.getISOStringFromDate(time);
    }

    /**
     * @param utcTime -
     */
    @JsonIgnore
    public void setTimeUTC(String utcTime)
    {
        this.time = utcTime;
    }

    /**
     * value of this time series reading
     * (Required)
     * @return -
     * 
     */
    @JsonProperty("value")
    public String getValue()
    {
        return this.value;
    }

    /**
     * value of this time series reading
     * (Required)
     * @param value -
     * 
     */
    @JsonProperty("value")
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other)
    {
        return EqualsBuilder.reflectionEquals(this, other);
    }

}
