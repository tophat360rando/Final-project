import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Supplier;
 
/**
 * Benchmarks traversal, random access, insertions and deletions on:
 *   MyArrayList, MyLinkedList, java.util.ArrayList, java.util.LinkedList
 *
 * Times are the average over TRIALS runs (after one warm-up run), in milliseconds.
 */
public class Benchmark {
 
    static final int N = 100_000;     // initial list size
    static final int OPS = 1_000;     // insertions / deletions per test
    static final int LOOKUPS = 100;   // random lookups
    static final int TRIALS = 5;
 
    static volatile long sink;        // consume results so the JIT can't discard the work
 
    interface Op {
        void run(MyList<Integer> list);
    }
 
    static MyList<Integer> filled(Supplier<MyList<Integer>> factory) {
        MyList<Integer> list = factory.get();
        for (int i = 0; i < N; i++) list.add(i);
        return list;
    }
 
    static double timeMs(Supplier<MyList<Integer>> factory, Op op) {
        op.run(filled(factory)); // warm-up
        long total = 0;
        for (int t = 0; t < TRIALS; t++) {
            MyList<Integer> list = filled(factory); // fresh list, not timed
            long start = System.nanoTime();
            op.run(list);
            total += System.nanoTime() - start;
        }
        return total / (double) TRIALS / 1_000_000.0;
    }
 
    // ---------------- correctness check ----------------
 
    static void verify() {
        MyList<Integer> mine = new MyArrayList<>();
        MyList<Integer> mineLL = new MyLinkedList<>();
        java.util.List<Integer> ref = new ArrayList<>();
        Random rnd = new Random(1);
        for (int step = 0; step < 5_000; step++) {
            int action = rnd.nextInt(3);
            if (action == 0 || ref.isEmpty()) {
                int idx = rnd.nextInt(ref.size() + 1);
                int val = rnd.nextInt(1000);
                ref.add(idx, val);
                mine.add(idx, val);
                mineLL.add(idx, val);
            } else if (action == 1) {
                int idx = rnd.nextInt(ref.size());
                int expected = ref.remove(idx);
                if (mine.remove(idx) != expected || mineLL.remove(idx) != expected) {
                    throw new AssertionError("remove mismatch at step " + step);
                }
            } else {
                int val = rnd.nextInt(1000);
                ref.add(val);
                mine.add(val);
                mineLL.add(val);
            }
        }
        if (mine.size() != ref.size() || mineLL.size() != ref.size()) {
            throw new AssertionError("size mismatch");
        }
        int i = 0;
        for (int v : mine)   if (v != ref.get(i++)) throw new AssertionError("ArrayList content mismatch");
        i = 0;
        for (int v : mineLL) if (v != ref.get(i++)) throw new AssertionError("LinkedList content mismatch");
        System.out.println("Correctness check passed (5000 random ops vs java.util.ArrayList).\n");
    }
 
    // ---------------- main ----------------
 
    public static void main(String[] args) {
        verify();
 
        String[] names = {"MyArrayList", "MyLinkedList", "java.util.ArrayList", "java.util.LinkedList"};
        @SuppressWarnings("unchecked")
        Supplier<MyList<Integer>>[] factories = new Supplier[] {
            (Supplier<MyList<Integer>>) MyArrayList::new,
            (Supplier<MyList<Integer>>) MyLinkedList::new,
            (Supplier<MyList<Integer>>) () -> new JavaListAdapter<>(new ArrayList<Integer>()),
            (Supplier<MyList<Integer>>) () -> new JavaListAdapter<>(new LinkedList<Integer>())
        };
 
        // fixed random indexes so every structure does identical lookups
        Random rnd = new Random(42);
        int[] lookupIdx = new int[LOOKUPS];
        for (int i = 0; i < LOOKUPS; i++) lookupIdx[i] = rnd.nextInt(N);
 
        String[] testNames = {
            "Sequential traversal (all N)",
            "Random access (" + LOOKUPS + " lookups)",
            "Insert at beginning (" + OPS + ")",
            "Insert at middle (" + OPS + ")",
            "Insert at end (" + OPS + ")",
            "Delete at beginning (" + OPS + ")",
            "Delete at middle (" + OPS + ")",
            "Delete at end (" + OPS + ")"
        };
 
        Op[] ops = {
            list -> { long s = 0; for (int x : list) s += x; sink = s; },
            list -> { long s = 0; for (int idx : lookupIdx) s += list.get(idx); sink = s; },
            list -> { for (int i = 0; i < OPS; i++) list.add(0, i); },
            list -> { for (int i = 0; i < OPS; i++) list.add(list.size() / 2, i); },
            list -> { for (int i = 0; i < OPS; i++) list.add(i); },
            list -> { for (int i = 0; i < OPS; i++) list.remove(0); },
            list -> { for (int i = 0; i < OPS; i++) list.remove(list.size() / 2); },
            list -> { for (int i = 0; i < OPS; i++) list.remove(list.size() - 1); }
        };
 
        System.out.printf("N = %,d elements, average of %d trials, times in milliseconds%n%n", N, TRIALS);
        System.out.printf("%-32s", "Operation");
        for (String n : names) System.out.printf("%22s", n);
        System.out.println();
        System.out.println("-".repeat(32 + 22 * names.length));
 
        for (int t = 0; t < ops.length; t++) {
            System.out.printf("%-32s", testNames[t]);
            for (int d = 0; d < factories.length; d++) {
                System.out.printf("%22.3f", timeMs(factories[d], ops[t]));
            }
            System.out.println();
        }
    }
}
 