package freemarker;

import com.alibaba.fastjson.JSON;
import freemarker.request.PltCarDto;
import freemarker.request.PltHeadDto;
import freemarker.request.PltQueryDto;
import freemarker.response.InsureQueryPacket;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerDemo {
    
    public static void main(String[] args) {
        FreemarkerDemo.test1();
    }

    public static void test1() {
        PltQueryDto pltQueryDto = new PltQueryDto();
        PltHeadDto pltHeadDto = new PltHeadDto();
        pltHeadDto.setUser("user");
        pltHeadDto.setPassword("password");
        pltHeadDto.setRequestType("01");
        pltQueryDto.setPltHeadDto(pltHeadDto);
        PltCarDto pltCarDto = new PltCarDto();
        pltCarDto.setCarKindCode("A0");
        pltCarDto.setCarUserNatureCode("85");
        pltCarDto.setEngineNo("5623154");
        pltCarDto.setEnrollDate("2021-01-20");
        pltCarDto.setFrameNo("LDSFADSHGF1245687");
        pltCarDto.setLicenseType("02");
        pltCarDto.setLicensePlateNo("冀A12145");
        pltCarDto.setLicenseColorCode("02");
        pltQueryDto.setPltCarDto(pltCarDto);
        Map<String, Object> velParamMap = transBeanToMap(pltQueryDto);
        // 转Xml
        String requestXml = convertToXml(velParamMap, "/01.ftl");
        System.out.println(requestXml);
        // 转JavaBean
        InsureQueryPacket insureQueryPacket = JaxbUtils.converyToJavaBean(requestXml.trim(), InsureQueryPacket.class);
        System.err.println(JSON.toJSONString(insureQueryPacket));
    }

    public static String convertToXml(Map<String, Object> velParamMap, String templateName) {
        StringWriter out = new StringWriter();
        try {
            // 创建一个Configuration实例
            Configuration configuration = new Configuration();
            // 设置FreeMarker的模版文件夹位置
            configuration.setClassForTemplateLoading(FreemarkerDemo.class, "/templates");
            String charsetName = "UTF-8";
            configuration.setDefaultEncoding(charsetName);
            Template template = configuration.getTemplate(templateName);
            // 字符集编码
            template.setEncoding(charsetName);
            // 显示生成的数据,//将合并后的数据打印到控制台
            template.process(velParamMap, out);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
            }
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Bean转Map 1: 利用Introspector和PropertyDescriptor 将Bean转为Map
     * 规则：如果首字母为大写，将key的首字母转为小写。
     *
     * @param obj Bean
     * @return map
     */
    public static Map<String, Object> transBeanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (null != key && !"callback".equalsIgnoreCase(key)) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    // 获取value对象
                    Object value = getter.invoke(obj);
                    if (null != key && !("".equals(key))) {
                        // 首字母转小写
                        if (!Character.isLowerCase(key.charAt(0))) {
                            key = (new StringBuilder()).append(Character.toLowerCase(key.charAt(0)))
                                    .append(key.substring(1)).toString();
                        }
                        map.put(key, value);
                    }
                }
            }

        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
        return map;
    }
}
