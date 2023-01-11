import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class tests {
    public static void main(String[] args) {
//        firstTest((int) Math.pow(2, 5));
//        firstTest((int) Math.pow(2, 10));
//        firstTest((int) Math.pow(2, 15));
//        firstTest((int) Math.pow(2, 20));
        secondTest((int) (Math.pow(3, 6)) -1);
        secondTest((int) (Math.pow(3, 8)) -1);
        secondTest((int) (Math.pow(3, 10)) -1);
        secondTest((int) (Math.pow(3, 12)) -1);
        secondTest((int) (Math.pow(3, 14)) -1);
    }
    public static void firstTest(int m){
        System.out.println("m: " + m);
        FibonacciHeap heap = new FibonacciHeap();
        Map<Integer, FibonacciHeap.HeapNode> nodes = new HashMap<>();
        long start = System.currentTimeMillis();
        for (int i = m-1; i > -2; i--){
            FibonacciHeap.HeapNode insertedNode = heap.insert(i);
            nodes.put(i, insertedNode);
        }
        heap.deleteMin();
        int num = (int) Math.floor(Math.log(m)/Math.log(2));
        for (int i = num; i >0; i--){
            heap.decreaseKey(nodes.get((int) (m -((Math.pow(2,i))))+1), m +1);

        }
        heap.decreaseKey(nodes.get(m-2), m+1);
        long finish = System.currentTimeMillis();
        long total = finish - start;
        System.out.println("running time: " + total);
        System.out.println("total links: " + FibonacciHeap.totalLinks);
        FibonacciHeap.totalLinks = 0;
        System.out.println("total cuts: " + FibonacciHeap.totalCuts);
        FibonacciHeap.totalCuts = 0;
        System.out.println("potential: " + heap.potential());

    }

    public static void secondTest(int m){
        System.out.println("m: " + m);
        FibonacciHeap heap = new FibonacciHeap();
        long start = System.currentTimeMillis();
        for (int i = 0; i < m+1; i++){
            heap.insert(i);
        }
        int num = (3*m/4)+1;
        for (int i = 1; i < num; i++){
            heap.deleteMin();

        }
        long finish = System.currentTimeMillis();
        long total = finish - start;
        System.out.println("running time: " + total);
        System.out.println("total links: " + FibonacciHeap.totalLinks);
        FibonacciHeap.totalLinks = 0;
        System.out.println("total cuts: " + FibonacciHeap.totalCuts);
        FibonacciHeap.totalCuts = 0;
        System.out.println("potential: " + heap.potential());

    }
}
