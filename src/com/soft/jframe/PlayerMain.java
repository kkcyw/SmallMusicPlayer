package com.soft.jframe;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.soft.util.PlayMusic;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JComboBox;

/**
 * 音乐播放器制作
 * @author cyw
 * @time 2018-07-03
 */

@SuppressWarnings("serial")
public class PlayerMain extends JFrame implements ActionListener{

	private JPanel contentPane;
	
	//窗口拖拽
	private boolean isMoved;
	private Point pre_point;
	private Point end_point;
	
	private boolean isMin = true;//判断是否第一次关闭
	private String nowmusic="";//正在播放的歌曲名
	
	
	private boolean flag = true;//判断标志
	@SuppressWarnings("rawtypes")
	JList list,pathlist;//歌曲名与歌曲路径列表
	JButton btnStar,btnStop,btnadd;//开始、停止、增加
	@SuppressWarnings("rawtypes")
	JComboBox comboBox;//列表循环
	PlayMusic play;//播放器播放驱动
	String old;//记录旧的路径
	@SuppressWarnings("rawtypes")
	DefaultListModel name_dlm = new DefaultListModel();//歌曲模型
	@SuppressWarnings("rawtypes")
	DefaultListModel path_dlm = new DefaultListModel();//歌曲路径模型
	private JButton btnclearlist;//清空列表
	private JButton btndel;//删除单条
	
	//最小化至托盘
	private TrayIcon trayIcon;//托盘图标
	 
	//logo照片
    String filePath = "/com/soft/music.png";
	 
    //最小化列表
    PopupMenu pmenu = new PopupMenu();       //托盘图标的弹出式菜单
	MenuItem openmenu = new MenuItem("打开");  //弹出菜单的打开选项
	MenuItem closemenu = new MenuItem("关闭");  //弹出菜单的关闭选项
	MenuItem startmenu = new MenuItem("开始/暂停");  //弹出菜单的打开选项
	MenuItem nextmenu = new MenuItem("下一首");  //弹出菜单的打开选项
	MenuItem lastmenu = new MenuItem("上一首");  //弹出菜单的打开选项
	public SystemTray systemtray = SystemTray.getSystemTray();  //得到当前系统托盘
	Image image = Toolkit.getDefaultToolkit().getImage("music.png");//
	
