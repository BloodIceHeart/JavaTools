import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

public class EMail {

    public static void main(String[] args) {
        new EMail().sendEMail();
    }
    public void sendEMail() {
        // 出参对象
        try {
            /** 1.发送邮件 */
            /** 465接口调用 SSL  */
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.qq.com");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtp.auth", "true");//开启认证
            //props.setProperty("mail.debug", "true");//启用调试
            props.setProperty("mail.smtp.timeout", "3000");//设置链接超时
            MailSSLSocketFactory sf = null;
            try {
                sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
            } catch (GeneralSecurityException e1) {
                e1.printStackTrace();
            }
            //props.put("mail.smtp.ssl.enable", "true");
            //props.put("mail.smtp.ssl.socketFactory", sf);
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            Session session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("393826310@qq.com", "oxzurrekwmqtcaji");
                }
            });
            session.setDebug(false);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("393826310@qq.com"));
            //msg.setFrom(new InternetAddress(emailVo.getFrom()));

            String[] a = {"fantasy0408@163.com"};
            Address to[] = new InternetAddress[a.length];
            for (int i = 0; i < a.length; i++) {
                to[i] = new InternetAddress(a[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, to);
            msg.setSubject("测试主题");

            //设置邮件内容
            if (false) {
                /**复杂邮件内容 带EXCEL 附件*/
                //一个Multipart对象包含一个或多个bodypart对象，组成邮件正文
                //1.文本文件节点
                MimeMultipart multipart = new MimeMultipart();
                MimeBodyPart text = new MimeBodyPart();
                text.setContent("123456", "text/html;charset=utf-8 ");
                //2.EXCEL附件节点
                MimeBodyPart excel = new MimeBodyPart();
                //excel
                DataHandler dh = new DataHandler(new ByteArrayDataSource("", "application/msexcel;charset= UTF-8"));
                excel.setDataHandler(dh);
                excel.setFileName(MimeUtility.encodeWord(null));
                //将文本和附件添加到multipart
                multipart.addBodyPart(text);
                multipart.addBodyPart(excel);
                multipart.setSubType("mixed");//混合关系
                msg.setContent(multipart);

            } else {
                /**简单邮件内容*/
                msg.setContent("Hello world!", "text/html;charset=utf-8 ");
                //设置发送的日期
                msg.setSentDate(new Date());
                //设置邮件附件
            }
            //调用Transport的send方法去发送邮件
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
