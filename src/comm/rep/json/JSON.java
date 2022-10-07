package comm.rep.json;

import java.util.*;

import static comm.rep.json.JSON.ParseMode.*;
import static comm.rep.json.Lexer.TokenType.*;
import static comm.rep.json.Lexer.lex;

public class JSON {
  public static class JsonValue {
  
  }
  
  public static class JsonObject extends JsonValue {
    Map<String, JsonValue> kvPairs;
    public JsonObject () {
      this.kvPairs = new HashMap();
    }
    @Override
    public String toString() {
      return "[Object]";
    }
  }
  
  public static class JsonArray extends JsonValue {
    ArrayList<JsonValue> items;
    public JsonArray() {
      this.items = new ArrayList();
    }
    @Override
    public String toString() {
      return "[Array]";
    }
  }
  
  public static class JsonString extends JsonValue {
    public String value;
    public JsonString(String v) {
      this.value = v;
    }
    @Override
    public String toString() {
      return this.value;
    }
  }
  
  public static class JsonNumber extends JsonValue {
    public double value;
    public JsonNumber (double n) {
      this.value = n;
    }
    @Override
    public String toString() {
      return Double.toString(this.value);
    }
  }
  
  public enum ParseMode {
    OPEN, //looking for {, [, or ], }
    STRING_KEY, //looking for "..." before :
    ASSIGN, //looking for :
    VALUE, //looking for "...", 0-9, { or [
  }
  
  public static String stringify (JsonValue v) {
    StringBuilder sb = new StringBuilder();
    
    if (v instanceof JsonObject) {
      sb.append("{");
      for (var e : ((JsonObject) v).kvPairs.entrySet()) {
        sb.append("\"");
        sb.append(e.getKey());
        sb.append("\":");
        
        JsonValue cv = e.getValue();
        
        sb.append(stringify(cv));
        
        sb.append(",");
      }
      sb.deleteCharAt(sb.length()-1); //remove trailing comma
      
      sb.append("}");
    } else if (v instanceof JsonString) {
      sb.append("\"");
      sb.append(v);
      sb.append("\"");
    } else if (v instanceof JsonNumber) {
      sb.append(v);
    } else if (v instanceof JsonArray) {
      sb.append("[");
      
      for (var iv : ((JsonArray) v).items) {
        sb.append(stringify(iv));
        sb.append(",");
      }
      sb.deleteCharAt(sb.length()-1);
      
      sb.append("]");
    }
    
    return sb.toString();
  }
  
  public static JsonValue parse (String src) {
    var tokens = lex(src);
    
    JsonValue result = null;
    
    var stack = new Stack<JsonValue>();
    
    String currentKey = null;
    
    var currentMode = ParseMode.OPEN;
    
    int len = tokens.size();
    Lexer.Token t;
    
    JsonValue parent;
    JsonValue value;
    
    for (int i=0; i<len; i++) {
      t = tokens.get(i);
      
      switch (currentMode) {
        case OPEN:
          if (t.is("{")) {
            stack.push(new JsonObject());
            currentMode = STRING_KEY;
          } else if (t.is("[")) {
            stack.push(new JsonArray());
            currentMode = VALUE;
          } else if (t.is(COMMA)) {
            currentMode = STRING_KEY;
          } else if (t.is("}")) {
            result = stack.pop();
          } else if (t.is("]")) {
            result = stack.pop();
          } else {
            System.err.printf("Mode: %s, Unexpected Token: %s\n", currentMode, t);
          }
          break;
        case STRING_KEY:
          if (t.is(STRING)) {
            currentKey = t.toString();
            currentMode = ASSIGN;
          } else {
            System.err.printf("Mode: %s, Unexpected Token: %s\n", currentMode, t);
          }
          break;
        case ASSIGN:
          if (t.is(":")) {
            currentMode = VALUE;
          } else {
            System.err.printf("Mode: %s, Unexpected Token: %s\n", currentMode, t);
          }
          break;
        case VALUE:
          parent = stack.lastElement();
          
          if (t.is(STRING)) {
            value = new JsonString(t.toString());
          } else if (t.is(NUMBER)) {
            value = new JsonNumber(Double.parseDouble(t.toString()));
          } else if (t.is("[")) {
            value = new JsonArray();
            stack.push(value);
            currentMode = VALUE;
          } else {
            value = null;
            System.err.printf("Mode: %s, cannot assign token content as value: %s\n", currentMode, t);
          }
          
          if (parent instanceof JsonArray) {
            ((JsonArray) parent).items.add(value);
            currentMode = OPEN;
          } else if (parent instanceof JsonObject) {
            if (currentKey == null) {
              System.err.printf("Mode: %s, cannot assign token as key: %s", currentMode, t);
            }
            ((JsonObject) parent).kvPairs.put(currentKey, value);
            currentMode = OPEN;
          } else {
            System.err.printf("Mode: %s, cannot assign value to object or array, last object in stack was not object or array: %s", currentMode, parent);
          }
          break;
      }
      
    }
    
    return result;
  }
}