	int selectedindex = 0;//存储列表选择的下标
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox_1;//判断是否播放结束
	private Timer timer = new Timer(0, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(comboBox_1.getSelectedIndex()==1) {
				int loop_index = comboBox.getSelectedIndex();
				int list_size = list.getModel().getSize();
				if(loop_index == 0) {//列表循环
					if(selectedindex>=(list_size-1)){
						list.setSelectedIndex(0);
						pathlist.setSelectedIndex(0);
					}else {
						list.setSelectedIndex(selectedindex+1);
						pathlist.setSelectedIndex(selectedindex+1);
					}
				}else if(loop_index == 1) {//单曲循环
					list.setSelectedIndex(selectedindex);
					pathlist.setSelectedIndex(selectedindex);
				}else if(loop_index == 2) {//随机播放
					Random r = new Random();
					int index = r.nextInt(list_size);
					if(index==selectedindex)
						index = r.nextInt(list_size);
					pathlist.setSelectedIndex(index);
					list.setSelectedIndex(index);
				}
				btnStar.doClick();
				comboBox_1.setSelectedIndex(0);
			}
			if(btnStar.getText() == "暂停"||btnStar.getText().equals("暂停")) {//判断是否在播放状态
				//修改托盘logo的tip
				nowmusic = name_dlm.getElementAt(selectedindex)+"";
				trayIcon.setToolTip("微型音乐播放器——cyw\n正在播放："+nowmusic);
			}else {
				trayIcon.setToolTip("微型音乐播放器——cyw");
			}
		}
	});
	private JButton btnlast;
	private JButton btnNext;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PlayerMain frame = new PlayerMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PlayerMain() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(0, 0, 354, 318);
		
		//设置logo
		URL logourl = getClass().getResource(filePath);
		final Image logoim = new ImageIcon(logourl).getImage();
		setIconImage(logoim);
		
		//设置最小化至托盘
		 try {
	          trayIcon = new TrayIcon(image, "微型音乐播放器——cyw", pmenu);
	          trayIcon.setImageAutoSize(true); // 设置系统图标大小自动。
	          trayIcon.addMouseListener(new MouseAdapter() {
		             public void mouseClicked(MouseEvent e) {
		            	 if (e.getClickCount() == 2) {//双击托盘窗口再现
		            		 setExtendedState(Frame.NORMAL);
		            		 setVisible(true);
		                  }
		             }
		         });
	          systemtray.add(trayIcon); // 将图标添加到系统托盘
	          
	          //隐藏前选择之前下标
	          if(name_dlm.isEmpty()==false) {
	        	  list.setSelectedIndex(selectedindex);
	        	  pathlist.setSelectedIndex(selectedindex);
	          }
	          
	          this.dispose();
	     } catch (AWTException e2) {
	            e2.printStackTrace();
	     }
		 
	        this.addWindowListener(new WindowAdapter() {
	             /*public void windowIconified(WindowEvent e) {
	            	//隐藏前选择之前下标
	            	 if(name_dlm.isEmpty()==false) {
	   	        	  	list.setSelectedIndex(selectedindex);
	   	        	  	pathlist.setSelectedIndex(selectedindex);
	            	 }
	            	 dispose();//窗口最小化时dispose该窗口
	             }*/
	             @Override
	            public void windowClosing(WindowEvent e) {
	            	// TODO Auto-generated method stub
	            	 if(isMin) {//判断是否第一次点击关闭操作
	            		 JOptionPane.showMessageDialog(null, "<html><font color=red>双击界面非控件位置可关闭窗口！！！</font></html>");
	            		 isMin = false;
	            	 }
	            	//隐藏前选择之前下标
	            	 if(name_dlm.isEmpty()==false) {
		   	        	  list.setSelectedIndex(selectedindex);
		   	        	  pathlist.setSelectedIndex(selectedindex);
		             }
	            	dispose();
	            }
	        });
	        
	        //最小化托盘列表监听
	        openmenu.addActionListener(new MenuListen(this));
			closemenu.addActionListener(new MenuListen(this));
			startmenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					btnStar.doClick();
				}
			});
			lastmenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					btnlast.doClick();
				}
			});
			nextmenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					btnNext.doClick();
				}
			});
			pmenu.add(openmenu);            //在弹出式菜单里面加入菜单。
			pmenu.add(closemenu);
			pmenu.add(startmenu);
			pmenu.add(lastmenu);
			pmenu.add(nextmenu);

		
		
		this.setLocationRelativeTo(null);//居中显示
        this.setResizable(false);
        timer.start();
        //setUndecorated(true);//去边框
        //this.setAlwaysOnTop(true);//置顶
		setTitle("\u5FAE\u578B\u97F3\u4E50\u64AD\u653E\u5668");
		movejframe();
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnStar = new JButton("开始");
		btnStar.setBounds(5, 10, 104, 37);
		btnStar.setFont(new Font("华文楷体", Font.PLAIN, 20));
		contentPane.add(btnStar);
		btnStar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		btnStop = new JButton("停止");
		btnStop.setFont(new Font("华文楷体", Font.PLAIN, 20));
		btnStop.setBounds(5, 57, 104, 37);
		btnStop.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btnStop);
		
		list = new JList();
		pathlist = new JList();
		list.setBounds(119, 10, 219, 161);
		JScrollPane jsp = new JScrollPane(list);
		jsp.setBounds(119, 10, 219, 171);
		contentPane.add(jsp);
		
		btnadd = new JButton("\u6DFB\u52A0\u64AD\u653E\u5217\u8868");
		btnadd.setFont(new Font("华文楷体", Font.PLAIN, 20));
		btnadd.setBounds(119, 191, 219, 37);
		btnadd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btnadd);
		
		JLabel lblNewLabel = new JLabel("Copyright\u00AEcyw 2018",JLabel.CENTER);
		lblNewLabel.setBounds(0, 275, 348, 15);
		contentPane.add(lblNewLabel);
		
		btnclearlist = new JButton("\u6E05\u7A7A\u64AD\u653E\u5217\u8868");
		btnclearlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list.getModel().getSize()>0){
					((DefaultListModel)pathlist.getModel()).removeAllElements();
					((DefaultListModel)list.getModel()).removeAllElements();
					selectedindex = 0;
				}
				btnStop.doClick();
			}
		});
		btnclearlist.setFont(new Font("华文楷体", Font.PLAIN, 20));
		btnclearlist.setBounds(119, 234, 219, 37);
		btnclearlist.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btnclearlist);
		
		btndel = new JButton("\u5220\u9664");
		btndel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list.getModel().getSize()>0) {
					String getf = (String) pathlist.getSelectedValue();
					int geti = pathlist.getSelectedIndex();
					if(getf==old||getf.equals(old)) 
						btnStop.doClick();
					((DefaultListModel)pathlist.getModel()).removeElementAt(geti);
					((DefaultListModel)list.getModel()).removeElementAt(geti);
					pathlist.setSelectedIndex(selectedindex);
					list.setSelectedIndex(selectedindex);
					selectedindex = pathlist.getSelectedIndex();
				}
			}
		});
		btndel.setFont(new Font("华文楷体", Font.PLAIN, 20));
		btndel.setBounds(5, 198, 104, 37);
		btndel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btndel);
		
		comboBox = new JComboBox();
		comboBox.setBounds(5, 244, 104, 27);
		comboBox.addItem("列表循环");
		comboBox.addItem("单曲循环");
		comboBox.addItem("随机播放");
		comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(comboBox);
		
		btnlast = new JButton("\u4E0A\u4E00\u66F2");
		btnlast.setFont(new Font("华文楷体", Font.PLAIN, 20));
		btnlast.setBounds(5, 104, 104, 37);
		btnlast.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btnlast);
		
		btnNext = new JButton("\u4E0B\u4E00\u66F2");
		btnNext.setFont(new Font("华文楷体", Font.PLAIN, 20));
		btnNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNext.setBounds(5, 151, 104, 37);
		contentPane.add(btnNext);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(5, 187, 104, 21);
		comboBox_1.addItem("0");
		comboBox_1.addItem("1");
		this.addMouseListener(new Dbclick());
		btnadd.addActionListener(this);
		btnStar.addActionListener(this);
		btnStop.addActionListener(this);
		btnlast.addActionListener(this);
		btnNext.addActionListener(this);
		list.addMouseListener(new ListDbclick());
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				pathlist.setSelectedIndex(list.getSelectedIndex());
				if(list.getSelectedIndex()==-1){
					list.setSelectedIndex(0);
					pathlist.setSelectedIndex(0);
				}
			}
		});
		
	}
	
	// 鼠标拖拽移动窗口
		public void movejframe() {
			this.setDragable(this);
		}

		private void setDragable(final PlayerMain move) {
			this.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					isMoved = false;
					move.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

				public void mousePressed(java.awt.event.MouseEvent e) {
					isMoved = true;
					pre_point = new Point(e.getX(), e.getY());
					move.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				}
			});
			this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				public void mouseDragged(java.awt.event.MouseEvent e) {
					if (isMoved) {
						end_point = new Point(move.getLocation().x + e.getX() - pre_point.x,
								move.getLocation().y + e.getY() - pre_point.y);
						move.setLocation(end_point);
					}
				}
			});
		}
	
	/**
	 * 双击非控件区域关闭窗口
	 * @author cyw
	 *
	 */
	class Dbclick extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int clickTimes = e.getClickCount();
		    if (clickTimes == 2) {
		      if(JOptionPane.showConfirmDialog(null,"确定关闭程序吗？","询问", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
		    	  //dispatchEvent(new WindowEvent(new login(), WindowEvent.WINDOW_CLOSING));
		    	  System.exit(0); 
		    }
		}
	}
	
	//列表内双击
	class ListDbclick extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int clickTimes = e.getClickCount();
		    if (clickTimes == 2) {
		      String filename = (String) pathlist.getSelectedValue();
		      selectedindex = pathlist.getSelectedIndex();
		      if(flag == true) {
					old = filename;
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("暂停");
				}else if(flag == false&&old != filename){
					old = filename;
					play.stop();
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("暂停");
				}else if(flag == false&&old == filename){
					if(play.isComplete()) {
						play.stop();
						old = filename;
						play = new PlayMusic(filename,btnStar,comboBox_1);
						flag = false;
						play.start();
						btnStar.setText("暂停");
					}else {
						JOptionPane.showMessageDialog(null, "<html><font color=red>歌曲播放中</font></html>");
					}
				}
		    }
		}
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==btnadd) {//将歌曲添加到列表
			selectedindex = 0;//默认下标为0
			JFileChooser filechooser = new JFileChooser("C:\\Users\\Administrator\\Desktop");//文件选择器默认打开地址
			filechooser.setDialogTitle("请选择音乐播放路径！");//文件选择器标题
			FileFilter filter1 = new FileNameExtensionFilter("*.mp3", "mp3");//文件格式过滤
			filechooser.setFileFilter(filter1);
			filechooser.setMultiSelectionEnabled(true);
			int i = filechooser.showOpenDialog(null);
			if(i == filechooser.APPROVE_OPTION) {//点击确定后操作
				File[]  f= filechooser.getSelectedFiles(); //获取到选择的值
				String abpath,name;
				for (int z = 0; z < f.length; z++) {
					abpath = f[z].getAbsolutePath();
					name = f[z].getName();
					path_dlm.addElement(abpath);
					name_dlm.addElement(name.substring(0, name.length()-4));//删去.mp3
				}
				
				//添加模型
				list.setModel(name_dlm);
				pathlist.setModel(path_dlm);
				
				//默认选第一项
				list.setSelectedIndex(0);
				pathlist.setSelectedIndex(0);
			}
		}else if(e.getSource() == btnStar) {//开始按钮监听
			String filename = (String) pathlist.getSelectedValue();//获得歌曲路径
			if(filename!=null) {
				selectedindex = pathlist.getSelectedIndex();
				if(flag == true&&btnStar.getText()=="开始") {
					old = filename;
					play = new PlayMusic(filename,btnStar,comboBox_1);//将要传的参数传至函数中
					flag = false;
					play.start();//开始线程
					btnStar.setText("暂停");
				}else if(flag == false&&btnStar.getText()=="开始"&&old != filename){
					old = filename;
					play.stop();
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("暂停");
				}else if(flag == false&&btnStar.getText()=="开始"&&old == filename){
					if(play.isComplete()) {
						play.stop();
						old = filename;
						play = new PlayMusic(filename,btnStar,comboBox_1);
						flag = false;
						play.start();
						btnStar.setText("暂停");
					}else {
						play.resume();//线程恢复
						btnStar.setText("暂停");
					}
				}else if(btnStar.getText()=="暂停") {
					play.suspend();//线程挂起即歌曲暂停
					btnStar.setText("开始");
				}
				//列表定位到选项位置
				Rectangle rectangle = list.getCellBounds(selectedindex, selectedindex);
				list.scrollRectToVisible(rectangle);
			}else {
				if(list.getModel().getSize()>0) {//无歌曲判定
					selectedindex = 0;
					filename = list.getModel().getElementAt(0)+"";
					list.setSelectedIndex(0);
					pathlist.setSelectedIndex(0);
					old = filename;
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("暂停");
				}else {
					JOptionPane.showMessageDialog(null, "<html><font color=red>请先添加播放曲目</font></html>");
				}
					
			}
		}else if(e.getSource() == btnStop) {//停止按钮监听,将一切重置
			selectedindex = 0;
			play.stop();
			btnStar.setText("开始");
			flag = true;
			old="";
		}else if(e.getSource() == btnlast) {//上一曲按钮监听
			selectedindex = pathlist.getSelectedIndex();//获取歌曲路径
			int loop_index = comboBox.getSelectedIndex();//获取循环下拉框
			int list_size = list.getModel().getSize();//得到模型的大小
			btnStar.setText("开始");
			if(loop_index == 2){//随机播放
				Random r = new Random();
				int index = r.nextInt(list_size);
				if(index==selectedindex)//避免随机同一首歌
					index = r.nextInt(list_size);
				pathlist.setSelectedIndex(index);
				list.setSelectedIndex(index);
			}else{
				if(selectedindex>0) {//下标大于等于1时向上退
					list.setSelectedIndex(selectedindex-1);
					pathlist.setSelectedIndex(selectedindex-1);
				}
			}
			btnStar.doClick();
		}else if(e.getSource() == btnNext) {//下一曲按钮监听
			selectedindex = pathlist.getSelectedIndex();//获取路径
			int size = list.getModel().getSize();//获取模型大小
			btnStar.setText("开始");
			if(size>1) {//当超过一首歌时下一曲才启用
				int loop_index = comboBox.getSelectedIndex();
				int list_size = list.getModel().getSize();
				if(loop_index == 2){//随机播放
					Random r = new Random();
					int index = r.nextInt(list_size);
					if(index==selectedindex)//避免随机同一首歌
						index = r.nextInt(list_size);
					pathlist.setSelectedIndex(index);
					list.setSelectedIndex(index);
				}else{
					if(selectedindex<(size-1)) {//非最后一曲时向前进一手
						pathlist.setSelectedIndex(selectedindex+1);
						list.setSelectedIndex(selectedindex+1);
					}
				}
				btnStar.doClick();
			}
		}
	}
	
	/**
	 * 最小化列表监听
	 * @author cyw
	 *
	 */
	class MenuListen implements ActionListener {
		public JFrame jf = null;

		public MenuListen(JFrame jf) {//构造函数
			this.jf = jf;
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("关闭")) {
				if(JOptionPane.showConfirmDialog(null,"确定关闭程序吗？","询问", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					System.exit(0); //关闭窗口
			} else
				jf.setVisible(true);//显示窗口
		}

	}
	
	

}
