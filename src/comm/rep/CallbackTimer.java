
package comm.rep;

public class CallbackTimer extends Timer {
    Runnable cb;
    
    public CallbackTimer (int fps, Runnable cb) {
        super(fps);
        this.cb = cb;
    }
    
    public boolean update () {
        boolean result = super.update();
        
        if (result) this.cb.run();
        
        return result;
    }
}
