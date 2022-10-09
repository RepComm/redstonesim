
package comm.rep.ui;

import comm.rep.AddQueue;
import comm.rep.math.MathEx;
import comm.rep.math.Vec2;
import comm.rep.voxel.*;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import javax.swing.*;

import static comm.rep.ui.MouseHandler.mouseListen;

public class Renderer extends JPanel {
  
  public ChunkRenderer cr;
  public Chunk c;
  
  BlockData calcBlock;
  BlockInfo calcBlockInfo;
  BlockData dragBlock;
  BlockInfo dragBlockInfo;
  
  boolean isDragging;
  public BasicStroke strokeStyle;
  
  public Vec2 selected;
  public Vec2 dragPos;
  public Vec2 dragPosTarget;
  
  public Vec2 renderSize;
  public Vec2 renderScale;
  float aspectRatio;
  public Vec2 viewSize;
  public byte blockPlaceType;
  
  private Rectangle2D selectedShape;
  
  private List<Integer> visited;
  private AddQueue<Integer> toVisit;
  private Neighbors neighbors;
  
  public ContextEx ctx;
  
  public Renderer () {
    super();
    this.blockPlaceType = 1;
    this.aspectRatio = 1;
  
    this.selectedShape = new Rectangle2D.Float(0,0,1,1);
    
    this.selected = new Vec2();
    
    this.viewSize = new Vec2(8, 1);
    this.renderSize = new Vec2(1, 1);
    this.renderScale = new Vec2(1, 1);
    
    this.cr = new ChunkRenderer();
    this.c = new Chunk(8, 8);
    
    this.calcBlock = new BlockData();
    this.dragBlock = new BlockData();
//    this.dragBlockInfo
    this.isDragging = false;
    this.dragPos = new Vec2();
    this.dragPosTarget = new Vec2();
    
    this.strokeStyle = new BasicStroke(0.1f);
    
    this.toVisit = new AddQueue<Integer>();
    
    this.visited = new ArrayList();
    this.neighbors = new Neighbors();
    
    this.ctx = new ContextEx();
    
    mouseListen(this, (t, e)->{
      this.selected.set(e.getX(), e.getY());
      this.renderToWorld(this.selected, this.selected);
      
      if (t == MouseHandler.EventType.MOUSE_CLICKED) {
  
        this.selected.set(e.getX(), e.getY()).div(this.renderScale);
        
        this.cr.interact(
          this.selected.x, this.selected.y,
          e.getButton() != 1,
          this.c,
          this.blockPlaceType
        );
      }
      
      if (t == MouseHandler.EventType.MOUSE_PRESSED) {
        this.dragPos.copy(this.selected).floor();
        
        this.c.get(
          (int)this.dragPos.x,
          (int)this.dragPos.y,
          this.dragBlock
        );
        this.dragBlockInfo = this.cr.getBlockInfo(this.dragBlock.type);
        
      }
      if (t == MouseHandler.EventType.MOUSE_DRAGGED) {
        if (!this.isDragging && this.dragBlockInfo.isDraggable) {
          this.isDragging = true;
          this.c.set(this.dragBlock.index, (byte) 0, (byte) 0);
        }
      }
      if (t == MouseHandler.EventType.MOUSE_RELEASED) {
        if (this.isDragging && this.dragBlockInfo.isDraggable) {
          this.isDragging = false;
          int x = (int) this.selected.x;
          int y = (int) this.selected.y;
          this.c.set(x, y, this.dragBlock);
        }
      }
    });
  }
  
  Rectangle bounds;
  
