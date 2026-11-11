import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.Supplier;
 

public class Tester {
 
    static int passed = 0;
    static int failed = 0;
    static String current = "";
 
    static void check(boolean ok, String what) {
        if (ok) {
            passed++;
        } else {
            failed++;
            System.out.println("  FAIL [" + current + "] " + what);
        }
    }
 
    static void expectThrows(Class<? extends Throwable> type, Runnable r, String what) {
        try {
            r.run();
            check(false, what + " (no exception thrown)");
        } catch (Throwable t) {
            check(type.isInstance(t), what + " (threw " + t.getClass().getSimpleName() + ")");
        }
    }
 
    static <T> boolean sameContents(MyList<T> mine, List<T> ref) {
        if (mine.size() != ref.size()) return false;
        int i = 0;
        for (T v : mine) {
            if (!v.equals(ref.get(i++))) return false;
        }
        return i == ref.size();
    }
 
    // individual test
 
    static void testEmpty(Supplier<MyList<Integer>> f) {
       MyList<Integer> l = f.get();
        check(l.size() == 0, "new list has size 0");
        check(l.isEmpty(), "new list isEmpty");
        check(!l.iterator().hasNext(), "empty iterator has no next");
        expectThrows(IndexOutOfBoundsException.class, () -> l.get(0), "get(0) on empty");
        expectThrows(IndexOutOfBoundsException.class, () -> l.remove(0), "remove(0) on empty");
        expectThrows(IndexOutOfBoundsException.class, () -> l.add(1, 5), "add(1) on empty");
        expectThrows(NoSuchElementException.class, () -> l.iterator().next(), "next() on empty iterator");
    }
 
    static void testBasicAddGet(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        for (int i = 0; i < 100; i++) l.add(i);
        check(l.size() == 100, "size after 100 appends");
        boolean ok = true;
        for (int i = 0; i < 100; i++) ok &= l.get(i) == i;
        check(ok, "get(i) returns appended values in order");
        expectThrows(IndexOutOfBoundsException.class, () -> l.get(-1), "get(-1)");
        expectThrows(IndexOutOfBoundsException.class, () -> l.get(100), "get(size)");
    }
 
    static void testPositionalInsert(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        l.add(0, 10);//into empty
        l.add(0, 5);//front
        l.add(2, 20);//end index
        l.add(1, 7);//mid
        List<Integer> ref = new ArrayList<>(List.of(5, 7, 10, 20));
        check(sameContents(l, ref), "insert front/middle/end gives 5,7,10,20");
        expectThrows(IndexOutOfBoundsException.class, () -> l.add(-1, 1), "add(-1, x)");
        expectThrows(IndexOutOfBoundsException.class, () -> l.add(5, 1), "add(size+1, x)");
    }
 
    static void testPositionalRemove(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        for (int i = 0; i < 10; i++) l.add(i);
        check(l.remove(0) == 0, "remove(0) returns 0");
        check(l.remove(l.size() - 1) == 9, "remove(last) returns 9");
        check(l.remove(3) == 4, "remove(middle) returns 4");
        List<Integer> ref = new ArrayList<>(List.of(1, 2, 3, 5, 6, 7, 8));
        check(sameContents(l, ref), "contents after three removals");
        expectThrows(IndexOutOfBoundsException.class, () -> l.remove(-1), "remove(-1)");
        expectThrows(IndexOutOfBoundsException.class, () -> l.remove(l.size()), "remove(size)");
    }
 
    /** Catches stale head/tail pointers in the linked list after emptying it. */
    static void testEmptyThenReuse(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        for(int i = 0; i < 5; i++) l.add(i);
        while (!l.isEmpty()) l.remove(l.size() -1);   // drain from the end
        
        check(l.size() == 0, "drained from the end");
        l.add(42);
        l.add(43);
        check(sameContents(l, new ArrayList<>(List.of(42, 43))), "append works after draining from the end");
 
        while (!l.isEmpty()) l.remove(0);  // drain from front
        l.add(7);
        l.add(8);
        check(sameContents(l, new ArrayList<>(List.of(7, 8))), "append works after draining from the front");
 
        MyList<Integer> one=  f.get();
        
        one.add(1);
     
        one.remove(0);
     
        one.add(0, 2);
        one.add(3);
        check(sameContents(one, new ArrayList<>(List.of(2, 3))), "single element removed then reused");

     
    }
 
    // Appending after removing the tail needs to link to new tail instead of old tail. 
    static void testTailAfterRemoveLast(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        for (int i = 0; i < 5; i++) l.add(i);
        l.remove(4);
        l.add(99);
        check(sameContents(l, new ArrayList<>(List.of(0, 1, 2, 3, 99))), "append after remove(last)");
    }
 
