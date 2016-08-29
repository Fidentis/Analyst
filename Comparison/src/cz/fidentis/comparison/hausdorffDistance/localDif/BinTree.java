/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fidentis.comparison.hausdorffDistance.localDif;

/**
 *
 * @author Rasto1
 */
public class BinTree {

    Node root;

    public void addNode(int key, float value) {

        // Create a new Node and initialize it
        Node newNode = new Node(key, value);

        // If there is no root this becomes root
        if (root == null) {

            root = newNode;

        } else {

            // Set root as the Node we will start
            // with as we traverse the tree
            Node focusNode = root;

            // Future parent for our new Node
            Node parent;

            while (true) {

                // root is the top parent so we start
                // there
                parent = focusNode;

                // Check if the new node should go on
                // the left side of the parent node
                if (key < focusNode.key) {

                    // Switch focus to the left child
                    focusNode = focusNode.leftChild;

                    // If the left child has no children
                    if (focusNode == null) {

                        // then place the new node on the left of it
                        parent.leftChild = newNode;
                        return; // All Done

                    }

                } else { // If we get here put the node on the right

                    focusNode = focusNode.rightChild;

                    // If the right child has no children
                    if (focusNode == null) {

                        // then place the new node on the right of it
                        parent.rightChild = newNode;
                        return; // All Done

                    }

                }

            }
        }

    }

    // All nodes are visited in ascending order
    // Recursion is used to go to one node and
    // then go to its child nodes and so forth
    public void inOrderTraverseTree(Node focusNode) {

        if (focusNode != null) {

            // Traverse the left node
            inOrderTraverseTree(focusNode.leftChild);

            // Visit the currently focused on node
            System.out.println(focusNode.toString());

            // Traverse the right node
            inOrderTraverseTree(focusNode.rightChild);

        }

    }

    public void preorderTraverseTree(Node focusNode) {

        if (focusNode != null) {

            System.out.println(focusNode);

            preorderTraverseTree(focusNode.leftChild);
            preorderTraverseTree(focusNode.rightChild);

        }

    }

    public void postOrderTraverseTree(Node focusNode) {

        if (focusNode != null) {

            postOrderTraverseTree(focusNode.leftChild);
            postOrderTraverseTree(focusNode.rightChild);

            System.out.println(focusNode);

        }

    }

    public float findNode(int key) {

        // Start at the top of the tree
        Node focusNode = root;

        // While we haven't found the Node
        // keep looking
        while (focusNode.key != key) {

            // If we should search to the left
            if (key < focusNode.key) {

                // Shift the focus Node to the left child
                focusNode = focusNode.leftChild;

            } else {

                // Shift the focus Node to the right child
                focusNode = focusNode.rightChild;

            }

            // The node wasn't found
            if (focusNode == null) {
                return 0;
            }

        }

        return focusNode.value;

    }

    class Node {

        int key;
        float value;

        Node leftChild;
        Node rightChild;

        Node(int key, float value) {

            this.key = key;
            this.value = value;

        }

        public String toString() {

            return "" + key;

            /*
		 * return name + " has the key " + key + "\nLeft Child: " + leftChild +
		 * "\nRight Child: " + rightChild + "\n";
             */
        }

    }
}
