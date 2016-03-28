package com.griddynamics.pojo.starsDomain;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author ksmirnov@griddynamics.com
 */
@XmlRootElement(name = "attributeDefs")
@XmlAccessorType(XmlAccessType.FIELD)
public class StarsAttributeDefs {

    @XmlElement(name="attributeDef")
    private List<StarsAttributeDef> attributeDefs;

    public List<StarsAttributeDef> getAttributeDefs() {
        return attributeDefs;
    }

    public void setAttributeDefs(List<StarsAttributeDef> attributeDefs) {
        this.attributeDefs = attributeDefs;
    }
}
