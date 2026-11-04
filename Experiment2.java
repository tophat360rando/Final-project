import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Times the same operations on four lists:
 *   MyArrayList2, MyLinkedList2, java.util.ArrayList, java.util.LinkedList
 * and writes the averages to results.csv (open it in Excel / Google Sheets to graph).
 *
 * Run with no arguments for the full experiment, or with "quick" to test it fast:
 *   java Experiment2 quick
 */
public class Experiment2 {

    static final int RUNS = 10;           // timed runs per test (averaged)
    static final int WARMUP_RUNS = 3;     // untimed runs first so Java can "warm up"
    static final int OPS_PER_RUN = 100;   // inserts/deletes/lookups per run
    static final long SEED = 42;          // fixed seed -> same random indexes every time

    /**
     * The four lists have the same method names but no shared type,
     * so this tiny "adapter" lets the timing code treat them all the same way.
     */
    interface TestList<T> extends Iterable<T> {
        void add(T item);
        void add(int index, T item);
        T get(int index);
        T remove(int index);
        int size();
    }

    // ---------- adapters: one per list ----------

    static <T> TestList<T> wrapMyArrayList() {
        MyArrayList2<T> list = new MyArrayList2<>();
        return new TestList<T>() {
            public void add(T item) { list.add(item); }
            public void add(int i, T item) { list.add(i, item); }
            public T get(int i) { return list.get(i); }
            public T remove(int i) { return list.remove(i); }
            public int size() { return list.size(); }
            public java.util.Iterator<T> iterator() { return list.iterator(); }
        };
    }

    static <T> TestList<T> wrapMyLinkedList() {
        MyLinkedList2<T> list = new MyLinkedList2<>();
        return new TestList<T>() {
            public void add(T item) { list.add(item); }
            public void add(int i, T item) { list.add(i, item); }
            public T get(int i) { return list.get(i); }
            public T remove(int i) { return list.remove(i); }
            public int size() { return list.size(); }
            public java.util.Iterator<T> iterator() { return list.iterator(); }
        };
    }

    static <T> TestList<T> wrapJavaArrayList() {
        ArrayList<T> list = new ArrayList<>();
        return new TestList<T>() {
            public void add(T item) { list.add(item); }
            public void add(int i, T item) { list.add(i, item); }
            public T get(int i) { return list.get(i); }
            public T remove(int i) { return list.remove(i); }
            public int size() { return list.size(); }
            public java.util.Iterator<T> iterator() { return list.iterator(); }
        };
    }

    static <T> TestList<T> wrapJavaLinkedList() {
        LinkedList<T> list = new LinkedList<>();
        return new TestList<T>() {
            public void add(T item) { list.add(item); }
            public void add(int i, T item) { list.add(i, item); }
            public T get(int i) { return list.get(i); }
            public T remove(int i) { return list.remove(i); }
            public int size() { return list.size(); }
            public java.util.Iterator<T> iterator() { return list.iterator(); }
        };
    }

    static final String[] LIST_NAMES = {
        "MyArrayList2", "MyLinkedList2", "JavaArrayList", "JavaLinkedList"
    };

    static <T> TestList<T> makeList(String name) {
        switch (name) {
            case "MyArrayList2":    return wrapMyArrayList();
            case "MyLinkedList2":   return wrapMyLinkedList();
            case "JavaArrayList":  return wrapJavaArrayList();
            default:               return wrapJavaLinkedList();
        }
    }

    // ---------- the operations being tested ----------

    static final String[] OPERATIONS = {
        "traversal", "randomAccess",
        "insertFront", "insertMiddle", "insertEnd",
        "deleteFront", "deleteMiddle", "deleteEnd"
    };

    /** Prevents Java from skipping "useless" work during traversal/lookups. */
    static long blackhole = 0;

    /**
     * Runs ONE operation once and returns how long it took in nanoseconds.
     * The list is built BEFORE the stopwatch starts, so only the operation is timed.
     */
    static <T> long runOnce(String listName, String op, T[] items, int[] randomIndexes) {
        TestList<T> list = makeList(listName);
        for (T item : items) {
            list.add(item);                 // build the list (not timed)
        }
        T extra = items[0];

        long start = System.nanoTime();     // ---- stopwatch starts ----
        switch (op) {
            case "traversal":
                for (T item : list) {       // for-each = iterator, fair for linked lists
                    blackhole += item.hashCode();
                }
                break;
            case "randomAccess":
                for (int idx : randomIndexes) {
                    blackhole += list.get(idx).hashCode();
                }
                break;
            case "insertFront":
                for (int i = 0; i < OPS_PER_RUN; i++) list.add(0, extra);
                break;
            case "insertMiddle":
                for (int i = 0; i < OPS_PER_RUN; i++) list.add(list.size() / 2, extra);
                break;
            case "insertEnd":
                for (int i = 0; i < OPS_PER_RUN; i++) list.add(extra);
                break;
            case "deleteFront":
                for (int i = 0; i < OPS_PER_RUN; i++) list.remove(0);
                break;
            case "deleteMiddle":
                for (int i = 0; i < OPS_PER_RUN; i++) list.remove(list.size() / 2);
                break;
            case "deleteEnd":
                for (int i = 0; i < OPS_PER_RUN; i++) list.remove(list.size() - 1);
                break;
        }
        long end = System.nanoTime();       // ---- stopwatch stops ----
        return end - start;
    }

    /** Warms up, then averages RUNS timed runs. Returns milliseconds. */
    static <T> double averageMs(String listName, String op, T[] items, int[] randomIndexes) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            runOnce(listName, op, items, randomIndexes);
        }
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            total += runOnce(listName, op, items, randomIndexes);
        }
        return (total / (double) RUNS) / 1_000_000.0;   // ns -> ms
    }

    // ---------- test data ----------

    static Integer[] makeIntegers(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        return arr;
    }

    static String[] makeStrings(int n) {
        String[] arr = new String[n];
        for (int i = 0; i < n; i++) arr[i] = "item" + i;
        return arr;
    }

    static int[] makeRandomIndexes(int n) {
        Random rand = new Random(SEED);
        int[] idx = new int[OPS_PER_RUN];
        for (int i = 0; i < OPS_PER_RUN; i++) idx[i] = rand.nextInt(n);
        return idx;
    }

    // ---------- main ----------

    public static void main(String[] args) throws IOException {
        boolean quick = args.length > 0 && args[0].equals("quick");
        int[] sizes = quick
                ? new int[] {1_000, 5_000}
                : new int[] {10_000, 50_000, 100_000, 300_000, 500_000};

        try (PrintWriter out = new PrintWriter(new FileWriter("results.csv"))) {
            out.println("list,operation,size,dataType,avgMs");

            for (int n : sizes) {
                int[] randomIndexes = makeRandomIndexes(n);
                Object[][] dataSets = { makeIntegers(n), makeStrings(n) };
                String[] typeNames = { "Integer", "String" };

                for (int t = 0; t < dataSets.length; t++) {
                    for (String op : OPERATIONS) {
                        for (String listName : LIST_NAMES) {
                            double ms = averageMs(listName, op, dataSets[t], randomIndexes);
                            String row = listName + "," + op + "," + n + "," + typeNames[t] + ","
                                    + String.format("%.4f", ms);
                            out.println(row);
                            System.out.println(row);   // so you can watch progress
                        }
                    }
                }
            }
        }
        System.out.println("Done! Results saved to results.csv  (ignore: " + (blackhole % 2) + ")");
    }
}
