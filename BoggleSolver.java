import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleSolver {
    private boolean[] marked;
    private Node root;      // root of trie

    // 26-way trie node
    private static class Node {
        private Node[] next = new Node[26];
        private boolean isString;
    }

    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    // Initializes the data structure using the given array of strings as the dictionary.
    public BoggleSolver(String[] dictionary) {
        for (String s : dictionary)
            add(s);
    }

    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    private void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) x.isString = true;
        else {
            char c = key.charAt(d);
            x.next[c - 65] = add(x.next[c - 65], key, d + 1);
        }
        return x;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - 65], key, d + 1);
    }

    private Iterable<Integer> adj(BoggleBoard board, int col, int row) {
        int cols = board.cols();
        int rows = board.rows();
        Stack<Integer> index = new Stack<>();
        if (col != 0) index.push(cols * row + col - 1);
        if (col != 0 && row != 0) index.push(cols * (row - 1) + col - 1);
        if (row != 0) index.push(cols * (row - 1) + col);
        if (row != 0 && col != cols - 1) index.push(cols * (row - 1) + col + 1);
        if (col != cols - 1) index.push(cols * row + col + 1);
        if (row != rows - 1 && col != cols - 1) index.push(cols * (row + 1) + col + 1);
        if (row != rows - 1) index.push(cols * (row + 1) + col);
        if (row != rows - 1 && col != 0) index.push(cols * (row + 1) + col - 1);
        return index;
    }

    private void depthFirstPaths(BoggleBoard board, int col, int row, HashSet<String> words) {
        marked = new boolean[board.rows() * board.cols()];
        Node x = root;
        dfs(board, col, row, new StringBuilder(), words, x); // initiating DFS from a given board cell;
    }

    // depth first search from a given board cell
    private void dfs(BoggleBoard board, int col, int row, StringBuilder stringBuilder, HashSet<String> words, Node x) {
        int cCol, cRow;
        Node pathNode;
        marked[row * board.cols() + col] = true;
        stringBuilder.append(board.getLetter(row, col));
        if (board.getLetter(row, col) == 'Q') {
            if (x.next[16] == null) return;
            pathNode = x.next[16].next[20];
            stringBuilder.append('U');
        } else
            pathNode = x.next[stringBuilder.charAt(stringBuilder.length() - 1) - 65];
        if (pathNode == null) return;
        if (pathNode.isString && stringBuilder.length() > 2)
            words.add(stringBuilder.toString());
        for (int w : adj(board, col, row)) {
            cRow = w / board.cols();
            cCol = w % board.cols();
            if (!marked[w]) {
                dfs(board, cCol, cRow, stringBuilder, words, pathNode);
                marked[w] = false;
                if (stringBuilder.charAt(stringBuilder.length() - 1) == 'U' && stringBuilder.charAt(stringBuilder.length() - 2) == 'Q')
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> words = new HashSet<>();
        for (int i = 0; i < board.rows(); i++)
            for (int j = 0; j < board.cols(); j++)
                depthFirstPaths(board, j, i, words);
        return words;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int wLength = word.length();
        if (get(root, word, 0) == null) return 0;
        if (!get(root, word, 0).isString) return 0;
        if (wLength < 3) return 0;
        else if (wLength < 5) return 1;
        else if (wLength < 6) return 2;
        else if (wLength < 7) return 3;
        else if (wLength < 8) return 5;
        else return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        long start = System.nanoTime();
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        long finish = System.nanoTime();
        StdOut.println("Score = " + score);
        StdOut.println((finish - start) / 1e9 + " sec ");
    }
}
