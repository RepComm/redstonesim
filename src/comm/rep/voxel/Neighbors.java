
package comm.rep.voxel;

public class Neighbors {
  public BlockData top;
  public BlockData left;
  public BlockData right;
  public BlockData bottom;
  
  public Neighbors () {
    this.top = new BlockData();
    this.left = new BlockData();
    this.right = new BlockData();
    this.bottom = new BlockData();
  }
}
    