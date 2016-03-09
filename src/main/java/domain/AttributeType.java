package domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author  npakhomova on 1/7/16.
 */
@XmlRootElement(name = "attributeType")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeType {
    private Long id;
    private Long contentTypeId;
    private String refTag;
    private String attributeDataTableName;
    private String attributeDataColumnName;
    private String systemAvailability;
    private String tenantAvailability;

    @XmlElement(name = "attributeDefs")
    private StarsAttributeDefs attributeDefs;

    @XmlElement(name="attributeValues")
    private StarsAttributeValues attributeValues;

    public StarsAttributeDefs getAttributeDefs() {
        return attributeDefs;
    }

    public void setAttributeDefs(StarsAttributeDefs attributeDefs) {
        this.attributeDefs = attributeDefs;
    }

    public void setAttributeDataTableName(String attributeDataTableName) {
        this.attributeDataTableName = attributeDataTableName;
    }

    public void setAttributeDataColumnName(String attributeDataColumnName) {
        this.attributeDataColumnName = attributeDataColumnName;
    }

    public String getSystemAvailability() {
        return systemAvailability;
    }

    public void setSystemAvailability(String systemAvailability) {
        this.systemAvailability = systemAvailability;
    }

    public String getTenantAvailability() {
        return tenantAvailability;
    }

    public void setTenantAvailability(String tenantAvailability) {
        this.tenantAvailability = tenantAvailability;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(Long contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public String getRefTag() {
        return refTag;
    }

    public void setRefTag(String refTag) {
        this.refTag = refTag;
    }

    public StarsAttributeValues getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(StarsAttributeValues attributeValues) {
        this.attributeValues = attributeValues;
    }

    public String getAttributeDataTableName() {
        return attributeDataTableName;
    }

    public String getAttributeDataColumnName() {
        return attributeDataColumnName;
    }
}
