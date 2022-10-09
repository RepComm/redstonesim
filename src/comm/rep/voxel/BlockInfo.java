
package comm.rep.voxel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class BlockInfo {
  public List<BufferedImage> images;
  public String label;
  public boolean isLine;
  public boolean isSource;
  
  public boolean isDraggable;
  
  byte type;
  
  public BlockInfo (byte type, String label) {
    this.type = type;
    this.label = label;
    this.images = new ArrayList();
  }
  public void addImages (String... fnames) {
    for (var fname : fnames) {
      this.addImage(fname);
    }
  }
  public void addImages (BufferedImage... images) {
    for (var image : images) {
      this.addImage(image);
    }
  }
  public void addImage (String fname) {
    BufferedImage image = null;
    try {
      image = ImageIO.read(BlockInfo.class.getResource(fname));
    } catch (IOException e) {
      System.err.printf("Could not load image from file name %s", fname);
      return;
    }
    this.addImage(image);
  }
  public void addImage (BufferedImage img) {
    this.images.add(img);
  }
  public boolean hasImage (int idx) {
    return this.images.size() > idx;
  }
  public boolean hasImage () {
    return this.hasImage(0);
  }
  public BufferedImage getImage (int idx) {
    if (this.hasImage(idx)) return this.images.get(idx);
    else if (this.hasImage()) return this.images.get(0);
    return null;
  }
  public BufferedImage getImage () {
    return this.getImage(0);
  }
  
}
