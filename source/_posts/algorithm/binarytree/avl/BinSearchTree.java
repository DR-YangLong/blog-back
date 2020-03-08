package io.yanglong.spring.security.binarytree.avl;

import io.yanglong.spring.security.binarytree.BinaryTreeImpl;
import io.yanglong.spring.security.binarytree.BinaryTreeNode;
import io.yanglong.spring.security.binarytree.BinaryTreeNodeImpl;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * package: io.yanglong.spring.security.binarytree.avl <br/>
 * functional describe:二分查找树
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/6/13
 */
public class BinSearchTree<T> extends BinaryTreeImpl<T> {
    //比较器,为什么要使用下边界：父类的比较器由于子类继承了父类的属性，方法，因而不会有歧义。
    // 如果上边界，子类自定义的方法会带来歧义
    private final Comparator<? super T> comparator;
    //最后操作的节点，用于AVL树调节平衡
    protected BinaryTreeNode<T> lastNode;

    public BinSearchTree(BinaryTreeNode<T> root, Comparator<? super T> comparator) {
        super(root);
        this.comparator = comparator;
    }

    /**
     * 插入新的节点
     *
     * @param element 元素
     * @return 树节点
     */
    public BinaryTreeNode<T> insert(T element) {
        BinaryTreeNode<T> node = new BinaryTreeNodeImpl<>();
        node.setElement(element);
        if (isEmpty()) {//空树
            lastNode = root = node;
        } else {//一般插入
            BinaryTreeNode<T> vRoot = root;//从根节点开始
            boolean asLeftChild;//是否将新节点作为左孩子插入
            while (true) {//不断
                vRoot = binSearch(vRoot, element, comparator);//循环查找element相等的节点，如果没有，直接返回最后一个
                if (comparator.compare(element, vRoot.getElement()) < 0) {//没有找到返回最后一个节点，此节点无左子树
                    asLeftChild = true;
                    break;
                } else if (comparator.compare(element, vRoot.getElement()) > 0) {//或返回最后一个节点，此节点无右子树
                    asLeftChild = false;
                    break;
                } else if (!vRoot.hasLeftChild()) {//查找成功，且此时节点没有左子树
                    asLeftChild = true;
                    break;
                } else if (!vRoot.hasRightChild()) {//查找成功，且此时节点有左子树，没有右子树
                    asLeftChild = false;
                    break;
                } else {
                    vRoot = vRoot.getLeftChild();//没有成功，如果左子树和右子树都有，选择一个子树进行查找
                }
            }
            //找到了合适的节点，并且知道插入位置
            node.setParent(vRoot);
            if (asLeftChild) vRoot.setLeftChild(node);
            else vRoot.setRightChild(node);
        }
        return node;
    }

    /**
     * 删除包含element值的节点。
     *
     * @param element
     * @return
     */
    public void remove(T element) {
        if (isEmpty()) return;//空树
        BinaryTreeNode<T> vRoot = binSearch(root, element, comparator);//查找
        if (0 != comparator.compare(element, vRoot.getElement())) return;//查找失败
        //至此，查找到第一个element所在的节点
        if (vRoot.hasLeftChild()) {//有左子树
            BinaryTreeNode<T> previous = vRoot.getPrevious();//获取中序遍历的直接前驱
            previous.setElement(vRoot.setElement(previous.getElement()));//交换数据
            vRoot = previous;//注意这里如果有左子树，交换完数据删除时需要对子节点做处理
        }
        //至此，删除节点为查找出的节点或其直接前驱节点：删除节点，由左子树或右子树替代其位置
        lastNode = vRoot.getParent();//保存父节点
        BinaryTreeNode<T> child = vRoot.hasLeftChild() ? vRoot.getLeftChild() : vRoot.getRightChild();
        if (null == lastNode) {//节点为树根
            if (null != child) child.secede();
            root = child;//作为树根
        } else {
            if (vRoot.isLeftChild()) {//是左孩子，摘出,子树代替
                //此时的情况：
                // 1.经过数据交换，vRoot是待删除节点的直接前驱，vRoot在左子树上，则必然vRoot只有左子树。删除，其左子树取代其位置。
                // 2.没有经过数据交换，vRoot是待删除节点，其只有右子树，并且本身是父节点左子树。删除，其右子树取代其位置。
                vRoot.secede();
                lastNode.attachLeft(child);
            } else {//是右孩子，摘出，子树替代
                //此时情况：
                //1.经过数据交换，vRoot是待删除节点直接前驱，vRoot在左子树上，则必然vRoot是右孩子且要么vRoot是叶子节点，要么其只有左孩子。删除，其左子树取代其位置。
                //2.没有经过数据交换，说明vRoot是待删除节点，其只有右子树，且其是右孩子。删除，其右子树代替其位置。
                vRoot.secede();
                lastNode.attachRight(child);
            }
        }
    }

    /**
     * 查找element所在的节点，若找不到，则返回最后访问的节点
     *
     * @param element 查找的目标值
     * @return 所在节点
     */
    protected BinaryTreeNode<T> binSearch(BinaryTreeNode<T> root, T element, Comparator<? super T> comparator) {
        BinaryTreeNode<T> subTree = root;//子树
        while (true) {//不停的遍历子树节点
            if (comparator.compare(element, subTree.getElement()) > 0) {//如果element大于节点元素，说明在右子树中
                if (subTree.hasRightChild()) {
                    subTree = subTree.getRightChild();//继续对右子树查找
                } else {
                    return subTree;//没有右子树，返回此节点
                }
            } else if (comparator.compare(element, subTree.getElement()) < 0) {//如果element小于节点元素，说明在左子树中
                if (subTree.hasLeftChild()) {
                    subTree = subTree.getLeftChild();//继续对左子树查找
                } else {
                    return subTree;//没有左子树，返回当前节点
                }
            } else {
                return subTree;//对比==0，查找命中第一个存储element的节点，返回
            }
        }
    }

    /**
     * 查找element所在的所有节点
     *
     * @param root    根节点
     * @param element 元素
     */
    protected void searchAll(BinaryTreeNode<T> root, ArrayList<BinaryTreeNode<T>> container, T element) {
        if (null == root || null == container) return;
        if (comparator.compare(element, root.getElement()) == 0) {//找到
            container.add(root);
        }
        //右子树遍历
        if (comparator.compare(element, root.getElement()) >= 0) searchAll(root.getRightChild(), container, element);
        //左子树遍历
        if (comparator.compare(element, root.getElement()) <= 0) searchAll(root.getLeftChild(), container, element);
    }
}
