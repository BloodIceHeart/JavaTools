import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * sftp工具类
 *
 * @author xxx
 * @version 1.0
 */
public class SFTPUtils {

    private static Logger log = Logger.getLogger(SFTPUtils.class.getName());

    /** */
    private String host;// 服务器连接ip
    /** */
    private String username;// 用户名
    /** */
    private String password;// 密码
    /** */
    private int port = 22;// 端口号
    /** */
    private ChannelSftp sftp = null;
    /** */
    private Session sshSession = null;

    public static void main(String[] args) {
        SFTPUtils sftp = null;
        try {
            sftp = new SFTPUtils("9.23.69.77", 10022, "uywyxsc", "pE#BPn*E6G");
            sftp.connect();
            String filePath = "D:\\code\\WorkOn\\jar\\aaa";
            File temp = new File(filePath);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            List<String> filenames = sftp.batchDownLoadFile("/ftp/ywyxsc/000/aaa/",
                    filePath, null, null, false);
            if (CollectionUtils.isEmpty(filenames)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sftp.exeRmRec("/ftp/ywyxsc/000");
            sftp.disconnect();
        }
    }
    
    /**
     * sftp构造函数
     *
     */
    public SFTPUtils() {
    }

    /**
     * 通过SFTP连接服务器
     *
     * @param host     host
     * @param port     port
     * @param username username
     * @param password password
     */
    public SFTPUtils(String host, int port, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    /**
     * 通过SFTP连接服务器
     *
     * @param host     host
     * @param username username
     * @param password password
     */
    public SFTPUtils(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * 通过SFTP连接服务器
     */
    public void connect() {
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            if (log.isInfoEnabled()) {
                log.info("Session created.");
            }
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            if (log.isInfoEnabled()) {
                log.info("Session connected.");
            }
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            if (log.isInfoEnabled()) {
                log.info("Opening Channel.");
            }
            sftp = (ChannelSftp) channel;
            if (log.isInfoEnabled()) {
                log.info("Connected to " + host + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
                if (log.isInfoEnabled()) {
                    log.info("sftp is closed already");
                }
            }
        }
        if (this.sshSession != null) {
            if (this.sshSession.isConnected()) {
                this.sshSession.disconnect();
                if (log.isInfoEnabled()) {
                    log.info("sshSession is closed already");
                }
            }
        }
    }

    /**
     * 批量下载文件
     *
     * @param remotePath    远程下载目录(以路径符号结束,可以为相对路径eg:/assess/sftp/jiesuan_2/2014/)
     * @param localPath     本地保存目录(以路径符号结束,D:\Duansha\sftp\)
     * @param fileFormat    下载文件格式(以特定字符开头,为空不做检验)
     * @param fileEndFormat 下载文件格式(文件格式)
     * @param del           下载后是否删除sftp文件
     * @return 列表
     */
    public List<String> batchDownLoadFile(String remotePath, String localPath, String fileFormat, String fileEndFormat,
                                          boolean del) {
        List<String> filenames = new ArrayList<String>();
        try {
            // connect();
            Vector v = listFiles(remotePath);
            // sftp.cd(remotePath);
            if (v.size() > 0) {
                Iterator it = v.iterator();
                while (it.hasNext()) {
                    LsEntry entry = (LsEntry) it.next();
                    String filename = entry.getFilename();
                    SftpATTRS attrs = entry.getAttrs();
                    if (!attrs.isDir()) {
                        boolean flag = false;
                        String localFileName = localPath + filename;
                        fileFormat = fileFormat == null ? "" : fileFormat.trim();
                        fileEndFormat = fileEndFormat == null ? "" : fileEndFormat.trim();
                        // 三种情况
                        if (fileFormat.length() > 0 && fileEndFormat.length() > 0) {
                            if (filename.startsWith(fileFormat) && filename.endsWith(fileEndFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (flag && del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else if (fileFormat.length() > 0 && "".equals(fileEndFormat)) {
                            if (filename.startsWith(fileFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (flag && del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else if (fileEndFormat.length() > 0 && "".equals(fileFormat)) {
                            if (filename.endsWith(fileEndFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (flag && del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else {
                            flag = downloadFile(remotePath, filename, localPath, filename);
                            if (flag) {
                                filenames.add(localFileName);
                                if (flag && del) {
                                    deleteSFTP(remotePath, filename);
                                }
                            }
                        }
                    }
                }
            }
            if (log.isInfoEnabled()) {
                log.info("download file is success:remotePath=" + remotePath + "and localPath=" + localPath
                        + ",file size is" + v.size());
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return filenames;
    }

    /**
     * 下载单个文件
     *
     * @param remotePath     远程下载目录(以路径符号结束)
     * @param remoteFileName 下载文件名
     * @param localPath      本地保存目录(以路径符号结束)
     * @param localFileName  保存文件名
     * @return true
     */
    public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileOutputStream fieloutput = null;
        try {
            // sftp.cd(remotePath);
            File file = new File(localPath + localFileName);
            // mkdirs(localPath + localFileName);
            fieloutput = new FileOutputStream(file);
            sftp.get(remotePath + remoteFileName, fieloutput);
            if (log.isInfoEnabled()) {
                log.info("===DownloadFile:" + remoteFileName + " success from sftp.");
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (null != fieloutput) {
                try {
                    fieloutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 上传单个文件
     *
     * @param remotePath     远程保存目录
     * @param remoteFileName 保存文件名
     * @param file           file
     * @return true
     */
    public boolean uploadFile(String remotePath, String remoteFileName, File file) {
        FileInputStream in = null;
        try {
            createDir(remotePath);
            in = new FileInputStream(file);
            sftp.put(in, remoteFileName);
            return true;
        } catch (FileNotFoundException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 上传单个文件
     *
     * @param remotePath     远程保存目录
     * @param remoteFileName 保存文件名
     * @param localPath      本地上传目录(以路径符号结束)
     * @param localFileName  上传的文件名
     * @return true
     */
    public boolean uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileInputStream in = null;
        try {
            createDir(remotePath);
            File file = new File(localPath + localFileName);
            in = new FileInputStream(file);
            sftp.put(in, remoteFileName);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 批量上传文件
     *
     * @param remotePath 远程保存目录
     * @param localPath  本地上传目录(以路径符号结束)
     * @param del        上传后是否删除本地文件
     * @return true
     */
    public boolean bacthUploadFile(String remotePath, String localPath, boolean del) {
        try {
            connect();
            File file = new File(localPath);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().indexOf("bak") == -1) {
                    if (this.uploadFile(remotePath, files[i].getName(), localPath, files[i].getName()) && del) {
                        deleteFile(localPath + files[i].getName());
                    }
                }
            }
            if (log.isInfoEnabled()) {
                log.info("upload file is success:remotePath=" + remotePath + "and localPath=" + localPath
                        + ",file size is " + files.length);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return false;

    }

    /**
     * 删除本地文件
     *
     * @param filePath filePath
     * @return true
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }
        boolean rs = file.delete();
        if (rs && log.isInfoEnabled()) {
            log.info("delete file success from local.");
        }
        return rs;
    }

    /**
     * 创建目录
     *
     * @param createpath createpath
     * @return boolean
     */
    public boolean createDir(String createpath) {
        try {
            if (isDirExist(createpath)) {
                this.sftp.cd(createpath);
                return true;
            }
            String[] pathArry = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if ("".equals(path)) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }

            }
            this.sftp.cd(createpath);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断目录是否存在
     *
     * @param directory directory
     * @return boolean
     */
    public boolean isDirExist(String directory) {
        boolean dirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            dirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                dirExistFlag = false;
            }
        }
        return dirExistFlag;
    }

    /**
     * 删除stfp文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void deleteSFTP(String directory, String deleteFile) {
        try {
            // sftp.cd(directory);
            sftp.rm(directory + deleteFile);
            if (log.isInfoEnabled()) {
                log.info("delete file success from sftp.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归删除执行.
     * @param pathString 文件路径
     */
    public void exeRmRec(final String pathString) {
        try {
            Vector<LsEntry> vector = sftp.ls(pathString);
            if (vector.size() == 1) { // 文件，直接删除
                sftp.rm(pathString);
            } else if (vector.size() == 2) { // 空文件夹，直接删除
                sftp.rmdir(pathString);
            } else {
                String fileName = "";
                // 删除文件夹下所有文件
                for (LsEntry en : vector) {
                    fileName = en.getFilename();
                    if (".".equals(fileName) || "..".equals(fileName)) {
                        continue;
                    } else {
                        exeRmRec(pathString + "/" + fileName);
                    }
                }
                // 删除文件夹
                sftp.rmdir(pathString);
            }
            if (log.isInfoEnabled()) {
                log.info("delete pathString success from sftp.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 如果目录不存在就创建目录
     *
     * @param path path
     */
    public void mkdirs(String path) {
        File f = new File(path);

        String fs = f.getParent();

        f = new File(fs);

        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @return Vector
     * @throws SftpException SftpException
     */
    public Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ChannelSftp getSftp() {
        return sftp;
    }

    public void setSftp(ChannelSftp sftp) {
        this.sftp = sftp;
    }

}