package io.yanglong.spring.security.binarytree;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:二叉树实现
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/28 13:39
 */
public class BinaryTreeImpl<T> implements BinaryTree<T> {
    protected BinaryTreeNode<T> root;//树根

    public BinaryTreeImpl() {
    }

    public BinaryTreeImpl(BinaryTreeNode<T> root) {
        this.root = root;
    }

    @Override
    public BinaryTreeNode<T> getRoot() {
        return root;
    }

    @Override
    public boolean isEmpty() {
        return null == root;
    }

    @Override
    public BinaryTree<T> secede(BinaryTreeNode<T> node) {
        return new BinaryTreeImpl<>(node.secede());
    }

    @Override
    public int getTreeHeight() {
        return root.getHeight();
    }

    @Override
    public int getTreeDepth() {
        return getTreeHeight();
    }

    @Override
    public int size() {
        return root.size();
    }

    @Override
    public BinaryTreeIterator<T> preOrderTraversal() {
        return new BinaryItr(0);
    }

    @Override
    public BinaryTreeIterator<T> postOrderTraversal() {
        return new BinaryItr(1);
    }

    @Override
    public BinaryTreeIterator<T> levelOrderTraversal() {
        return new BinaryItr(2);
    }

    @Override
    public BinaryTreeIterator<T> inOrderTraversal() {
        return new BinaryItr(3);
    }

    private class BinaryItr implements BinaryTreeIterator<T> {
        private ArrayList<BinaryTreeNode<T>> itr;//访问顺序组成的list
        private BinaryTreeNode<T> next;//下一个
        private int position = 0;//单前指针位置
        int itrType;//0-pre,1-post,2-level,3-in order

        public BinaryItr(int itrType) {
            this.itrType = itrType;
            if (null != root && root.size() > 0) {
                this.itr = new ArrayList<>();
                switch (itrType) {
                    case 0:
                        preOrder(root);
                        break;
                    case 1:
                        postOrder(root);
                        break;
                    case 2:
                        levelOrder(root);
                        break;
                    case 3:
                        inOrder(root);
                        break;
                }
                next = itr.get(position);
            } else {
                itr = null;
                next = null;
            }
        }

        public boolean hasNext() {
            return null != next;
        }

        public BinaryTreeNode<T> next() {
            if (hasNext()) {
                BinaryTreeNode<T> current = next;
                position++;
                if (position >=itr.size()) {
                    next = null;
                } else {
                    next = itr.get(position);
                }
                return current;
            }
            return null;
        }

        /**
         * 前序遍历
         *
         * @param root
         */
        void preOrder(BinaryTreeNode<T> root) {
            if (null == root) return;
            itr.add(root);//先访问根节点
            preOrder(root.getLeftChild());//遍历左子树
            preOrder(root.getRightChild());//遍历右子树
        }

        /**
         * 后序遍历
         *
         * @param root
         */
        void  postOrder(BinaryTreeNode<T> root) {
            if (null == root) return;
            postOrder(root.getLeftChild());//先遍历左子树
            postOrder(root.getRightChild());//然后遍历右子树
            itr.add(root);//最后访问根节点
        }

        /**
         * 层次遍历，使用队列，依次放入根节点，根节点的左右节点，然后出队根节点，左节点，右节点...
         * 使用循环，通过判断队列不为空，从而不断将每一层元素入队
         *
         * @param root
         */
        void levelOrder(BinaryTreeNode<T> root) {
            if (root == null) return;
            LinkedList<BinaryTreeNode<T>> queue = new LinkedList<>();
            queue.offer(root);
            /**
             * 循环出队和入队，条件是队列不为空，当有2个子节点时，只出队了一个节点，但是由于队列的特性，顺序得到保证。
             * 到叶子节点后，循环功能就只为出队
             */
            while (!queue.isEmpty()) {
                BinaryTreeNode<T> currentRoot = queue.poll();//当前子树的根节点
                itr.add(currentRoot);//遍历
                if (currentRoot.hasLeftChild()) queue.offer(currentRoot.getLeftChild());//左子树入队
                if (currentRoot.hasRightChild()) queue.offer(currentRoot.getRightChild());//右子树入队
            }
        }

        /**
         * 中序遍历
         *
         * @param root
         */
        void inOrder(BinaryTreeNode<T> root) {
            if (null == root) return;
            inOrder(root.getLeftChild());//先遍历左子树
            itr.add(root);//然后遍历根节点
            inOrder(root.getRightChild());//最后遍历右子树
        }
    }
}
