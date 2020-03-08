package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:一般树实现，因为一般树无法固定度数，<br/>
 * 因而不能实现自动添加，删除的功能，只能用过操作节点内部的引用来删除替换或者新增节点
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/28 9:37
 */
public class TreeImpl<T> extends AbstractTree<T> {
    private TreeNode<T> root;//树根

    public TreeImpl() {
    }

    public TreeImpl(TreeNode<T> root) {
        this.root = root;
    }

    @Override
    public TreeNode<T> getRoot() {
        return root;
    }

    @Override
    public boolean isEmpty() {
        return null == root;
    }

    @Override
    public int getTreeHeight() {
        if (!isEmpty()) {
            return root.getHeight();
        }
        return 0;
    }

    @Override
    public int getTreeDepth() {
        return getTreeHeight();
    }

    @Override
    public int size() {
        if (!isEmpty()) {
            return root.size();
        }
        return 0;
    }

    public void setRoot(TreeNode<T> root) {
        this.root = root;
    }


}
