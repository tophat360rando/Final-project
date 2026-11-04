import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple singly linked list. Each node holds an item and a link to the next node
 * ("scavenger hunt cards").
 *
 * Big idea: no shifting, but you have to walk from the head to reach a position.
 *  - add/remove front -> O(1)  just move the head
 *  - add at end       -> O(1)  thanks to the tail pointer
 *  - remove at end    -> O(n)  must walk to the second-to-last node (singly linked!)
 *  - get(i)           -> O(n)  walk i nodes from the head
 *
 * @param <T> the type of item stored (Integer, String, Double, ...)
 */
public class MyLinkedList2<T> implements Iterable<T> {

    /** One "card": the item plus a link to the next card. */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;   // first node
    private Node<T> tail;   // last node (makes add-at-end O(1))
    private int size;

    /** Creates an empty list. */
    public MyLinkedList2() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Adds an item to the front. O(1). */
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = head;   // new card points at the old first card
        head = newNode;        // new card becomes the first card
        if (tail == null) {    // list was empty, so it is also the last card
            tail = newNode;
        }
        size++;
    }

    /** Adds an item to the end. O(1) because we keep a tail pointer. */
    public void add(T item) {
        Node<T> newNode = new Node<>(item);
        if (head == null) {        // empty list
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;   // old last card points at the new card
            tail = newNode;        // new card becomes the last card
        }
        size++;
    }

    /** Inserts an item at a position. O(index) because of the walk. */
    public void add(int index, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            addFirst(item);
            return;
        }
        if (index == size) {
            add(item);
            return;
        }
        Node<T> before = nodeAt(index - 1);   // walk to the node just before the spot
        Node<T> newNode = new Node<>(item);
        newNode.next = before.next;           // new card points at what came after
        before.next = newNode;                // previous card now points at new card
        size++;
    }

    /** Returns the item at a position. O(index). */
    public T get(int index) {
        checkIndex(index);
        return nodeAt(index).data;
    }

    /** Replaces the item at a position and returns the old one. O(index). */
    public T set(int index, T item) {
        checkIndex(index);
        Node<T> node = nodeAt(index);
        T old = node.data;
        node.data = item;
        return old;
    }

    /** Removes the first item. O(1). */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        T removed = head.data;
        head = head.next;      // second card becomes the first
        if (head == null) {    // list is now empty
            tail = null;
        }
        size--;
        return removed;
    }

    /**
     * Removes the item at a position. O(index) because of the walk.
     * Note: removing the LAST item is O(n) in a singly linked list, because
     * we have to find the node before it to update the tail.
     */
    public T remove(int index) {
        checkIndex(index);
        if (index == 0) {
            return removeFirst();
        }
        Node<T> before = nodeAt(index - 1);
        Node<T> target = before.next;
        before.next = target.next;   // skip over the removed card
        if (target == tail) {        // removed the last card
            tail = before;
        }
        size--;
        return target.data;
    }

    /** Walks from the head to the node at a position. O(index). */
    private Node<T> nodeAt(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /** Lets you write: for (T item : myList) { ... }  -- O(n) total traversal. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T item = current.data;
                current = current.next;
                return item;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        return sb.append("]").toString();
    }
}
