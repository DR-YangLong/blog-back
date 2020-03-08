package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/5/3 16:58
 */
public class TreeTest {
    public static void main(String[] args) {
        //normal tree
        TreeNodeImpl<String> r=new TreeNodeImpl<>();
        r.setElement("root");
        TreeNodeImpl<String> r0=new TreeNodeImpl<>();
        r0.setElement("0");
        r.setLeftChild(r0);
        TreeNodeImpl<String> r1=new TreeNodeImpl<>();
        r1.setElement("1");
        r0.setSibling(r1);
        TreeNodeImpl<String> r2=new TreeNodeImpl<>();
        r2.setElement("2");
        r1.setSibling(r2);
        TreeNodeImpl<String> r3=new TreeNodeImpl<>();
        r3.setElement("3");
        r1.setLeftChild(r3);
        TreeNodeImpl<String> r4=new TreeNodeImpl<>();
        r4.setElement("4");
        r3.setSibling(r4);
        TreeNodeImpl<String> r5=new TreeNodeImpl<>();
        r5.setElement("5");
        r2.setLeftChild(r5);
        Tree<String> tree=new TreeImpl<>(r);
        System.out.println(tree.getTreeDepth());
        System.out.println(tree.getTreeHeight());
        System.out.println(tree.size());
        TreeIterator<String> itr=tree.preOrderTraversal();
        System.out.println("前序遍历");
        while (itr.hasNext()){
            System.out.print(itr.next().getElement());
        }
        itr=tree.postOrderTraversal();
        System.out.println("\n后序遍历");
        while (itr.hasNext()){
            System.out.print(itr.next().getElement());
        }
        itr=tree.levelOrderTraversal();
        System.out.println("\n层次遍历");
        while (itr.hasNext()){
            System.out.print(itr.next().getElement());
        }
        System.out.println("\n");
        //二叉树
        BinaryTreeNode<String> b_root=new BinaryTreeNodeImpl<>();
        b_root.setElement("root");
        BinaryTreeNode<String> b=new BinaryTreeNodeImpl<>();
        b.setElement("b");
        b_root.attachLeft(b);
        BinaryTreeNode<String> d=new BinaryTreeNodeImpl<>();
        d.setElement("d");
        b.attachLeft(d);
        BinaryTreeNode<String> e=new BinaryTreeNodeImpl<>();
        e.setElement("e");
        b.attachRight(e);
        BinaryTreeNode<String> c=new BinaryTreeNodeImpl<>();
        c.setElement("c");
        b_root.attachRight(c);
        BinaryTreeNode<String> f=new BinaryTreeNodeImpl<>();
        f.setElement("f");
        c.attachLeft(f);
        BinaryTreeNode<String> g=new BinaryTreeNodeImpl<>();
        g.setElement("g");
        c.attachRight(g);
        BinaryTreeNode<String> h=new BinaryTreeNodeImpl<>();
        h.setElement("h");
        d.attachLeft(h);
        BinaryTreeNode<String> i=new BinaryTreeNodeImpl<>();
        i.setElement("i");
        d.attachRight(i);
        BinaryTreeNode<String> j=new BinaryTreeNodeImpl<>();
        j.setElement("j");
        e.attachLeft(j);
        BinaryTreeNode<String> k=new BinaryTreeNodeImpl<>();
        k.setElement("k");
        e.attachRight(k);
        BinaryTreeNode<String> l=new BinaryTreeNodeImpl<>();
        l.setElement("l");
        f.attachLeft(l);
        BinaryTree<String> binaryTree=new BinaryTreeImpl<>(b_root);
        System.out.println(binaryTree.getTreeDepth());
        System.out.println(binaryTree.getTreeHeight());
        System.out.println(binaryTree.size());
        BinaryTree.BinaryTreeIterator<String> iterator=binaryTree.preOrderTraversal();
        System.out.println("前序遍历");
        while (iterator.hasNext()){
            System.out.print(iterator.next().getElement());
        }
        iterator=binaryTree.postOrderTraversal();
        System.out.println("\n后序遍历");
        while (iterator.hasNext()){
            System.out.print(iterator.next().getElement());
        }
        iterator=binaryTree.levelOrderTraversal();
        System.out.println("\n层次遍历");
        while (iterator.hasNext()){
            System.out.print(iterator.next().getElement());
        }
        iterator=binaryTree.inOrderTraversal();
        System.out.println("\n中序遍历");
        while (iterator.hasNext()){
            System.out.print(iterator.next().getElement());
        }
        System.out.println("\n");
    }
}
