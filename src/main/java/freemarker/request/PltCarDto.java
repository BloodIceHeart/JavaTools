package freemarker.request;

import java.io.Serializable;

/**
 * 车辆标的信息表
 */
public class PltCarDto implements Serializable {
    private String licensePlateNo;
    private String licenseColorCode;
    private String licenseType;
    private String carKindCode;
    private String carUserNatureCode;
    private String enrollDate;
    private String frameNo;
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
