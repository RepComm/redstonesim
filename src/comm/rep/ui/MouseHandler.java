package comm.rep.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Make using MouseListener and MouseMotionListener not painful, use lambas!
 *
 * Example:
 * <pre>
 * {@code
 * import javax.swing.JFrame;
 * import static comm.rep.ui.MouseHandler.EventType.MOUSE_CLICKED;
 * import static comm.rep.ui.MouseHandler.mouseListen;
 *
 * public class Test {
 *   public static void main (String[] args) {
 *     var frame = new JFrame("Title");
 *     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 *
 *     mouseListen(frame, (type, evt)->{
 *       if (type == MOUSE_CLICKED) {
 *         //mouse was clicked on UI component
 *       }
 *     });
 *
 *     //removing a listener
 *     var listener = mouseListen(frame, (type, evt)->{});
 *     mouseDeafen(frame, listener);
 *
 *     frame.pack();
 *
 *     frame.setVisible(true);
 *
 *     frame.setSize(512, 512);
 *   }
 * }
 * </pre>
 */
public class MouseHandler implements MouseListener, MouseMotionListener {
  public static MouseHandler mouseListen(javax.swing.JComponent b, Callback cb) {
    var h = new MouseHandler(cb);
    b.addMouseListener(h);
    b.addMouseMotionListener(h);
    return h;
  }
  public static void mouseDeafen (javax.swing.JComponent b, MouseHandler h) {
    b.removeMouseListener(h);
    b.removeMouseMotionListener(h);
  }
  
  @Override
  public void mouseDragged(MouseEvent e) {
    this.cb.run(EventType.MOUSE_DRAGGED, e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e) {
    this.cb.run(EventType.MOUSE_MOVED, e);
  }
  
  public enum EventType {
    MOUSE_CLICKED,
    MOUSE_PRESSED,
    MOUSE_RELEASED,
    MOUSE_ENTERED,
    MOUSE_EXITED,
    MOUSE_DRAGGED,
    MOUSE_MOVED,
  }
  
  @FunctionalInterface
  public interface Callback {
    public abstract void run(EventType t, MouseEvent e);
  }
  
  
  
  Callback cb;
  
  public MouseHandler (Callback cb) {
    this.cb = cb;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    this.cb.run(EventType.MOUSE_CLICKED, e);
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    this.cb.run(EventType.MOUSE_PRESSED, e);
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    this.cb.run(EventType.MOUSE_RELEASED, e);
  }
  
  @Override
  public void mouseEntered(MouseEvent e) {
    this.cb.run(EventType.MOUSE_ENTERED, e);
  }
  
  @Override
  public void mouseExited(MouseEvent e) {
    this.cb.run(EventType.MOUSE_EXITED, e);
  }
}
