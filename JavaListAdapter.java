import java.util.Iterator;
import java.util.List;
 
/** Wraps a java.util.List (ArrayList / LinkedList) behind the MyList interface. */
public class JavaListAdapter<T> implements MyList<T> {
    private final List<T> list;
 
    public JavaListAdapter(List<T> list) {
        this.list = list;
    }
 
    @Override public void add(T element)            { list.add(element); }
    @Override public void add(int index, T element) { list.add(index, element); }
    @Override public T get(int index)               { return list.get(index); }
    @Override public T remove(int index)            { return list.remove(index); } // remove(int), not remove(Object)
    @Override public int size()                     { return list.size(); }
    @Override public Iterator<T> iterator()         { return list.iterator(); }
}
 
