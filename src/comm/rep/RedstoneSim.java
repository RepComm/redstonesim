
package comm.rep;

import comm.rep.json.JSON;
import comm.rep.math.Vec2;
import comm.rep.timer.CallbackTimer;
import comm.rep.ui.Renderer;
import comm.rep.voxel.BlockInfo;
import comm.rep.voxel.ChunkRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static comm.rep.ui.MouseHandler.EventType.MOUSE_CLICKED;
import static comm.rep.ui.MouseHandler.mouseListen;
import static java.lang.System.out;

public class RedstoneSim {
  
  public static void loadBlockDefs (ChunkRenderer cr) {
    try {
      URL blocksUrl = RedstoneSim.class.getResource("/blocks.json");
      URI blocksUri = blocksUrl.toURI();
      File blocksFile = new File(blocksUri);
      byte[] blocksBytes = Files.readAllBytes(blocksFile.toPath());
      
      var src = new String(blocksBytes);
      
      //parse
      var json = JSON.parse(src);

      JSON.JsonArray blocks = json.asObject().get("blocks").asArray();
      int size = blocks.size();
      
      JSON.JsonObject block;
      String blockLabel;
      JSON.JsonArray blockImages;
      String[] blockImageStrings;
      boolean isLine = false;
      boolean isSource = false;
      boolean isDraggable = true;
      
      for (int i=0; i<size; i++) {
        block = blocks.get(i).asObject();
        
        blockLabel = block.get("label").asString();
        
        if (block.has("images")) {
          blockImages = block.get("images").asArray();
          
          int blockImageCount = blockImages.size();
          blockImageStrings = new String[blockImageCount];
          for (int j=0; j<blockImageCount; j++) {
            blockImageStrings[j] = blockImages.get(j).asString();
          }
          isLine = false;
          isSource = false;
          isDraggable = true;
          
          if (block.has("isLine")) isLine = block.get("isLine").asBoolean();
          if (block.has("isSource")) isSource = block.get("isSource").asBoolean();
          if (block.has("isDraggable")) isDraggable = block.get("isDraggable").asBoolean();
          
          BlockInfo bi = cr.registerBlock(blockLabel);
          bi.isSource = isSource;
          bi.isLine = isLine;
          bi.isDraggable = isDraggable;
          bi.addImages(blockImageStrings);
          
        } else {
          cr.registerBlock(blockLabel);
        }
      }
      
    } catch (Exception e) {
      System.err.printf("Couldn't load blocks.json: %s\n", e);
    }
  }
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
    
    loadBlockDefs(r.cr);
    
    for (byte i=0; i<r.cr.blockSize(); i++) {
      final byte fi = i;
  
      var b = r.cr.getBlockInfo(fi);
      
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
      
      BufferedImage img = b.getImage();
      ImageIcon icon;
      
      if (img != null) {
        icon = new ImageIcon(
          img.getScaledInstance(
            iconSize, iconSize,
            Image.SCALE_REPLICATE
          )
        );
        
        btn.setIcon(icon);
      }
      
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
    
    var redstone = new CallbackTimer(5, ()->{

//        long start = System.currentTimeMillis();
        r.propagate();
//        long end = System.currentTimeMillis();
//
//        out.printf("redstone took %d ms\n", end-start);

    });
    
    var timerThread = new Thread(()->{
      
      while (keepAlive) {
        cbt.update();
        redstone.update();
      }
      
    });
    timerThread.start();
    
  }
  
}
