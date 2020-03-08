package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 10:15
 */
public interface TreeIterator<T> {
    /**
     * 是否有下一个遍历节点
     * @return
     */
    boolean hasNext();

    /**
     * 获取下一个遍历节点
     * @return
     */
    TreeNode<T> next();
}
