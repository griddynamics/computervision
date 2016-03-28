package com.griddynamics.pojo.starsDomain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author  npakhomova on 1/7/16.
 */
@XmlRootElement
public class Result {
    private String numTotal;

    private String status;

    private Data data;

    public String getNumTotal ()
    {
        return numTotal;
    }

    public void setNumTotal (String numTotal)
    {
        this.numTotal = numTotal;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }


}
