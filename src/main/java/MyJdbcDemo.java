import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyJdbcDemo {
    public static void main(String[] args) {
        String sql = "";
        String url = "jdbc:oracle:thin:@192.168.31.152:1521/helowin";
        String user = "kehusuqiu";
        String password = "kehusuqiu";
        ResultSet rs = null;
        PreparedStatement psmt = null;
        Connection conn = null;
        try {
            // 可以通过 Class.forName 把它加载进去，也可以通过初始化来驱动起来 
            // Class.forName("oracle.jdbc.OracleDriver");// 动态加载驱动
            // System.out.println("成功加载 Oracle 驱动程序");
            // 一个 Connection 代表一个数据库连接
            conn = DriverManager.getConnection(url, user, password);
            // Statement 里面带有很多方法，比如 executeUpdate 可以实现插入，更新和删除等
            sql = "select nickname from account where id in (?,?,?)";
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, "2");
            psmt.setString(2, "4");
            psmt.setString(3, "4");
            rs = psmt.executeQuery();
            while (rs.next()) {// 遍历 user 表中所有数据
                String name = "" + rs.getString("nickname");
                System.out.println("nickname：" + name);//
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close(); // 关闭结果数据集
                if (psmt != null)
                    psmt.close(); // 关闭执行环境
                if (conn != null)
                    conn.close(); // 关闭数据库连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
