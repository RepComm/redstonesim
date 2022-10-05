
package comm.rep;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BlockInfo {
    public BufferedImage image;
    public String label;

    public BlockInfo (String label, BufferedImage image) {
        this.image = image;
        this.label = label;
    }
    public static BlockInfo create (String fname, String label) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(BlockInfo.class.getResource(fname));
        } catch (IOException e) {
            return null;
        }

        return new BlockInfo(label, image);

    }
}
