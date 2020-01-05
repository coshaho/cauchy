package com.coshaho.fptree;

import java.util.HashMap;
import java.util.Map;

/**
 * FP树节点
 * @Author coshaho
 */
public class FPNode {
    // 单词
    private String word;
    // 单词出现次数
    private int count = 1;
    // 子节点
    Map<String, FPNode> children = new HashMap<>();
    // 父节点
    private FPNode father;
    // 线索：指向下一个相同单词节点
    private FPNode next;
    // 是否有线索指向自己
    private boolean visited = false;

    public FPNode(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public void increase() {
        count++;
    }

    public void print(int n) {
        for(int i = 0; i < n; i++) {
            if(i == n - 1) {
                System.out.print("--");
            } else {
                System.out.print("  ");
            }
        }
        System.out.println(word + ":" + count);
        for(FPNode child : children.values()) {
            child.print(n + 1);
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<String, FPNode> getChildren() {
        return children;
    }

    public void setChildren(Map<String, FPNode> children) {
        this.children = children;
    }

    public FPNode getFather() {
        return father;
    }

    public void setFather(FPNode father) {
        this.father = father;
    }

    public FPNode getNext() {
        return next;
    }

    public void setNext(FPNode next) {
        this.next = next;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
