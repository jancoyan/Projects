package Utils.DBUtils;

import Domain.Book;
import Domain.User;
import Utils.EncryptUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Jingcun Yan
 */
public class DMLUtils {
    private DMLUtils(){}

    /**
     *  添加一个用户
     * @param user 用户实体
     * @return 添加成功还是失败
     */
    public static boolean addUser(User user){
        boolean success = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "insert into users values (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUserName());
            ps.setString(3, String.valueOf(user.getUserType()));
            ps.setString(4, String.valueOf(user.getUserSex()));
            ps.setString(5, user.getUserTel());
            // 将 md5 加密后的密码存入数据库
            ps.setString(6, new EncryptUtil().MD5(user.getUserPwd()));
            int rs = ps.executeUpdate();
            if(rs != 0){
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭数据库连接
            DBUtil.close(conn, ps, null);
        }
        return success;
    }


    /**
     * 更新一个用户
     * @param user 用户实体
     * @param column 要更新的字段
     * @param changed 更新后的值
     * @return 更新成功还是失败
     */
    public static boolean updateUser(User user, String column, String changed){
        boolean succ = false;
        Connection conn = null;
        PreparedStatement ps = null;
        /**
         * 在这里，使用sql语句的时候不能把要修改的字段名也作为 ？ 匹配的成员，因为在转化的时候系统会自动在String
         * 类型的变量前后加上单引号，
         */
        try {
            conn = DBUtil.getConnection();
            String sql = "update users set " + column + " = ? where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, changed);
            ps.setString(2, user.getUserId());
            int result = ps.executeUpdate();
            if(result != 0){
                succ = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return succ;
    }

    /**
     *  删除用户工具类
     * @param user 用户对象， 其中只包含用户的id
     * @return 删除成功还是失败
     */
    public static boolean deleteUser(User user) {
        boolean succ = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "delete from users where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            int result = ps.executeUpdate();
            if(result != 0){
                succ = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        return succ;
    }

    /**
     *  添加图书，如果已经有了就添加到库存里
     * @param book 要添加的图书
     * @return 添加图书的业务是否成功了
     */
    public static boolean addBook(Book book){
        // inStock 来存储在数据库中的书，以获得其库存量。
        Book inStock = DQLUtils.getBook(book.getBookISBN());
        if(inStock != null){
            // 如果已经存在，此时book已经是完整的一本书了，就添加到库存中
            int last = book.getStock() + inStock.getStock();
            DMLUtils.updateBook(book, "stock", String.valueOf(last));
            return true;
        }
        boolean rst = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "insert into books values (?, ?, ?, ?, ?, ?, 0)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, book.getBookISBN());
            ps.setString(2, book.getBookName());
            ps.setString(3, String.valueOf(book.getBookPrice()));
            ps.setString(4, book.getAuthor());
            ps.setString(5, String.valueOf(book.getStock()));
            ps.setString(6, book.getDescription());
            int rs = ps.executeUpdate();
            if(rs != 0){
                rst = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭数据库连接
            DBUtil.close(conn, ps, null);
        }
        return rst;
    }

    /**
     * @param book 书籍对象
     * @param column 要修改的字段
     * @return 执行的情况 成功/失败 + 数量
     */
    public static boolean updateBook(Book book, String column, String changed){
        Connection conn = null;
        PreparedStatement ps = null;
        boolean succ = false;
        try {
            conn = DBUtil.getConnection();
            /*
                这里要针对不同的字段修改做不同的语句处理
                一种本身就是字符串型的
                一种是数字型的，这俩分别写sql
             */
            if("lent".equals(column) || "stock".equals(column) || "price".equals(column)){
                String sqlInt = "update books set " + column + " = " + changed + " where ISBN = " + book.getBookISBN();
                ps = conn.prepareStatement(sqlInt);
            }else{
                String sqlString = "update books set " + column + " = '" + changed + "' where ISBN = " + book.getBookISBN();
                ps = conn.prepareStatement(sqlString);
            }
            int rst = ps.executeUpdate();
            if(rst != 0) {
                succ = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return succ;
    }

    /**
     *  删除一列书的数据
     * @param book 传进来的book参数，只有一个ISBN 属性
     * @return 删除成功或者失败，在这里选中的列一定是数据库中已经存在的，所以一般不会有删除失败的情况
     */
    public static boolean deleteBook(Book book){
        boolean success = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "delete from books where ISBN = " + book.getBookISBN();
            ps = conn.prepareStatement(sql);
            int rst = ps.executeUpdate();
            if(rst != 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return  success;
    }

    /**
     *  借书操作
     * @param book 要借的书籍对象
     * @param user 借书人
     * @return 结束成功还是失败
     */
    public static boolean borrowBook(Book book, User user){
        if(book.getStock() <= 0){
            return false;
        }
        boolean succ = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "insert into borrow value (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, book.getBookISBN());
            ps.setTimestamp(3, Timestamp.valueOf(String.valueOf(new Timestamp(System.currentTimeMillis()))));
            int result = ps.executeUpdate();
            succ = updateBook(book, "stock", String.valueOf(book.getStock() - 1));
            succ = updateBook(book, "lent", String.valueOf(book.getLent() + 1));
            if(result != 0){
                succ = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(conn, ps, null);
        }
        return succ;
    }

    /**
     *  还书操作
     * @param book 要归还的书籍对象
     * @param user 还书的人
     * @return 还书成功还是失败
     */
    public static boolean returnBook(Book book, User user) {
        boolean succ = false;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "delete from borrow where userId = ? and bookId = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, book.getBookISBN());
            int result = ps.executeUpdate();
            if (result != 0) {
                succ = true;
                DMLUtils.updateBook(book, "stock", String.valueOf(book.getStock() + 1));
                DMLUtils.updateBook(book, "lent", String.valueOf(book.getLent() - 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        return succ;
    }
}