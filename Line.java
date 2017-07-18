package pro_CAD;

import java.awt.Graphics;
import java.awt.Point;
public class Line extends Figure {
private Point pS;
private Point pE;
public Line(Point ps,Point pe){
	pS=ps;
	pE=pe;
}
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		g.drawLine(pS.x, pS.y, pE.x, pE.y);
	}

}
