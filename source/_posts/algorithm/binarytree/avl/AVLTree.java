package io.yanglong.spring.security.binarytree.avl;

import io.yanglong.spring.security.binarytree.BinaryTreeNode;

import java.util.Comparator;

/**
 * package: io.yanglong.spring.security.binarytree.avl <br/>
 * functional describe:平衡二分查找树
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/6/20
 */
public class AVLTree<T> extends BinSearchTree<T> {

    public AVLTree(BinaryTreeNode<T> root, Comparator<? super T> comparator) {
        super(root, comparator);
    }


    /**
     * 新增节点，会破坏平衡
     *
     * @param element 元素
     * @return
     */
    @Override
    public BinaryTreeNode<T> insert(T element) {
        BinaryTreeNode<T> node = super.insert(element);
        root = rebalance(node.getParent(), root);
        return node;
    }

    /**
     * 删除节点，会破坏平衡
     *
     * @param element
     */
    @Override
    public void remove(T element) {
        super.remove(element);
        rebalance(lastNode, root);
    }

    /**
     * 从<code>node</code>开始，自下而上重新平衡树
     *
     * @param node 开始节点
     * @param root 树根
     * @return
     */
    public BinaryTreeNode<T> rebalance(BinaryTreeNode<T> node, BinaryTreeNode<T> root) {
        if (null == node) return root;
        while (true) {
            if (!isBalance(node)) rotate(node);
            if (!node.hasParent()) return node;
            node = node.getParent();
        }
    }

    /**
     * AVL树的平衡有4种情况：左左（不平衡节点2个真子树：左子树，左子树的左子树），右右（不平衡节点有2个真子树：右子树，右子树的右子树），
     * 左右（不平衡节点2个真子树：左子树，左子树的右子树）和右左（平衡节点2个真子树：右子树，右子树的左子树）。
     * 分别对应平衡方法：
     * 右旋：左子树以不平衡节点为中心向右旋转，左子树根节点变为整棵树的根节点，不平衡节点变为其右孩子，其原来右孩子脱离变为不平衡节点左子树。
     * 左旋：右子树以不平衡节点为中心向左旋转，右子树根节点变为整棵树的根节点，不平衡节点变为其左孩子，其原来左孩子脱离变为不平衡节点右子树
     * 左旋-右旋：1.左子树的右子树以左子树的根节点为中心左旋，左子树的右子树根节点变为左子树根节点，其左孩子脱离变为原来左子树根节点的右孩子。2.树变为左左情况，按左左处理。
     * 右旋-左旋：2.右孩子的左子树以右子树的根节点为中心右旋，右子树的左子树根节点变为右子树的根节点，其右孩子脱离变为原来右子树根节点的左孩子。2.树变为右右情况，按右右处理。
     * 旋转，使节点<code>node</code>的平衡因子绝对值不超过1
     * {@linkplain https://zh.wikipedia.org/wiki/AVL%E6%A0%91}
     *
     * @param node 不平衡节点
     * @return 新的子树根
     */
    public BinaryTreeNode<T> rotate(BinaryTreeNode<T> node) {
        //新的子树根节点
        BinaryTreeNode<T> root;
        //取不平衡节点的子树
        BinaryTreeNode<T> child = tallerChild(node);
        //取不平衡节点的子树的子树
        BinaryTreeNode<T> grandchild = tallerChild(child);
        //判断不平横节点所处位置
        boolean rType = node.isLeftChild();
        //不平衡树的父节点
        BinaryTreeNode<T> parent = node.getParent();
        //判断情形
        if (child.isLeftChild()) {
            if (grandchild.isLeftChild()) {//左左
                BinaryTreeNode<T> childR = child.getRightChild();
                node.secede();
                child.secede();
                if (null != childR) {
                    childR.secede();
                    node.attachLeft(childR);
                }
                child.attachRight(node);
                root = child;
            } else {//左右
                BinaryTreeNode<T> grandL = grandchild.getLeftChild();
                BinaryTreeNode<T> grandR = grandchild.getRightChild();
                node.secede();
                child.secede();
                grandchild.secede();
                if (null != grandL) {
                    grandL.secede();
                    child.attachLeft(grandL);
                }
                if (null != grandR) {
                    grandR.secede();
                    node.attachLeft(grandR);
                }
                grandchild.attachLeft(child);
                grandchild.attachRight(node);
                root = grandchild;
            }
        } else {
            if (grandchild.isLeftChild()) {//右左
                BinaryTreeNode<T> grandL = grandchild.getLeftChild();
                BinaryTreeNode<T> grandR = grandchild.getRightChild();
                node.secede();
                child.secede();
                grandchild.secede();
                if (null != grandL) {
                    grandL.secede();
                    node.attachRight(grandL);
                }
                if (null != grandR) {
                    grandR.secede();
                    child.attachLeft(grandR);
                }
                grandchild.attachLeft(node);
                grandchild.attachRight(child);
                root = grandchild;
            } else {//右右
                BinaryTreeNode childL = child.getLeftChild();
                node.secede();
                child.secede();
                if (null != childL) {
                    childL.secede();
                    node.attachRight(childL);
                }
                child.attachRight(node);
                root = child;
            }
        }
        if (null != parent) {//加入父树
            if (rType) {
                parent.attachLeft(root);
            } else {
                parent.attachRight(root);
            }
        }
        return root;
    }

    /**
     * 判断单前节点是否平衡
     *
     * @param node 树中节点
     * @return 是否平衡
     */
    protected boolean isBalance(BinaryTreeNode node) {
        if (null == node) return true;
        int lHeight = root.hasLeftChild() ? root.getLeftChild().getHeight() : -1;
        int rHeight = root.hasRightChild() ? root.getRightChild().getHeight() : -1;
        return -1 <= (lHeight - rHeight) && (lHeight - rHeight) <= 1;//若平衡，2个子树的高度差绝对值不超过1
    }

    /**
     * 获取<code>root</code>为根的树中较高的子树根节点
     *
     * @param root 树根节点
     * @return 子树根节点
     */
    protected BinaryTreeNode<T> tallerChild(BinaryTreeNode<T> root) {
        int lHeight = root.hasLeftChild() ? root.getLeftChild().getHeight() : -1;
        int rHeight = root.hasRightChild() ? root.getRightChild().getHeight() : -1;
        if (lHeight > rHeight) return root.getLeftChild();
        if (lHeight < rHeight) return root.getRightChild();
        if (root.isLeftChild()) return root.getLeftChild();
        else return root.getRightChild();
    }

    /**
     * 获取<code>root</code>为根的树中较矮的子树根节点
     *
     * @param root 树根节点
     * @return 子树根节点
     */
    protected BinaryTreeNode<T> smallerChild(BinaryTreeNode<T> root) {
        int lHeight = root.hasLeftChild() ? root.getLeftChild().getHeight() : -1;
        int rHeight = root.hasRightChild() ? root.getRightChild().getHeight() : -1;
        if (lHeight > rHeight) return root.getRightChild();
        if (lHeight < rHeight) return root.getLeftChild();
        if (root.isLeftChild()) return root.getRightChild();
        else return root.getLeftChild();
    }
}
