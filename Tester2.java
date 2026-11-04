/**
 * Quick checks that MyArrayList2 and MyLinkedList2 work before timing anything.
 * Run this first. Every line should say PASS.
 */
public class Tester2 {

    public static void main(String[] args) {
        System.out.println("=== MyArrayList2 ===");
        MyArrayList2<String> arr = new MyArrayList2<>();
        arr.add("b");
        arr.add("c");
        arr.add(0, "a");          // insert at front
        arr.add(3, "d");          // insert at end by index
        check("add + insert", arr.toString(), "[a, b, c, d]");
        check("get(2)", arr.get(2), "c");
        check("remove front", arr.remove(0), "a");
        check("after remove", arr.toString(), "[b, c, d]");
        for (int i = 0; i < 100; i++) {
            arr.add("x");         // forces several resizes
        }
        check("size after resizes", arr.size(), 103);

        System.out.println("=== MyLinkedList2 ===");
        MyLinkedList2<Integer> list = new MyLinkedList2<>();
        list.add(2);
        list.add(3);
        list.addFirst(1);
        list.add(1, 99);          // insert in middle
        check("add + insert", list.toString(), "[1, 99, 2, 3]");
        check("get(3)", list.get(3), 3);
        check("remove middle", list.remove(1), 99);
        check("remove last", list.remove(2), 3);
        list.add(4);              // tail must still be correct after removing last
        check("tail still works", list.toString(), "[1, 2, 4]");
        int sum = 0;
        for (int x : list) {      // uses the iterator
            sum += x;
        }
        check("for-each sum", sum, 7);
    }

    private static void check(String name, Object actual, Object expected) {
        String result = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.println(result + "  " + name + "  -> got " + actual + ", expected " + expected);
    }
}
