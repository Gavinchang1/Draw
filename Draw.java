package draw;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class QingDrawPad extends JFrame // 主类，扩展了JFrame类，用来生成主界面
{
	private ObjectInputStream input;
	private ObjectOutputStream output; 

	private JButton choices[]; 

	private String names[] = { "New", "Open", "Save", 
			"Pencil", "Line", "Rect", "FRect", "Oval", "FOval", "Circle", "FCircle", "RoundRect", "FrRect", "Rubber",
			"Color", "Stroke", "Word" };

	private String styleNames[] = { " 宋体 ", " 隶书 ", " 华文彩云 ", " 仿宋_GB2312 ", " 华文行楷 ", " 方正舒体 ", " Times New Roman ",
			" Serif ", " Monospaced ", " SonsSerif ", " Garamond " };

	private Icon items[];

	private String tipText[] = {
			"新建一个文件", "打开一个文件", "保存当前文件", "绘制线条", "绘制直线", "绘制空心矩形", "绘制实心矩形", "绘制空心椭圆", "绘制实心椭圆", "绘制空心圆形", "绘制实心圆形",
			"绘制空心圆角矩形", "绘制实心圆角矩形", "橡皮擦", "选择颜色", "设置线条粗细", "输入文字" };

	JToolBar buttonPanel; // 定义按钮面板

	private DrawPanel drawingArea; // 画图区域
	private int width = 850, height = 550;

	drawings[] itemList = new drawings[5000]; 
	private int currentChoice = 3; 
	int index = 0; 
	private Color color = Color.black;
	int R, G, B;

	int f1, f2; 
	String style1;
	private float stroke = 1.0f;

	JCheckBox bold, italic;
	
	public QingDrawPad() {
		super("画图板");
		JMenuBar bar = new JMenuBar(); // 定义菜单条
		JMenu fileMenu = new JMenu("文件");
			// 新建文件菜单条
		JMenuItem newItem = new JMenuItem("新建");
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile(); 
			}
		});
		fileMenu.add(newItem);

		JMenuItem saveItem = new JMenuItem("保存");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(); // 如果被触发，则调用保存文件函数段
			}
		});
		fileMenu.add(saveItem);

		JMenuItem loadItem = new JMenuItem("打开");
		loadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		fileMenu.add(loadItem);

		fileMenu.addSeparator();

		JMenuItem exitItem = new JMenuItem("退出");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		bar.add(fileMenu);

		JMenu colorMenu = new JMenu("颜色");

		JMenuItem colorItem = new JMenuItem("选择颜色");
		colorItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseColor();
			}
		});
		colorMenu.add(colorItem);
		bar.add(colorMenu);

		JMenu strokeMenu = new JMenu("线条粗细");

		JMenuItem strokeItem = new JMenuItem("设置线条粗细");
		strokeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStroke();
			}
		});
		strokeMenu.add(strokeItem);
		bar.add(strokeMenu);

		
		items = new ImageIcon[names.length];

		drawingArea = new DrawPanel();
		choices = new JButton[names.length];
		buttonPanel = new JToolBar(JToolBar.VERTICAL);
		buttonPanel = new JToolBar(JToolBar.HORIZONTAL);
		ButtonHandler handler = new ButtonHandler();
		ButtonHandler1 handler1 = new ButtonHandler1();

		for (int i = 0; i < choices.length; i++) {
			items[i] = new ImageIcon("./pic/" + names[i] + ".gif");
			choices[i] = new JButton("", items[i]);
			choices[i].setToolTipText(tipText[i]);
			buttonPanel.add(choices[i]);
		}

		for (int i = 3; i < choices.length - 3; i++) {
			choices[i].addActionListener(handler);
		}

		choices[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});

		choices[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});

		choices[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		choices[choices.length - 3].addActionListener(handler1);
		choices[choices.length - 2].addActionListener(handler1);
		choices[choices.length - 1].addActionListener(handler1);

		bold = new JCheckBox("粗体");
		italic = new JCheckBox("斜体");

		checkBoxHandler cHandler = new checkBoxHandler();
		bold.addItemListener(cHandler);
		italic.addItemListener(cHandler);

		JPanel wordPanel = new JPanel();
		buttonPanel.add(bold);
		buttonPanel.add(italic);

		Container c = getContentPane();
		super.setJMenuBar(bar);
		c.add(buttonPanel, BorderLayout.NORTH);
		c.add(drawingArea, BorderLayout.CENTER);


		createNewItem();
		setSize(width, height);
		show();
	}

	public class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int j = 3; j < choices.length - 3; j++) {
				if (e.getSource() == choices[j]) {
					currentChoice = j;
					createNewItem();
					repaint();
				}
			}
		}
	}

	public class ButtonHandler1 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == choices[choices.length - 3]) {
				chooseColor();
			}
			if (e.getSource() == choices[choices.length - 2]) {
				setStroke();
			}
			if (e.getSource() == choices[choices.length - 1]) {
				JOptionPane.showMessageDialog(null, "请点击绘图板选择输入文本的位置", "提示", JOptionPane.INFORMATION_MESSAGE);
				currentChoice = 14;
				createNewItem();
				repaint();
			}
		}
	}

	class mouseA extends MouseAdapter {
		public void mousePressed(MouseEvent e) {

			itemList[index].x1 = itemList[index].x2 = e.getX();
			itemList[index].y1 = itemList[index].y2 = e.getY();

			if (currentChoice == 3 || currentChoice == 13) {
				itemList[index].x1 = itemList[index].x2 = e.getX();
				itemList[index].y1 = itemList[index].y2 = e.getY();
				index++;
				createNewItem();
			}

			if (currentChoice == 14) {
				itemList[index].x1 = e.getX();
				itemList[index].y1 = e.getY();

				String input;
				input = JOptionPane.showInputDialog("请输入你想要输入的文本");
				itemList[index].s1 = input;
				itemList[index].x2 = f1;
				itemList[index].y2 = f2;
				itemList[index].s2 = style1;

				index++;
				currentChoice = 14;
				createNewItem();
				drawingArea.repaint();
			}
		}

		public void mouseReleased(MouseEvent e) {

			if (currentChoice == 3 || currentChoice == 13) {
				itemList[index].x1 = e.getX();
				itemList[index].y1 = e.getY();
			}
			itemList[index].x2 = e.getX();
			itemList[index].y2 = e.getY();
			repaint();
			index++;
			createNewItem();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	class mouseB extends MouseMotionAdapter {
		public void mouseDragged(MouseEvent e) {

			if (currentChoice == 3 || currentChoice == 13) {
				itemList[index - 1].x1 = itemList[index].x2 = itemList[index].x1 = e.getX();
				itemList[index - 1].y1 = itemList[index].y2 = itemList[index].y1 = e.getY();
				index++;
				createNewItem();
			} else {
				itemList[index].x2 = e.getX();
				itemList[index].y2 = e.getY();
			}
			repaint();
		}

		public void mouseMoved(MouseEvent e) {
		}
	}

	private class checkBoxHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == bold)
				if (e.getStateChange() == ItemEvent.SELECTED)
					f1 = Font.BOLD;
				else
					f1 = Font.PLAIN;
			if (e.getSource() == italic)
				if (e.getStateChange() == ItemEvent.SELECTED)
					f2 = Font.ITALIC;
				else
					f2 = Font.PLAIN;
		}
	}

	class DrawPanel extends JPanel {
		public DrawPanel() {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			setBackground(Color.white);
			addMouseListener(new mouseA());
			addMouseMotionListener(new mouseB());
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;

			int j = 0;
			while (j <= index) {
				draw(g2d, itemList[j]);
				j++;
			}
		}

		void draw(Graphics2D g2d, drawings i) {
			i.draw(g2d);	}
	}

	// 新建一个画图基本单元对象的程序段
	void createNewItem() {
		if (currentChoice == 14)
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		else
			drawingArea.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		switch (currentChoice) {
		case 3:
			itemList[index] = new Pencil();
			break;
		case 4:
			itemList[index] = new Line();
			break;
		case 5:
			itemList[index] = new Rect();
			break;
		case 6:
			itemList[index] = new fillRect();
			break;
		case 7:
			itemList[index] = new Oval();
			break;
		case 8:
			itemList[index] = new fillOval();
			break;
		case 9:
			itemList[index] = new Circle();
			break;
		case 10:
			itemList[index] = new fillCircle();
			break;
		case 11:
			itemList[index] = new RoundRect();
			break;
		case 12:
			itemList[index] = new fillRoundRect();
			break;
		case 13:
			itemList[index] = new Rubber();
			break;
		case 14:
			itemList[index] = new Word();
			break;
		}
		itemList[index].type = currentChoice;
		itemList[index].R = R;
		itemList[index].G = G;
		itemList[index].B = B;
		itemList[index].stroke = stroke;
	}

	// 选择当前颜色程序段
	public void chooseColor() {
		color = JColorChooser.showDialog(QingDrawPad.this, "请选择一种颜色", color); // 自带调色器
		R = color.getRed();
		G = color.getGreen();
		B = color.getBlue();
		itemList[index].R = R;
		itemList[index].G = G;
		itemList[index].B = B;
	}

	// 选择当前线条粗细程序段
	public void setStroke() {
		String input;
		input = JOptionPane.showInputDialog("请输入一个浮点型线条粗细值 ( >0 )");
		stroke = Float.parseFloat(input);
		itemList[index].stroke = stroke;
	}

	// 保存图形文件程序段
	public void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		File fileName = fileChooser.getSelectedFile();
		fileName.canWrite();

		if (fileName == null || fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser, "文件名无效", "文件名无效", JOptionPane.ERROR_MESSAGE);
		else {
			try {
				fileName.delete();
				FileOutputStream fos = new FileOutputStream(fileName);

				output = new ObjectOutputStream(fos);
				drawings record;

				output.writeInt(index);

				for (int i = 0; i < index; i++) {
					drawings p = itemList[i];
					output.writeObject(p);
					output.flush();
					}
				output.close();
				fos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void loadFile() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		File fileName = fileChooser.getSelectedFile();
		fileName.canRead();
		if (fileName == null || fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser, "文件名无效", "文件名无效", JOptionPane.ERROR_MESSAGE);
		else {
			try {

				FileInputStream fis = new FileInputStream(fileName);

				input = new ObjectInputStream(fis);
				drawings inputRecord;

				int countNumber = 0;
				countNumber = input.readInt();

				for (index = 0; index < countNumber; index++) {
					inputRecord = (drawings) input.readObject();
					itemList[index] = inputRecord;

				}

				createNewItem();
				input.close();

				repaint();
			} catch (EOFException endofFileException) {
				JOptionPane.showMessageDialog(this, "文件里没有更多的记录", "无法找到类", JOptionPane.ERROR_MESSAGE);
			} catch (ClassNotFoundException classNotFoundException) {
				JOptionPane.showMessageDialog(this, "不能创建对象", "文件结束", JOptionPane.ERROR_MESSAGE);
			} catch (IOException ioException) {
				JOptionPane.showMessageDialog(this, "从文件读取资料出错", "读出错误", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// 新建一个文件程序段
	public void newFile() {
		index = 0;
		currentChoice = 3;
		color = Color.black;
		stroke = 1.0f;
		createNewItem();
		repaint();
	}

	// 主函数段
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		} 
		QingDrawPad newPad = new QingDrawPad();
		newPad.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}

// 定义画图的基本图形单元
class drawings implements Serializable
{
	int x1, y1, x2, y2; 
	int R, G, B; 
	float stroke;
	int type; 
	String s1;
	String s2;

	void draw(Graphics2D g2d) {
	};
}


class Line extends drawings // 直线类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}
}

class Rect extends drawings// 矩形类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class fillRect extends drawings// 实心矩形类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class Oval extends drawings// 椭圆类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class fillOval extends drawings// 实心椭圆
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class Circle extends drawings// 圆类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)),
				Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)));
	}
}

class fillCircle extends drawings// 实心圆
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)),
				Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)));
	}
}

class RoundRect extends drawings// 圆角矩形类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);
	}
}

class fillRoundRect extends drawings// 实心圆角矩形类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);
	}
}

class Pencil extends drawings// 铅笔类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}
}

class Rubber extends drawings// 橡皮擦类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(255, 255, 255));
		g2d.setStroke(new BasicStroke(stroke + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}
}

class Word extends drawings// 输入文字类
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setFont(new Font(s2, x2 + y2, ((int) stroke) * 18));
		if (s1 != null)
			g2d.drawString(s1, x1, y1);
	}
}
