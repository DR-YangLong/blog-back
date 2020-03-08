package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:二叉树节点，基于 父亲-左孩子-右孩子 模型。
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 10:28
 */
public interface BinaryTreeNode<T>{
    /**
     * 以当前节点为根的树的规模，方便tree操作
     *
     * @return
     */
    int size();

    /**
     * 以当前节点为根的树的高度，方便tree操作
     *
     * @return
     */
    int getHeight();

    /**
     * 以当前节点为根的树的深度，方便tree操作
     *
     * @return
     */
    int getDepth();

    /**
     * 获取当前节点数据
     *
     * @return
     */
    T getElement();

    /**
     * 将新值放入当前节点
     *
     * @param val 新值
     * @return 旧值
     */
    T setElement(T val);

    /**
     * 获得父亲节点
     *
     * @return
     */
    BinaryTreeNode<T> getParent();

    /**
     * 获得第一个孩子节点
     *
     * @return
     */
    BinaryTreeNode<T> getLeftChild();

    /**
     * 获得右孩子
     *
     * @return
     */
    BinaryTreeNode<T> getRightChild();

    /**
     * 是否有父节点
     *
     * @return
     */
    boolean hasParent();

    /**
     * 是否有左孩子
     *
     * @return
     */
    boolean hasLeftChild();

    /**
     * 是否有右孩子
     *
     * @return
     */
    boolean hasRightChild();

    /**
     * 是否为叶子节点
     *
     * @return
     */
    boolean isLeaf();

    /**
     * 本节点是否是左孩子
     *
     * @return
     */
    boolean isLeftChild();

    /**
     * 设置左孩子
     *
     * @param node
     */
    void setLeftChild(BinaryTreeNode<T> node);

    /***
     * 设置右孩子
     * @param node
     */
    void setRightChild(BinaryTreeNode<T> node);

    /**
     * 设置父节点
     *
     * @param node
     */
    void setParent(BinaryTreeNode<T> node);


    /**
     * 结构发生改变时更新规模
     */
    void updateSize();

    /**
     * 结构发生改变时更新高度
     */
    void updateHeight();

    /**
     * 结构发生改变时更新深度
     */
    void updateDepth();

    /**
     * 获得中序遍历时的前驱
     *
     * @return
     */
    BinaryTreeNode<T> getPrevious();

    /**
     * 获得中序遍历时的后继
     *
     * @return
     */
    BinaryTreeNode<T> getNext();

    /**
     * 将节点作为本节点的左孩子
     *
     * @param node
     * @return
     */
    BinaryTreeNode<T> attachLeft(BinaryTreeNode<T> node);

    /**
     * 将节点作为本节点的右孩子
     *
     * @param node
     * @return
     */
    BinaryTreeNode<T> attachRight(BinaryTreeNode<T> node);


    /**
     * 将以此节点为根的子树从树中摘除
     */
    BinaryTreeNode<T> secede();
}
