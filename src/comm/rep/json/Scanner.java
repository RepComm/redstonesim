package comm.rep.json;

public class Scanner {
  
  public static class State {
    public String src;
    public int offset;
    
    public int srcLength;
    
    public State (String src) {
      this.src = src;
      this.srcLength = this.src.length();
      this.offset = 0;
    }
  }
  
  public static class Output {
    public int successfulReadCount;
    public int contentStart;
    public int contentEnd;
  }
  
  public static String whitespaceChars = " \n\t\r";
  
  public static void whitespace (State s, Output o) {
    clear(o);
    
    o.contentStart = s.offset;
    
    char c;
    for (int i = s.offset; i<s.srcLength; i++) {
      c = s.src.charAt(i);
      
      if (whitespaceChars.indexOf(c) < 0) {
        o.contentEnd = i;
        o.successfulReadCount = o.contentEnd - o.contentStart;
        break;
      }
    }
  }
  
  public static void clear (Output o) {
    o.successfulReadCount = 0;
    o.contentStart = 0;
    o.contentEnd = 0;
  }
  
  public static void single (State s, Output o, char matcher) {
    clear(o);
    if (s.src.charAt(s.offset) == matcher) {
      o.contentStart = s.offset;
      o.contentEnd = o.contentStart+1;
      o.successfulReadCount = 1;
    }
  }
  public static void str (State s, Output o, String matcher) {
    clear(o);
    
    int searchLen = matcher.length();
    for (int i=s.offset; i<searchLen; i++) {
      if (matcher.charAt(i) != s.src.charAt(i)) return;
    }
    o.contentStart = s.offset;
    o.contentEnd = o.contentStart + searchLen;
    o.successfulReadCount = searchLen;
  }
  public static void charset (State s, Output o, String charset) {
    clear(o);
  
    o.contentStart = s.offset;
  
    char c;
    for (int i = s.offset; i<s.srcLength; i++) {
      c = s.src.charAt(i);
    
      if (charset.indexOf(c) < 0) {
        o.contentEnd = i;
        o.successfulReadCount = o.contentEnd - o.contentStart;
        break;
      }
    }
  }
  public static void fromTo (State s, Output o, char begin, char end) {
    if (end == -1) end = begin;
    
    single(s, o, begin);
    if (!success(o)) return;
    advance(s, o);
    
    o.contentStart = s.offset;
    char c;
    for (int i=s.offset; i<s.srcLength; i++) {
      c = s.src.charAt(i);
      if (c == end) {
        o.contentEnd = i;
        o.successfulReadCount = o.contentEnd - o.contentStart+1;
        break;
      }
    }
    
  }
  
  public static boolean success (Output o) {
    return o.successfulReadCount > 0;
  }
  
  public static void advance (State s, Output o) {
    s.offset += o.successfulReadCount;
  }
  
}
