package freemarker;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @XmlType，将Java类或枚举类型映射到XML模式类型
 * @XmlAccessorType(XmlAccessType.FIELD) ，控制字段或属性的序列化。
 *                                       FIELD表示JAXB将自动绑定Java类中的每个非静态的（static）、非瞬态的（由@XmlTransient标
 *                                       注）字段到XML。
 *                                       其他值还有XmlAccessType.PROPERTY和XmlAccessType.NONE。
 * @XmlAccessorOrder，控制JAXB 绑定类中属性和字段的排序。 @XmlJavaTypeAdapter，使用定制的适配器（即扩展抽象类XmlAdapter并覆盖marshal()和unmarshal()方法），以序列化Java类为XML。
 *                          对于数组或集合（即包含多个元素的成员变量），生成一个包装该数组或集合的XML元素（称为包装器）。
 * @XmlRootElement，将Java类或枚举类型映射到XML元素。 @XmlElement，将Java类的一个属性映射到与属性同名的一个XML元素。 @XmlAttribute，将Java类的一个属性映射到与属性同名的一个XML属性
 */
public class JaxbUtils {

    /**
     * JavaBean转换成xml 默认编码UTF-8
     * 
     * @param obj 请求实体
     * @return xml
     */
    public static String convertToXml(Object obj) {
        return convertToXml(obj, "UTF-8");
    }

    /**
     * JavaBean转换成xml
     * 
     * @param obj 请求实体
     * @param encoding 字符格式
     * @return xml
     */
    public static String convertToXml(Object obj, String encoding) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * xml转换成JavaBean
     * 
     * @param xml 请求xml
     * @param c c
     * @param <T> T
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T converyToJavaBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}
