package comm.rep.json;

import javax.management.StringValueExp;
import java.lang.reflect.Type;
import java.util.*;

import static comm.rep.json.Lexer.lex;

public class JSON {
  public static class JsonValue {
    public JsonObject asObject () {
      return (JsonObject) this;
    }
    public JsonArray asArray () {
      return (JsonArray) this;
    }
    public String asString () {
      return ((JsonString) this).value;
    }
    public double asNumber () {
      return ((JsonNumber) this).value;
    }
    public boolean asBoolean () {
      return ((JsonBoolean)this).value;
    }
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
    public JsonValue get (String key) {
      return this.kvPairs.get(key);
    }
    public void set (String key, JsonValue v) {
      this.kvPairs.put(key, v);
    }
    public boolean has (String key) {return this.kvPairs.containsKey(key);}
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
    
    public JsonValue get (int index) {
      return this.items.get(index);
    }
    public void set (int index, JsonValue v) {
      this.items.set(index, v);
    }
    
    public int size () {
      return this.items.size();
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
    public JsonNumber (String s) {
      this.value = Double.parseDouble(s);
    }
    @Override
    public String toString() {
      return Double.toString(this.value);
    }
  }
  
  public static class JsonBoolean extends JsonValue {
    public boolean value;
    public JsonBoolean(boolean v) { this.value = v;}
    public JsonBoolean(String s) { this.value = Boolean.parseBoolean(s);}
    public String toString () {
      return Boolean.toString(this.value);
    }
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
    } else if (v instanceof JsonBoolean) {
      var b = (JsonBoolean)v;
      var s = b.toString();
      sb.append(s);
    }
    
    return sb.toString();
  }
  
  public static Parser PARSER = new Parser();
  
  public static JsonValue parse (String src) throws Exception {
    var tokens = lex(src);
    
    var tokensArray = new Lexer.Token[tokens.size()];
    
    tokens.toArray(tokensArray);
    
//    int i=0;
//    for (var t : tokensArray) {
//      System.out.printf("%d '%s' ", i, t);
//      i++;
//    }
    
    return PARSER.parse(tokensArray);
  }
}
