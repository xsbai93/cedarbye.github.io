package WordSegmentFrame;
import WordSegment.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.File;
import java.util.Vector;

import javax.swing.*;

public class WordSegFrame extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static int ALGO_FMM = 1;
	final static int ALGO_BMM = 2;
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem openDicItem, closeItem;
	private JRadioButtonMenuItem fmmItem, bmmItem;
	private JMenuItem openTrainFileItem, saveDicItem, aboutItem;
	private JButton btSeg;
	private JTextField tfInput;
	private JTextArea taOutput;
	//private JPanel panel;
	JLabel infoDic, infoAlgo;
	private WordSegment seger;
	private DicAdder adder = new DicAdder();

	private void initFrame()
	{
		setTitle("匹配分词");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("文件");
		JMenu algorithmMenu =  new JMenu("分词算法");
		JMenu trainMenu =  new JMenu("添加词库");
		JMenu helpMenu =  new JMenu("关于");
		openDicItem = fileMenu.add("载入词库");
		fileMenu.addSeparator();
		closeItem = fileMenu.add("退出");
		
		algorithmMenu.add(fmmItem = new JRadioButtonMenuItem("正向最大匹配", true));
		algorithmMenu.add(bmmItem = new JRadioButtonMenuItem("逆向最大匹配", false));
		ButtonGroup algorithms = new ButtonGroup();
		algorithms.add(fmmItem);
		algorithms.add(bmmItem);
		
		openTrainFileItem = trainMenu.add("载入词库");
		saveDicItem = trainMenu.add("保存词库");
		
		aboutItem = helpMenu.add("关于Java中文分词");		
		
		//menuBar.add(fileMenu);
		menuBar.add(algorithmMenu);
		menuBar.add(trainMenu);
		menuBar.add(helpMenu);
		openDicItem.addActionListener(this);
		closeItem.addActionListener(this);
		openTrainFileItem.addActionListener(this);
		saveDicItem.addActionListener(this);
		aboutItem.addActionListener(this);	
		fmmItem.addActionListener(this);
		bmmItem.addActionListener(this);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout());
		JPanel bottomPanel = new JPanel();
		this.getContentPane().add(topPanel, BorderLayout.NORTH);
	    this.getContentPane().add(centerPanel, BorderLayout.CENTER);
	    this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		btSeg = new JButton("分词");
		tfInput = new JTextField("", 30);
		taOutput = new JTextArea();
		topPanel.add(tfInput);
		topPanel.add(btSeg);
		
		centerPanel.add(taOutput);
		
		infoDic = new JLabel();
		infoAlgo = new JLabel();
		bottomPanel.add(infoDic);
		bottomPanel.add(infoAlgo);
		saveDicItem.setEnabled(false);
		btSeg.addActionListener(this);
	}
	
	public WordSegFrame() 
	{ 
		initFrame();		
		seger = new WordSegment();
		loadDic("dic.dat");
		setAlgo(ALGO_FMM);		
	} 
	
	private void loadDic(String fileName)
	{
		seger.SetDic(fileName);
		infoDic.setText("词典 " + fileName + "已载入");
	}
	
	private void setAlgo(int type)
	{
		String algo = null;
		switch(type)
		{
		case ALGO_FMM:
			seger.setStrategy(new FMM());
			algo = "FMM";
			break;
		case ALGO_BMM:
			seger.setStrategy(new BMM());
			algo = "BMM";
			break;
		}
		infoAlgo.setText("分词算法：" + algo);
	}
	
	private File openFile()
	{
		JFileChooser chooser = new JFileChooser();
        int ret = chooser.showOpenDialog(this);

        if (ret != JFileChooser.APPROVE_OPTION) {
        	return null;
        }

        File f = chooser.getSelectedFile();
        if (f.isFile() && f.canRead()) 
        {
        	return f;
        } 
        else 
        {
            JOptionPane.showMessageDialog(this,
                    "Could not open file: " + f,
                    "Error opening file",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
	}
	
	private void addDic(File f)
	{
		adder.Adder(f.getAbsolutePath());
		seger.SetDic(adder.getDic());
		infoDic.setText("添加完成，词库已载入");		
		saveDicItem.setEnabled(true);
	}
	
	private void saveDic()
	{
		 JFileChooser chooser = new JFileChooser();
         int ret = chooser.showSaveDialog(this);

         if (ret != JFileChooser.APPROVE_OPTION) {
             return;
         }

         File f = chooser.getSelectedFile();
         adder.SaveDic(f.getAbsolutePath());
         infoDic.setText("词典已保存到" + f.getAbsolutePath());
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == openDicItem)
		{
			File dicFile = openFile();
			if(dicFile == null)
				return;
			loadDic(dicFile.getAbsolutePath());
			saveDicItem.setEnabled(false);
			return;
		}
		if(e.getSource() == closeItem)
		{
			dispose();
			System.exit(0);
			return;
		}
		if(e.getSource() == openTrainFileItem)
		{
			File trainFile = openFile();
			if(trainFile == null)
				return;
			else
				addDic(trainFile);
			return;
		}
		if(e.getSource() == saveDicItem)
		{
			saveDic();
			return;
		}
		if(e.getSource() == aboutItem)
		{
			JOptionPane.showMessageDialog(this, "demo", "Java中文分词", 
                    JOptionPane.INFORMATION_MESSAGE);

			return;
		}
		if(e.getSource() == fmmItem)
		{
			setAlgo(ALGO_FMM);
			return;
		}
		if(e.getSource() == bmmItem)
		{
			setAlgo(ALGO_BMM);
			return;
		}
		if(e.getSource() == btSeg)
		{
			String sentence = tfInput.getText();
			Vector<String> vec = seger.Segment(sentence);
			taOutput.setText("");
			for(int i=0;i<vec.size();i++)
				taOutput.append(vec.get(i) + "  ");
			return;
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WordSegFrame window = new WordSegFrame();
		Toolkit theKit = window.getToolkit();
		Dimension wndSize = theKit.getScreenSize();
		
		window.setBounds(wndSize.width/4, wndSize.height/4,
						wndSize.width/2, wndSize.height/2);
		window.setVisible(true);		
	}
}

