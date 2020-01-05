package com.coshaho.fptree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FP树
 * @Author coshaho
 */
public class FPTree {
    // FP树根节点
    FPNode root = new FPNode("root", -1);
    // FP树节点线索头
    Map<String, FPNode> firstNodeTable = new HashMap<>();
    // FP树节点线索尾
    Map<String, FPNode> lastNodeTable = new HashMap<>();

    public FPTree(List<List<String>> data) {
        // line为一行日志
        for(List<String> line : data) {
            FPNode curNode = root;
            for(String word : line) {
                if(curNode.getChildren().containsKey(word)) {
                    // 子节点存在则访问次数加一
                    curNode.getChildren().get(word).increase();
                } else {
                    // 子节点不存在则新增子节点
                    FPNode child = new FPNode(word, 1);
                    curNode.getChildren().put(word, child);
                    child.setFather(curNode);
                }
                curNode = curNode.getChildren().get(word);

                // 当前节点有线索指向，则不必重复建立线索
                if(curNode.isVisited()) {
                    continue;
                }

                // 创建线索
                if(firstNodeTable.containsKey(word)) {
                    lastNodeTable.get(word).setNext(curNode);
                } else {
                    firstNodeTable.put(word, curNode);
                }
                lastNodeTable.put(word, curNode);
                curNode.setVisited(true);
            }
        }
    }

    public void print() {
        root.print(0);
    }

    public static void main(String[] args) {
        List<String> line1 = new ArrayList<>();
        line1.add("A");
        line1.add("B");
        line1.add("C");
        List<String> line2 = new ArrayList<>();
        line2.add("A");
        line2.add("B");
        line2.add("D");
        List<String> line3 = new ArrayList<>();
        line3.add("A");
        line3.add("B");
        List<String> line4 = new ArrayList<>();
        line4.add("C");
        line4.add("E");
        List<List<String>> data = new ArrayList<>();
        data.add(line1);
        data.add(line2);
        data.add(line3);
        data.add(line4);

        FPTree tree = new FPTree(data);
        tree.print();
    }

}
