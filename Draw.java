package draw;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class QingDrawPad extends JFrame // ���࣬��չ��JFrame�࣬��������������
{
	private ObjectInputStream input;
	private ObjectOutputStream output; 

	private JButton choices[]; 

	private String names[] = { "New", "Open", "Save", 
			"Pencil", "Line", "Rect", "FRect", "Oval", "FOval", "Circle", "FCircle", "RoundRect", "FrRect", "Rubber",
			"Color", "Stroke", "Word" };

	private String styleNames[] = { " ���� ", " ���� ", " ���Ĳ��� ", " ����_GB2312 ", " �����п� ", " �������� ", " Times New Roman ",
			" Serif ", " Monospaced ", " SonsSerif ", " Garamond " };

	private Icon items[];

	private String tipText[] = {
			"�½�һ���ļ�", "��һ���ļ�", "���浱ǰ�ļ�", "��������", "����ֱ��", "���ƿ��ľ���", "����ʵ�ľ���", "���ƿ�����Բ", "����ʵ����Բ", "���ƿ���Բ��", "����ʵ��Բ��",
			"���ƿ���Բ�Ǿ���", "����ʵ��Բ�Ǿ���", "��Ƥ��", "ѡ����ɫ", "����������ϸ", "��������" };

	JToolBar buttonPanel; // ���尴ť���

	private DrawPanel drawingArea; // ��ͼ����
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
		super("��ͼ��");
		JMenuBar bar = new JMenuBar(); // ����˵���
		JMenu fileMenu = new JMenu("�ļ�");
			// �½��ļ��˵���
		JMenuItem newItem = new JMenuItem("�½�");
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile(); 
			}
		});
		fileMenu.add(newItem);

		JMenuItem saveItem = new JMenuItem("����");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(); // ���������������ñ����ļ�������
			}
		});
		fileMenu.add(saveItem);

		JMenuItem loadItem = new JMenuItem("��");
		loadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		fileMenu.add(loadItem);

		fileMenu.addSeparator();

		JMenuItem exitItem = new JMenuItem("�˳�");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		bar.add(fileMenu);

		JMenu colorMenu = new JMenu("��ɫ");

		JMenuItem colorItem = new JMenuItem("ѡ����ɫ");
		colorItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseColor();
			}
		});
		colorMenu.add(colorItem);
		bar.add(colorMenu);

		JMenu strokeMenu = new JMenu("������ϸ");

		JMenuItem strokeItem = new JMenuItem("����������ϸ");
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

		bold = new JCheckBox("����");
		italic = new JCheckBox("б��");

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
				JOptionPane.showMessageDialog(null, "������ͼ��ѡ�������ı���λ��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
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
				input = JOptionPane.showInputDialog("����������Ҫ������ı�");
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

	// �½�һ����ͼ������Ԫ����ĳ����
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

	// ѡ��ǰ��ɫ�����
	public void chooseColor() {
		color = JColorChooser.showDialog(QingDrawPad.this, "��ѡ��һ����ɫ", color); // �Դ���ɫ��
		R = color.getRed();
		G = color.getGreen();
		B = color.getBlue();
		itemList[index].R = R;
		itemList[index].G = G;
		itemList[index].B = B;
	}

	// ѡ��ǰ������ϸ�����
	public void setStroke() {
		String input;
		input = JOptionPane.showInputDialog("������һ��������������ϸֵ ( >0 )");
		stroke = Float.parseFloat(input);
		itemList[index].stroke = stroke;
	}

	// ����ͼ���ļ������
	public void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showSaveDialog(this);
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		File fileName = fileChooser.getSelectedFile();
		fileName.canWrite();

		if (fileName == null || fileName.getName().equals(""))
			JOptionPane.showMessageDialog(fileChooser, "�ļ�����Ч", "�ļ�����Ч", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(fileChooser, "�ļ�����Ч", "�ļ�����Ч", JOptionPane.ERROR_MESSAGE);
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
				JOptionPane.showMessageDialog(this, "�ļ���û�и���ļ�¼", "�޷��ҵ���", JOptionPane.ERROR_MESSAGE);
			} catch (ClassNotFoundException classNotFoundException) {
				JOptionPane.showMessageDialog(this, "���ܴ�������", "�ļ�����", JOptionPane.ERROR_MESSAGE);
			} catch (IOException ioException) {
				JOptionPane.showMessageDialog(this, "���ļ���ȡ���ϳ���", "��������", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// �½�һ���ļ������
	public void newFile() {
		index = 0;
		currentChoice = 3;
		color = Color.black;
		stroke = 1.0f;
		createNewItem();
		repaint();
	}

	// ��������
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

// ���廭ͼ�Ļ���ͼ�ε�Ԫ
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


class Line extends drawings // ֱ����
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}
}

class Rect extends drawings// ������
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class fillRect extends drawings// ʵ�ľ�����
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class Oval extends drawings// ��Բ��
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class fillOval extends drawings// ʵ����Բ
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}

class Circle extends drawings// Բ��
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)),
				Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)));
	}
}

class fillCircle extends drawings// ʵ��Բ
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)),
				Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)));
	}
}

class RoundRect extends drawings// Բ�Ǿ�����
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);
	}
}

class fillRoundRect extends drawings// ʵ��Բ�Ǿ�����
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRoundRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2), 50, 35);
	}
}

class Pencil extends drawings// Ǧ����
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}
}

class Rubber extends drawings// ��Ƥ����
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(255, 255, 255));
		g2d.setStroke(new BasicStroke(stroke + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
	}
}

class Word extends drawings// ����������
{
	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));
		g2d.setFont(new Font(s2, x2 + y2, ((int) stroke) * 18));
		if (s1 != null)
			g2d.drawString(s1, x1, y1);
	}
}
