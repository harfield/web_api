package com.fancydsp.data.domain;

public class Pair<K,V> {
    K left;
    V right;

    public Pair(K key,V value){
        this.left = key;
        this.right = value;
    }


    public K getLeft() {
        return left;
    }

    public void setLeft(K left) {
        this.left = left;
    }

    public V getRight() {
        return right;
    }

    public void setRight(V right) {
        this.right = right;
    }
}
