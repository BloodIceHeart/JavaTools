package freemarker.response;

import javax.xml.bind.annotation.*;

/**
 * 
 * xml报文解析对象
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Packet")
@XmlType(propOrder = { "head", "body", "uuid" })
public class InsureQueryPacket {
    /** 请求Head */
    @XmlElement(name = "Head")
    private InsureQueryHead head;
    /** 请求Body */
    @XmlElement(name = "Body")
    private InsureQueryBody body;
    /** uuid */
    private String uuid;

    public InsureQueryHead getHead() {
        return head;
    }

    public void setHead(InsureQueryHead head) {
        this.head = head;
    }

    public InsureQueryBody getBody() {
        return body;
    }

    public void setBody(InsureQueryBody body) {
        this.body = body;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "InsureQueryPacket [head=" + head + ", body=" + body + ", uuid=" + uuid + "]";
    }
}
