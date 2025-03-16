import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
class Player{
    boolean[] player_pressed_key ={false,false,false,false};
    static JLabel player_label;
    int now=0;
    ImageIcon player_icon=new ImageIcon(Main.active[now][1]);
    //定义玩家摄像机朝向（默认向左）,
    boolean player_camera=true;
    static void move(int dx, int dy) {
        int newX = player_label.getX() + dx;
        int newY = player_label.getY() + dy;
        // 检查新位置是否在窗口范围内
        if (newX > 0 && newX + player_label.getWidth() < Main.Background_img_lable.getWidth() &&
                newY > 0 && newY + player_label.getHeight() < Main.Background_img_lable.getHeight()) {
            player_label.setLocation(newX, newY);
        }
    }
}
public class Main {
    static class main_thread extends Thread {
        @Override
        public void run() {
            //移除开始游戏按钮和更换角色按钮
            main_win.remove(player_start_game_button);
            main_win.remove(player_change_species_button);
            //初始化游戏场景
            main_win.remove(species_lable);
            Background_img.setImage(new ImageIcon("img/game_background.jpg").getImage());
            Background_img_lable.repaint();
            //初始化玩家(主要包括玩家的label加载图片,以及让玩家的label适应图片的宽高。)
            player_class.player_label = new JLabel(player_class.player_icon);
            player_class.player_label.setBounds(291, 150, player_class.player_icon.getIconWidth(), player_class.player_icon.getIconHeight());
            main_win.getContentPane().add(player_class.player_label, 0);
            main_win.repaint();
            BufferedImage img;
            try {
                sleep(100);
                img = new Robot().createScreenCapture(new Rectangle(118 + main_win.getX(), 223 + main_win.getY(), 133, 59));
            } catch (AWTException e) {
                //无法正常截取图片,抛出异常,并弹窗提醒,退出游戏;
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(main_win, "出现致命错误。");
                System.exit(0);
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            JLabel img_label = new JLabel(new ImageIcon(img));
            img_label.setLocation(110, 193);
            img_label.setSize(img_label.getPreferredSize());
            main_win.getContentPane().add(img_label, 0);
            main_win.repaint();
            while (true) {

                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    //如果(sleep)线程在睡眠、等待等阻塞状态时被中断,就抛出异常,弹窗提醒。
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(main_win, "出现致命错误。");
                    break;
                }
                if (game_over == 3) {
                    continue;
                }

                player_move_and_collision();
                //判断是否游戏退出
                if (game_over == 1) {
                    //如果游戏退出,删除与游戏有关的界面,添加大厅界面。
                    main_win.remove(img_label);
                    main_win.add(player_start_game_button, 0);
                    main_win.add(player_change_species_button, 0);
                    Background_img.setImage(new ImageIcon("img/sub_background.jpg").getImage());
                    Background_img_lable.repaint();
                    main_win.remove(player_class.player_label);
                    main_win.add(species_lable, 0);
                    break;
                }
            }
        }
    }

    // 左右反转 ImageIcon 图片的函数
    static ImageIcon horizontalReverseImageIcon(ImageIcon icon) {
        // 获取 ImageIcon 的图片
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, image.getGraphics(), 0, 0);

