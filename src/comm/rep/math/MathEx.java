
package comm.rep.math;

public class MathEx {
    public static int _2dTo1d (int x, int y, int width) {
        return x + width * y;
    }
    public static int _1dTo2dX (int index, int width) {
        return index % width;
    }
    
    public static int _1dTo2dY (int index, int width) {
        return index / width;
    }
    public static float lerp (float from, float to, float by) {
        return from*(1-by)+to*by;
    }
    public static float inverseLerp (float from, float to, float value) {
        return (value - from) / (to - from);
    }
}
