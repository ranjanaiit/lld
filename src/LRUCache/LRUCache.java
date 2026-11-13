package LRUCache;

import java.util.HashMap;

public class LRUCache<K,V> {
    int capacity;
    Node<K,V> head;
    Node<K,V> tail;
    HashMap<K,Node<K,V>> cache;
    LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node<K, V>(null, null);
        tail = new Node<K, V>(null, null);
        cache = new HashMap<K,Node<K,V>>(capacity);
        this.head.next = tail;
        this.tail.prev = head;
    }

    public synchronized void put(K Key, V Value){
//
//        System.out.println(
//                Thread.currentThread().getName() + " PUT " + Key
//        );
        if (capacity <= 0) {
            return;
        }

        if(cache.containsKey(Key)){
            Node<K, V> node = cache.get(Key);
            node.value = Value;
            remove(node);
            add(node);
            return;
        }

        if(cache.size() >= capacity){
            Node<K, V> node = tail.prev;
            remove(node);
            cache.remove(node.key);
        }

        Node<K, V> currNode = new Node(Key, Value);

        cache.put(Key,currNode );

        add(currNode);

    }


    public synchronized V get(K key){


//        System.out.println(
//                Thread.currentThread().getName() + " GET " + key
//        );
        if (capacity <= 0) {
            return null;
        }

        if(cache.containsKey(key)){
            Node<K, V> node = cache.get(key);
            remove(node);
            add(node);
            return node.value;
        }

        return null;
    }

    private void remove(Node<K, V> node){
        node.prev.next = node.next;
        node.next.prev = node.prev;

    }

    private void add(Node<K, V> node){
        Node<K,V> nextNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = nextNode;
        nextNode.prev= node;

    }

}

class Node<K,V> {
    K key;
    V value;
    Node<K,V> next;
    Node<K,V> prev;

    Node(K key, V value){
        this.key = key;
        this.value = value;
        this.next = null;
        this.prev = null;
    }
}

