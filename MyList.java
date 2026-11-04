/**
 * Minimal list interface shared by the custom implementations and by the
 * adapter that wraps java.util lists, so one benchmark can drive all four.
 */
public interface MyList<T> extends Iterable<T> {
    void add(T element);              // append at end
    void add(int index, T element);   // insert at index (0..size)
    T get(int index);
    T remove(int index);              // remove and return element at index
    int size();
 
    default boolean isEmpty() {
        return size() == 0;
    }
}
 