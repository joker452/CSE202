package kdtree;


import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PointST<Value> {

    private RedBlackBST<Point2D, Value> rbTree;

    // construct an empty symbol table of points
    public PointST() {
        rbTree = new RedBlackBST<>();
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        // constant time
        return rbTree.isEmpty();
    }

    // number of points
    public int size() {
        // constant time
        return rbTree.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        // logarithmic time
        // throw IllegalArguementException if p is null, rbtree only do so when p is null, so throw exception here
        if (p != null && val != null) {
            rbTree.put(p, val);
            return;
        }
        throw new IllegalArgumentException("try to put null key or null value into symbol table");
    }

    // value associated with point p
    public Value get(Point2D p) {
        // logarithmic time
        // rbTree will throw an IllegalArugmentException if p is null
        return rbTree.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        // logarithmic time
        // rbTree will throw an IllegalArugmentException if p is null
        return rbTree.contains(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        // linear time
        return rbTree.keys();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        // linear time
        if (rect != null) {
            List<Point2D> pointList = new LinkedList<>();
            for (Point2D p : rbTree.keys()) {
                if (rect.contains(p)) {
                    pointList.add(p);
                }
            }
            return pointList;
        }
        throw new IllegalArgumentException("rect is null");
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        // linear time
        if (p != null) {
            Point2D nearest = null;
            if (rbTree.size() > 0) {
                for (Point2D x : rbTree.keys()) {
                    if (nearest == null || x.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
                        nearest = x;
                    }
                }
            }
            return nearest;
        }
        throw new IllegalArgumentException("null Point p");
    }

    // unit testing (required)
    public static void main(String[] args) {
        PointST<Integer> st = new PointST<>();
        double qx = Double.parseDouble(args[0]);
        double qy = Double.parseDouble(args[1]);
        double rx1 = Double.parseDouble(args[2]);
        double rx2 = Double.parseDouble(args[3]);
        double ry1 = Double.parseDouble(args[4]);
        double ry2 = Double.parseDouble(args[5]);
        int k = Integer.parseInt(args[6]);

        Point2D query = new Point2D(qx, qy);
        RectHV rect = new RectHV(rx1, ry1, rx2, ry2);
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(args[7]))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] ss = line.split(" ");
                double x = Double.parseDouble(ss[0]), y = Double.parseDouble(ss[1]);
                Point2D p = new Point2D(x, y);
                st.put(p, i++);
            }
            StdOut.println("st.empty()? " + st.isEmpty());
            StdOut.println("st.size() = " + st.size());
            StdOut.println("First " + k + " values:");
            i = 0;
            for (Point2D p : st.points()) {
                StdOut.println("  " + st.get(p));
                if (i++ == k) {
                    break;
                }
            }
            StdOut.println("st.contains(" + query + ")? " + st.contains(query));
            StdOut.println("st.range(" + rect + "):");
            for (Point2D p : st.range(rect)) {
                StdOut.println("  " + p);
            }
            StdOut.println("st.nearest(" + query + ") = " + st.nearest(query));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
