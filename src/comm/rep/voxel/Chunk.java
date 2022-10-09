
package comm.rep.voxel;

import static comm.rep.math.MathEx._2dTo1d;
import static comm.rep.math.MathEx._1dTo2dX;
import static comm.rep.math.MathEx._1dTo2dY;


public class Chunk {
  public int width;
  public int height;
  byte[] data;
  
  boolean changed;
  
  public Chunk (int w, int h) {
    this.width = w;
    this.height = h;
    
    this.data = new byte[
      w * h * BlockData.byteLength
    ];
    
    this.changed = true;
    
  }
  
  public void setIfEmpty (int x, int y, BlockData d) {
    this.setIfEmpty(x, y, d.type, d.data);
  }
  public void setIfEmpty (int x, int y, byte t, byte d) {
    if (!this.bounded(x, y)) return;
    
    int idx = _2dTo1d(x, y, this.width);
    int bidx = idx * BlockData.byteLength;
    
    if (this.data[bidx] == 0) {
      this.data[bidx] = t;
      this.data[bidx+1] = d;
    }
  }
  public boolean isEmpty (int x, int y) {
    if (!this.bounded(x, y)) return true;
    int idx = _2dTo1d(x, y, this.width);
    int bidx = idx * BlockData.byteLength;
    return this.data[bidx] == 0;
  }
  public int xyToIdx (int x, int y) {
    return _2dTo1d(x, y, this.width);
  }
  public int idxToX (int idx) {
    return _1dTo2dX(idx, this.width);
  }
  public int idxToY (int idx) {
    return _1dTo2dY(idx, this.width);
  }
  public void set (int x, int y, BlockData d) {
    this.set(x, y, d.type, d.data);
  }
  public void set (int x, int y, byte t, byte d) {
    if (!this.bounded(x, y)) return;
    
    int idx = _2dTo1d(x, y, this.width);
    
    this.set(idx, t, d);
  }
  public void set (int idx, BlockData d) {
    this.set(idx, d.type, d.data);
  }
  public void set (int idx, byte t, byte d) {
    int bidx = idx * BlockData.byteLength;
    this.data[bidx] = t;
    this.data[bidx+1] = d;
    this.changed = true;
  }
  public boolean bounded (int x, int y) {
    return (x >= 0 && x < this.width && y >= 0 && y < this.height);
  }
  public boolean get (int x, int y, BlockData out) {
    if (!this.bounded(x,y)) {
      out.index = -1;
      return false;
    }
    
    int idx = _2dTo1d(x, y, this.width);
    int bidx = idx * BlockData.byteLength;
    
    out.type = this.data[bidx];
    out.data = this.data[bidx+1];
    out.index = idx;
    return true;
  }
  public boolean get (int idx, BlockData out) {
    if (idx < 0 || idx > this.width*this.height) return false;
    int bidx = idx * BlockData.byteLength;
  
    out.type = this.data[bidx];
    out.data = this.data[bidx+1];
    out.index = idx;
    return true;
  }
  public void neighbors (int x, int y, Neighbors out) {
    this.get(x, y-1, out.top);
    this.get(x, y+1, out.bottom);
    this.get(x-1, y, out.left);
    this.get(x+1, y, out.right);
  }
  public boolean hasChanged () {
    boolean result = this.changed;
    this.changed = false;
    return result;
  }
}
