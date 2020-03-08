package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:二叉树接口定义
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 10:05
 */
public interface BinaryTree<T> {
    //迭代器
    interface BinaryTreeIterator<E>{
        boolean hasNext();
        BinaryTreeNode<E> next();
    }
    /**
     * 获得树根
     *
     * @return
     */
    BinaryTreeNode<T> getRoot();

    /**
     * 树是否为空
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 获取树的高度
     *
     * @return
     */
    int getTreeHeight();

    /**
     * 获取树的深度
     *
     * @return
     */
    int getTreeDepth();

    /**
     * 树的规模，节点数量
     *
     * @return
     */
    int size();

    /**
     * 前序遍历
     */
    BinaryTreeIterator<T> preOrderTraversal();

    /**
     * 后序遍历
     */
    BinaryTreeIterator<T> postOrderTraversal();

    /**
     * 层次遍历
     */
    BinaryTreeIterator<T> levelOrderTraversal();
    /**
     * 中序遍历
     */
    BinaryTreeIterator<T> inOrderTraversal();

    /**
     * 将以node节点为根的子树从此树摘离
     *
     * @param node
     * @return
     */
    BinaryTree<T> secede(BinaryTreeNode<T> node);
}
