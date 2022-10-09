
package comm.rep.voxel;

import org.w3c.dom.css.Rect;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class ChunkRenderer {
  
  private Map<Byte, BlockInfo> blocks;
  
  private BlockData renderBlock;
  private BlockInfo renderBlockInfo;
  private Neighbors renderNeighbors;
  
  public int blockSize () {
    return this.blocks.size();
  }
  
  public ChunkRenderer () {
    this.blocks = new HashMap();
    this.renderBlock = new BlockData();
    this.renderNeighbors = new Neighbors();
  }
  
  public void setBlockInfo (byte b, BlockInfo bi) {
    this.blocks.put(b, bi);
  }
  
  public boolean createBlock (byte b, String fname, String label) {
    BlockInfo bi = BlockInfo.create(fname, label);
    if (bi == null) return false;
    this.setBlockInfo(b, bi);
    return true;
  }
  
  public byte registerBlock (String fname, String label) {
    byte b = (byte) this.blocks.size();
    
    this.createBlock(b, fname, label);
    
    return b;
  }
  
  public BlockInfo getBlockInfo (byte b) {
    return this.blocks.get(b);
  }
  
  private Rectangle2D borderRect = new Rectangle2D.Float(0,0,1,1);
  
  public boolean renderBlock (Graphics2D ctx, BlockData d) {
    this.renderBlockInfo = this.getBlockInfo(d.type);
    
    if (this.renderBlockInfo == null || this.renderBlockInfo.image == null) return false;
    
    AffineTransform t = ctx.getTransform();
    
    float sx = 1f/(float)this.renderBlockInfo.image.getWidth();
    float sy = 1f/(float)this.renderBlockInfo.image.getHeight();
    
    if (this.renderBlock.data > 0) {
//            ctx.scale(0.9f, 0.9f);
      ctx.draw(this.borderRect);
    }
    
    ctx.scale(sx, sy);
    
    
    
    ctx.drawImage(this.renderBlockInfo.image, 0, 0, null);
    
    ctx.setTransform(t);
    return true;
  }
  
  public void render (Graphics2D ctx, Chunk c) {
    AffineTransform t;
    
    for (int x=0; x<c.width; x++) {
      for (int y=0; y<c.height; y++) {
        //save
        t = ctx.getTransform();
        
        //get block at coord
        c.get(x, y, this.renderBlock);
        
        //display in correct spot
        ctx.translate(x, y);
        
        //render
        if (this.renderBlock(ctx, this.renderBlock)) {
//                    System.out.printf("{ x: %d, y: %d, b:%d }\n", x, y, b);
        }
        
        //restore
        ctx.setTransform(t);
      }
    }
  }
  
  public void interact (float x, float y, boolean place, Chunk c, byte type) {
    int ix = (int)x;
    int iy = (int)y;
    
    if (place) {
      c.setIfEmpty(ix, iy, type, (byte)0);
    } else {
      c.set(ix, iy, (byte)0, (byte)0);
    }
  }
}
