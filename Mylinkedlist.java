import java.util.Iterator;
import java.util.NoSuchElementException;
 
/**
 * Singly linked list with head and tail pointers.
 * Append and remove-first are O(1); everything index-based is O(n).
 */
public class MyLinkedList<T> implements MyList<T> {
 
    private static class Node<T> {
        T value;
        Node<T> next;
 
        Node(T value) {
            this.value = value;
        }
    }
 
    private Node<T> head;
    private Node<T> tail;
    private int size;
 
    @Override
    public void add(T element) {
        Node<T> node = new Node<>(element);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }
 
    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == size) {          // end
            add(element);
            return;
        }
        Node<T> node = new Node<>(element);
        if (index == 0) {             // beginning
            node.next = head;
            head = node;
        } else {                      // middle: walk to the predecessor
            Node<T> prev = nodeAt(index - 1);
            node.next = prev.next;
            prev.next = node;
        }
        size++;
    }
 
    @Override
    public T get(int index) {
        checkIndex(index);
        return nodeAt(index).value;
    }
 
    @Override
    public T remove(int index) {
        checkIndex(index);
        Node<T> removed;
        if (index == 0) {
            removed = head;
            head = head.next;
            if (head == null) tail = null;
        } else {
            Node<T> prev = nodeAt(index - 1);
            removed = prev.next;
            prev.next = removed.next;
            if (removed == tail) tail = prev;   // removed the last node
        }
        removed.next = null;
        size--;
        return removed.value;
    }
 
    @Override
    public int size() {
        return size;
    }
 
    private Node<T> nodeAt(int index) {
        Node<T> cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur;
    }
 
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
 
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> cur = head;
 
            @Override
            public boolean hasNext() {
                return cur != null;
            }
 
            @Override
            public T next() {
                if (cur == null) throw new NoSuchElementException();
                T v = cur.value;
                cur = cur.next;
                return v;
            }
        };
    }
}
 
