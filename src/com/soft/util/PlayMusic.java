package com.soft.util;

import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javazoom.jl.player.Player;

public class PlayMusic{
	private Player player;
	private String filename;
	JButton btn;
	String old;
	@SuppressWarnings("rawtypes")
	JComboBox comboBox;
	boolean flag=false;
	Thread r = new R();
	//private String[] str = {"杨坤,郭采洁 - 答案.mp3","许嵩 - 你若成风.mp3","纸短情长（女声完整版）-纸短情长（女声完整版）.mp3","纸短情长-粤语-纸短情长-粤语.mp3"};
	
	public PlayMusic(String filename,JButton btn,JComboBox comboBox) {
		// TODO Auto-generated constructor stub
		this.filename = filename;
		this.btn = btn;
		this.comboBox = comboBox;
	}
	public PlayMusic() {
		// TODO Auto-generated constructor stub
	}
	
	public static AudioClip loadSound(String filename) {
		URL url = null;
		try {
			String src = "file:" + filename;
			System.out.println(src);
			url = new URL(src);
		} 
		catch (MalformedURLException e) {;}
		return JApplet.newAudioClip(url);
	}
	
	public void play() {
        try {
        	comboBox.setSelectedIndex(0);
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(filename));
            System.out.println("播放音乐："+filename+"-----"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            player = new Player(buffer);
            player.play();
            if(player.isComplete()==true) {
            	flag=true;
            	System.out.println("-----------"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"-------------");
            	close();
            	btn.setText("开始");
            	comboBox.setSelectedIndex(1);
            	/*Random r = new Random();
            	play("E:\\CloudMusic\\"+str[r.nextInt(str.length)]);*/
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
	public void close() {
		player.close();
	}
	public void start() {
		r.start();
	}
	@SuppressWarnings("deprecation")
	public void stop() {
		r.stop();
	}
	@SuppressWarnings("deprecation")
	public void suspend() {
		r.suspend();
	}
	@SuppressWarnings("deprecation")
	public void resume() {
		r.resume();
	}
	public boolean isComplete(){
		return flag;
	}
	
	class R extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			play();
		}
	}
}
