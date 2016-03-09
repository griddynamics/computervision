package domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * A binding for one of Stella's input parameters.
 *
 * @author ksmirnov@griddynamics.com
 */
@XmlRootElement(name = "attributeDefs")
@XmlAccessorType(XmlAccessType.FIELD)
public class StarsAttributeDef implements Serializable{

    private Long id;
    private Tenants tenant;
    private String name;
    private DataType dataType;
    private Boolean requiredFlag;
    private Boolean setFlag;
    private Long setParentId;
    private Boolean displayInGuiFlag;
    private Boolean searchableFlag;
    private Boolean repeatingFlag;
    private Boolean bulkUpdateFlag;

    public Long getId() {
        return id;
    }

    public Tenants getTenant() {
        return tenant;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Boolean getRequiredFlag() {
        return requiredFlag;
    }

    public Boolean getSetFlag() {
        return setFlag;
    }

    public Long getSetParentId() {
        return setParentId;
    }

    public Boolean getDisplayInGuiFlag() {
        return displayInGuiFlag;
    }

    public Boolean getSearchableFlag() {
        return searchableFlag;
    }

    public Boolean getRepeatingFlag() {
        return repeatingFlag;
    }

    public Boolean getBulkUpdateFlag() {
        return bulkUpdateFlag;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTenant(Tenants tenant) {
        this.tenant = tenant;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setRequiredFlag(Boolean requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public void setSetFlag(Boolean setFlag) {
        this.setFlag = setFlag;
    }

    public void setSetParentId(Long setParentId) {
        this.setParentId = setParentId;
    }

    public void setDisplayInGuiFlag(Boolean displayInGuiFlag) {
        this.displayInGuiFlag = displayInGuiFlag;
    }

    public void setSearchableFlag(Boolean searchableFlag) {
        this.searchableFlag = searchableFlag;
    }

    public void setRepeatingFlag(Boolean repeatingFlag) {
        this.repeatingFlag = repeatingFlag;
    }

    public void setBulkUpdateFlag(Boolean bulkUpdateFlag) {
        this.bulkUpdateFlag = bulkUpdateFlag;
    }
}
