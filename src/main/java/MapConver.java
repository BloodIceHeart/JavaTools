import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * map对象转换
 */
public class MapConver {

    public static void main(String[] args) {
        List<String> warnList = new ArrayList<>();
        warnList.add("aaaa");
        String ddd = "aa" + "aa";
        System.out.println(warnList.contains(ddd));
    }

    /**
     * map 对象转换
     *
     * @param source
     * @param dest
     * @param parentFieldName
     * @param codeConvers
     * @return
     */
    public Map<String, Object> convert(Map<String, Object> source, Map<String, Object> dest, Map<CodeConverDto, Object> codeConvers) {
        //根据传入的codeConvert从source里取值
        //首先判断类型，如果是List Object 则递归调用自己
        codeConvers.forEach((k, v) -> {
            if ("List".equals(k.getFieldType())) {
                List<Map<String, Object>> dest_s = new ArrayList<Map<String, Object>>();
                Map<String, Object> dest_ = new HashMap<String, Object>();
                dest.put(k.getFieldName(), dest_s);
                int length = getListLength(source, k);
                Map<CodeConverDto, Object> codeConver = (Map<CodeConverDto, Object>) v;
                for (int i = 0; i < length; i++) {
                    dest_ = new HashMap<String, Object>();
                    dest_s.add(dest_);
                    int finalI = i;
                    codeConver.forEach((v1, k1) -> {
                        v1.setIndex(finalI);
                    });
                    convert(source, dest_, codeConver);
                }
            } else if ("Object".equals(k.getFieldType())) {
                Map<String, Object> dest_ = new HashMap<String, Object>();
                dest.put(k.getFieldName(), dest_);
                convert(source, dest_, (Map<CodeConverDto, Object>) v);
            } else if ("Field".equals(k.getFieldType())) {
                //如果是取值的 直接取值赋值
                dest.put(k.getFieldName(), getValueByPath(source, (CodeConverDto) v));
            }
        });
        return dest;
    }

    /**
     * 查询map对象转换规则 存入缓存中
     *
     * @param objectName
     * @return
     */
    public Map<CodeConverDto, Object> getObjectConvers(String objectName) {
        Map<CodeConverDto, Object> codeConvers = new HashMap<>();
        //查询数据
        List<CodeConverDto> converDtoList = new ArrayList<>();
        //属性字段为List或者为Object则递归调用
        converDtoList.forEach(codeConver -> {
            if (!"Field".equals("")) {
                codeConvers.put(codeConver, getObjectConvers(codeConver.getObjectName()));
            } else {
                codeConvers.put(codeConver, codeConver);
            }
        });
        return codeConvers;
    }

    /**
     * 根据配置规则判断List长度
     *
     * @param source
     * @param codeConver
     * @return
     */
    private int getListLength(Map<String, Object> source, CodeConverDto codeConver) {
        return 0;
    }

    /**
     * 根据配置路径取值
     *
     * @param source
     * @param codeConver
     * @return
     */
    private Object getValueByPath(Map<String, Object> source, CodeConverDto codeConver) {
        return null;
    }

    /**
     * 对象转换映射规则表dto
     */
    class CodeConverDto {
        /**
         * ID
         * 系统代码 GPIC、DBIC、PICC
         */
        private String systemCode;
        /**
         * ID
         * 对象类型Policy、Endorse
         */
        private String type;
        /**
         * ID
         * 对象名称
         */
        private String objectName;
        /**
         * 属性名称
         */
        private String fieldName;
        /**
         * 属性类型 List Object Filed
         */
        private String fieldType;
        /**
         * 属性格式化（日期类型）
         */
        private String fieldFormat;
        /**
         * 取值路径
         */
        private String valuePath;
        /**
         * 取值属性类型
         */
        private String valueType;
        /**
         * 取值规则
         */
        private String valueRule;
        /**
         * List集合对象取值下标
         */
        private int index;


        public String getSystemCode() {
            return systemCode;
        }

        public void setSystemCode(String systemCode) {
            this.systemCode = systemCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getObjectName() {
            return objectName;
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getFieldFormat() {
            return fieldFormat;
        }

        public void setFieldFormat(String fieldFormat) {
            this.fieldFormat = fieldFormat;
        }

        public String getValuePath() {
            return valuePath;
        }

        public void setValuePath(String valuePath) {
            this.valuePath = valuePath;
        }

        public String getValueType() {
            return valueType;
        }

        public void setValueType(String valueType) {
            this.valueType = valueType;
        }

        public String getValueRule() {
            return valueRule;
        }

        public void setValueRule(String valueRule) {
            this.valueRule = valueRule;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