  @Override
  protected void paintComponent(Graphics g) {
    this.ctx.ctx = (Graphics2D) g;
    
    ctx.save();
    
    this.bounds = this.getBounds();
    
    ctx.clearRect(0, 0, bounds.width, bounds.height);
    
    this.renderSize.set(bounds.width, bounds.height);
    this.aspectRatio = this.renderSize.ratio();
    
    this.viewSize.y = this.viewSize.x * this.aspectRatio;
    
    this.renderScale.copy(this.renderSize).div(this.viewSize);
    
    ctx.scale(this.renderScale.x, this.renderScale.y);
  
    ctx.setStroke(this.strokeStyle);
    
    if (this.isDragging) {
      ctx.save();
  
      BufferedImage img = this.dragBlockInfo.getImage(this.dragBlock.data);
      if (this.dragBlockInfo.isDraggable && img != null) {
        int w = img.getWidth();
        int h = img.getHeight();
  
        float sx = 1f / w;
        float sy = 1f / h;
        
        this.dragPosTarget.copy(this.selected).floor();
        
        this.dragPos.lerp(
          this.dragPosTarget,
          0.5f
        );
  
        ctx.scale(sx, sy);
  
        ctx.translate(
          (int) (this.dragPos.x / sx),
          (int) (this.dragPos.y / sy)
        );
  
        ctx.drawImage(img);
  
        ctx.restore();
      }
    } else {
  
      this.selectedShape.setRect(
        (int)this.selected.x,
        (int)this.selected.y,
        1,1
      );
      ctx.draw(this.selectedShape);
    }
    
    this.cr.render(ctx, this.c);
    
    ctx.restore();
    
  }
  
  public boolean hasVisited (BlockData d) {
    return this.visited.contains(d.index);
  }
  public boolean shouldVisit(BlockData d, BlockInfo b) {
    return d.index > -1 && b.isLine && !this.hasVisited(d);
  }
  
  public BlockInfo info (BlockData d) {
    return this.cr.getBlockInfo(d.type);
  }
  public void propagate () {
    //reset
    this.visited.clear();
    this.toVisit.clear();
    
    //loop thru every block, find sources and schedule them a visit
    int len = this.c.width*this.c.height;
    for (int idx=0; idx<len; idx++) {
      this.c.get(idx, this.calcBlock);
      this.calcBlockInfo = info(this.calcBlock);
      
      if (this.calcBlockInfo.isLine) { //clear redstone level
        this.calcBlock.data = 0;
        this.c.set(idx, this.calcBlock);
      } else if (this.calcBlockInfo.isSource) { //schedule a visit to source nodes
        this.toVisit.add(idx);
      }
      
    }
    
    int x = 0;
    int y = 0;
    int idx = 0;
    
    //breadth first search for line nodes, starting with source node's neighbors
    while (toVisit.hasNext()) {
      //current node
      idx = toVisit.next();
      
      //x and y of this node
      x = this.c.idxToX(idx);
      y = this.c.idxToY(idx);
      
      //mark as visited, remove from scheduled visit queue
      this.visited.add(idx); toVisit.remove();
      
      //calculate neighbors index and data
      this.c.neighbors(x, y, this.neighbors);
  
      //check should visit first (stops infinite queue addition)
      //schedule a visit if isLine and not visited already
      
      this.calcBlockInfo = cr.getBlockInfo(this.calcBlock.type);
      if (
        shouldVisit(
          this.neighbors.top,
          info(this.neighbors.top)
        )
      ) this.toVisit.add(this.neighbors.top.index);
      
      if (
        shouldVisit(
          this.neighbors.bottom,
          info(this.neighbors.bottom)
        )
      ) this.toVisit.add(this.neighbors.bottom.index);
  
      if (
        shouldVisit(
          this.neighbors.left,
          info(this.neighbors.left)
        )
      ) this.toVisit.add(this.neighbors.left.index);
  
      if (
        shouldVisit(
          this.neighbors.right,
          info(this.neighbors.right)
        )
      ) this.toVisit.add(this.neighbors.right.index);
    }
    
    //perform updates on visited nodes
    for (var v : this.visited) {
      //get node data
      this.c.get(v, this.calcBlock);
      
      //ignore source nodes
      if (info(this.calcBlock).isSource) continue;
      
      //redstone level
      this.calcBlock.data = 1;
      this.c.set(v, this.calcBlock);
    }
    
  }
  
  public void renderToWorld (Vec2 v, Vec2 out) {
    out.copy(v).div(this.renderScale);
  }
}
