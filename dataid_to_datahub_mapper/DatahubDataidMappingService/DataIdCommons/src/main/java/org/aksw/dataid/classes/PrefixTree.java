package org.aksw.dataid.classes;

import java.util.*;

class TrieNode<T> {
    int c;
    PrefixTreeMap<Integer, TrieNode<T>> children = new PrefixTreeMap<Integer, TrieNode<T>>();
    Set<T> load = new HashSet<>();
    boolean isLeaf;

    public TrieNode() {}

    public TrieNode(int c){
        this.c = c;
    }

    public void addLoad(T load)
    {
        this.load.add(load);
    }

    public Set<T> getLoad() {
        return load;
    }
}
public class PrefixTree<T> {
    private TrieNode root;

    public PrefixTree() {
        root = new TrieNode<>();
    }

    // Inserts a word into the trie.
    public void insert(String word, T load) {
        PrefixTreeMap<Integer, TrieNode> children = root.children;
        int[] bytes = splitWord(word);

        for(int i=0; i<bytes.length; i++){
            int c = bytes[i];

            TrieNode t;
            if(children.containsKey(c)){
                t = children.get(c);
                t.addLoad(load);
            }else{
                t = new TrieNode(c);
                t.addLoad(load);
                children.put(c, t);
            }

            children = t.children;

            //set leaf node
            if(i==bytes.length-1)
                t.isLeaf = true;
        }
    }

    // Returns if the word is in the trie.
    public Set<T> search(String word) {
        int[] bytes = splitWord(word);
        TrieNode<T> t = searchNode(bytes);
        if(t != null && t.isLeaf)
            return t.getLoad();
        else
            return null;
    }

    // Returns if the word is in the trie.
    public Set<T> search(int[] bytes) {
        TrieNode<T> t = searchNode(bytes);
        if(t != null && t.isLeaf)
            return t.getLoad();
        else
            return null;
    }

    // Returns if the word is in the trie.
    public Set<T> search(List<Integer> zw) {
        int[] bytes = new int[zw.size()];
        for(int i =0; i< zw.size(); i++)
            bytes[i] = zw.get(i);
        TrieNode<T> t = searchNode(bytes);
        if(t != null && t.isLeaf)
            return t.getLoad();
        else
            return null;
    }

    private int[] splitWord(String word) {
        String[] bytes = word.split(" ");
        if (bytes.length < 2)
            bytes = word.split(",");
        int[] zw = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++){
            if (bytes[i].toLowerCase().contains("x")) {
                if (bytes[i].trim().toLowerCase().equals("xx"))
                    zw[i] = -2000;
                else {
                    if (bytes[i].charAt(0) == 'x') {
                        int zz = Integer.parseInt(new String(bytes[i].substring(1, 2)), 16) * (-1) - 500;
                        zw[i] = zz;
                    }
                    if (bytes[i].charAt(1) == 'x') {
                        int zz = Integer.parseInt(new String(bytes[i].substring(0, 1)), 16) * (-16) - 1000;
                        zw[i] = zz;
                    }
                }
            }
            else
                zw[i] = Integer.parseInt(bytes[i], 16);
        }
        return zw;
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public boolean startsWith(String prefix) {
        int[] bytes = splitWord(prefix);
        if(searchNode(bytes) == null)
            return false;
        else
            return true;
    }

    public boolean startsWith(int[] bytes) {
        if(searchNode(bytes) == null)
            return false;
        else
            return true;
    }



    public boolean startsWith(List<Integer> zw) {
        int[] bytes = new int[zw.size()];
        for(int i =0; i< zw.size(); i++)
            bytes[i] = zw.get(i);
        if(searchNode(bytes) == null)
            return false;
        else
            return true;
    }

    public TrieNode<T> searchNode(int[] bytes){
        PrefixTreeMap<Integer, TrieNode> children = root.children;
        TrieNode t = null;
        for(int i=0; i<bytes.length; i++){
            int c = bytes[i];
            if(children.containsKey(c)){
                t = children.get(c);
                children = t.children;
            }else{
                return null;
            }
        }

        return t;
    }
}
