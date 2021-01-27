package freemarker.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Vehicle")
public class Vehicle {
    @XmlElement(name = "LicensePlateNo")
    private String licensePlateNo;
    @XmlElement(name = "LicensePlateColorCode")
    private String licenseColorCode;
    @XmlElement(name = "LicensePlateType")
    private String licenseType;
    @XmlElement(name = "MotorTypeCode")
    private String carKindCode;
    @XmlElement(name = "MotorUsageTypeCode")
    private String carUserNatureCode;
    @XmlElement(name = "FirstRegisterDate")
    private String enrollDate;
    @XmlElement(name = "VIN")
    private String frameNo;
    @XmlElement(name = "EngineNo")
    private String engineNo;

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getLicenseColorCode() {
        return licenseColorCode;
    }

    public void setLicenseColorCode(String licenseColorCode) {
        this.licenseColorCode = licenseColorCode;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getCarKindCode() {
        return carKindCode;
    }

    public void setCarKindCode(String carKindCode) {
        this.carKindCode = carKindCode;
    }

    public String getCarUserNatureCode() {
        return carUserNatureCode;
    }

    public void setCarUserNatureCode(String carUserNatureCode) {
        this.carUserNatureCode = carUserNatureCode;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }
}
