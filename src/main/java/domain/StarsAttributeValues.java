package domain;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author  npakhomova on 1/7/16.
 */
@XmlRootElement(name = "attributeValues")
@XmlAccessorType(XmlAccessType.FIELD)
public class StarsAttributeValues {

    @XmlElement(name="attributeValue")
    private List<StarsAttributeValue> attributeValues;

    public List<StarsAttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(List<StarsAttributeValue> attributeValues) {
        this.attributeValues = attributeValues;
    }
}
