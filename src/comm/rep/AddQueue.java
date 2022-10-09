package comm.rep;

import java.util.ArrayList;
import java.util.List;

public class AddQueue<T> {
  private List<T> list;
  
  private int offset;
  
  public AddQueue () {
    this.list = new ArrayList();
  }
  
  public T next () {
    var result = this.list.get(offset);
    this.offset ++;
    return result;
  }
  
  public void reset () {
    this.offset = 0;
  }
  
  public void clear () {
    this.reset();
    this.list.clear();
  }
  
  public boolean hasNext () {
    return this.offset < this.list.size();
  }
  
  public void add (T i) {
    this.list.add(i);
  }
  
  public T remove () {
    T result;
    
//    if (this.offset < 1) result = this.list.remove(this.offset);
//    else result = this.list.remove(this.offset-1);
    
    result = this.list.remove(this.offset-1);
    
    this.offset--;
    
    return result;
  }
  
  public int size () {
    return this.list.size();
  }
  
}
