package comm.rep.json;

import java.util.ArrayList;
import java.util.List;

import static comm.rep.json.Scanner.*;

public class Lexer {
  public enum TokenType {
    IDENTIFIER,
    CURLY_BRACKET,
    SQUARE_BRACKET,
    COLON,
    STRING,
    NUMBER,
    COMMA
  }
  public static class Token {
    public String src;
    public int contentStart;
    public int contentEnd;
    
    public TokenType type;
    
    public Token () {
    
    }
    boolean is (TokenType t) {
      return this.type == t;
    }
    boolean is (TokenType t, String exactly) {
      return this.type == t && this.toString().equals(exactly);
    }
    boolean is (String exactly) {
      return this.toString().equals(exactly);
    }
    @Override
    public String toString () {
      return src.substring(this.contentStart, this.contentEnd);
    }
    public static Token from (Scanner.State s, Scanner.Output o, TokenType type) {
      Token t = new Token();
      t.type = type;
      t.src = s.src;
      t.contentStart = o.contentStart;
      t.contentEnd = o.contentEnd;
      return t;
    }
  }
  
  public static List<Token> lex (String src) {
    var result = new ArrayList<Token>();
    
    var s = new Scanner.State(src);
    var o = new Scanner.Output();
    
    while (s.offset < s.srcLength) {
      whitespace(s, o); //ignore whitespace
      if (success(o)) advance(s, o);
      
      single(s, o, '{');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.SQUARE_BRACKET)); continue; }
  
      single(s, o, '[');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.SQUARE_BRACKET)); continue; }
  
      single(s, o, ']');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.SQUARE_BRACKET)); continue; }
  
      single(s, o, '}');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.CURLY_BRACKET)); continue; }
  
      single(s, o, ':');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.COLON)); continue; }
  
      single(s, o, ',');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.COMMA)); continue; }
      
      fromTo(s, o, '"', '"');
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.STRING)); continue; }
      
      charset(s, o, "0123456789");
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.NUMBER)); continue; }
      
      charset(s, o, "abcdefghijklmnopqrstuvwxyz");
      if (success(o)) { advance(s, o); result.add(Token.from(s, o, TokenType.IDENTIFIER)); continue; }
  
      break;
    }
    
    
    return result;
  }
  
}
