package kdtree;

import edu.princeton.cs.algs4.*;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


public class KdTreeST<Value> {

    private class Node {
        Point2D key;
        Value val;
        Node left, right;
        RectHV rect;

        Node(Point2D k, Value v) {
            key = k;
            val = v;
        }
    }

    private Node root;
    private int size;
    // 0 for vertical, 1 for horizontal
    private int rootDir = 0;
    private double rootXMin = Double.NEGATIVE_INFINITY, rootYMin = Double.NEGATIVE_INFINITY;
    private double rootXMax = Double.POSITIVE_INFINITY, rootYMax = Double.POSITIVE_INFINITY;

    // construct an empty symbol table of points
    public KdTreeST() {
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points
    public int size() {
        return size;
    }

    // insert key-value pair in the subtree rooted at x
    private Node put(Node x, Point2D p, Value val, int dir, double xmin, double ymin, double xmax, double ymax) {
        if (x != null) {
            double order = dir == 0 ? p.x() - x.key.x() : p.y() - x.key.y();
            double xleftMin, yleftMin, xleftMax, yleftMax;
            double xrightMin, yrightMin, xrightMax, yrightMax;
            if (dir == 0) {
                // vertical split, children have different x range
                xleftMin = xmin;
                yleftMin = ymin;
                xleftMax = x.key.x();
                yleftMax = ymax;
                xrightMin = x.key.x();
                yrightMin = ymin;
                xrightMax = xmax;
                yrightMax = ymax;
            } else {
                // horizontal split, children have have different y range, left node at bottom
                xleftMin = xmin;
                yleftMin = ymin;
                xleftMax = xmax;
                yleftMax = x.key.y();
                xrightMin = xmin;
                yrightMin = x.key.y();
                xrightMax = xmax;
                yrightMax = ymax;
            }
            if (order < 0) {
                x.left = put(x.left, p, val, 1 - dir, xleftMin, yleftMin, xleftMax, yleftMax);
            } else if (order > 0) {
                x.right = put(x.right, p, val, 1 - dir, xrightMin, yrightMin, xrightMax, yrightMax);
            } else {
                x.val = val;
            }
        } else {
            x = new Node(p, val);
            x.rect = new RectHV(xmin, ymin, xmax, ymax);
            ++size;
        }
        return x;
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p != null && val != null) {
            root = put(root, p, val, rootDir, rootXMin, rootYMin, rootXMax, rootYMax);
            return;
        }
        throw new IllegalArgumentException("try to put null key or null value into symbol table");
    }

