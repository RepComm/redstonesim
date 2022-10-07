
package comm.rep.timer;

public class Timer {
    long timeLast;
    long timeNow;
    long timeElapsed;
    long timeBetween;
    
    public Timer (int fps) {
        this.timeNow = 0;
        this.timeLast = 0;
        this.timeElapsed = 0;
        this.timeBetween = 1000/ (long)fps;
    }
    
    public boolean update () {
        this.timeNow = System.currentTimeMillis();
        this.timeElapsed = this.timeNow - this.timeLast;
        
        if (this.timeElapsed >= this.timeBetween) {
            this.timeLast = this.timeNow;
            return true;
        }
        return false;
    }
}
