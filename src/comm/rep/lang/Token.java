package comm.rep.lang;

public class Token {
  public enum Type {
    BRACKET_CURLY,
    BRACKET_SQUARE,
    PARENTHESIS,
    
    IDENTIFIER,
    
    STRING,
    NUMBER,
  
    KEYWORD,
    OPERATOR,
    
    COMMA,
    SEMICOLON,
    COLON
  }
  
  public Type type;
  public String value;
  
  public static Token from (comm.rep.lang.Lexer.State s, comm.rep.lang.Lexer.Output o, Type type) {
    var t = new Token();
    t.type = type;
    t.value = s.source.substring(o.contentStart, o.contentEnd);
    return t;
  }
  
}
