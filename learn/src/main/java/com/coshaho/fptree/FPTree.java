package com.coshaho.fptree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FP树：仅考虑算法
 * @author coshaho
 * @since 2020/1/5
 */
public class FPTree {
    // FP树根节点
    FPNode root = new FPNode("Root", -1);
    // FP树节点线索头
    Map<String, FPNode> firstNodeTable = new HashMap<>();
    // FP树节点线索尾
    Map<String, FPNode> lastNodeTable = new HashMap<>();
    // 支持度
    private int support = 1;

    public FPTree(List<List<String>> data, int support) {
        this.support = support;
        data = sort(data);
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

    private List<List<String>> sort(List<List<String>> data) {
        Map<String, Integer> wordCount = new HashMap<>();
        // 统计单词出现的次数
        for(List<String> line : data) {
            for(String word : line) {
                if(wordCount.containsKey(word)) {
                    wordCount.put(word, wordCount.get(word) + 1);
                } else {
                    wordCount.put(word, 1);
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        // 单词排序
        for(List<String> line : data) {
            List<String> newLine = line.stream().filter(word -> wordCount.get(word) >= support)
                    .sorted(Comparator.comparing(word -> wordCount.get(word)).reversed()).collect(Collectors.toList());
            if(null != newLine && 0 != newLine.size()) {
                result.add(newLine);
            }
        }
        return result;
    }

    public void print() {
        root.print(0);
    }

    public static void main(String[] args) {
        List<String> line1 = new ArrayList<>();
        line1.add("C");
        line1.add("A");
        line1.add("B");
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

        FPTree tree = new FPTree(data, 1);
        tree.print();
    }
}
