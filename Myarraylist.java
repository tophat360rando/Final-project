import java.util.Iterator;
import java.util.NoSuchElementException;
 
/**
 * ArrayList backed by a dynamic array that doubles when full
 * (and shrinks by half when it becomes a quarter full).
 */
public class MyArrayList<T> implements MyList<T> {
    private static final int DEFAULT_CAPACITY = 10;
 
    private Object[] data;
    private int size;
 
    public MyArrayList() {
        this(DEFAULT_CAPACITY);
    }
 
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive: " + initialCapacity);
        }
        data = new Object[initialCapacity];
    }
 
    @Override
    public void add(T element) {
        ensureCapacity();
        data[size++] = element;
    }
 
    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity();
        // shift elements right to open a gap at index
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }
 
    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }
 
    @Override
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        // shift elements left to close the gap
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null; // let GC reclaim the reference
        shrinkIfNeeded();
        return removed;
    }
 
    @Override
    public int size() {
        return size;
    }
 
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
 
    private void ensureCapacity() {
        if (size == data.length) {
            resize(data.length * 2);
        }
    }
 
    private void shrinkIfNeeded() {
        if (data.length > DEFAULT_CAPACITY && size <= data.length / 4) {
            resize(Math.max(DEFAULT_CAPACITY, data.length / 2));
        }
    }
 
    private void resize(int newCapacity) {
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }
 
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;
 
            @Override
            public boolean hasNext() {
                return cursor < size;
            }
 
            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) data[cursor++];
            }
        };
    }
}
 