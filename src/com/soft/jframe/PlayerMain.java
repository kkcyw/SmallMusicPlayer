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
 * ���ֲ���������
 * @author cyw
 * @time 2018-07-03
 */

@SuppressWarnings("serial")
public class PlayerMain extends JFrame implements ActionListener{

	private JPanel contentPane;
	
	//������ק
	private boolean isMoved;
	private Point pre_point;
	private Point end_point;
	
	private boolean isMin = true;//�ж��Ƿ��һ�ιر�
	private String nowmusic="";//���ڲ��ŵĸ�����
	
	
	private boolean flag = true;//�жϱ�־
	@SuppressWarnings("rawtypes")
	JList list,pathlist;//�����������·���б�
	JButton btnStar,btnStop,btnadd;//��ʼ��ֹͣ������
	@SuppressWarnings("rawtypes")
	JComboBox comboBox;//�б�ѭ��
	PlayMusic play;//��������������
	String old;//��¼�ɵ�·��
	@SuppressWarnings("rawtypes")
	DefaultListModel name_dlm = new DefaultListModel();//����ģ��
	@SuppressWarnings("rawtypes")
	DefaultListModel path_dlm = new DefaultListModel();//����·��ģ��
	private JButton btnclearlist;//����б�
	private JButton btndel;//ɾ������
	
	//��С��������
	private TrayIcon trayIcon;//����ͼ��
	 
	//logo��Ƭ
    String filePath = "/com/soft/music.png";
	 
    //��С���б�
    PopupMenu pmenu = new PopupMenu();       //����ͼ��ĵ���ʽ�˵�
	MenuItem openmenu = new MenuItem("��");  //�����˵��Ĵ�ѡ��
	MenuItem closemenu = new MenuItem("�ر�");  //�����˵��Ĺر�ѡ��
	MenuItem startmenu = new MenuItem("��ʼ/��ͣ");  //�����˵��Ĵ�ѡ��
	MenuItem nextmenu = new MenuItem("��һ��");  //�����˵��Ĵ�ѡ��
	MenuItem lastmenu = new MenuItem("��һ��");  //�����˵��Ĵ�ѡ��
	public SystemTray systemtray = SystemTray.getSystemTray();  //�õ���ǰϵͳ����
	Image image = Toolkit.getDefaultToolkit().getImage("music.png");//
	
