package io.yanglong.spring.security.binarytree;


import java.util.ArrayList;
import java.util.LinkedList;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:抽象树，抽象出公共遍历方法
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/27 11:17
 */
public abstract class AbstractTree<T> implements Tree<T> {

    @Override
    public TreeIterator<T> preOrderTraversal() {
        return new TreeItr(0, this);
    }

    @Override
    public TreeIterator<T> postOrderTraversal() {
        return new TreeItr(1, this);
    }

    @Override
    public TreeIterator<T> levelOrderTraversal() {
        return new TreeItr(2, this);
    }

    /**
     * 一般树的遍历指针实现
     */
    class TreeItr implements TreeIterator<T> {
        TreeNode<T> next;
        int position = 0;
        ArrayList<TreeNode<T>> itr;
        int itrType;//0-pre,1-post,2-level

        TreeItr(int itrType, Tree<T> tree) {
            this.itrType = itrType;
            if (tree != null && !tree.isEmpty()) {
                this.itr=new ArrayList<>();
                switch (itrType) {
                    case 0:
                        this.preOrderIterator(tree.getRoot());
                        break;
                    case 1:
                        this.postOrderIterator(tree.getRoot());
                        break;
                    case 2:
                        this.levelOrderIterator(tree.getRoot());
                        break;
                }
                next = itr.get(position);
            } else {
                next = null;
                itr = null;
            }
        }

        @Override
        public boolean hasNext() {
            return null!=next;
        }

        @Override
        public TreeNode<T> next() {
            if (hasNext()) {
                TreeNode<T> current = next;
                position++;
                if (position >= itr.size()) {
                    next = null;
                } else {
                    next = itr.get(position);
                }
                return current;
            }
            return null;
        }

        /**
         * 前序遍历指针，递归调用
         *
         * @param root
         */
        private void preOrderIterator(TreeNode<T> root) {
            if (root == null) return;//递归基
            itr.add(root);//前序遍历，先访问根节点
            TreeNode<T> subTree = root.getLeftChild();//取出最左子树
            while (subTree != null) {
                this.preOrderIterator(subTree);//对当前节点的最左子树依次前序遍历
                subTree = subTree.getNextSibling();//最左树便利完，依次遍历其兄弟树
            }

        }

        /**
         * 后序遍历指针，递归调用
         *
         * @param root
         */
        private void postOrderIterator(TreeNode<T> root) {
            if (root == null) return;//递归基
            TreeNode<T> subTree = root.getLeftChild();//取出最左子树
            while (subTree != null) {
                this.postOrderIterator(subTree);//对当前节点的最左子树依次前序遍历
                subTree = subTree.getNextSibling();//最左树便利完，依次遍历其兄弟树
            }
            itr.add(root);//所有左右子树访问完，再访问根
        }

        /**
         * 生成层次遍历的指针，使用队列先放入第一层，然后取出，放入list，<br/>
         * 再取出第二层第一个节点，入队，第二层第二个节点，入队......<br/>
         * 循环直到第二层所有节点入队，然后循环出队并放入list，每出队一个再对其子树进行同样的操作。<br/>
         * 2层循环，外层控制父节点，内层循环控制孩子节点的兄弟节点。
         *
         * @param root
         */
        private void levelOrderIterator(TreeNode<T> root) {
            if (root == null) return;//如果是空树，无法层次遍历
            LinkedList<TreeNode<T>> nodeQueue = new LinkedList<>();//队列
            nodeQueue.offer(root);//根节点入队
            while (!nodeQueue.isEmpty()) {//当队列并不为空
                TreeNode<T> head = nodeQueue.poll();//取出队首
                itr.add(head);//放入list
                TreeNode<T> subTree = head.getLeftChild();//取出最左子树
                while (subTree != null) {//依次找出所有孩子，将节点入队
                    nodeQueue.offer(subTree);
                    subTree = subTree.getNextSibling();//获得兄弟节点入队
                }
            }
        }
    }
}
