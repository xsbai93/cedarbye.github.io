import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: </p>
 * @author cedar
 * @version 1.0
 */

public class Frame2 extends JFrame {
  JPanel contentPane;
  JPanel jPanel1 = new JPanel();
  JTextArea ta1 = new JTextArea();
  JTextField fname = new JTextField();
  JButton getFile = new JButton();
  JPanel jPanel2 = new JPanel();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;
  JTextArea ta2 = new JTextArea();
  JButton train = new JButton();
  JButton singleTest = new JButton();
  Dictionary dic=new Dictionary("SDIC.txt");
  Traning t=new Traning(dic);
  TargetText tt=new TargetText();
  String fName;

  //Construct the frame
  public Frame2() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  //Component initialization
  private void jbInit() throws Exception  {
    contentPane = (JPanel) this.getContentPane();
    titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(164, 163, 165)),"系统说明");
    titledBorder2 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(164, 163, 165)),"结果显示");
    contentPane.setLayout(null);
    this.setSize(new Dimension(410, 470));
    this.setTitle("NBC文本分类器");
    jPanel1.setBorder(titledBorder1);
    jPanel1.setToolTipText("");
    jPanel1.setBounds(new Rectangle(12, 8, 385, 190));
    jPanel1.setLayout(null);
    ta1.setEnabled(false);
    ta1.setText("本文本分类器基于朴素贝叶斯算法，支持对中文文本分类，\n该版本下仅设置了3个类别用以测试分类器性能，分别历史，体育，\n科学。\n操作说明：\n首先点击“训练”按钮训练分类器，然后打开需要进行分类的测试文件\n(测试文件必须为TXT)，点击“分类”按钮完成分类，分类结果将\n显示在结果显示框中。(训练时需要等待片刻)");
    ta1.setBounds(new Rectangle(5, 17, 374, 159));
    contentPane.setPreferredSize(new Dimension(411, 388));
    fname.setEnabled(false);
    fname.setText("");
    fname.setBounds(new Rectangle(13, 211, 293, 23));
    fname.addActionListener(new Frame2_fname_actionAdapter(this));
    getFile.setBounds(new Rectangle(316, 210, 81, 24));
    getFile.setEnabled(false);
    getFile.setRolloverEnabled(false);
    getFile.setText("打开文件");
    getFile.addActionListener(new Frame2_getFile_actionAdapter(this));
    jPanel2.setBorder(titledBorder2);
    jPanel2.setToolTipText("");
    jPanel2.setBounds(new Rectangle(13, 291, 385, 108));
    jPanel2.setLayout(null);
    ta2.setEnabled(false);
    ta2.setText("");
    ta2.setBounds(new Rectangle(8, 21, 368, 68));
    train.setBounds(new Rectangle(12, 248, 82, 24));
    train.setText("训练");
    train.addActionListener(new Frame2_train_actionAdapter(this));
    singleTest.setBounds(new Rectangle(315, 247, 81, 23));
    singleTest.setEnabled(false);
    singleTest.setFocusPainted(true);
    singleTest.setText("分类");
    singleTest.addActionListener(new Frame2_singleTest_actionAdapter(this));
    contentPane.add(jPanel1, null);
    jPanel1.add(ta1, null);
    contentPane.add(getFile, null);
    contentPane.add(fname, null);
    contentPane.add(jPanel2, null);
    jPanel2.add(ta2, null);
    contentPane.add(singleTest, null);
    contentPane.add(train, null);
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void fname_actionPerformed(ActionEvent e) {

  }

  void getFile_actionPerformed(ActionEvent e) {
    FileDialog fd=new FileDialog(this,"打 开 文 件",FileDialog.LOAD);
    fd.setVisible(true);
    if(fd.getFile()!=null){
      String s=fd.getDirectory()+fd.getFile();
      fname.setText(s);
      fName=fd.getFile();
      singleTest.setEnabled(true);
    }else{
      fname.setText("");
    }
  }

  void train_actionPerformed(ActionEvent e) {
    t.loadVocabulary();
    ta2.setText("样本训练结束");

    getFile.setEnabled(true);

    train.setEnabled(false);

    try{
      BufferedWriter out=new BufferedWriter(new FileWriter("vocabulary.txt"));
      out.write(new String(""),0,0);
      out.flush();
    }catch(Exception el){}

  }

  void singleTest_actionPerformed(ActionEvent e) {
    tt.init(dic);
    String s=fname.getText();
    String r;
    if(s!=""){
      r=fName+" belongs to "+tt.categorize(t.v, 3, s);
      ta2.setText(r);
      singleTest.setEnabled(false);
    }
  }
}

class Frame2_fname_actionAdapter implements java.awt.event.ActionListener {
  Frame2 adaptee;

  Frame2_fname_actionAdapter(Frame2 adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.fname_actionPerformed(e);
  }
}

class Frame2_getFile_actionAdapter implements java.awt.event.ActionListener {
  Frame2 adaptee;

  Frame2_getFile_actionAdapter(Frame2 adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.getFile_actionPerformed(e);
  }
}

class Frame2_train_actionAdapter implements java.awt.event.ActionListener {
  Frame2 adaptee;

  Frame2_train_actionAdapter(Frame2 adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.train_actionPerformed(e);
  }
}

class Frame2_singleTest_actionAdapter implements java.awt.event.ActionListener {
  Frame2 adaptee;

  Frame2_singleTest_actionAdapter(Frame2 adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.singleTest_actionPerformed(e);
  }
}
