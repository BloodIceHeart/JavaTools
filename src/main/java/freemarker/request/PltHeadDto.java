package freemarker.request;

import java.io.Serializable;

/** 平台报文头信息表 */
public class PltHeadDto implements Serializable {
    /**
     * 序列
     */
    private static final long serialVersionUID = -7939876145242501022L;
    /** 用户名 */
    private String user;
    /** 密码 */
    private String password;
    /** 请求类型 */
    private String requestType;
    /** 机构名称代码 */
    private String reqInstitutionCode;
    /** 地区名称 */
    private String reqAreaCode;
    /** 平台地址 */
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReqInstitutionCode() {
        return reqInstitutionCode;
    }

    public void setReqInstitutionCode(String reqInstitutionCode) {
        this.reqInstitutionCode = reqInstitutionCode;
    }

    public String getReqAreaCode() {
        return reqAreaCode;
    }

    public void setReqAreaCode(String reqAreaCode) {
        this.reqAreaCode = reqAreaCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
