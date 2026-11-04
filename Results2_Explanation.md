# Results2 Explanation

This file explains `results2.csv`: what was tested, how it was measured, what the numbers mean, and why each result happened.

Code used (only these files): `MyArrayList2.java`, `MyLinkedList2.java`, `Tester2.java`, `Experiment2.java`.

---

## 1. What was compared

| List in results | Where it comes from |
|---|---|
| `MyArrayList2` | Our ArrayList (`MyArrayList2.java`): a dynamic array that doubles when full |
| `MyLinkedList2` | Our singly linked list (`MyLinkedList2.java`): nodes with `next` links, plus `head` and `tail` pointers |
| `JavaArrayList` | Java's built-in `java.util.ArrayList` |
| `JavaLinkedList` | Java's built-in `java.util.LinkedList` (doubly linked) |

---

## 2. How the results were produced

1. **Correctness check first.** `Tester2` was run before any timing. All 11 checks passed, so the lists add, insert, remove, and look up items correctly.
2. **Experiment.** `Experiment2` ran every combination of:
   - **5 list sizes:** 10,000 / 50,000 / 100,000 / 300,000 / 500,000
   - **2 data types:** Integer and String
   - **8 operations** (table below)
   - **4 lists**
   - = **320 results** (one row each in `results2.csv`)
3. **For each result:**
   - A fresh list was built and filled with items. **This was not timed.**
   - The stopwatch (`System.nanoTime()`) started, the operation ran, and the stopwatch stopped.
   - **3 untimed warm-up runs** came first, because Java speeds code up after running it a few times (JIT compiler).
   - **10 timed runs** were averaged, as the assignment requires.
   - Nanoseconds were converted to milliseconds.

### The 8 operations

| Operation | What it does |
|---|---|
| `traversal` | Visit every item from start to end with a for-each loop (iterator) |
| `randomAccess` | `get()` at 100 random positions (same 100 positions for every list, fixed seed 42) |
| `insertFront` | `add(0, x)` 100 times |
| `insertMiddle` | `add(size/2, x)` 100 times |
| `insertEnd` | `add(x)` 100 times |
| `deleteFront` | `remove(0)` 100 times |
| `deleteMiddle` | `remove(size/2)` 100 times |
| `deleteEnd` | `remove(size - 1)` 100 times |

### Fairness choices

- Traversal uses an **iterator**, not `get(i)` in a loop. Using `get(i)` on a linked list is O(n^2) and would be unfair.
- Every list gets the **same data** and the **same random positions**.
- Only the operation itself is timed, not list building or data creation.

---

## 3. Results at 500,000 items (average milliseconds)

### Integer

| Operation | MyArrayList2 | MyLinkedList2 | JavaArrayList | JavaLinkedList |
|---|---|---|---|---|
| Traversal | 6.2474 | 4.9551 | 4.7061 | 6.7939 |
| Random access (100) | 0.0151 | 44.1354 | 0.0114 | 29.4820 |
| Insert front (100) | 450.5408 | 0.0017 | 9.0873 | 0.0075 |
| Insert middle (100) | 226.3200 | 41.4049 | 2.7873 | 45.2645 |
| Insert end (100) | 0.0024 | 0.0045 | 0.0027 | 0.0017 |
| Delete front (100) | 469.4652 | 0.0059 | 7.7672 | 0.0030 |
| Delete middle (100) | 215.4425 | 41.0070 | 2.3041 | 43.6194 |
| Delete end (100) | 0.0077 | 97.2016 | 0.0028 | 0.0033 |

### String

| Operation | MyArrayList2 | MyLinkedList2 | JavaArrayList | JavaLinkedList |
|---|---|---|---|---|
| Traversal | 5.8056 | 6.0827 | 6.2233 | 7.1983 |
| Random access (100) | 0.0130 | 57.8262 | 0.0383 | 31.7055 |
| Insert front (100) | 479.8919 | 0.0071 | 8.3699 | 0.0054 |
| Insert middle (100) | 225.9064 | 41.3909 | 2.7298 | 48.2823 |
| Insert end (100) | 0.0026 | 0.0049 | 0.0048 | 0.0025 |
| Delete front (100) | 452.7105 | 0.0029 | 9.0253 | 0.0113 |
| Delete middle (100) | 228.3782 | 45.7967 | 2.9013 | 50.4604 |
| Delete end (100) | 0.0028 | 128.5251 | 0.0030 | 0.0114 |

Full results for all sizes are in `results2.csv`.

---

