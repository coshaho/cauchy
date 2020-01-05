package com.coshaho.fptree;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志模板
 *
 * @author coshaho
 * @since 2020/1/6
 */
public class LogTemplate {
    private List<String> words = new ArrayList<>();
    private int count;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
