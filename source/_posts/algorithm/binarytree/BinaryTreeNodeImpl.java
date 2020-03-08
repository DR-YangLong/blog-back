package io.yanglong.spring.security.binarytree;

/**
 * package: io.yanglong.spring.security.binarytree <br/>
 * functional describe:二叉树实现
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2017/4/28 9:52
 */
public class BinaryTreeNodeImpl<T> implements BinaryTreeNode<T> {
    protected T element;//节点存储对象
    protected BinaryTreeNode<T> parent;
    protected BinaryTreeNode<T> leftChild;
    protected BinaryTreeNode<T> rightChild;
    protected int size;//本节点为根的树的规模
    protected int height;//本节点为根的树的高度
    protected int depth;//本节点的深度，本节点到root节点的路径长度

    public BinaryTreeNodeImpl() {
        this.size = this.height = this.depth = 1;
    }

    public BinaryTreeNodeImpl(T element, BinaryTreeNode<T> parent, boolean asLeft, BinaryTreeNode<T> leftChild, BinaryTreeNode<T> rightChild, int size, int height, int depth) {
        this.element = element;
        this.parent = this.leftChild = this.rightChild = null;
        this.size = this.height = this.depth = 1;
        if (null != parent) {
            if (asLeft) parent.attachLeft(this);
            else parent.attachRight(this);
        }
        if (null != leftChild) attachLeft(leftChild);
        if (null != rightChild) attachRight(rightChild);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public BinaryTreeNode<T> getRightChild() {
        return rightChild;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public T getElement() {
        return element;
    }

    @Override
    public T setElement(T val) {
        return element = val;
    }

    @Override
    public BinaryTreeNode<T> getParent() {
        return parent;
    }

    @Override
    public BinaryTreeNode<T> getLeftChild() {
        return leftChild;
    }

    @Override
    public void setParent(BinaryTreeNode<T> parent) {
        this.parent = parent;
    }

    @Override
    public void setLeftChild(BinaryTreeNode<T> leftChild) {
        this.leftChild = leftChild;
    }

    @Override
    public void setRightChild(BinaryTreeNode<T> rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public boolean hasParent() {
        return null != parent;
    }

    @Override
    public boolean hasLeftChild() {
        return null != leftChild;
    }

    @Override
    public boolean hasRightChild() {
        return null != rightChild;
    }

    @Override
    public boolean isLeaf() {
        return !hasLeftChild() && !hasRightChild();
    }

    @Override
    public boolean isLeftChild() {
        return hasParent() && this == getParent().getLeftChild();
    }

    @Override
    public void updateSize() {
        size = 1;
        if (hasLeftChild()) size += getLeftChild().size();//左子树规模
        if (hasRightChild()) size += getRightChild().size();//右子树规模
        if (hasParent()) getParent().updateSize();//更新祖先的规模
    }

    @Override
    public void updateHeight() {
        height = 1;//初始化，假设没有孩子
        if (hasLeftChild()) height = Math.max(height, 1 + getLeftChild().getHeight());//有左孩子
        if (hasRightChild()) height = Math.max(height, 1 + getRightChild().getHeight());//有右孩子
        if (hasParent()) getParent().updateHeight();//更新祖先高度
    }

    @Override
    public void updateDepth() {
        depth = hasParent() ? 1 + getParent().getDepth() : 1;//当前节点
        if (hasLeftChild()) getLeftChild().updateDepth();//更新孩子深度
        if (hasRightChild()) getRightChild().updateDepth();
    }

    @Override
    public BinaryTreeNode<T> getPrevious() {
        //中序遍历先遍历左孩子，再遍历本节点，接着遍历右节点
        if (hasLeftChild()) return findMaxRightLeaf();//左子树非空，左子树的最底层最右节点即为前驱
        if (!isLeftChild()) return getParent();//当前节点没有左孩子且是右孩子，前驱是其父亲节点
        //当前节点没有左孩子且是左孩子，说明此时节点是在一棵右子树上
        BinaryTreeNode<T> node = this;
        while (node.isLeftChild()) node = node.getParent();//找到所处右子树的root节点,如果父节点是左孩子，已经被访问过
        //按中序遍历的顺序，右子树的父节点是此节点的前驱
        return node.getParent();
    }

    @Override
    public BinaryTreeNode<T> getNext() {
        //右子树非空，右子树的左子树最下层最左叶子节点为后继
        if (hasRightChild()) return findMaxLeftChild();
        //没有右孩子且当前节点是左孩子，父节点为后继
        if (isLeftChild()) return getParent();
        //没有右孩子且是右孩子，找到所处子树的root节点，root节点的父节点为后继
        BinaryTreeNode<T> node = this;
        while (!node.isLeftChild()) {
            //如果父节点是右孩子，说明已经被访问过，向上找到第一个是左孩子的节点就是root
            node = node.getParent();
        }
        return node.getParent();
    }

    /**
     * 找出节点左子树最左的叶子节点
     *
     * @return
     */
    private BinaryTreeNode<T> findMaxLeftChild() {
        BinaryTreeNode<T> node = leftChild;
        if (null != node) {
            node = leftChild;
            //一直从左子树下降
            while (node.hasLeftChild()) node = node.getLeftChild();
        }
        //循环完，node为最底层左树的第一个节点
        return node;
    }

    /**
     * 找出右子树最底层的最右的叶子节点
     *
     * @return
     */
    private BinaryTreeNode<T> findMaxRightLeaf() {
        BinaryTreeNode<T> node = rightChild;
        if (null != node) {
            while (node.hasRightChild()) {
                node = node.getRightChild();
            }
        }
        return node;
    }

    @Override
    public BinaryTreeNode<T> attachLeft(BinaryTreeNode<T> node) {
        if(hasLeftChild())getLeftChild().secede();//有左孩子，摘除
        if(null!=node){
            node.secede();//脱离
            leftChild=node;
            //更新
            processUpdate(node);
        }
        return this;
    }

    @Override
    public BinaryTreeNode<T> attachRight(BinaryTreeNode<T> node) {
        if(hasRightChild())getRightChild().secede();//摘除右孩子
        if(null!=node){
            node.secede();//脱离
            rightChild=node;
            //更新
            processUpdate(node);
        }
        return this;
    }

    /**
     * 更新自己节点属性和祖先属性
     * @param node
     */
    private void processUpdate(BinaryTreeNode<T> node){
        node.setParent(this);
        updateSize();
        updateHeight();
        node.updateDepth();
    }
    @Override
    public BinaryTreeNode<T> secede() {
        if(null!=parent){
            if(isLeftChild())parent.setLeftChild(null);
            else parent.setRightChild(null);
            parent.updateSize();
            parent.updateHeight();
            parent.updateDepth();
            parent=null;
            updateDepth();
        }
        return this;
    }
}
