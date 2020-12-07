/**
 * @Author Jingcun Yan
 * @Date Create in 10:50 2020/11/30
 * @Description
 */

package Components;

import Components.Dialog.BorrowBookDialog;
import Domain.Book;
import Domain.User;
import Utils.DBUtils.DBUtil;
import Utils.DBUtils.DQLUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GuessYouLikeComponent extends Box {

    JFrame jf;
    User localUser;
    Vector<Vector<Object>> td = new Vector<>();
    JTable table;
    DefaultTableModel model;

    public GuessYouLikeComponent(JFrame jf, User user){
        super(BoxLayout.Y_AXIS);
        localUser = user;
        this.jf = jf;

        Object[] columnNames = {"序号","ISBN", "书名", "作者", "价格"};

        Vector<Object> columnName = new Vector<>();
        Collections.addAll(columnName, columnNames);
        model = new DefaultTableModel(td, columnName);
        table = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        table.setRowHeight(25);

        // 标题的panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("图  书  推  荐");
        titleLabel.setFont(new Font(null, Font.PLAIN, 40));
        titlePanel.add(titleLabel);

        // 借阅的 panel
        JPanel borrowPanel = new JPanel();
        borrowPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 借阅的button
        JButton borrowButton = new JButton("借阅");
        borrowPanel.add(borrowButton);

        // 借阅的监听器
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() == -1){
                    JOptionPane.showMessageDialog(jf, "请选择一本书");
                } else {
                    Book toBorrow = new Book(
                            (String) td.get(table.getSelectedRow()).get(1),
                            (String) td.get(table.getSelectedRow()).get(2),
                            Double.parseDouble((String) td.get(table.getSelectedRow()).get(4)) ,
                            (String) td.get(table.getSelectedRow()).get(3),
                            0, ""
                    );
                    new BorrowBookDialog(toBorrow, user, jf, "Borrow Book", true);
                    model.getDataVector().clear();
                    requestData();
                }
            }
        });

        this.add(Box.createVerticalStrut(30));
        this.add(titlePanel);
        this.add(borrowPanel);
        JScrollPane jsp = new JScrollPane(table);
        this.add(jsp);
        requestData();
    }

    /**
     * 获取用户最近10次借书记录，统计类型并且按照类型的比例推送与类型相关的书
     * 如果用户当前没有借阅记录，就随机推送各种类型的书籍
     */
    public void requestData(){
        Connection conn = null;
        PreparedStatement ps =  null;
        ResultSet rs = null;
        Map<Integer, Integer> userPreference = new HashMap<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT t_book.TYPE,count( t_book.TYPE ) cnt FROM t_user_log, t_book WHERE t_user_log.ISBN = t_book.ISBN AND t_user_log.id = ? AND t_user_log.TYPE = 0 AND ROWNUM <= 10 GROUP BY t_book.TYPE";
            ps = conn.prepareStatement(sql);
            ps.setString(1, localUser.getUserId());
            rs = ps.executeQuery();
            // 获取每一种类型的书的键值对
            // 书籍的类型 - 借阅书籍的数量
            while(rs.next()) {
                userPreference.put(rs.getInt("TYPE"), rs.getInt("CNT"));
            }

            //最开始我想的是从书籍的库中获取5本书，然后按照比例分配。但是后来感觉比较麻烦。
            //目前想法：直接获取和用户最近 10 次的书籍的数量，按照这些类型进行分配
            //直接显示出来这个书籍的界面

            // 获取每一种书籍的类型和数量
            Map<Integer, Integer> bookTypeAndNumber = DQLUtils.bookTypeAndNumber();

            Iterator<Integer> iter = userPreference.keySet().iterator();

            // 填写sql语句，直接获取查询结果集，然后在循环rs的时候对书籍的行数进行获取
            // 就不再sql里面直接获取某一行的数据了
            // iter 是用户偏好的迭代器

            int tableRowNumber = 1;
            // 基于以上的结果集获取数据
            while(iter.hasNext()){
                // 用户 10 次以来借阅这种书籍的类型
                int borrowType = iter.next();

                // 从数据库中获取instock = 0的这种类型的书籍
                sql = "select ROWNUM, isbn, name, author, price from t_book where instock = 0 and type = ? order by type";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, borrowType);
                // 获取查询结果集
                rs = ps.executeQuery();

                // 间隔 = 书籍的总数量 / 用户借阅的数量
                int interval = bookTypeAndNumber.get(borrowType) / userPreference.get(borrowType);
                // 按照选取书籍的间隔进行选取
                // 借阅这种类型的书籍的总数量
                int totalNumber = userPreference.get(borrowType);
                // 进行书籍的获取
                // i 用来控制循环
                int i = 0;
                while(rs.next()) {
                    if (++i == 1 || i % interval == 0) {
                        Vector<Object> temp = new Vector<>();
                        temp.add(tableRowNumber++);
                        temp.add(rs.getString("ISBN"));
                        temp.add(rs.getString("NAME"));
                        temp.add(rs.getString("AUTHOR"));
                        temp.add(rs.getString("PRICE"));
                        td.add(temp);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        model.fireTableDataChanged();
    }
}
