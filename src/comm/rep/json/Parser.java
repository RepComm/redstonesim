package comm.rep.json;

import java.util.List;
import java.util.Stack;

public class Parser {
  Lexer.Token[] tokens;
  int offset;
  
  Stack<JSON.JsonValue> stack;
  
  public Parser() {
  }
  
  public Lexer.Token read () throws IndexOutOfBoundsException {
    if (this.offset > this.tokens.length) throw new IndexOutOfBoundsException(
      String.format(
        "Cannot read, offset %d > tokens.length %d",
        this.offset,
        this.tokens.length
      )
    );
    return this.tokens[this.offset];
  }
  public void next () {
    this.offset ++;
  }
  private void expected (String v) throws Exception {
    throw new Exception(
      String.format(
        "Error at %d, expected %s, got %s",
        this.offset,
        v,
        this.read().toString()
      )
    );
  }
  public void readKey () throws Exception {
    Lexer.Token t = read();
    if (!t.is(Lexer.TokenType.STRING)) this.expected("string type with double quotes");
    this.next();
    
    this.stack.push(new JSON.JsonString(t.toString()));
  }
  public void parseObject () throws Exception {
    if (!read().is("{")) this.expected("{");
    int startOffset = this.offset;
    this.next();
    
    this.stack.push(new JSON.JsonObject());
    
    try {
      while (!read().is("}")) {
        this.readKey();
        if (read().is(Lexer.TokenType.COLON)) {
          this.next();
        } else {
          this.expected(":");
        }
        this.parseValue();
        this.child();
  
        //detect if next is comma, if so consume it and move on
        //otherwise we expect to close the object
        if (read().is(Lexer.TokenType.COMMA)) {
          this.next();
        } else {
          if (!read().is("}")) {
            this.expected("} or ,");
          }
        }
      }
    } catch (IndexOutOfBoundsException ex) {
      throw new Exception(
        String.format(
          "Object at %d was never closed",
          startOffset
        )
      );
    }
    
    if (!read().is("}")) this.expected("}");
    this.next();
  }
  public void parseValue () throws Exception {
    Lexer.Token peeked = this.read();
    if (peeked.is("{")) {
      this.parseObject();
    } else if (peeked.is("[")) {
      this.parseArray();
    } else if (peeked.is(Lexer.TokenType.STRING)) {
      this.stack.push(new JSON.JsonString(peeked.toString()));
      this.next();
    } else if (peeked.is(Lexer.TokenType.NUMBER)) {
      this.stack.push(new JSON.JsonNumber(peeked.toString()));
      this.next();
    } else if (peeked.is("true")) {
      this.stack.push(new JSON.JsonBoolean(true));
      this.next();
    } else if (peeked.is("false")) {
      this.stack.push(new JSON.JsonBoolean(false));
      this.next();
    }
  }
  public void child () {
    int childOffset = this.stack.size()-1;
    var child = this.stack.get(childOffset);
    
    int keyOrArrayOffset = childOffset-1;
    var keyOrArray = this.stack.get(keyOrArrayOffset);
    
    if (keyOrArray instanceof JSON.JsonString) {
      int parentOffset = keyOrArrayOffset-1;
      var parent = (JSON.JsonObject) this.stack.get(parentOffset);
      
      parent.kvPairs.put(keyOrArray.toString(), child);
      this.stack.pop(); //pop key
      this.stack.pop(); //pop value
    } else if (keyOrArray instanceof JSON.JsonArray) {
      var parent = (JSON.JsonArray) keyOrArray;
      parent.items.add(child);
      this.stack.pop(); //pop value
    }
  }
  public void parseArray () throws Exception {
    if (!read().is("[")) this.expected("[");
    int startOffset = this.offset;
    this.next();
    
    var a = new JSON.JsonArray();
    this.stack.push(a);
    
    try {
      while (!read().is("]")) {
        this.parseValue();
        this.child();
        
        //detect if next is comma, if so consume it and move on
        //otherwise we expect to close the array
        if (read().is(Lexer.TokenType.COMMA)) {
          this.next();
        } else {
          if (!read().is("]")) {
            this.expected("] or ,");
          }
        }
        
      }
    } catch (IndexOutOfBoundsException e) {
      throw new Exception(
        String.format(
          "Array at %d was never closed",
          startOffset
        )
      );
    }
    
    if (!read().is("]")) this.expected("]");
    this.next();
    
  }
  public JSON.JsonValue parse (Lexer.Token[] tokens) throws Exception {
    this.tokens = tokens;
    this.offset = 0;
    this.stack = new Stack();
    
    try { this.parseObject(); }
    catch (Exception e) {
      try { this.parseArray(); }
      catch (Exception e2) {
        throw e;
      }
    }
    
    return this.stack.firstElement();
  }
}