	int selectedindex = 0;//�洢�б�ѡ����±�
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox_1;//�ж��Ƿ񲥷Ž���
	private Timer timer = new Timer(0, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(comboBox_1.getSelectedIndex()==1) {
				int loop_index = comboBox.getSelectedIndex();
				int list_size = list.getModel().getSize();
				if(loop_index == 0) {//�б�ѭ��
					if(selectedindex>=(list_size-1)){
						list.setSelectedIndex(0);
						pathlist.setSelectedIndex(0);
					}else {
						list.setSelectedIndex(selectedindex+1);
						pathlist.setSelectedIndex(selectedindex+1);
					}
				}else if(loop_index == 1) {//����ѭ��
					list.setSelectedIndex(selectedindex);
					pathlist.setSelectedIndex(selectedindex);
				}else if(loop_index == 2) {//�������
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
			if(btnStar.getText() == "��ͣ"||btnStar.getText().equals("��ͣ")) {//�ж��Ƿ��ڲ���״̬
				//�޸�����logo��tip
				nowmusic = name_dlm.getElementAt(selectedindex)+"";
				trayIcon.setToolTip("΢�����ֲ���������cyw\n���ڲ��ţ�"+nowmusic);
			}else {
				trayIcon.setToolTip("΢�����ֲ���������cyw");
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
		
		//����logo
		URL logourl = getClass().getResource(filePath);
		final Image logoim = new ImageIcon(logourl).getImage();
		setIconImage(logoim);
		
		//������С��������
		 try {
	          trayIcon = new TrayIcon(image, "΢�����ֲ���������cyw", pmenu);
	          trayIcon.setImageAutoSize(true); // ����ϵͳͼ���С�Զ���
	          trayIcon.addMouseListener(new MouseAdapter() {
		             public void mouseClicked(MouseEvent e) {
		            	 if (e.getClickCount() == 2) {//˫�����̴�������
		            		 setExtendedState(Frame.NORMAL);
		            		 setVisible(true);
		                  }
		             }
		         });
	          systemtray.add(trayIcon); // ��ͼ����ӵ�ϵͳ����
	          
	          //����ǰѡ��֮ǰ�±�
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
	            	//����ǰѡ��֮ǰ�±�
	            	 if(name_dlm.isEmpty()==false) {
	   	        	  	list.setSelectedIndex(selectedindex);
	   	        	  	pathlist.setSelectedIndex(selectedindex);
	            	 }
	            	 dispose();//������С��ʱdispose�ô���
	             }*/
	             @Override
	            public void windowClosing(WindowEvent e) {
	            	// TODO Auto-generated method stub
	            	 if(isMin) {//�ж��Ƿ��һ�ε���رղ���
	            		 JOptionPane.showMessageDialog(null, "<html><font color=red>˫������ǿؼ�λ�ÿɹرմ��ڣ�����</font></html>");
	            		 isMin = false;
	            	 }
	            	//����ǰѡ��֮ǰ�±�
	            	 if(name_dlm.isEmpty()==false) {
		   	        	  list.setSelectedIndex(selectedindex);
		   	        	  pathlist.setSelectedIndex(selectedindex);
		             }
	            	dispose();
	            }
	        });
	        
	        //��С�������б����
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
			pmenu.add(openmenu);            //�ڵ���ʽ�˵��������˵���
			pmenu.add(closemenu);
			pmenu.add(startmenu);
			pmenu.add(lastmenu);
			pmenu.add(nextmenu);

		
		
		this.setLocationRelativeTo(null);//������ʾ
        this.setResizable(false);
        timer.start();
        //setUndecorated(true);//ȥ�߿�
        //this.setAlwaysOnTop(true);//�ö�
		setTitle("\u5FAE\u578B\u97F3\u4E50\u64AD\u653E\u5668");
		movejframe();
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnStar = new JButton("��ʼ");
		btnStar.setBounds(5, 10, 104, 37);
		btnStar.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
		contentPane.add(btnStar);
		btnStar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		btnStop = new JButton("ֹͣ");
		btnStop.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
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
		btnadd.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
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
		btnclearlist.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
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
		btndel.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
		btndel.setBounds(5, 198, 104, 37);
		btndel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btndel);
		
		comboBox = new JComboBox();
		comboBox.setBounds(5, 244, 104, 27);
		comboBox.addItem("�б�ѭ��");
		comboBox.addItem("����ѭ��");
		comboBox.addItem("�������");
		comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(comboBox);
		
		btnlast = new JButton("\u4E0A\u4E00\u66F2");
		btnlast.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
		btnlast.setBounds(5, 104, 104, 37);
		btnlast.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPane.add(btnlast);
		
		btnNext = new JButton("\u4E0B\u4E00\u66F2");
		btnNext.setFont(new Font("���Ŀ���", Font.PLAIN, 20));
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
	
	// �����ק�ƶ�����
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
	 * ˫���ǿؼ�����رմ���
	 * @author cyw
	 *
	 */
	class Dbclick extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int clickTimes = e.getClickCount();
		    if (clickTimes == 2) {
		      if(JOptionPane.showConfirmDialog(null,"ȷ���رճ�����","ѯ��", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
		    	  //dispatchEvent(new WindowEvent(new login(), WindowEvent.WINDOW_CLOSING));
		    	  System.exit(0); 
		    }
		}
	}
	
	//�б���˫��
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
					btnStar.setText("��ͣ");
				}else if(flag == false&&old != filename){
					old = filename;
					play.stop();
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("��ͣ");
				}else if(flag == false&&old == filename){
					if(play.isComplete()) {
						play.stop();
						old = filename;
						play = new PlayMusic(filename,btnStar,comboBox_1);
						flag = false;
						play.start();
						btnStar.setText("��ͣ");
					}else {
						JOptionPane.showMessageDialog(null, "<html><font color=red>����������</font></html>");
					}
				}
		    }
		}
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==btnadd) {//��������ӵ��б�
			selectedindex = 0;//Ĭ���±�Ϊ0
			JFileChooser filechooser = new JFileChooser("C:\\Users\\Administrator\\Desktop");//�ļ�ѡ����Ĭ�ϴ򿪵�ַ
			filechooser.setDialogTitle("��ѡ�����ֲ���·����");//�ļ�ѡ��������
			FileFilter filter1 = new FileNameExtensionFilter("*.mp3", "mp3");//�ļ���ʽ����
			filechooser.setFileFilter(filter1);
			filechooser.setMultiSelectionEnabled(true);
			int i = filechooser.showOpenDialog(null);
			if(i == filechooser.APPROVE_OPTION) {//���ȷ�������
				File[]  f= filechooser.getSelectedFiles(); //��ȡ��ѡ���ֵ
				String abpath,name;
				for (int z = 0; z < f.length; z++) {
					abpath = f[z].getAbsolutePath();
					name = f[z].getName();
					path_dlm.addElement(abpath);
					name_dlm.addElement(name.substring(0, name.length()-4));//ɾȥ.mp3
				}
				
				//���ģ��
				list.setModel(name_dlm);
				pathlist.setModel(path_dlm);
				
				//Ĭ��ѡ��һ��
				list.setSelectedIndex(0);
				pathlist.setSelectedIndex(0);
			}
		}else if(e.getSource() == btnStar) {//��ʼ��ť����
			String filename = (String) pathlist.getSelectedValue();//��ø���·��
			if(filename!=null) {
				selectedindex = pathlist.getSelectedIndex();
				if(flag == true&&btnStar.getText()=="��ʼ") {
					old = filename;
					play = new PlayMusic(filename,btnStar,comboBox_1);//��Ҫ���Ĳ�������������
					flag = false;
					play.start();//��ʼ�߳�
					btnStar.setText("��ͣ");
				}else if(flag == false&&btnStar.getText()=="��ʼ"&&old != filename){
					old = filename;
					play.stop();
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("��ͣ");
				}else if(flag == false&&btnStar.getText()=="��ʼ"&&old == filename){
					if(play.isComplete()) {
						play.stop();
						old = filename;
						play = new PlayMusic(filename,btnStar,comboBox_1);
						flag = false;
						play.start();
						btnStar.setText("��ͣ");
					}else {
						play.resume();//�ָ̻߳�
						btnStar.setText("��ͣ");
					}
				}else if(btnStar.getText()=="��ͣ") {
					play.suspend();//�̹߳��𼴸�����ͣ
					btnStar.setText("��ʼ");
				}
				//�б�λ��ѡ��λ��
				Rectangle rectangle = list.getCellBounds(selectedindex, selectedindex);
				list.scrollRectToVisible(rectangle);
			}else {
				if(list.getModel().getSize()>0) {//�޸����ж�
					selectedindex = 0;
					filename = list.getModel().getElementAt(0)+"";
					list.setSelectedIndex(0);
					pathlist.setSelectedIndex(0);
					old = filename;
					play = new PlayMusic(filename,btnStar,comboBox_1);
					flag = false;
					play.start();
					btnStar.setText("��ͣ");
				}else {
					JOptionPane.showMessageDialog(null, "<html><font color=red>������Ӳ�����Ŀ</font></html>");
				}
					
			}
		}else if(e.getSource() == btnStop) {//ֹͣ��ť����,��һ������
			selectedindex = 0;
			play.stop();
			btnStar.setText("��ʼ");
			flag = true;
			old="";
		}else if(e.getSource() == btnlast) {//��һ����ť����
			selectedindex = pathlist.getSelectedIndex();//��ȡ����·��
			int loop_index = comboBox.getSelectedIndex();//��ȡѭ��������
			int list_size = list.getModel().getSize();//�õ�ģ�͵Ĵ�С
			btnStar.setText("��ʼ");
			if(loop_index == 2){//�������
				Random r = new Random();
				int index = r.nextInt(list_size);
				if(index==selectedindex)//�������ͬһ�׸�
					index = r.nextInt(list_size);
				pathlist.setSelectedIndex(index);
				list.setSelectedIndex(index);
			}else{
				if(selectedindex>0) {//�±���ڵ���1ʱ������
					list.setSelectedIndex(selectedindex-1);
					pathlist.setSelectedIndex(selectedindex-1);
				}
			}
			btnStar.doClick();
		}else if(e.getSource() == btnNext) {//��һ����ť����
			selectedindex = pathlist.getSelectedIndex();//��ȡ·��
			int size = list.getModel().getSize();//��ȡģ�ʹ�С
			btnStar.setText("��ʼ");
			if(size>1) {//������һ�׸�ʱ��һ��������
				int loop_index = comboBox.getSelectedIndex();
				int list_size = list.getModel().getSize();
				if(loop_index == 2){//�������
					Random r = new Random();
					int index = r.nextInt(list_size);
					if(index==selectedindex)//�������ͬһ�׸�
						index = r.nextInt(list_size);
					pathlist.setSelectedIndex(index);
					list.setSelectedIndex(index);
				}else{
					if(selectedindex<(size-1)) {//�����һ��ʱ��ǰ��һ��
						pathlist.setSelectedIndex(selectedindex+1);
						list.setSelectedIndex(selectedindex+1);
					}
				}
				btnStar.doClick();
			}
		}
	}
	
	/**
	 * ��С���б����
	 * @author cyw
	 *
	 */
	class MenuListen implements ActionListener {
		public JFrame jf = null;

		public MenuListen(JFrame jf) {//���캯��
			this.jf = jf;
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("�ر�")) {
				if(JOptionPane.showConfirmDialog(null,"ȷ���رճ�����","ѯ��", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					System.exit(0); //�رմ���
			} else
				jf.setVisible(true);//��ʾ����
		}

	}
	
	

}
