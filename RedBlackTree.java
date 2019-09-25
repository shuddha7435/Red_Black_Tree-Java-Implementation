
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * Red Black Tree Implementation
 
 
 *
 * References
 * http://pages.cs.wisc.edu/~skrentny/cs367-common/readings/Red-Black-Trees/
 * http://staff.ustc.edu.cn/~csli/graduate/algorithms/book6/chap14.htm
 * http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 * https://en.wikipedia.org/wiki/Red%E2%80%93black_tree
 * http://www.cs.dartmouth.edu/~thc/cs10/lectures/0519/RBTree.java
 * https://github.com/mission-peace/interview/tree/master/src/com/interview/tree
 */

public class RedBlackTree {

    public enum Color {
        RED,
        BLACK
    }

    public static class Node {
        int data;
        Color color;
        Node left;
        Node right;
        Node parent;
        boolean isNullLeaf;
    }

    private static Node createBlackNode(int data) {
        Node node = new Node();
        node.data = data;
        node.color = Color.BLACK;
        node.left = createNullLeafNode(node);
        node.right = createNullLeafNode(node);
        return node;
    }

    private static Node createNullLeafNode(Node parent) {
        Node leaf = new Node();
        leaf.color = Color.BLACK;
        leaf.isNullLeaf = true;
        leaf.parent = parent;
        return leaf;
    }

    private static Node createRedNode(Node parent, int data) {
        Node node = new Node();
        node.data = data;
        node.color = Color.RED;
        node.parent = parent;
        node.left = createNullLeafNode(node);
        node.right = createNullLeafNode(node);
        return node;
    }

    /**
     * Main insert method of red black tree.
     */
    public Node insert(Node root, int data) {
        return insert(null, root, data);
    }

    /**
     * Main delete method of red black tree.
     */
    public Node delete(Node root, int data) {
        AtomicReference<Node> rootReference = new AtomicReference<>();
        delete(root, data, rootReference);
        if(rootReference.get() == null) {
            return root;
        } else {
            return rootReference.get();
        }
    }

    /**
     * Main print method of red black tree.
     */
    public void printRedBlackTree(Node root) {
        printRedBlackTree(root, 0);
    }

    /**
     * Main validate method of red black tree.
     */
    public boolean validateRedBlackTree(Node root) {

        if(root == null) {
            return true;
        }
        //check if root is black
        if(root.color != Color.BLACK) {
            System.out.print("Root is not black");
            return false;
        }
        //Use of AtomicInteger solely because java does not provide any other mutable int wrapper.
        AtomicInteger blackCount = new AtomicInteger(0);
        //make sure black count is same on all path and there is no red red relationship
        return checkBlackNodesCount(root, blackCount, 0) && noRedRedParentChild(root, Color.BLACK);
    }

    private void rightRotate(Node root, boolean changeColor) {
        Node parent = root.parent;
        root.parent = parent.parent;
        if(parent.parent != null) {
            if(parent.parent.right == parent) {
                parent.parent.right = root;
            } else {
                parent.parent.left = root;
            }
        }
        Node right = root.right;
        root.right = parent;
        parent.parent = root;
        parent.left = right;
        if(right != null) {
            right.parent = parent;
        }
        if(changeColor) {
            root.color = Color.BLACK;
            parent.color = Color.RED;
        }
    }

    private void leftRotate(Node root, boolean changeColor) {
        Node parent = root.parent;
        root.parent = parent.parent;
        if(parent.parent != null) {
            if(parent.parent.right == parent) {
                parent.parent.right = root;
            } else {
                parent.parent.left = root;
            }
        }
        Node left = root.left;
        root.left = parent;
        parent.parent = root;
        parent.right = left;
        if(left != null) {
            left.parent = parent;
        }
        if(changeColor) {
            root.color = Color.BLACK;
            parent.color = Color.RED;
        }
    }

    private Optional<Node> findSiblingNode(Node root) {
        Node parent = root.parent;
        if(isLeftChild(root)) {
            return Optional.ofNullable(parent.right.isNullLeaf ? null : parent.right);
        } else {
            return Optional.ofNullable(parent.left.isNullLeaf ? null : parent.left);
        }
    }

    private boolean isLeftChild(Node root) {
        Node parent = root.parent;
        if(parent.left == root) {
            return true;
        } else {
            return false;
        }
    }

