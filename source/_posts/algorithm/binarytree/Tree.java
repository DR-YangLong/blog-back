package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 10:08
 */
public interface Tree<T> {

    /**
     * 获得树根
     *
     * @return
     */
    TreeNode<T> getRoot();

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
    TreeIterator<T> preOrderTraversal();

    /**
     * 后序遍历
     */
    TreeIterator<T> postOrderTraversal();

    /**
     * 层次遍历
     */
    TreeIterator<T> levelOrderTraversal();
}
