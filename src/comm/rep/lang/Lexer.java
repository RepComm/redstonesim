package comm.rep.lang;

import java.util.ArrayList;

public class Lexer {
  
  public static void advance (State s, Output o) {
    s.offset += o.successfulReadCount;
  }
  
  public static String whitespaceChars = " \n\t\r";
  
  public static void whitespace (State s, Output o) {
    o.clear();
    
    o.contentStart = s.offset;
    
    char c;
    for (int i = s.offset; i<s.sourceLength; i++) {
      c = s.source.charAt(i);
      
      if (whitespaceChars.indexOf(c) < 0) {
        o.contentEnd = i;
        o.successfulReadCount = o.contentEnd - o.contentStart;
        break;
      }
    }
  }
  
  public static void single (State s, Output o, char matcher) {
    o.clear();
    if (s.source.charAt(s.offset) == matcher) {
      o.contentStart = s.offset;
      o.contentEnd = o.contentStart+1;
      o.successfulReadCount = 1;
    }
  }
  
  public static void str (State s, Output o, String matcher) {
    o.clear();
    
    int searchLen = matcher.length();
    for (int i=s.offset; i<searchLen; i++) {
      if (matcher.charAt(i) != s.source.charAt(i)) return;
    }
    o.contentStart = s.offset;
    o.contentEnd = o.contentStart + searchLen;
    o.successfulReadCount = searchLen;
  }
  
  
  public static void charset (State s, Output o, String charset) {
    o.clear();
    
    o.contentStart = s.offset;
    
    char c;
    for (int i = s.offset; i<s.sourceLength; i++) {
      c = s.source.charAt(i);
      
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
    if (!o.success()) return;
    advance(s, o);
    
    o.contentStart = s.offset;
    char c;
    for (int i=s.offset; i<s.sourceLength; i++) {
      c = s.source.charAt(i);
      if (c == end) {
        o.contentEnd = i;
        o.successfulReadCount = o.contentEnd - o.contentStart+1;
        break;
      }
    }
    
  }
  
  
  public static class State {
    public String source;
    public int offset;
    public int sourceLength;
    
    public State (String source) {
      this.source = source;
      this.offset = 0;
      this.sourceLength = this.source.length();
    }
  }
  
  public static class Output {
    public int successfulReadCount;
    public int contentStart;
    public int contentEnd;
    
    public void clear () {
      this.successfulReadCount = 0;
      this.contentStart = 0;
      this.contentEnd = 0;
    }
    
    public boolean success () {
      return this.successfulReadCount > 0;
    }
  }
  
  public static String[] keywords = new String[]{
    "let", "function", "if", "else", "class"
  };
  
  public Token[] lex (String src) {
    var tokens = new ArrayList<Token>();
    
    var s = new State(src);
    var o = new Output();
    
    while (s.offset < s.sourceLength) {
      whitespace(s, o);
      if (o.success()) advance(s, o);
      
      charset(s, o, "abcdefghijklmnopqrstuvwxyz");
      var t = Token.from(s, o, Token.Type.IDENTIFIER);
      for (var kw : keywords) {
        if (t.value.equals(kw)) {
          t.type = Token.Type.KEYWORD;
          break;
        }
      }
      if (o.success()) advance(s, o); tokens.add(t);
      
      
      break;
    }
    
    var result = new Token[tokens.size()];
    tokens.toArray(result);
    
    return result;
  }
}
