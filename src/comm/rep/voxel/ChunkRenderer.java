
package comm.rep.voxel;

import comm.rep.ui.ContextEx;
import org.w3c.dom.css.Rect;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ChunkRenderer {
  
  public Map<Byte, BlockInfo> blocks;
  
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
  
  public BlockInfo registerBlock (String label) {
    byte b = (byte) this.blocks.size();
  
    var bi = new BlockInfo(b, label);
    setBlockInfo(b, bi);
    return bi;
  }
  
  byte getBlockInfoCachedType = -1;
  BlockInfo getBlockInfoCached;
  
  public BlockInfo getBlockInfo (byte b) {
    if (b == getBlockInfoCachedType) return getBlockInfoCached;
    getBlockInfoCachedType = b;
    getBlockInfoCached = this.blocks.get(b);
    
    return getBlockInfoCached;
  }
  
  private Rectangle2D borderRect = new Rectangle2D.Float(0,0,1,1);
  
  public boolean renderBlock (ContextEx ctx, BlockData d) {
    this.renderBlockInfo = this.getBlockInfo(d.type);
  
    BufferedImage img;
    if (this.renderBlockInfo == null) return false;
    img = this.renderBlockInfo.getImage(d.data);
    
    if (img == null) return false;
    int w = img.getWidth();
    int h = img.getHeight();
    
    ctx.save();
    
    float sx = 1f/w;
    float sy = 1f/h;
    
//    if (this.renderBlock.data > 0) {
////    if (this.renderBlockInfo.isLine) {
//      ctx.draw(this.borderRect);
//    }
    
    ctx.scale(sx, sy);
    
    ctx.drawImage(img);
    
    ctx.restore();
    return true;
  }
  
  public void render (ContextEx ctx, Chunk c) {
    AffineTransform t;
    
    for (int x=0; x<c.width; x++) {
      for (int y=0; y<c.height; y++) {
        ctx.save();
        
        //get block at coord
        c.get(x, y, this.renderBlock);
        
        //display in correct spot
        ctx.translate(x, y);
        
        this.renderBlock(ctx, this.renderBlock);
        ctx.restore();
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