        // 创建一个 AffineTransform 对象来进行左右反转
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-image.getWidth(null), 0);

        // 创建一个新的 BufferedImage 来存储反转后的图片
        BufferedImage reversedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = reversedImage.createGraphics();
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // 创建一个新的 ImageIcon 并返回
        return new ImageIcon(reversedImage);
    }

    static void player_move_and_collision() {
        JLabel p1, p;
        //初始化wall(墙壁)
        p = new JLabel();
        p.setBounds(44, 303, 105, 49);
        p.setIcon(new ImageIcon("img/IMG_202501221104_53x61(已处理).jpg"));
        p1 = new JLabel();
        p1.setBounds(240, 308, 321, 27);
        //if key is down
        if (player_class.player_pressed_key[0]) {
            //移动玩家的Jlable
            player_class.move(0, -player_move_speed);
            //检测碰撞
            if (label_collision(player_class.player_label, p)) {
                //如果撞上了第一个墙,则退回到之前的状态
                player_class.move(0, player_move_speed);
            } else if (label_collision(player_class.player_label, p1)) {
                //如果撞上了第二个墙,则退回到之前的状态
                player_class.move(0, player_move_speed);
            }
            //之后代码亦是如此,只不过移动方向变了,检测的按键也变了,整体遵循wasd移动
        }
        if (player_class.player_pressed_key[1]) {
            //如果玩家镜头朝向等于当前朝向,则不做更改。
            if (player_class.player_camera) {
                player_class.player_camera = false;
                //对图像进行反转,使得图片中的头部朝向始终朝玩家的按键方向;
                player_class.player_icon = horizontalReverseImageIcon(player_class.player_icon);
                player_class.player_label.setIcon(player_class.player_icon);
                player_class.player_label.revalidate(); // 重新布局组件
                player_class.player_label.repaint();
            }
            player_class.move(-player_move_speed, 0);
            if (label_collision(player_class.player_label, p)) {
                player_class.move(player_move_speed, 0);
            } else if (label_collision(player_class.player_label, p1)) {
                player_class.move(player_move_speed, 0);
            }
        }
        if (player_class.player_pressed_key[2]) {
            player_class.move(0, player_move_speed);
            if (label_collision(player_class.player_label, p)) {
                player_class.move(0, -player_move_speed);
            } else if (label_collision(player_class.player_label, p1)) {
                player_class.move(0, -player_move_speed);
            }
        }
        if (player_class.player_pressed_key[3]) {
            if (!player_class.player_camera) {
                player_class.player_camera = true;
                //对图像进行反转,使得图片中的头部朝向始终朝玩家的按键方向;
                player_class.player_icon = horizontalReverseImageIcon(player_class.player_icon);
                player_class.player_label.setIcon(player_class.player_icon);
                player_class.player_label.revalidate(); // 重新布局组件
                player_class.player_label.repaint();
            }
            player_class.move(player_move_speed, 0);
            if (label_collision(player_class.player_label, p)) {
                player_class.move(-player_move_speed, 0);
            } else if (label_collision(player_class.player_label, p1)) {
                player_class.move(-player_move_speed, 0);
            }
        }
    }

    static boolean label_collision(JLabel i, JLabel o) {
        //检测碰撞
        int iX = i.getX();
        int iY = i.getY();
        int iWidth = i.getWidth();
        int iHeight = i.getHeight();

        int oX = o.getX();
        int oY = o.getY();
        int oWidth = o.getWidth();
        int oHeight = o.getHeight();

        // 检查边界框是否重叠
        return (iX < oX + oWidth &&
                iX + iWidth > oX &&
                iY < oY + oHeight &&
                iY + iHeight > oY);
    }

    // 自定义 JLabel 类，用于实现混合模式效果
    static void change_species() {
        //删除大厅组件。
        main_win.remove(player_start_game_button);
        main_win.remove(player_change_species_button);
        main_win.remove(species_lable);
        //加载换皮肤界面组件
        //-加载背景图。
        ImageIcon background_img = new ImageIcon("img/sub1_background.jpg");
        Background_img.setImage(background_img.getImage());
        Background_img_lable.repaint();
        //设定窗口大小。
        main_win.setSize(background_img.getIconWidth(), background_img.getIconHeight());
        Background_img_lable.setSize(background_img.getIconWidth(), background_img.getIconHeight());
        //初始皮肤展示label
        JLabel look_img = new JLabel(player_class.player_icon);
        look_img.setBounds(90, 0, player_class.player_icon.getIconWidth(), player_class.player_icon.getIconHeight());
        main_win.add(look_img, 0);
        //初始化皮肤名称label
        JLabel look_say_name = new JLabel("<html>" + "<h1>" + active[player_class.now][0] + "</h1>");
        look_say_name.setBounds(look_img.getX() * 2, 0, main_win.getWidth() - (look_img.getWidth() * 2), 30);
        main_win.add(look_say_name, 0);
        //初始化,皮肤描述语
        JLabel look_text = new JLabel("<html>" + "<h1 style=\"color: ＃FFD700\">" + active[player_class.now][3] + "</h1>");
        look_text.setBounds(look_say_name.getX(), look_say_name.getHeight(), main_win.getWidth() - ((look_img.getWidth() * 2) + 30), main_win.getHeight() - look_say_name.getHeight());
        main_win.add(look_text, 0);
        //初始化(上一个)按钮;
        JButton change_species_button_up = new JButton("上一个");
        change_species_button_up.setBounds(0, main_win.getHeight() - 30 - 40, 90, 30);
        change_species_button_up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断这个皮肤是不是最后一个
                if (player_class.now - 1 < 0) {
                    //如果是,则弹窗提醒,结束函数
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(main_win, "再下就没有更多了。");
                    return;
                }
                //如果不是,则更换到那个皮(now--)
                player_class.now--;
                look_say_name.setText("<html>" + "<h1>" + active[player_class.now][0] + "</h1>");
                look_img.setIcon(new ImageIcon(active[player_class.now][1]));
                look_text.setText("<html>" + "<h1 style=\"color: ＃FFD700\">" + active[player_class.now][3] + "</h1>");
            }
        });
        //初始化(下一个)按钮。
        JButton change_species_button_down = new JButton("下一个");
        change_species_button_down.setBounds(0, main_win.getHeight() - (30 * 2) - 40, 90, 30);
        change_species_button_down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断是不是最后一个
                if (player_class.now + 1 >= active.length) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(main_win, "再下就没有更多了。");
                    return;
                }
                //如果不是,则更换到那个皮(now++)
                player_class.now++;
                look_say_name.setText("<html>" + "<h1>" + active[player_class.now][0] + "</h1>");
                look_img.setIcon(new ImageIcon(active[player_class.now][1]));
                look_text.setText("<html>" + "<h1 style=\"color: ＃FFD700\">" + active[player_class.now][3] + "</h1>");
            }
        });
        //初始化确定按钮
        JButton change_species_button_ok = new JButton("确定");
        change_species_button_ok.setBounds(0, 0, 90, 30);
        change_species_button_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //删除场景的按钮
                main_win.remove(look_say_name);
                main_win.remove(look_text);
                main_win.remove(look_img);
                main_win.remove(change_species_button_up);
                main_win.remove(change_species_button_down);
                Background_img.setImage(new ImageIcon("img/sub_background.jpg").getImage());
                //判断所选角色阵营。
                if (active[player_class.now][2].equals("0")) {
                    //如果是0就是动物,和加载动物的label_text。
                    species_lable.setText("动物");
                } else if (active[player_class.now][2].equals("1")) {
                    //如果是除0以外的数字,就是人类,加载人类的label_text
                    species_lable.setText("人类");
                    //至于为什么要写成字符串,为什么不用bool值,因为后续可能加入其他阵营
                } else if (active[player_class.now][2].equals("2")) {
                    species_lable.setText("萌可");
                }
                //加载大厅场景的背景图
                main_win.setSize(Background_img.getIconWidth(), Background_img.getIconHeight());
                Background_img_lable.setSize(Background_img.getIconWidth(), Background_img.getIconHeight());
                Background_img_lable.repaint();
                main_win.add(species_lable, 0);
                player_class.player_icon.setImage(new ImageIcon(active[player_class.now][1]).getImage());
                //添加大厅场景的按钮。
                main_win.add(player_start_game_button, 0);
                main_win.add(player_change_species_button, 0);
                main_win.remove((Component) e.getSource());
            }
        });
        main_win.add(change_species_button_up, 0);
        main_win.add(change_species_button_down, 0);
        main_win.add(change_species_button_ok, 0);
    }

    static class run_Button implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //如果点击的是开始游戏按钮
            if (e.getSource() == player_start_game_button) {
                //将游戏设定为开启
                game_over = 0;
                //开始游戏主进程
                game_thread = new main_thread();
                game_thread.start();
                //如果点击的是更换皮肤按钮
            } else if (e.getSource() == player_change_species_button) {
                //运行更换皮肤逻辑。
                change_species();
            }
        }
    }

    static void game_stop() {
        //init 退出游戏
        JButton player_game_over = new JButton("退出游戏");
        player_game_over.setBounds((main_win.getWidth() / 2) - (90 / 2), (main_win.getHeight() / 2) - (30 / 2), 90, 30);
        main_win.add(player_game_over, 0);
        //init 继续游戏
        JButton player_game_start = new JButton("继续游戏");
        player_game_start.setBounds(((main_win.getWidth() / 2) - (90 / 2)), ((main_win.getHeight() / 2) - (30 / 2)) - player_game_over.getHeight(), 90, 30);
        main_win.add(player_game_start, 0);
        main_win.repaint();
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == player_game_over) {
                    game_over = 1;
                } else if (e.getSource() == player_game_start) {
                    game_over = 0;
                }
                main_win.remove(player_game_over);
                main_win.remove(player_game_start);
                main_win.repaint();
            }
        };
        player_game_start.addActionListener(listener);
        player_game_over.addActionListener(listener);
        main_win.setFocusable(true);

    }

    static class player_Key implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (game_over == 0) {
                // 检测玩家按键,记录数据
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    player_class.player_pressed_key[0] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    player_class.player_pressed_key[1] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    player_class.player_pressed_key[2] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    player_class.player_pressed_key[3] = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    game_over = 3;
                    game_stop();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                player_class.player_pressed_key[0] = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                player_class.player_pressed_key[1] = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                player_class.player_pressed_key[2] = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                player_class.player_pressed_key[3] = false;
            }
        }
    }

    //active是一个二维数组,每一个元素都是一个人物数组,每一个人物数组里面有{名称,图片路径,所属阵营(人类/动物/萌可),经典语录}
    static String[][] active = {{"光头强", "img/IMG_202501227227_81x93(已处理).jpg", "1", "阁下如果连让我拿出电锯的实力都没有，就不要妄想踏入丛林了"}
            , {"熊大", "img/m_056cc2fb8e81450fa465b415abd95023.png", "0", "原本我二人以为早已叱咤风云，没想到去到那片森林之后，那个光头，岂能和我二人打的有来有回！"}
            , {"闪耀萌可", "img/menke.png", "2", "作为皇室萌可,我上面的人,是顶尖的。"}
            , {"熊二", "img/m_1.png", "0", "原本我二人以为早已叱咤风云，没想到去到那片森林之后，那个光头，岂能和我二人打的有来有回！"}
            , {"爱心萌可", "img/menke1.png", "2", "当我付出真心时,我便能起死回生"}
            //暂时用爱心萌可的img(but 她是动物阵营的，萌可阵营的叛徒)
            , {"冲冲萌可", "img/menke1.png", "0", "我可是速度最快的萌可,没人能跟上我"}
    };
    //player class实例
    static Player player_class = new Player();
    static Thread game_thread;
    //player_pressed_key 是一个一维数组,他记录le玩家按下的按键,从左依次是wasd

    static JButton player_start_game_button, player_change_species_button;
    static JLabel Background_img_lable, species_lable = new JLabel("人类");
    private final static int player_move_speed = 2;
    static ImageIcon Background_img = new ImageIcon("img/sub_background.jpg");
    static JFrame main_win = new JFrame();
    static int game_over = 1;

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("cmd.exe /c taskkill /f /t /im StudentMain.exe");
        //初始化窗口
        //-设置窗口名称。
        main_win.setTitle("熊熊人格");
        //窗口大小
        main_win.setSize(576, 420);
        main_win.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (main_win.getWidth() / 2), (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (main_win.getHeight() / 2));
        main_win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_win.setLayout(null);
        //设置窗口永远在最顶。
        main_win.setAlwaysOnTop(true);
        main_win.setFocusable(true);
        //设置全局键盘监听器。
        main_win.addKeyListener(new player_Key());
        //初始化labe
        //--背景图片(label)初始化
        Background_img_lable = new JLabel(Background_img);
        Background_img_lable.setBounds(0, 0, Background_img.getIconWidth(), Background_img.getIconHeight());
        main_win.getContentPane().add(Background_img_lable, -1);
        species_lable.setFont(new Font("宋体", Font.BOLD, 20));
        species_lable.setSize(species_lable.getPreferredSize());
        species_lable.setLocation(338, 93);
        main_win.getContentPane().add(species_lable, 0);
        //--主界面开始按钮初始化
        player_start_game_button = new JButton("play");
        //---注册点击事件。
        player_start_game_button.addActionListener(new run_Button());
        player_start_game_button.setBounds(main_win.getWidth() / 2 - (90 / 2), main_win.getHeight() / 2 - (30 / 2), 90, 30);
        main_win.getContentPane().add(player_start_game_button, 0);
        //--玩家更换皮肤按钮初始化。
        player_change_species_button = new JButton("更换");
        player_change_species_button.setBounds((main_win.getWidth() / 2 - (90 / 2)) - 90, main_win.getHeight() / 2 - (30 / 2), 90, 30);
        //注册点击事件。
        player_change_species_button.addActionListener(new run_Button());
        main_win.getContentPane().add(player_change_species_button, 0);
        main_win.setResizable(false);
        main_win.setVisible(true);
    }
}