    // value associated with the given key in subtree rooted at x; null if no such key
    private Value get(Node x, Point2D p, int dir) {
        while (x != null) {
            double order = dir == 0 ? p.x() - x.key.x() : p.y() - x.key.y();
            dir = 1 - dir;
            if (order < 0) {
                x = x.left;
            } else if (order > 0) {
                x = x.right;
            } else {
                return x.val;
            }
        }
        return null;
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p != null) {
            return get(root, p, rootDir);
        }
        throw new IllegalArgumentException("arugment to get() is null");
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        Queue<Point2D> keys = new LinkedList<>();
        if (root != null) {
            Queue<Node> q = new LinkedList<>();
            q.offer(root);
            // bfs
            while (!q.isEmpty()) {
                Node x = q.poll();
                keys.offer(x.key);
                if (x.left != null) {
                    q.offer(x.left);
                }
                if (x.right != null) {
                    q.offer(x.right);
                }
            }
        }
        return keys;
    }

    // find all points in the subtree rooted at x that are inside the rectangle (or on the boundary)
    private void range(Node x, RectHV rect, List<Point2D> pointList) {
        if (x != null) {
            if (x.rect.intersects(rect)) {
                if (rect.contains(x.key)) {
                    pointList.add(x.key);
                }
                range(x.left, rect, pointList);
                range(x.right, rect, pointList);
            }
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect != null) {
            List<Point2D> pointList = new LinkedList<>();
            range(root, rect, pointList);
            return pointList;
        }
        throw new IllegalArgumentException("rect is null");
    }

    // search the nearest neighbor of point p in the subtree rooted at x
    private Point2D nearest(Node x, Point2D p, Point2D nearest, int dir) {
        if (x != null) {
            if (nearest == null || nearest.distanceSquaredTo(p) > x.rect.distanceSquaredTo(p)) {
                // there exists potential closer point
                if (nearest == null || x.key.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
                    nearest = x.key;
                }
                // choose the subtree that is on the same side of the splitting line as the query point first
                double order = dir == 0 ? p.x() - x.key.x() : p.y() - x.key.y();
                if (order < 0) {
                    nearest = nearest(x.left, p, nearest, 1 - dir);
                    return nearest(x.right, p, nearest, 1 - dir);
                } else {
                    nearest = nearest(x.right, p, nearest, 1 - dir);
                    return nearest(x.left, p, nearest, 1 - dir);
                }
            }
        }
        return nearest;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p != null) {
            Point2D nearest = null;
            if (root != null) {
                nearest = nearest(root, p, nearest, rootDir);
            }
            return nearest;
        }

        throw new IllegalArgumentException("null point doesn't have nearest neighbor");
    }

    // search k nearest neighbors of point p in the subtree rooted at x; null if the symbol table is empty
    private void nearest(Node x, Point2D p, int k, PriorityQueue<Point2D> pq, int dir) {
        if (x != null) {
            if (pq.size() < k || pq.peek().distanceSquaredTo(p) > x.rect.distanceSquaredTo(p)) {
                // there exists potential closer point
                pq.offer(x.key);
                if (pq.size() > k) {
                    pq.poll();
                }

                double order = dir == 0? p.x() - x.key.x(): p.y() - x.key.y();
                if (order < 0) {
                    nearest(x.left, p, k, pq, 1 - dir);
                    nearest(x.right, p, k, pq, 1 - dir);
                } else {
                    nearest(x.right, p, k, pq, 1 - dir);
                    nearest(x.left, p, k, pq, 1 - dir);
                }
            }
        }
    }

    // k nearest neighbors of point p; null if the symbol table is empty
    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p != null && k > 0) {
            if (k >= size) {
                return points();
            }
            /* max heap */
            PriorityQueue<Point2D> pq = null;
            if (root != null) {
                pq = new PriorityQueue<>((Point2D x, Point2D y) -> (int) (y.distanceSquaredTo(p) - x.distanceSquaredTo(p)));
                nearest(root, p, k, pq, rootDir);
            }
            return pq;
        }

        throw new IllegalArgumentException("null Point p or non-positive k");

    }

    // unit testing (required)
    public static void main(String[] args) {
        // intializes the PointSt data type
        KdTreeST<Integer> map = new KdTreeST<>();
        // tests the isEmpty method
        System.out.println("Is empty? " + map.isEmpty()); // should be empty
        // add random points and tests the put method
        for (int x = 0; x < 10; x++)
        {
            Point2D p = new Point2D(StdRandom.uniform(), StdRandom.uniform());
            map.put(p, x);
        }
        Point2D testPoint = new Point2D(0.5, 0.5);
        map.put(testPoint, 18);
        System.out.println("Size: " + map.size()); // should be 11

        // print all points in the map
        for (Point2D p : map.points())
            System.out.println(p);

        System.out.println("Is empty? " + map.isEmpty()); // should return false
        // tests the contains method, and should return true
        System.out.println("Contains (0.5, 0.5)? " + map.contains(testPoint));
        // tests the get method, should return 18
        System.out.println("Value with (0.5, 0.5): " + map.get(testPoint));


        // generates a random RectHV
        double a1 = StdRandom.uniform();
        double a2 = StdRandom.uniform();
        double a3 = StdRandom.uniform();
        double a4 = StdRandom.uniform();
        double xMin = Math.min(a1, a2);
        double xMax = Math.max(a1, a2);
        double yMin = Math.min(a3, a4);
        double yMax = Math.max(a3, a4);
        // creates the RectHV object, based off of the random values
        RectHV rect = new RectHV(xMin, yMin, xMax, yMax);
        System.out.println(rect);

        // print points in range of RectHV
        System.out.println("Points in RectHV: ");
        for (Point2D p : map.range(rect)) // tests the range method
            System.out.println(p);

        // draws points the points
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : map.points()) // tests the point method
            p.draw();

        // draw rectangle
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        rect.draw();

        // draw points in rectangle in red
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.RED);
        // tests the range method
        for (Point2D p : map.range(rect))
            p.draw();

        // draws one random point in green
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.GREEN);
        Point2D p = new Point2D(StdRandom.uniform(), StdRandom.uniform());
        p.draw();
        System.out.println("Random point: " + p);

        // draws the point closest to random point in blue
        StdDraw.setPenRadius(0.015);
        StdDraw.setPenColor(StdDraw.BLUE);
        Point2D n = map.nearest(p); // tests the nearest method
        n.draw();
        System.out.println("Closest to random point: " + n);
    }
}
