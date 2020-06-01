package mongo;

import com.mongodb.DBObject;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: TODO
 * @author: Administrator
 * @date: 2017年11月16日 下午5:23:11
 */
public class BeanUtil {
    /**
     * @Description:私有化构造器
     */
    private BeanUtil() {
    }

    /**
     *
     */
    private static volatile BeanUtil singleton = null;

    /**
     * @return BeanUtil
     * @Description: TODO
     */
    public static BeanUtil getInstance() {
        if (singleton == null) {
            synchronized (BeanUtil.class) {
                if (singleton == null) {
                    singleton = new BeanUtil();
                }
            }
            singleton = new BeanUtil();
        }
        return singleton;
    }

    /**
     * @param obj Object
     * @return Document
     * @throws Exception Exception
     * @Description: (把实体bean对象转换成BsonDocument).
     */
    public static Document bean2Document(Object obj) throws Exception {
        Document bson = new Document();
        Method metd = null;
        String fdname = null;
        Class<?> clazz = obj.getClass();
        Field[] fds = clazz.getDeclaredFields();
        for (Field field : fds) {
            fdname = field.getName();
            if ("serialVersionUID".equals(fdname)) {
                continue;
            }
            metd = clazz.getMethod("get" + change(fdname));
            Object value = metd.invoke(obj);
            // 存储属性和对应值
            bson.append(fdname, value);
        }
        return bson;
    }

    /**
     * @param map Map<String, Object>
     * @return Document
     * @Description:map2BsonDocument:(把map对象转换成BsonDocument).
     */
    public static Document map2Document(Map<String, Object> map) {
        Document bson = new Document();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            bson.append(key, map.get(key));
        }
        return bson;
    }

    /**
     * @param objs List<?>
     * @return List<Document>
     * @throws Exception Exception
     * @Description:beans2DBObjs:(把实体bean对象转换成Document)
     */
    public static List<Document> beans2Documents(List<?> objs) throws Exception {
        List<Document> list = new ArrayList<Document>();
        for (Object obj : objs) {
            list.add(bean2Document(obj));
        }
        return list;
    }

    /**
     * @param list List<Map<String, Object>>
     * @return List<Document>
     * @Description: (把Map对象转换成DBObject).
     */
    public static List<Document> map2Documents(List<Map<String, Object>> list) {
        List<Document> docs = new ArrayList<Document>();
        for (Map<String, Object> obj : list) {
            docs.add(map2Document(obj));
        }
        return docs;
    }

    /**
     * @param doc doc
     * @param bean bean
     * @param <T> <T>
     * @return <T> T
     * @throws Exception Exception
     * @Description:beans2Map:(把实体bean对象转换成Document)
     */
    public static <T> T doc2Bean(Document doc, T bean) throws Exception {
        if (bean == null) {
            return null;
        }
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                String varName = field.getName();
                Object object = doc.get(varName);
                if (object != null) {
                    BeanUtils.setProperty(bean, varName, object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * @param docs docs
     * @return List<Map<String, Object>>
     * @throws Exception Exception
     * @Description:beans2Map:(把实体bean对象转换成Document)
     */
    public static List<Map<String, Object>> documents2Maps(List<Document> docs) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Document doc : docs) {
            Map<String, Object> map = new HashMap<String, Object>();
            doc.putAll(map);
            list.add(map);
        }
        return list;
    }

    /**
     * @param dbObject DBObject
     * @param bean     T
     * @param <T>      T
     * @return <T> T aa
     * @Description:把DBObject转换成bean对象
     */
    public static <T> T dbObj2Bean(DBObject dbObject, T bean) {
        if (bean == null) {
            return null;
        }
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                String varName = field.getName();
                Object object = dbObject.get(varName);
                if (object != null) {
                    BeanUtils.setProperty(bean, varName, object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 将字符串第一个字符大写并返还
     *
     * @param src 源字符串
     * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
     * @author seamus
     */
    public static String change(String src) {
        if (src != null) {
            StringBuffer sb = new StringBuffer(src);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        } else {
            return null;
        }
    }
}
