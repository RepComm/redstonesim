
package comm.rep;

import comm.rep.json.JSON;
import comm.rep.math.Vec2;
import comm.rep.timer.CallbackTimer;
import comm.rep.ui.Renderer;
import comm.rep.voxel.BlockInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static comm.rep.ui.MouseHandler.EventType.MOUSE_CLICKED;
import static comm.rep.ui.MouseHandler.mouseListen;

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
    content.add(dInventory, BorderLayout.PAGE_END);
  
    r.cr.setBlockInfo((byte)0, new BlockInfo("air", null));
    r.cr.registerBlock("/images/block-wire.png", "wire");
    r.cr.registerBlock("/images/block-note.png", "note");
    
    mouseListen(r, (t, e)->{
      r.selected
        .set(e.getX(), e.getY())
        .div(r.renderScale);
    });
  
    JSON.JsonValue json = JSON.parse("{ \"key\":\"value\", \"nums\": [123] }");
    
    System.out.println(JSON.stringify(json));
    
    for (byte i=0; i<r.cr.blockSize(); i++) {
      final byte fi = i;
  
      BlockInfo b = r.cr.getBlockInfo(fi);
      
      var btn = new JButton(b.label);
      var iconSize = 42;
      var textSize = btn.getFont().getSize2D();
      var textHeight = textSize * 2;
      
      btn.setPreferredSize(new Dimension(iconSize, (int)textHeight + iconSize ));
      
      btn.setMargin(new Insets(0, 0, 0, 0));
  
      btn.setVerticalTextPosition(SwingConstants.BOTTOM);
      btn.setHorizontalTextPosition(SwingConstants.CENTER);
      btn.setBackground(Color.GRAY);
      btn.setForeground(Color.WHITE);
      
      if (b.image != null) btn.setIcon(new ImageIcon(b.image.getScaledInstance(iconSize, iconSize, Image.SCALE_REPLICATE)));
      
      mouseListen(btn, (t, e)->{
        if (t == MOUSE_CLICKED) r.blockPlaceType = fi;
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
