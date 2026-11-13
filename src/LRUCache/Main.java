package LRUCache;

public class Main {
    public static void main(String[] args) throws InterruptedException{


        LRUCache<String, String> lru = new LRUCache<String, String>(3);


        lru.put("ranjana", "prabhat");
//
        lru.put("r","p");
        lru.put("A","B");
        lru.put("C","D");

        Thread thread1 = new Thread(() -> {
            System.out.println("inside thread1");
            lru.put("r","p1");
            System.out.println("thread1 "+lru.get("r"));
            lru.put("ranjana","rani");
            lru.put("D","E");
            System.out.println("thread1 "+lru.get("ranjana"));

        });

        Thread thread2 = new Thread(() -> {
            System.out.println("inside thread2");
            System.out.println("thread2 "+lru.get("ranjana"));
            lru.put("r1","p2");
            System.out.println("got t2 "+lru.get("r"));
        });
        thread1.start();
        thread2.start();


        thread1.join();
        thread2.join();
        System.out.println("got main  "+lru.get("ranjana"));
        System.out.println("got main "+lru.get("r"));
        System.out.println("got main "+lru.get("A"));
        System.out.println("got main "+lru.get("C"));


    }
}
