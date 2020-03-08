package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 17:25
 */
public class TreeNodeImpl<T> implements TreeNode<T>{
    private T element;//节点存储的值
    private TreeNode<T> parent;//父亲节点
    private TreeNode<T> leftChild;//左子树节点
    private TreeNode<T> sibling;//兄弟节点

    @Override
    public T getElement() {
        return this.element;
    }

    @Override
    public T setElement(T val) {
        T old=element;
        this.element=val;
        return old;
    }

    @Override
    public TreeNode<T> getParent() {
        return parent;
    }

    @Override
    public TreeNode<T> getLeftChild() {
        return leftChild;
    }

    @Override
    public TreeNode<T> getNextSibling() {
        return sibling;
    }

    @Override
    public int size() {
        int size=1;//自己计算在内
        TreeNode<T> subTree=leftChild;//从第一个子树开始
        while (null!=subTree){
            size+=subTree.size();//循环获取子树规模相加
            subTree=subTree.getNextSibling();
        }
        return size;
    }

    @Override
    public int getHeight() {
        int height=0;
        TreeNode<T> subTree=leftChild;//从第一个子树开始
        while (null!=subTree){
            height=Math.max(height,subTree.getHeight());//比较第一个子树和第二个子树的高度，返回最大
            subTree=subTree.getNextSibling();
        }
        return height+1;//根节点也包含在高度中
    }

    @Override
    public int getDepth() {
        int depth=1;//自身所在也包含在深度计算中
        TreeNode<T> p=parent;//深度由下向上计算
        while(null!=p){
            depth++;
            p=p.getParent();//向上遍历各个parent节点，计数
        }
        return depth;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public void setLeftChild(TreeNode<T> leftChild) {
        this.leftChild = leftChild;
    }

    public TreeNode<T> getSibling() {
        return sibling;
    }

    public void setSibling(TreeNode<T> sibling) {
        this.sibling = sibling;
    }
}
