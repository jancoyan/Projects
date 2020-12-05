/**
 * @Author Jingcun Yan
 * @Date 12:32 2020/11/14
 * @Description
 */

package UI;

import Components.Panel.BackgroundPanel;
import Domain.User;
import Utils.DBUtils.DQLUtils;
import Utils.EncryptUtil;
import Utils.RealPath;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录界面，程序的入口
 * @author JingcunYan
 */
public class LoginFrame {

    final int WIDTH = 600;
    final int HEIGHT = 500;

    /**
     * 本地用户，用来判断用户是否具有管理员权限，以及修改信息等
     */
    User localUser;

    void initUI() throws Exception {

        // 设置全局字体
        initGlobalFont(new Font("alias", Font.PLAIN, 20));

        JFrame jf = new JFrame("登录");
        jf.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH)/2, (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT)/2, WIDTH, HEIGHT);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        BackgroundPanel bgp = new BackgroundPanel(ImageIO.read(new File(RealPath.realPath("login.png"))));
        bgp.setBounds(0, 0, WIDTH, HEIGHT);

        //Panel 中的大容器，用来存放三组控件
        Box vBox = Box.createVerticalBox();

        //标题Box
        Box tBox = Box.createHorizontalBox();
        JLabel title = new JLabel("图书馆管理系统 V1.1");
        vBox.add(Box.createVerticalStrut(30));
        tBox.add(title);
        vBox.add(tBox);

        //登录相关的元素
        //账号
        Box uBox = Box.createHorizontalBox();
        JLabel uLabel = new JLabel("账   号  ");
        JTextField uField = new JTextField(15);
        uBox.add(uLabel);
        uBox.add(Box.createHorizontalStrut(20));
        uBox.add(uField);

        //密码
        Box pBox = Box.createHorizontalBox();
        JLabel pLabel = new JLabel("密    码    ");
        JPasswordField pFiled = new JPasswordField(15);
        pBox.add(pLabel);
        pBox.add(Box.createVerticalStrut(20));
        pBox.add(pFiled);

        //按钮
        Box bBox = Box.createHorizontalBox();
        JButton signIn = new JButton("登录");
        JButton signUp = new JButton("注册");
        signIn.setFont(new Font(null, Font.PLAIN, 20));
        signUp.setFont(new Font(null, Font.PLAIN, 20));
        bBox.add(signIn);
        bBox.add(Box.createHorizontalStrut(100));
        bBox.add(signUp);

        // 登录监听
        signIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = uField.getText().trim();
                String passWord = pFiled.getText().trim();
                if("".equals(userId)|| "".equals(passWord)){
                    JOptionPane.showMessageDialog(jf,"用户名和密码都不能为空");
                } else {
                    // 查询数据库，看看这个用户是否存在
                    Map<String, String> user = new HashMap<>(16);
                    user.put("userId", userId);
                    user.put("userPwd", new EncryptUtil().MD5(passWord));
                    localUser = DQLUtils.login(user);
                    if (localUser != null) { //用户存在
                        JOptionPane.showMessageDialog(jf, "登录成功！");
                        new LibraryBodyFrame().initUI(localUser);
                        //这里在用户进行登录的时候，只传进去了一个”虚拟“的用户
                        //这是一个残缺的用户，只有密码和用户名
                        //所以在执行一些操作的时候还要和数据库进行通讯查询是否有权限。
                        jf.dispose();
                    } else { //密码错误
                        JOptionPane.showMessageDialog(jf, "密码错误");
                        uField.setText("");
                        pFiled.setText("");
                    }
                }
            }
        });

        // 注册监听
        signUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpFrame().initUI();
                jf.dispose();
            }
        });

        vBox.add(Box.createVerticalStrut(60));
        vBox.add(uBox);
        vBox.add(Box.createVerticalStrut(50));
        vBox.add(pBox);
        vBox.add(Box.createVerticalStrut(80));
        vBox.add(bBox);

        bgp.add(vBox);

        jf.add(bgp);
        jf.setVisible(true);
        jf.setResizable(false);
    }

    /**
     *  初始化全局字体
     * @param font 字体对象
     */
    private static void initGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys();
             keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }

    /**
     * 主函数，程序的入口
     * @param args 形参。
     */
    public static void main(String[] args) {
        try {
            new LoginFrame().initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
