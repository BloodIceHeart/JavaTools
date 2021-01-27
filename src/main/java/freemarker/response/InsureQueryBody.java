package freemarker.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Body")
public class InsureQueryBody {
    /** 基本信息 */
    @XmlElement(name = "BasePart")
    private BasePart basePart;
    /** 基本信息 */
    @XmlElement(name = "Vehicle")
    private Vehicle vehicle;

    public BasePart getBasePart() {
        return basePart;
    }

    public void setBasePart(BasePart basePart) {
        this.basePart = basePart;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