## 4. What each result means and why it happened

### Random access: ArrayList wins by about 3,000x
- MyArrayList2: **0.015 ms** vs MyLinkedList2: **44 ms**
- **Why:** an ArrayList stores items in numbered slots of one array, so it jumps straight to any index. A linked list must start at the head and follow links one by one until it reaches the index.
- **Theory:** ArrayList O(1), LinkedList O(n). **Matches.**

### Insert / delete at the front: LinkedList wins by about 200,000x
- MyLinkedList2: **~0.002 ms** vs MyArrayList2: **~450 ms**
- **Why:** to change index 0, an ArrayList must shift every one of the 500,000 items one slot over, and it does this 100 times. A linked list just creates a node and moves the `head` pointer, which takes the same 2 steps at any size.
- **Theory:** ArrayList O(n), LinkedList O(1). **Matches.**

### Insert / delete at the end: tie
- All four lists: **under 0.01 ms**
- **Why:** an ArrayList puts the item in the next empty slot. A linked list attaches it to the `tail` pointer. Neither has to move or search anything.
- **Theory:** both O(1) (ArrayList is amortized O(1) because of occasional resizing). **Matches.**

### Delete at the end: only MyLinkedList2 is slow
- MyLinkedList2: **97 ms**, while the other three: **~0.003 ms**
- **Why:** to remove the last node, the list needs the second-to-last node so it can become the new tail. In a **singly** linked list, nodes only point forward, so it must walk the whole list to find it. Java's LinkedList is **doubly** linked (each node also points backward), so it steps back from the tail instantly.
- **Theory:** singly linked O(n), doubly linked O(1). **Matches.**

### Insert / delete in the middle: Java's ArrayList wins, ours loses
- MyArrayList2: **~220 ms**, MyLinkedList2: **~41 ms**, JavaArrayList: **~2.5 ms**, JavaLinkedList: **~44 ms**
- **Theory says it is a tie:** the ArrayList shifts half the items (O(n)) and the linked list walks to the middle (O(n)).
- **Why Java's ArrayList wins:** it shifts items with `System.arraycopy`, which moves the whole block of memory at once. That makes shifting very cheap, and arrays sit side by side in memory, which the CPU cache handles well.
- **Why our ArrayList loses:** `MyArrayList2` shifts items one at a time in a `for` loop. That one difference makes Java's version about **50-100x faster** at shifting. It is also why Java's ArrayList is far faster than ours at the front (about 9 ms vs 450 ms).
- **Why both linked lists take ~41-45 ms:** walking to the middle means following 250,000 links. Nodes are scattered in memory, so the CPU keeps waiting on RAM (cache misses).

### Traversal: too close to call in this run
- All four lists: **about 4.7-7.2 ms**
- **Theory:** both O(n). The sources predict ArrayList is about 2x faster in practice because of the CPU cache.
- **Why the results are mixed:** traversal takes only a few milliseconds, so small background activity on the computer shifts the numbers. This run was done on a shared cloud server, which is noisier than a personal laptop. An earlier run of the same code did show ArrayList about 2x faster (2.3 ms vs 4.7 ms).

### Integer vs String: nearly identical
- **Why:** a list does not store the actual number or text. It stores a **reference** (a small memory address) to each object. A reference is the same size whether it points to an Integer or a String, so the lists behave the same way.

---

## 5. Answers to the analysis questions

| Question | Answer |
|---|---|
| When does ArrayList outperform LinkedList? | Random access, insert/delete at the end, and (Java's ArrayList) insert/delete in the middle |
| When does LinkedList shine? | Insert/delete at the **front** |
| Do the results align with theory? | Mostly yes. Exceptions: in the middle, Java's ArrayList beat both linked lists even though Big-O calls it a tie; traversal was too close to call in this run |
| Custom vs. Java API differences? | Java's ArrayList shifts 50-100x faster than ours (`System.arraycopy` vs a manual loop). Java's LinkedList deletes from the end instantly because it is doubly linked, while ours walks the whole list |

---

## 6. Test environment and caveat

- **Machine:** cloud server, Intel Xeon @ 2.80 GHz, 2 CPU cores, 7 GB RAM
- **Java:** OpenJDK 21
- **Settings:** 10 timed runs averaged, 3 warm-up runs, 100 operations per run, random seed 42

These numbers are a **preview**. Exact times depend on the computer. The patterns (who wins and why) should stay the same on any machine. For the final poster, run `Experiment2` on one team laptop, use those numbers, and record that laptop's specs here.
