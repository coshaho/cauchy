package com.coshaho.fptree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FP树：仅考虑算法
 *
 * @author coshaho
 * @since 2020/1/5
 */
public class FPTree {
    // FP树根节点
    private FPNode root = new FPNode("Root", -1);
    // FP树节点线索头
    private Map<String, FPNode> firstNodeTable = new HashMap<>();
    // FP树节点线索尾
    private Map<String, FPNode> lastNodeTable = new HashMap<>();
    // 支持度
    private int support = 1;
    // 树的单词统计列表，降序
    private List<FPNode> table = new ArrayList<>();

    /**
     * 创建FP树
     * @param data 多行数据
     * @param count 每行数据出现次数
     * @param support 支持度
     */
    public FPTree(List<List<String>> data, List<Integer> count, int support) {
        this.support = support;
        if (null == count) {
            int size = data.size();
            count = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                count.add(1);
            }
        }
        data = sort(data, count);
        // line为一行日志
        int i = 0;
        for (List<String> line : data) {
            FPNode curNode = root;
            for (String word : line) {
                if (curNode.getChildren().containsKey(word)) {
                    // 子节点存在则访问次数加一
                    curNode.getChildren().get(word).increase(count.get(i));
                } else {
                    // 子节点不存在则新增子节点
                    FPNode child = new FPNode(word, count.get(i));
                    curNode.getChildren().put(word, child);
                    child.setFather(curNode);
                }
                curNode = curNode.getChildren().get(word);
                // 当前节点有线索指向，则不必重复建立线索
                if (curNode.isVisited()) {
                    continue;
                }
                // 创建线索
                if (firstNodeTable.containsKey(word)) {
                    lastNodeTable.get(word).setNext(curNode);
                } else {
                    firstNodeTable.put(word, curNode);
                }
                lastNodeTable.put(word, curNode);
                curNode.setVisited(true);
            }
            i++;
        }
    }

    public void print() {
        root.print(0);
    }

    /**
     * 获取日志模板
     * @param last 下层节点
     */
    public void growth(List<String> last, List<LogTemplate> templates) {
        if (isSingleTree(this.root)) {
            getSingleTreeTemplate(last, templates);
        } else {
            getMultiTreeTemplate(last, templates);
        }
    }

    private void getWordTable(Map<String, Integer> wordCount) {
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() >= this.support) {
                table.add(new FPNode(entry.getKey(), entry.getValue()));
            }
        }
        if (0 != table.size()) {
            table = table.stream().sorted(Comparator.comparing(FPNode::getCount).reversed())
                    .collect(Collectors.toList());
        }
    }

    private Map<String, Integer> getWordCount(List<List<String>> data, List<Integer> count) {
        Map<String, Integer> wordCount = new HashMap<>();
        // 统计单词出现的次数
        int i = 0;
        for (List<String> line : data) {
            for (String word : line) {
                if (wordCount.containsKey(word)) {
                    wordCount.put(word, wordCount.get(word) + count.get(i));
                } else {
                    wordCount.put(word, count.get(i));
                }
            }
            i++;
        }
        return wordCount;
    }

    private List<List<String>> sortData(Map<String, Integer> wordCount, List<List<String>> data) {
        List<List<String>> result = new ArrayList<>();
        // 单词排序
        for (List<String> line : data) {
            List<String> newLine = line.stream()
                    .filter(word -> wordCount.get(word) >= support)
                    .sorted(Comparator.comparing(word -> wordCount.get(word)).reversed())
                    .collect(Collectors.toList());
            if (0 != newLine.size()) {
                result.add(newLine);
            }
        }
        return result;
    }

    private List<List<String>> sort(List<List<String>> data, List<Integer> count) {
        Map<String, Integer> wordCount = getWordCount(data, count);
        getWordTable(wordCount);
        return sortData(wordCount, data);
    }

    private void getSingleTreeTemplate(List<String> last, List<LogTemplate> templates) {
        // 获取单树路径上所有节点
        List<FPNode> wordCount = new ArrayList<>();
        FPNode child = getFirstChild(root);
        while (null != child) {
            wordCount.add(child);
            child = getFirstChild(child);
        }
        // 获取wordCount所有非空子集
        List<LogTemplate> sonTemplates = getSonSet(wordCount);
        for (LogTemplate template : sonTemplates) {
            // 子集合出现次数大于支撑度则保留为模板
            if (template.getCount() >= support) {
                templates.add(template);
                template.getWords().addAll(last);
            }
        }
    }

    private void getMultiTreeTemplate(List<String> last, List<LogTemplate> templates) {
        // table为树包含单词集合，降序
        // 此处转换为升序，从下往上计算以每个节点结尾的模板
        Collections.reverse(table);
        for (FPNode node : table) {
            List<String> curWords = new ArrayList<>();
            curWords.add(node.getWord());
            // last为上一层递归调用计算的节点
            curWords.addAll(last);
            // 当前节点当做一个日志模板
            if(null == last || 0 == last.size()) {
                LogTemplate template = new LogTemplate();
                template.setCount(node.getCount());
                List<String> words = new ArrayList<>();
                words.add(node.getWord());
                template.setWords(words);
                templates.add(template);
            }

            FPNode link = this.firstNodeTable.get(node.getWord());
            List<List<String>> data = new ArrayList<>();
            List<Integer> count = new ArrayList<>();
            // 一条线索上有多个节点，每个节点从下往上对应一条日志模板路径
            while (null != link) {
                FPNode me = link;
                List<String> meWords = new ArrayList<>();
                me = me.getFather();
                // 线索上每个节点往上走
                while (null != me.getFather()) {
                    meWords.add(me.getWord());
                    me = me.getFather();
                }
                count.add(link.getCount());
                // 不加这一句会导致排序不稳定
                Collections.reverse(meWords);
                data.add(meWords);
                link = link.getNext();
            }

            // 以上述节点构造新树
            FPTree newTree = new FPTree(data, count, this.support);
            newTree.growth(curWords, templates);
        }
    }

    private List<LogTemplate> getSonSet(List<FPNode> wordCount) {
        List<LogTemplate> result = new ArrayList<>();
        int length = wordCount.size();
        int mark;
        int nEnd = 1 << length;
        // 对于length位二进制数，每个数字对应一个子集合取法
        for (mark = 0; mark < nEnd; mark++) {
            LogTemplate template = new LogTemplate();
            // 循环查找每位是否应该放入集合
            for (int i = 0; i < length; i++) {
                //该位有元素输出
                if (((1 << i) & mark) != 0) {
                    template.getWords().add(wordCount.get(i).getWord());
                    // wordCount按照count降序排列，template count取最小值
                    template.setCount(wordCount.get(i).getCount());
                }
            }
            // 空集合舍弃
            if (template.getCount() != 0) {
                result.add(template);
            }
        }
        return result;
    }

    private boolean isSingleTree(FPNode tree) {
        if (null == tree || null == tree.getChildren() || 0 == tree.getChildren().size()) {
            return true;
        }
        // 有多个子节点则不是单树
        if (1 < tree.getChildren().size()) {
            return false;
        } else {
            return isSingleTree(getFirstChild(tree));
        }
    }

    private FPNode getFirstChild(FPNode tree) {
        if (null == tree || null == tree.getChildren() || 0 == tree.getChildren().size()) {
            return null;
        } else {
            for (FPNode child : tree.getChildren().values()) {
                return child;
            }
            return null;
        }
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

        FPTree tree = new FPTree(data, null, 1);
        tree.print();
        List<LogTemplate> templates = new ArrayList<>();
        tree.growth(new ArrayList<>(), templates);
        for (LogTemplate template : templates) {
            template.print();
        }
    }
}