    // Exercises MyArrayList increases past its capacity and shrinking back down. 
    static void testGrowAndShrink(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        List<Integer> ref = new ArrayList<>();
        for (int i = 0; i < 5_000; i++) { l.add(i); ref.add(i); }
        check(sameContents(l, ref), "contents after growing to 5000");
        while (ref.size() > 3) { l.remove(l.size() - 1); ref.remove(ref.size() - 1); }
        check(sameContents(l, ref), "contents after shrinking to 3");
        for (int i = 0; i < 50; i++) { l.add(i); ref.add(i); }
        check(sameContents(l, ref), "contents after growing again");
    }
 
    static void testRandomized(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        List<Integer> ref = new ArrayList<>();
     
        Random rnd = new Random(7);
     
        boolean ok = true;
        for (int step = 0; step < 20_000 && ok; step++) {
            int action = rnd.nextInt(3);
            if (action == 0 || ref.isEmpty()) {
                int idx = rnd.nextInt(ref.size() + 1);
                int val = rnd.nextInt(1000);
                ref.add(idx, val);
                l.add(idx, val);
            } else if (action == 1) {
                int idx = rnd.nextInt(ref.size());
                int expected = ref.remove(idx);
                ok &= l.remove(idx) == expected;
            } else {
                int val = rnd.nextInt(1000);
                ref.add(val);
                l.add(val);
            }
            ok &= l.size() == ref.size(); //checks if works
        }
        check(ok, "20000 random ops: sizes and removed values match java.util.ArrayList");
        check(sameContents(l, ref), "20000 random ops: final contents match");
    }
 
    static void testStrings(Supplier<MyList<String>> f) {
        MyList<String> l = f.get();
        List<String> ref = new ArrayList<>();
        Random rnd = new Random(11);
        for (int step = 0; step < 5_000; step++) {
            if (rnd.nextInt(3) < 2 || ref.isEmpty()) {
                int idx = rnd.nextInt(ref.size() + 1);
                String s = "s" + rnd.nextInt(500);
                ref.add(idx, s);
                l.add(idx, s);
            } else {
                int idx = rnd.nextInt(ref.size());
                String expected = ref.remove(idx);
                check(l.remove(idx).equals(expected), "String remove returns right value");
            }
        }
     
        check(sameContents(l, ref), "String list matches java.util.ArrayList");
    }
 
    static void testIteratorOrder(Supplier<MyList<Integer>> f) {
        MyList<Integer> l = f.get();
        for (int i = 0; i < 20; i++) l.add(i);
        Iterator<Integer> it = l.iterator();
        int expect = 0;
        while (it.hasNext()) check(it.next() == expect++, "iterator order");
        check(expect == 20, "iterator visits all 20 elements");
        expectThrows(NoSuchElementException.class, it::next, "next() past the end");
    }
 
    // driver
 
    static void runAll(String name, Supplier<MyList<Integer>> intFactory, Supplier<MyList<String>> strFactory) {
        System.out.println("Testing " + name + " ...");
        current = name;
        int failedBefore = failed;
        testEmpty(intFactory);
        testBasicAddGet(intFactory);
        testPositionalInsert(intFactory);
        testPositionalRemove(intFactory);
        testEmptyThenReuse(intFactory);
        testTailAfterRemoveLast(intFactory);
        testGrowAndShrink(intFactory);
        testRandomized(intFactory);
        testStrings(strFactory);
        testIteratorOrder(intFactory);
        System.out.println(failed == failedBefore ? "  ok" : "  FAILURES in " + name);
    }
 
    public static void main(String[] args) {
        runAll("MyArrayList", MyArrayList::new, MyArrayList::new);
        runAll("MyLinkedList", MyLinkedList::new, MyLinkedList::new);
        runAll("java.util.ArrayList (adapter)",
                () -> new JavaListAdapter<>(new ArrayList<Integer>()),
                () -> new JavaListAdapter<>(new ArrayList<String>()));
        runAll("java.util.LinkedList (adapter)",
                () -> new JavaListAdapter<>(new LinkedList<Integer>()),
                () -> new JavaListAdapter<>(new LinkedList<String>()));
 
        current = "MyArrayList";
        expectThrows(IllegalArgumentException.class, () -> new MyArrayList<Integer>(0), "capacity 0 rejected");
 
        System.out.printf("%n%d checks passed, %d failed%n", passed, failed);
        if (failed > 0) 
        {System.exit(1);}
    }
}
 