    private Node insert(Node parent, Node root, int data) {
        if(root  == null || root.isNullLeaf) {
            
            if(parent != null) {
                return createRedNode(parent, data);
            } else { 
                return createBlackNode(data);
            }
        }

        
        if(root.data == data) {
            throw new IllegalArgumentException("Duplicate date " + data);
        }
        
        boolean isLeft;
        if(root.data > data) {
            Node left = insert(root, root.left, data);
            
            if(left == root.parent) {
                return left;
            }
            
            root.left = left;            
            isLeft = true;
        } else {
            Node right = insert(root, root.right, data);            
            if(right == root.parent) {
                return right;
            }
            
            root.right = right;
            isLeft = false;
        }

        if(isLeft) {
            
            if(root.color == Color.RED && root.left.color == Color.RED) {
                Optional<Node> sibling = findSiblingNode(root);
                if(!sibling.isPresent() || sibling.get().color == Color.BLACK) {
                    if(isLeftChild(root)) {
                        rightRotate(root, true);
                    } else {
                        rightRotate(root.left, false);
                        root = root.parent;
                        leftRotate(root, true);
                    }

                } else {
                    root.color = Color.BLACK;
                    sibling.get().color = Color.BLACK;
                    if(root.parent.parent != null) {
                        root.parent.color = Color.RED;
                    }
                }
            }
        } else {
            if(root.color == Color.RED && root.right.color == Color.RED) {
                Optional<Node> sibling = findSiblingNode(root);
                if(!sibling.isPresent() || sibling.get().color == Color.BLACK) {
                    if(!isLeftChild(root)) {
                        leftRotate(root, true);
                    } else {
                        leftRotate(root.right, false);
                        root = root.parent;
                        rightRotate(root, true);
                    }
                } else {
                    root.color = Color.BLACK;
                    sibling.get().color = Color.BLACK;
                    if(root.parent.parent != null) {
                        root.parent.color = Color.RED;
                    }
                }
            }
        }
        return root;
    }

   
    private void delete(Node root, int data, AtomicReference<Node> rootReference) {
        if(root == null || root.isNullLeaf) {
            return;
        }
        if(root.data == data) {

            if(root.right.isNullLeaf || root.left.isNullLeaf) {
                deleteOneChild(root, rootReference);
            } else {

                Node inorderSuccessor = findSmallest(root.right);
                root.data = inorderSuccessor.data;
                delete(root.right, inorderSuccessor.data, rootReference);
            }
        }
        if(root.data < data) {
            delete(root.right, data, rootReference);
        } else {
            delete(root.left, data, rootReference);
        }
    }

    private Node findSmallest(Node root) {
        Node prev = null;
        while(root != null && !root.isNullLeaf) {
            prev = root;
            root = root.left;
        }
        return prev != null ? prev : root;
    }

    private void deleteOneChild(Node nodeToBeDelete, AtomicReference<Node> rootReference) {
        Node child = nodeToBeDelete.right.isNullLeaf ? nodeToBeDelete.left : nodeToBeDelete.right;
        replaceNode(nodeToBeDelete, child, rootReference);
     
        if(nodeToBeDelete.color == Color.BLACK) {
            if(child.color == Color.RED) {
                child.color = Color.BLACK;
            } else {
                deleteCase1(child, rootReference);
            }
        }
    }


/
    private void deleteCase1(Node doubleBlackNode, AtomicReference<Node> rootReference) {
        if(doubleBlackNode.parent == null) {
            rootReference.set(doubleBlackNode);
            return;
        }
        deleteCase2(doubleBlackNode, rootReference);
    }

 
    private void deleteCase2(Node doubleBlackNode, AtomicReference<Node> rootReference) {
        Node siblingNode = findSiblingNode(doubleBlackNode).get();
        if(siblingNode.color == Color.RED) {
            if(isLeftChild(siblingNode)) {
                rightRotate(siblingNode, true);
            } else {
                leftRotate(siblingNode, true);
            }
            if(siblingNode.parent == null) {
                rootReference.set(siblingNode);
            }
        }
        deleteCase3(doubleBlackNode, rootReference);
    }

 
    private void deleteCase3(Node doubleBlackNode, AtomicReference<Node> rootReference) {

        Node siblingNode = findSiblingNode(doubleBlackNode).get();

        if(doubleBlackNode.parent.color == Color.BLACK && siblingNode.color == Color.BLACK && siblingNode.left.color == Color.BLACK
                && siblingNode.right.color == Color.BLACK) {
            siblingNode.color = Color.RED;
            deleteCase1(doubleBlackNode.parent, rootReference);
        } else {
            deleteCase4(doubleBlackNode, rootReference);
        }
    }

    private void deleteCase4(Node doubleBlackNode, AtomicReference<Node> rootReference) {
        Node siblingNode = findSiblingNode(doubleBlackNode).get();
        if(doubleBlackNode.parent.color == Color.RED && siblingNode.color == Color.BLACK && siblingNode.left.color == Color.BLACK
        && siblingNode.right.color == Color.BLACK) {
            siblingNode.color = Color.RED;
            doubleBlackNode.parent.color = Color.BLACK;
            return;
        } else {
            deleteCase5(doubleBlackNode, rootReference);
        }
    }

