
package comm.rep.ui;

import comm.rep.math.Vec2;
import comm.rep.voxel.Chunk;
import comm.rep.voxel.ChunkRenderer;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class Renderer extends JPanel implements MouseListener {
    
    public ChunkRenderer cr;
    public Chunk c;
    
    public BasicStroke strokeStyle;
//    Fill fillStyle;
    
    public Vec2 selected;
    
    public Vec2 renderSize;
    public Vec2 renderScale;
    float aspectRatio = 1;
    public Vec2 viewSize = new Vec2(1, 1);
    
    public Renderer () {
        super();
        this.selected = new Vec2();
        
        this.viewSize = new Vec2(8, 1);
        this.renderSize = new Vec2(1, 1);
        this.renderScale = new Vec2(1, 1);
        
        this.cr = new ChunkRenderer();
        this.c = new Chunk(8, 8);
        
        this.strokeStyle = new BasicStroke(0.1f);
        
        super.addMouseListener(this);
    }
    
    private Rectangle2D selectedShape = new Rectangle2D.Float(0,0,1,1);
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D ctx = (Graphics2D)g;
        
        Rectangle r = this.getBounds();
        
        ctx.clearRect(0, 0, r.width, r.height);
        
        AffineTransform t = ctx.getTransform();
        
        this.renderSize.set(r.width, r.height);
        this.aspectRatio = this.renderSize.ratio();
        
        this.viewSize.y = this.viewSize.x * this.aspectRatio;
        
        this.renderScale.copy(this.renderSize).div(this.viewSize);
        
        ctx.scale(this.renderScale.x, this.renderScale.y);
        
        ctx.setStroke(this.strokeStyle);
        
        this.selectedShape.setRect(
            (int)this.selected.x,
            (int)this.selected.y,
            1,1
        );
        ctx.draw(this.selectedShape);
                
        this.cr.render(ctx, this.c);
        
        ctx.setTransform(t);
        
    }
    
    public byte blockPlaceType = 1;
    
    public void renderToWorld (Vec2 v, Vec2 out) {
        out.copy(v).div(this.renderScale);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        this.selected.set(e.getX(), e.getY()).div(this.renderScale);
        
        this.cr.interact(
            this.selected.x, this.selected.y,
            e.getButton() != 1,
            this.c,
            this.blockPlaceType
        );
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
