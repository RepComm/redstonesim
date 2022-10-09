package comm.rep.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.Stack;

public class ContextEx {
  public Graphics2D ctx;
  private Stack<AffineTransform> transformStack;
  
  public ContextEx () {
    this.transformStack = new Stack();
  }
  public void save () {
    this.transformStack.push(this.ctx.getTransform());
  }
  public void restore () {
    this.ctx.setTransform(this.transformStack.pop());
  }
  public void clearRect (int x, int y, int w, int h) {
    this.ctx.clearRect(x, y, w, h);
  }
  public void scale (double x, double y) {
    this.ctx.scale(x, y);
  }
  public void setStroke( Stroke s ) {
    this.ctx.setStroke(s);
  }
  public void translate( int x, int y ) {
    this.ctx.translate(x, y);
  }
  public boolean drawImage( Image img, int x, int y, ImageObserver observer ) {
    return this.ctx.drawImage(img, x, y, observer);
  }
  public boolean drawImage (Image img, int x, int y) {
    return this.drawImage(img, x, y, null);
  }
  public boolean drawImage (Image img) {
    return this.drawImage(img, 0, 0);
  }
  public void draw( Shape s ) {
    this.ctx.draw(s);
  }
}
