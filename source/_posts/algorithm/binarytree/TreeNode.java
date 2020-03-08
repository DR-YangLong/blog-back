package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:基于 父亲-长子-兄弟 模型的树node
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 10:04
 */
public interface TreeNode<T> {
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
    TreeNode<T> getParent();

    /**
     * 获得第一个孩子节点
     *
     * @return
     */
    TreeNode<T> getLeftChild();

    /**
     * 获得第一个兄弟节点
     *
     * @return
     */
    TreeNode<T> getNextSibling();

}
