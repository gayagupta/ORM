package edu.brown.cs.student.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;


public class KDTree {
  private Node root;
  private int size;
  private int depth;
  private final int dimensions;
  private ArrayList<Node> nodes;

  ///////////////////////////////////////////////////////////////////////////////////
  public class Node implements Comparable<Node> {
    private int id;
    private final int[] point;
    private String sign;
    private Node left;
    private Node right;
    private int depth;
    private double distance;

    //Constructor used for loaded data
    public Node(int id, int x, int y, int z, String sign) {
      this.id = id;
      int[] p = {x, y, z};
      this.point = p;
      this.sign = sign;
    }

    //Constructor for nodes based on target data
    public Node(int x, int y, int z) {
      int[] p = {x, y, z};
      this.point = p;
    }

    //Setters and getters for Node instance variables
    public int getDepth() {
      return depth;
    }

    public void setDepth(int d) {
      this.depth = d % dimensions;
    }

    private int[] getPoint() {
      return this.point;
    }

    public double getDistance() {
      return distance;
    }

    public void setDistance(double distance) {
      this.distance = distance;
    }

    public int getId() {
      return id;
    }

    //Equality based on node ID.
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Node node = (Node) o;
      return id == node.id;
    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    //Converts the node to the ID. More could be added to this based on changes in what is needed
    @Override
    public String toString() {
      return "ID: " + id;
    }

    //Comparison is based purely on distance. This is used in the nearest neighbors search.
    @Override
    public int compareTo(Node o) {
      if (this.getDistance() < o.getDistance()) {
        return -1;
      } else if (this.getDistance() > o.getDistance()) {
        return 1;
      }
      return 0;
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////
  //End of nested Node Class
  //Default constructor uses 3 dimensions as this is what we expect to use in this project.
  public KDTree() {
    this.root = null;
    this.dimensions = 3;
  }

  //Alternate constructor takes in the number of dimensions of the tree.
  public KDTree(int n) {
    this.root = null;
    this.dimensions = n;
  }

  /* Insert node calls upon the insert method to put a new node into the tree. Additionally, the
  depth and size of the tree are updated. The order in which this happens changes based on whether
  the inserted node would be on a new depth level.
   */
  public void insertNode(Node n) {
    //if insert would place the new node on a different depth level
    if (size == 0
        || (int) (Math.log(size) / Math.log(2)) != (int) (Math.log(size + 1) / Math.log(2))) {
      size++;
      this.updateDepth();
      //assign the node's depth
      n.setDepth(this.depth);
      this.root = insert(this.root, n);
    } else {
      this.root = insert(this.root, n);
      size++;
      this.updateDepth();
      //assign the node's depth
      n.setDepth(this.depth);
    }
  }

  /*
  Inserts a node into the KD tree by recursively moving through the tree and finding the correct
  location.
   */
  private Node insert(Node source, Node insert) {
    //Determines the dimension to be compared for the current iteration
    int dim = 0;
    if (source != null) {
      dim = source.getDepth() % dimensions;
    }
    //If null, return to add new leaf
    if (source == null) {
      return insert;
      //Go left if inserted point is smaller on relevant axis
    } else if (insert.getPoint()[dim] < source.getPoint()[dim]) {
      source.left = insert(source.left, insert);
      //Go right if inserted point is larger on relevant axis
    } else if (insert.getPoint()[dim] > source.getPoint()[dim]) {
      source.right = insert(source.right, insert);
    } else {
      //If same node, throw error
      if (source.equals(insert)) {
        throw new IllegalArgumentException("Node already exists in the tree");
      }
      //default to right side if equal
      source.right = insert(source.right, insert);
    }
    return source;
  }
  /*
  Runs the node search algorithm on the input arguments. It creates a false node to search for the
  nearest neighbors.
  */
  public ArrayList<Node> nearestNeighbors(int k, int weight, int height, int age) {
    Node input = new Node(weight, height, age);
    input.setDistance(Double.MAX_VALUE);
    this.nodes = new ArrayList<>(k);
    nodeSearch(k, this.root, input);
    return this.nodes;
  }
  /*
  Runs the node search algorithm on the input arguments. First, it finds the node with the given
  id, then passes this into node search.
   */
  public ArrayList<Node> nearestNeighbors(int k, int id) {
    this.nodes = new ArrayList<>(k);
    Node input = findNodeByID(id);
    if (input == null) {
      throw new NullPointerException("The requested node could not be found");
    }
    nodeSearch(k, this.root, input);
    return this.nodes;
  }
  //Uses BFS to search for the node with the given input ID.
  public Node findNodeByID(int id) {
    LinkedList<Node> queue = new LinkedList<>();
    queue.add(this.root);
    while (queue.size() != 0) {
      Node n = queue.poll();
      if (n.getId() == id) {
        return n;
      }
      if (n.left != null) {
        queue.add(n.left);
      }
      if (n.right != null) {
        queue.add(n.right);
      }
    }
    return null;
  }
  /*
  Recursively searches through the tree to find and store the k nearest neighbors to the target.
   */
  public void nodeSearch(int k, Node source, Node target) {
    if (source == null) {
      return;
    }
    double distance = Math.sqrt(Math.pow(source.getPoint()[0] - target.getPoint()[0], 2)
        + Math.pow(source.getPoint()[1] - target.getPoint()[1], 2)
        + Math.pow(source.getPoint()[2] - target.getPoint()[2], 2));
    source.setDistance(distance);
    if (this.nodes.size() < k && target.getId() != source.getId()) {
      this.nodes.add(source);
    } else {
      for (int i = 0; i < nodes.size(); i++) {
        //source is smaller than list elements, new node is inserted there and the old node removed
        if (source.compareTo(nodes.get(i)) < 0 && target.getId() != source.getId()) {
          nodes.add(i, source);
          nodes.remove(nodes.size() - 1);
          break;
        } else if (source.compareTo(nodes.get(i)) == 0 && target.getId() != source.getId()
            && i == k - 1) {
          //if node is equal and at the end randomly keep or add to de-bias data.
          double coinFlip = Math.random();
          if (coinFlip > (1.f / 2.f)) {
            nodes.add(i, source);
            nodes.remove(nodes.size() - 1);
            break;
          }
        }
      }
    }
    if (nodes.size() != 0 && nodes.get(nodes.size() - 1).getDistance()
        > target.getPoint()[target.getDepth()]) {
      nodeSearch(k, source.left, target);
      nodeSearch(k, source.right, target);
    } else {
      if (source.getPoint()[target.getDepth()] <= target.getPoint()[target.getDepth()]) {
        nodeSearch(k, source.right, target);
      } else {
        nodeSearch(k, source.left, target);
      }
    }
  }

  //Sets the depth equal to log base 2 of the size.
  private void updateDepth() {
    this.depth = (int) (Math.log(size) / Math.log(2));
  }

  public int getSize() {
    return this.size;
  }

  //Prints out nodes in breadth first search order
  @Override
  public String toString() {
    String output = "";
    LinkedList<Node> queue = new LinkedList<>();
    queue.add(this.root);
    while (queue.size() != 0) {
      Node n = queue.poll();
      if (!output.equals("")) {
        output += ", ";
      }
      output += n.toString();
      if (n.left != null) {
        queue.add(n.left);
      }
      if (n.right != null) {
        queue.add(n.right);
      }
    }
    return output;
  }
}
