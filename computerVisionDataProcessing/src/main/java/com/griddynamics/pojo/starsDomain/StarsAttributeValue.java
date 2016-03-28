package com.griddynamics.pojo.starsDomain;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A binding for one of Stella's input parameters.
 *
 * @author ksmirnov@griddynamics.com
 */
@XmlRootElement(name = "attributeValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class StarsAttributeValue implements Serializable{

    private Long id;
    private Tenants tenant;
    private String displayValue;
    private String varcharValue;
    private String destAttributeName;
    private Long destAttributeId;
    private Long destAttributeVlItemId;
    private Long sequenceNum;
    private Long eapAttrValId;

    public Long getId() {
        return id;
    }

    public Tenants getTenant() {
        return tenant;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getVarcharValue() {
        return varcharValue;
    }

    public String getDestAttributeName() {
        return destAttributeName;
    }

    public Long getDestAttributeId() {
        return destAttributeId;
    }

    public Long getDestAttributeVlItemId() {
        return destAttributeVlItemId;
    }

    public Long getSequenceNum() {
        return sequenceNum;
    }

    public Long getEapAttrValId() {
        return eapAttrValId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTenant(Tenants tenant) {
        this.tenant = tenant;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public void setVarcharValue(String varcharValue) {
        this.varcharValue = varcharValue;
    }

    public void setDestAttributeName(String destAttributeName) {
        this.destAttributeName = destAttributeName;
    }

    public void setDestAttributeId(Long destAttributeId) {
        this.destAttributeId = destAttributeId;
    }

    public void setDestAttributeVlItemId(Long destAttributeVlItemId) {
        this.destAttributeVlItemId = destAttributeVlItemId;
    }

    public void setSequenceNum(Long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public void setEapAttrValId(Long eapAttrValId) {
        this.eapAttrValId = eapAttrValId;
    }
}
