import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BSTree<T> implements Cloneable, Serializable, Iterable<T>, MyCollection<T>
{
    private BSTNode<T> root;
    private int treeSize;

    //Return: A reference to the node containing item or null if the search fails
    private BSTNode<T> findNode(T item)
    {
        BSTNode<T> t = root;

        while(t != null)
        {
            int result = ((Comparable)item).compareTo(t.value);
            if (result == 0)
                return t;
            else if (result < 0)
                t = t.left;
            else
                t = t.right;
        }
        return null;
    }

    //Post: BSTree initialized to empty.
    public BSTree()
    {
        root = null;
        treeSize = 0;
    }

    public int size()
    {
        return treeSize;
    }

    public boolean isEmpty()
    {
        return treeSize == 0;
    }

    public void clear()
    {
        treeSize = 0;
        root = null;
    }

    //Post  : item inserted to this tree if item is not already in the tree.
    //Return: true if item inserted to this tree; false otherwise
    public boolean add(T item)
    {
        BSTNode<T> t = root, parent = null;
        int result = 0;

        while(t != null)
        {
            parent = t;
            result = ((Comparable)item).compareTo(t.value);

            if (result == 0)
                return false;
            else if (result < 0)
                t = t.left;
            else
                t = t.right;
        }

        BSTNode<T> newNode = new BSTNode<T>(item, parent);

        if (parent == null)
            root = newNode;
        else if (result < 0)
            parent.left = newNode;
        else
            parent.right = newNode;

        treeSize++;

        return true;
    }

    public boolean contains(Object item)
    {
        BSTNode<T> t = findNode((T)item);
        return (t == null) ? false : true;
    }

    //Post:	The inorder traversal of the values of BSTree printed on screen. 
    public void inorderOutput()
    {
        inorder(root);
    }

    private void inorder(BSTNode<T> t)
    {
        if (t != null)
        {
            inorder(t.left);
            System.out.print(t.value + " ");
            inorder(t.right);
        }
    }

    //Return: A copy of this BSTree. All the nodes in the BSTree are cloned, but the data elements are not.
    public Object clone()
    {
        BSTree<T> copy = null;

        try
        {
            copy = (BSTree<T>)super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
        }

        copy.root = copyTree(root);
        return copy;
    }

    //Desc  : Make a copy of a BSTree
    //Pre   : t points to a BSTree
    //Return: A BSTree that is an exact copy of the one pointed to by t
    private BSTNode<T> copyTree(BSTNode<T> t)
    {
        if (t == null)
            return null;

        BSTNode<T> left = copyTree(t.left);
        BSTNode<T> right = copyTree(t.right);
        BSTNode<T> copyRoot = new BSTNode<T>(t.value, null);

        copyRoot.left = left;
        copyRoot.right = right;

        if (left != null)
            left.parent = copyRoot;
        if (right!= null)
            right.parent = copyRoot;

        return copyRoot;
    }

    public boolean remove(Object item)
    {
        BSTNode<T> t  = findNode((T)item);
        if (t == null)
            return false;

        removeNode(t);
        treeSize--;

        return true;
    }

    private void removeNode(BSTNode<T> D)
    {
        BSTNode<T> pOfD = D.parent;
        BSTNode<T> R;
        BSTNode<T> pOfR;

        if ((D.left == null) && (D.right == null))		//D is a leaf
            R = null;						            //R is null
        else if ((D.left == null) && (D.right != null))	//D has 1 son, right
        {
            R = D.right;					            //R is right son of D
            R.parent = pOfD;
        }
        else if ((D.left != null) && (D.right == null))	//D has 1 son, left
        {
            R = D.left;					                //R is left son of D
            R.parent = pOfD;
        }
        else if (D.right.left == null)				    //D has 2 sons, right son has
        {						                        //no left
            R = D.right;				                //R is right son of D, i.e. R is
            R.left = D.left;				            //the smallest in D right subtree
            R.parent = pOfD;
            D.left.parent = R;
        }
        else						                    //D has 2 sons, right son has
        {						                        //left.
            pOfR = D;
            R = D.right;
            //R is the leftmost node
            while(R.left != null)                       //in D's right subtree, i.e. R is
            {					                        //the smallest in D right subtree.
                pOfR = R;			                    //all other cases, pOfR is D, so no
                R = R.left;			                    //need to modify pOfR, except
            }
            //this case
            pOfR.left = R.right;
            if (R.right != null)
                R.right.parent = pOfR;

            R.left = D.left;
            R.right = D.right;
            R.parent = pOfD;
            R.left.parent = R;
            R.right.parent = R;
        }

        //Set pOfD to reference R for all cases.  This is done last because then D is still being
        //referenced by pOfD up to this point, and thus D will not be garbage collected.  
        if (pOfD == null)
            root = R;			                        //D is the root
        else if (((Comparable)(D.value)).compareTo(pOfD.value) < 0)
            pOfD.left = R;
        else pOfD.right = R;
    }

    //Return: Height of the BSTree; -1 if tree is empty.
    public int height()
    {
        return height(root);
    }

    public <T> int height(BSTNode<T> root)
    {
        if (root == null)
            return -1;
        else
        {
            int heightLeft = height(root.left);
            int heightRight = height(root.right);

            return 1 + (heightLeft > heightRight ? heightLeft : heightRight);
        }
    }

    //Return: The level of the node that contains item; -1 if item is not in BSTree
    public int findLevelOf(T item)
    {
        if (contains(item) == false)
            return -1;

        BSTNode<T> t = root;
        int level = findLevelOf(t, item, 0);

        return level;
    }

    public int findLevelOf(BSTNode<T> t, T item, int level)
    {
        if (t == null)
            return 0;

        if (((Comparable)item).compareTo(t.value) == 0)
            return level;

        int result = findLevelOf(t.left, item, level + 1);
        if (result != 0)
            return result;

        result = findLevelOf(t.right, item, level + 1);

        return result;
    }

    public Object[] toArray()
    {
        Object[] arr = new Object[treeSize];
        Iterator<T> iter = iterator();
        int i = 0;

        while (iter.hasNext())
        {
            arr[i] = iter.next();
            i++;
        }

        return arr;
    }

    //Return: A string representation of this tree, which is a comma separated list in iterator order enclosed in square brackets.
    public String toString()
    {
        String s = "[";
        Iterator<T> iter = iterator();

        for (int i = 0; i < treeSize; i++)
        {
            s += iter.next();
            if (i < treeSize - 1)
                s += ", ";
        }
        s += "]";

        return s;
    }


    public Iterator<T> iterator()
    {
        return new BSTreeIterator();
    }

    protected class BSTreeIterator implements Iterator<T>
    {
        private BSTNode<T> lastReturned = null;
        private BSTNode<T> nextNode = null;

        //Post: nextNode is the leftmost node in the tree
        public BSTreeIterator()
        {
            nextNode = root;
            if (nextNode != null)
                while (nextNode.left != null)
                    nextNode = nextNode.left;
        }

        public boolean hasNext()
        {
            return nextNode != null;
        }

        public T next()
        {
            if (nextNode == null)
                throw new NoSuchElementException("No more elements");
            lastReturned = nextNode;

            if (nextNode.right != null)		//next node is the leftmost in its right subtree
            {
                nextNode = nextNode.right;

                while (nextNode.left != null)
                    nextNode = nextNode.left;
            }
            else				//next node is its closest left ancestor
            {
                BSTNode<T> p = nextNode.parent;

                while (p != null && nextNode == p.right)	//short circuit &&
                {
                    nextNode = p;
                    p = p.parent;
                }
                nextNode = p;
            }
            return lastReturned.value;
        }

        public void remove()
        {
            if (lastReturned == null)
                throw new IllegalStateException("Iterator call to next() required before calling remove()");

            removeNode(lastReturned);
            lastReturned = null;
            treeSize--;
        }
    }
}