package mongo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DemoTest {
    public static void main(String[] args) {
        InteractionLogDto logDto = new InteractionLogDto();
        String uuid = DemoTest.getUUID();
        String operationType = "/url";
        logDto.setId(uuid);
        logDto.setOperationType(operationType);
        logDto.setVersions("0");
        logDto.setRequestXml("{dfsdfd}");
        logDto.setResponseXml("{sdfsdf}");
        logDto.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        DemoTest.saveInteractionLog(logDto);
    }
    /**
     * 生成UUID
     *
     * @return uuid
     * @Description: 生成UUID
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }

    public static void saveInteractionLog(InteractionLogDto logDto) {
        MongodbUtil mongo = MongodbUtil.getInstance();
        //接口日志报文过大，按年月分区存储
        Date date = java.util.Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String yearMonth = sdf.format(date);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MongodbUtil mongo = MongodbUtil.getInstance();
                        mongo.insert(mongo.getDatabase(), "InteractionLog", logDto);
                    } catch (Exception e) {
                        System.out.println("保存mongoDB失败！" + e.getMessage());
                    }
                }
            }).start();
        } catch (Exception e) {
            System.out.println("保存mongoDB失败！" + e.getMessage());
        }
    }
}
