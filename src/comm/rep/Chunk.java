
package comm.rep;

import static comm.rep.MathEx._2dTo1d;

public class Chunk {
    int width;
    int height;
    byte[] data;
    
    public Chunk (int w, int h) {
        this.width = w;
        this.height = h;
        
        this.data = new byte[
            w * h * BlockData.byteLength
        ];
        
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
    public void set (int x, int y, BlockData d) {        
        this.set(x, y, d.type, d.data);
    }
    public void set (int x, int y, byte t, byte d) {
        if (!this.bounded(x, y)) return;

        int idx = _2dTo1d(x, y, this.width);
        int bidx = idx * BlockData.byteLength;
        
        this.data[bidx] = t;
        this.data[bidx+1] = d;
    }
    public boolean bounded (int x, int y) {
        return (x >= 0 && x < this.width && y >= 0 && y < this.height);
    }
    public boolean get (int x, int y, BlockData out) {
        if (!this.bounded(x,y)) return false;
        
        int idx = _2dTo1d(x, y, this.width);
        int bidx = idx * BlockData.byteLength;
        
        out.type = this.data[bidx];
        out.data = this.data[bidx+1];
        return true;
    }
    public void neighbors (int x, int y, Neighbors out) {
        this.get(x, y-1, out.top);
        this.get(x, y+1, out.bottom);
        this.get(x-1, y, out.left);
        this.get(x+1, y, out.right);
        
    }
}
