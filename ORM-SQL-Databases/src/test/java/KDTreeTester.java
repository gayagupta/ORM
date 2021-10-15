import edu.brown.cs.student.main.KDTree;
import org.junit.Assert;
import org.junit.Test;

public class KDTreeTester {

  @Test
  public void testInsertOne(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 1, 1, 1, "Leo"));
    Assert.assertEquals("ID: 1", tree.toString());
  }
  @Test
  public void testInsertInCorrectOrder(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 0, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(3, 15, 10, 10, "Leo"));
    Assert.assertEquals("ID: 1, ID: 2, ID: 3", tree.toString());
  }
  @Test
  public void testRightSubtree(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 15, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(3, 20, 5, 10, "Leo"));
    tree.insertNode(tree.new Node(4, 20, 15, 10, "Leo"));
    Assert.assertEquals("ID: 1, ID: 2, ID: 3, ID: 4", tree.toString());
  }
  @Test
  public void testLeftSubtree(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(3, 5, 5, 10, "Leo"));
    tree.insertNode(tree.new Node(4, 5, 15, 10, "Leo"));
    Assert.assertEquals("ID: 1, ID: 2, ID: 3, ID: 4", tree.toString());
  }
  @Test
  public void testSize(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(3, 5, 5, 10, "Leo"));
    tree.insertNode(tree.new Node(4, 5, 15, 10, "Leo"));
    assert (tree.getSize() == 4);
  }
  @Test
  public void oneNeighborPosEasy(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 5, 5, "Leo"));
    tree.insertNode(tree.new Node(3, 15, 15, 15, "Leo"));
    tree.insertNode(tree.new Node(4, 3, 3, 3, "Leo"));
    tree.insertNode(tree.new Node(5, 8, 8, 8, "Leo"));
    tree.insertNode(tree.new Node(6, 13, 13, 13, "Leo"));
    tree.insertNode(tree.new Node(7, 18, 18, 18, "Leo"));
    Assert.assertEquals("[ID: 1]", tree.nearestNeighbors(1, 10, 10, 10).toString());
  }
  @Test
  public void oneNeighborPosHard(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 5, 5, "Leo"));
    tree.insertNode(tree.new Node(3, 15, 15, 15, "Leo"));
    tree.insertNode(tree.new Node(4, 3, 3, 3, "Leo"));
    tree.insertNode(tree.new Node(5, 8, 8, 8, "Leo"));
    tree.insertNode(tree.new Node(6, 13, 13, 13, "Leo"));
    tree.insertNode(tree.new Node(7, 18, 18, 18, "Leo"));
    Assert.assertEquals("[ID: 6]", tree.nearestNeighbors(1, 12, 12, 12).toString());
  }
  @Test
  public void severalNeighborPos(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 5, 5, "Leo"));
    tree.insertNode(tree.new Node(3, 15, 15, 15, "Leo"));
    tree.insertNode(tree.new Node(4, 3, 3, 3, "Leo"));
    tree.insertNode(tree.new Node(5, 8, 8, 8, "Leo"));
    tree.insertNode(tree.new Node(6, 13, 13, 13, "Leo"));
    tree.insertNode(tree.new Node(7, 18, 18, 18, "Leo"));
    Assert.assertEquals("[ID: 1, ID: 5, ID: 6]", tree.nearestNeighbors(3, 10, 10, 10).toString());
  }
  @Test
  public void oneNeighborID(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 5, 5, "Leo"));
    tree.insertNode(tree.new Node(3, 15, 15, 15, "Leo"));
    tree.insertNode(tree.new Node(4, 3, 3, 3, "Leo"));
    tree.insertNode(tree.new Node(5, 8, 8, 8, "Leo"));
    tree.insertNode(tree.new Node(6, 13, 13, 13, "Leo"));
    tree.insertNode(tree.new Node(7, 18, 18, 18, "Leo"));
    Assert.assertEquals("[ID: 3]", tree.nearestNeighbors(1, 7).toString());
  }
  @Test
  public void severalNeighborID(){
    KDTree tree = new KDTree();
    tree.insertNode(tree.new Node(1, 10, 10, 10, "Leo"));
    tree.insertNode(tree.new Node(2, 5, 5, 5, "Leo"));
    tree.insertNode(tree.new Node(3, 15, 15, 15, "Leo"));
    tree.insertNode(tree.new Node(4, 3, 3, 3, "Leo"));
    tree.insertNode(tree.new Node(5, 8, 8, 8, "Leo"));
    tree.insertNode(tree.new Node(6, 13, 13, 13, "Leo"));
    tree.insertNode(tree.new Node(7, 18, 18, 18, "Leo"));
    Assert.assertEquals("[ID: 3, ID: 6, ID: 1]", tree.nearestNeighbors(3, 7).toString());

  }


}