    private void deleteCase5(Node doubleBlackNode, AtomicReference<Node> rootReference) {
        Node siblingNode = findSiblingNode(doubleBlackNode).get();
        if(siblingNode.color == Color.BLACK) {
            if (isLeftChild(doubleBlackNode) && siblingNode.right.color == Color.BLACK && siblingNode.left.color == Color.RED) {
                rightRotate(siblingNode.left, true);
            } else if (!isLeftChild(doubleBlackNode) && siblingNode.left.color == Color.BLACK && siblingNode.right.color == Color.RED) {
                leftRotate(siblingNode.right, true);
            }
        }
        deleteCase6(doubleBlackNode, rootReference);
    }

    private void deleteCase6(Node doubleBlackNode, AtomicReference<Node> rootReference) {
        Node siblingNode = findSiblingNode(doubleBlackNode).get();
        siblingNode.color = siblingNode.parent.color;
        siblingNode.parent.color = Color.BLACK;
        if(isLeftChild(doubleBlackNode)) {
            siblingNode.right.color = Color.BLACK;
            leftRotate(siblingNode, false);
        } else {
            siblingNode.left.color = Color.BLACK;
            rightRotate(siblingNode, false);
        }
        if(siblingNode.parent == null) {
            rootReference.set(siblingNode);
        }
    }

    private void replaceNode(Node root, Node child, AtomicReference<Node> rootReference) {
        child.parent = root.parent;
        if(root.parent == null) {
            rootReference.set(child);
        }
        else {
            if(isLeftChild(root)) {
                root.parent.left = child;
            } else {
                root.parent.right = child;
            }
        }
    }

    private void printRedBlackTree(Node root, int space) {
        if(root == null || root.isNullLeaf) {
            return;
        }
        printRedBlackTree(root.right, space + 5);
        for(int i=0; i < space; i++) {
            System.out.print(" ");
        }
        System.out.println(root.data + " " + (root.color == Color.BLACK ? "B" : "R"));
        printRedBlackTree(root.left, space + 5);
    }

    private boolean noRedRedParentChild(Node root, Color parentColor) {
        if(root == null) {
            return true;
        }
        if(root.color == Color.RED && parentColor == Color.RED) {
            return false;
        }

        return noRedRedParentChild(root.left, root.color) && noRedRedParentChild(root.right, root.color);
    }

    private boolean checkBlackNodesCount(Node root, AtomicInteger blackCount, int currentCount) {

        if(root.color == Color.BLACK) {
            currentCount++;
        }

        if(root.left == null && root.right == null) {
            if(blackCount.get() == 0) {
                blackCount.set(currentCount);
                return true;
            } else {
                return currentCount == blackCount.get();
            }
        }
        return checkBlackNodesCount(root.left, blackCount, currentCount) && checkBlackNodesCount(root.right, blackCount, currentCount);
    }

    public static void main(String args[]) {
        Node root = null;
        RedBlackTree redBlackTree = new RedBlackTree();
        
        root = redBlackTree.insert(root, 41);
        root = redBlackTree.insert(root, 38);
        root = redBlackTree.insert(root, 31);
        root = redBlackTree.insert(root, 12);
        root = redBlackTree.insert(root, 19);
        root = redBlackTree.insert(root, 8);
        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.delete(root, 12);
        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.insert(root, 32);
        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.delete(root, 41);
        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.insert(root, 834);
        root = redBlackTree.insert(root, 807);
        root = redBlackTree.insert(root, 512);
        root = redBlackTree.insert(root, 882);
        root = redBlackTree.insert(root, 127);
        root = redBlackTree.insert(root, 675);
        root = redBlackTree.insert(root, 75);
        root = redBlackTree.insert(root, 216);
        root = redBlackTree.insert(root, 822);
        root = redBlackTree.insert(root, 249);
        root = redBlackTree.insert(root, 114);
        root = redBlackTree.insert(root, 689);
        root = redBlackTree.insert(root, 625);
        root = redBlackTree.insert(root, 974);
        root = redBlackTree.insert(root, 221);
        root = redBlackTree.insert(root, 92);
        root = redBlackTree.insert(root, 374);
        root = redBlackTree.insert(root, 123);
        root = redBlackTree.insert(root, 838);
        root = redBlackTree.insert(root, 930);
        root = redBlackTree.insert(root, 654);
        root = redBlackTree.insert(root, 806);
        root = redBlackTree.insert(root, 234);
        root = redBlackTree.insert(root, 381);

        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.delete(root, 127);
        root = redBlackTree.delete(root, 221);
       
        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.insert(root, 41);
        root = redBlackTree.insert(root, 38);
        root = redBlackTree.insert(root, 31);
        root = redBlackTree.insert(root, 12);
        root = redBlackTree.insert(root, 19);
        root = redBlackTree.insert(root, 8);
        redBlackTree.printRedBlackTree(root);
        
        root = redBlackTree.insert(root, 10);
        root = redBlackTree.delete(root, 10);
        redBlackTree.printRedBlackTree(root);

      
    }
}