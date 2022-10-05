
package comm.rep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class RedstoneSim {
  
  public static void main(String[] args) {
    JFrame frame = new JFrame("RedstoneSim");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    var content = frame.getContentPane();
    
    content.setLayout(new BorderLayout());
    
    var r = new Renderer();
    content.add(r, BorderLayout.CENTER);
    
    var dInventory = new JPanel();
    dInventory.setBackground(Color.GRAY);
//    dInventory.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));
    content.add(dInventory, BorderLayout.PAGE_END);
  
    r.cr.setBlockInfo((byte)0, new BlockInfo("air", null));
    r.cr.registerBlock("/images/block-wire.png", "wire");
    r.cr.registerBlock("/images/block-note.png", "note");
  
    var mouseMotionHandler = new MouseMotionListener(){
      @Override
      public void mouseDragged(MouseEvent e) {}
      @Override
      public void mouseMoved(MouseEvent e) {
        r.selected.set(e.getX(), e.getY()).div(r.renderScale);
        
      }
    };
    r.addMouseMotionListener(mouseMotionHandler);
  
    for (int i=0; i<r.cr.blockSize(); i++) {
      final int fi = i;
  
      BlockInfo b = r.cr.getBlockInfo((byte) fi);
      
      var btn = new JButton(b.label);
      btn.setPreferredSize(new Dimension(42, 62));
      
      btn.setMargin(new Insets(0, 0, 0, 0));
  
      btn.setVerticalTextPosition(SwingConstants.BOTTOM);
      btn.setHorizontalTextPosition(SwingConstants.CENTER);
      btn.setBackground(Color.GRAY);
      btn.setForeground(Color.WHITE);

      BufferedImage bi = b.image;
      if (bi != null) {
        btn.setIcon(new ImageIcon(
            bi.getScaledInstance(42, 42, Image.SCALE_REPLICATE)
        ));
      }
      
      btn.addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
          r.blockPlaceType = (byte) fi;
        }
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
      });
      dInventory.add(btn);
    }
    
    frame.pack();
    
    frame.setVisible(true);
    
    frame.setSize(512, 512);
    
    var keepAlive = true;
    
    var cbt = new CallbackTimer(10, ()->{
      r.repaint();
    });
    
    var timerThread = new Thread(()->{
      
      while (keepAlive) {
        cbt.update();
      }
      
    });
    timerThread.start();
    
  }
  
}
