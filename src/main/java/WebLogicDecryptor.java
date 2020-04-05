import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import weblogic.security.internal.SerializedSystemIni;
import weblogic.security.internal.encryption.ClearOrEncryptedService;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

/**	
 * @description webLogic11g解析密码
 * 1、domain路径：E:\\oracle\\Middleware1036\\user_projects\\domains\\7001domain
 * 2、security\\SerializedSystemIni.dat
 * 3、config\\jdbc\\e59bbde5afbfe58a9fe883bde5ba93-5517-jdbc.xml
 * 4、requires weblogic.jar in the class path
 * @author wangpeng
 * @since 2019/7/16 11:55
 */
public class WebLogicDecryptor {
    //private static final String PREFIX = "{3DES}";
    private static final String PREFIX = "{AES}";
    private static final String XPATH_EXPRESSION = "//node()[starts-with(text(), '"	+ PREFIX + "')] | //@*[starts-with(., '" + PREFIX + "')]";
    private static ClearOrEncryptedService ces;

    public static void main(String[] args) throws Exception {
        args = new String[2];
        args[0] = "E:\\oracle\\Middleware1036\\user_projects\\domains\\GPIC-POC";
        args[1] = args[0] + "\\config\\jdbc\\JDBC_Data_Source-1-3408-jdbc.xml";
        if (args.length < 2) {
            throw new Exception("Usage: [domainDir] [configFile]");
        }

        ces = new ClearOrEncryptedService(SerializedSystemIni.getEncryptionService(new File(args[0]).getAbsolutePath()));
        File file = new File(args[1]);
        if (file.getName().endsWith(".xml")) {
            processXml(file);
        }

        else if (file.getName().endsWith(".properties")) {
            processProperties(file);
        }

    }

    private static void processXml(File file) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        XPathExpression expr = XPathFactory.newInstance().newXPath().compile(XPATH_EXPRESSION);
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            print(node.getNodeName(), node.getTextContent());
        }

    }

    private static void processProperties(File file) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        for (Map.Entry p : properties.entrySet()) {
            if (p.getValue().toString().startsWith(PREFIX)) {
                print(p.getKey(), p.getValue());
            }
        }
    }

    private static void print(Object attributeName, Object encrypted) {
        System.out.println("Node name: " + attributeName);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + ces.decrypt((String) encrypted) + "\n");
    }
}