import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple ArrayList built on a plain Java array that grows when it fills up.
 *
 * Big idea: items sit side by side in one array ("numbered seats").
 *  - get(i)            -> O(1)  jump straight to the seat
 *  - add at end        -> O(1) amortized (occasionally resize)
 *  - add/remove front  -> O(n)  every item has to shift
 *
 * @param <T> the type of item stored (Integer, String, Double, ...)
 */
public class MyArrayList2<T> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private T[] data;   // the "row of seats"
    private int size;   // how many seats are actually filled

    /** Creates an empty list with room for 10 items. */
    @SuppressWarnings("unchecked")
    public MyArrayList2() {
        // Java does not allow "new T[10]", so we make an Object[] and cast it.
        data = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /** Returns how many items are in the list. */
    public int size() {
        return size;
    }

    /** Returns true if the list has no items. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Adds an item to the end of the list. O(1) amortized. */
    public void add(T item) {
        if (size == data.length) {
            resize(data.length * 2);   // row is full -> build a row twice as big
        }
        data[size] = item;
        size++;
    }

    /**
     * Inserts an item at a position. Everything from that position onward
     * shifts one seat to the right. O(n - index).
     */
    public void add(int index, T item) {
        if (index < 0 || index > size) {   // index == size means "add at end"
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == data.length) {
            resize(data.length * 2);
        }
        // Shift right, starting from the back so nothing gets overwritten.
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = item;
        size++;
    }

    /** Returns the item at a position. O(1). */
    public T get(int index) {
        checkIndex(index);
        return data[index];
    }

    /** Replaces the item at a position and returns the old one. O(1). */
    public T set(int index, T item) {
        checkIndex(index);
        T old = data[index];
        data[index] = item;
        return old;
    }

    /**
     * Removes the item at a position. Everything after it shifts one seat
     * to the left to close the gap. O(n - index).
     */
    public T remove(int index) {
        checkIndex(index);
        T removed = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;   // clear the old last seat so Java can free memory
        size--;
        return removed;
    }

    /** Copies everything into a new array of the given size. O(n). */
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] bigger = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
        }
        data = bigger;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /** Lets you write: for (T item : myList) { ... } */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return data[current++];
            }
        };
    }

    /** Prints like [a, b, c] -- handy for testing. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }
}
