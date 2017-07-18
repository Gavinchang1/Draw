package pro_CAD;

import java.awt.Graphics;
import java.awt.Point;

public class Oval extends Figure {
private Point pS;
private Point pE;
public Oval(Point ps,Point pe){
	pS=ps;
	pE=pe;
}
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		g.drawOval(pS.x, pS.y, pE.x-pS.x, pE.y-pS.y);
	}

}
