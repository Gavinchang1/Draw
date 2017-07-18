package pro_CAD;

import javax.swing.SwingUtilities;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

public class CAD_software extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JToolBar jToolBar1 = null;
	private JToolBar jToolBar2 = null;
	private JPanel jPanel = null;
	private JToggleButton jToggleButton = null;
	private JToggleButton jToggleButton1 = null;
	private JLabel jLabel = null;
	private JToggleButton jToggleButton2 = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private Point pStart=new Point(0,0);  //  @jve:decl-index=0:
	private Point pEnd=new Point(0,0);  //  @jve:decl-index=0:
	private ButtonGroup bg=new ButtonGroup();//定义组类变量，目的是使工具栏中的两个双状态按钮排斥，始终只有一个变量被选中  //  @jve:decl-index=0:
	private int select=-1;
	private ArrayList arrList=new ArrayList();  //  @jve:decl-index=0:
	private JPopupMenu jPopupMenu = null;  //  @jve:decl-index=0:visual-constraint="586,52"
	private JMenuItem jMenuItem2 = null;
	private JMenuItem jMenuItem1 = null;
	
	
	
	/**
	 * This method initializes jToolBar1	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	
	private JToolBar getJToolBar1() {
		if (jToolBar1 == null) {
			jToolBar1 = new JToolBar();
			jToolBar1.add(getJToggleButton());
			jToolBar1.add(getJToggleButton1());
			jToolBar1.add(getJToggleButton2());
		}
		return jToolBar1;
	}

	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);
		Graphics g=jPanel.getGraphics();
		if(arrList.isEmpty()!=true){
			Figure f;
			for(int i=0;i<arrList.size();i++){
				f=(Figure)arrList.get(i);
				f.draw(g);
			}
		}
	}

	/**
	 * This method initializes jToolBar2	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBar2() {
		if (jToolBar2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("");
			jLabel1 = new JLabel();
			jLabel1.setText("");
			
			jLabel = new JLabel();
			jLabel.setText("状态栏");
			jToolBar2 = new JToolBar();
			jToolBar2.add(jLabel);
			jToolBar2.addSeparator();
			jToolBar2.add(jLabel1);
			jToolBar2.addSeparator();
			jToolBar2.add(jLabel2);
		}
		return jToolBar2;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			jPanel.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					if (e.getModifiers()==MouseEvent.BUTTON1_MASK) {
						pStart = e.getPoint();
						pEnd = e.getPoint();
						}
					else if(e.getModifiers()==MouseEvent.BUTTON3_MASK){
						Point p=e.getLocationOnScreen();
						jPopupMenu.setLocation(p);
						jPopupMenu.setVisible(true);
					}
				}
				public void mouseReleased(MouseEvent e){
					if (e.getModifiers()==MouseEvent.BUTTON1_MASK) {
						if (select == 1) {
							Figure f = new Line(pStart, e.getPoint());
							arrList.add(f);
						} else if (select == 2) {
							Figure f = new Rect(pStart, e.getPoint());
							arrList.add(f);
						} else if (select == 3) {
							Figure f = new Oval(pStart, e.getPoint());
							arrList.add(f);
						}
					}
				}
			});
			jPanel.addMouseMotionListener(new MouseMotionAdapter(){
				public void mouseDragged(MouseEvent e){
					if (e.getModifiers()==MouseEvent.BUTTON1_MASK) {
						String str;
						str = "pEnd:(" + Integer.toString(pEnd.x);
						str = str + "," + Integer.toString(pEnd.y) + ")";
						jLabel1.setText(str);
						str = "e.point(" + Integer.toString(e.getX());
						str = str + "," + Integer.toString(e.getY()) + ")";
						jLabel2.setText(str);
						//					int x,y;
						//					x=e.getX();
						//					y=e.getY();
						//					System.out.println("x="+x+"y="+y);
						Graphics g = jPanel.getGraphics();
						g.setColor(jPanel.getBackground());
						if (select == 1) {
							g.drawLine(pStart.x, pStart.y, pEnd.x, pEnd.y);
						} else if (select == 2) {
							g.drawRect(pStart.x, pStart.y, pEnd.x - pStart.x,
									pEnd.y - pStart.y);
						} else if (select == 3) {
							g.drawOval(pStart.x, pStart.y, pEnd.x - pStart.x,
									pEnd.y - pStart.y);
						}
						g.setColor(getForeground());
						if (select == 1) {
							g.drawLine(pStart.x, pStart.y, e.getX(), e.getY());
						} else if (select == 2) {
							g.drawRect(pStart.x, pStart.y, e.getX() - pStart.x,
									e.getY() - pStart.y);
						} else if (select == 3) {
							g.drawOval(pStart.x, pStart.y, e.getX() - pStart.x,
									e.getY() - pStart.y);
						}
						pEnd = e.getPoint();
						if (!arrList.isEmpty()) {
							Figure f;
							for (int i = 0; i < arrList.size(); i++) {
								f = (Figure) arrList.get(i);
								f.draw(g);
							}
						}
					}
				}
			});
		}
		return jPanel;
	}

	/**
	 * This method initializes jToggleButton	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */
	private JToggleButton getJToggleButton() {
		if (jToggleButton == null) {
			jToggleButton = new JToggleButton();
			jToggleButton.setText("Line");
			bg.add(jToggleButton);
			jToggleButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					select=1;
				}
			});
		}
		return jToggleButton;
	}

	/**
	 * This method initializes jToggleButton1	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */
	private JToggleButton getJToggleButton1() {
		if (jToggleButton1 == null) {
			jToggleButton1 = new JToggleButton();
			jToggleButton1.setText("Rect");
			bg.add(jToggleButton1);
			jToggleButton1.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					select=2;
				}
			});
		}
		return jToggleButton1;
	}

	/**
	 * This method initializes jToggleButton2	
	 * 	
	 * @return javax.swing.JToggleButton	
	 */
	private JToggleButton getJToggleButton2() {
		if (jToggleButton2 == null) {
			jToggleButton2 = new JToggleButton();
			jToggleButton2.setText("Oval");
			bg.add(jToggleButton2);
			jToggleButton2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					select=3;
				}
			});
		}
		return jToggleButton2;
	}

	/**
	 * This method initializes jPopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.add(getJMenuItem1());
			jPopupMenu.add(getJMenuItem2());
		}
		return jPopupMenu;
	}

	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("清屏");
		}
		return jMenuItem2;
	}

	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("撤销上一步");
			jMenuItem1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(arrList.size()!=0){
						arrList.remove(arrList.size()-1);
						Graphics g=jPanel.getGraphics();
						jPanel.paint(g);
					}
				}
			});
		}
		return jMenuItem1;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CAD_software thisClass = new CAD_software();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});

	}

	/**
	 * This is the default constructor
	 */
	public CAD_software() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(350, 300);
		this.setContentPane(getJContentPane());
		this.setTitle("CAD绘图软件");
		this.getJPopupMenu();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJToolBar1(), BorderLayout.NORTH);
			jContentPane.add(getJToolBar2(), BorderLayout.SOUTH);
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="168,5"
