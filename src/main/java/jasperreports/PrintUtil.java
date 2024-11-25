package jasperreports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintUtil {


    public static void main(String[] args) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("title", "title");
        params.put("startTime", "startTime");
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> obj = new HashMap<>();
        obj.put("sn", "sn");
        obj.put("name", "sn");
        obj.put("price", "sn");
        obj.put("logtime", "sn");
        list.add(obj);
        
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(list);
        InputStream inputStream = new PrintUtil().getClass().getClassLoader().getResourceAsStream("templates/auctionLog.jasper");
        byte[] bytes = JasperRunManager.runReportToPdf(inputStream, params, jrDataSource);
        File file = new File("D:\\code\\1.pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
    }
}
