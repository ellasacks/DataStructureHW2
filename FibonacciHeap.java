/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    public HeapNode min;
    public HeapNode first;
    public int size;
    public int totalMarkedNodes;
    public int totalTrees;
    public static int totalCuts = 0;
    public static int totalLinks = 0;

    public FibonacciHeap(){
        this.min = null;
        this.first = null;
        this.size = 0;
        this.totalMarkedNodes = 0;
        this.totalTrees = 0;
    }


    /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return this.size == 0;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode(key);
        if (this.isEmpty()){
            this.min = newNode;
            this.first = newNode;
        }
        else{
            HeapNode tempFirst = this.first;
            HeapNode last = this.first.getPrev();
            newNode.setPrev(last);
            newNode.setNext(tempFirst);
            tempFirst.setPrev(newNode);
            last.setNext(newNode);
            this.first = newNode;
            if (newNode.getKey() < tempFirst.getKey()){
                this.min = newNode;
            }
        }
        this.totalTrees += 1;
        this.size += 1;
        return newNode;
    }
   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
        if (this.isEmpty()){
          return;
        }
        HeapNode tempMin = this.min;

        if (tempMin.getChild() == null){
            if (this.totalTrees == 1){
                this.first = null;
                this.min = null;
            }
            HeapNode prevMin = tempMin.getPrev();
            HeapNode nextMin = tempMin.getNext();
            prevMin.setNext(nextMin);
            nextMin.setPrev(prevMin);
            if (this.first == tempMin){
                this.first = tempMin.getNext();
            }
        }

        else{
            HeapNode childMin = tempMin.getChild();
            if (this.first == tempMin){
                this.first = childMin;
            }

            if (this.totalTrees != 1){
                HeapNode prevMin = tempMin.getPrev();
                HeapNode nextMin = tempMin.getNext();
                HeapNode lastChildMin = childMin.getPrev();
                prevMin.setNext(childMin);
                childMin.setPrev(prevMin);
                nextMin.setPrev(lastChildMin);
                lastChildMin.setNext(nextMin);
            }

            childMin.setParent(null);
            this.totalTrees += 1;
            if (childMin.getMarked()){
                childMin.setMarked(false);
                this.totalMarkedNodes -= 1;
            }
            HeapNode temp = childMin.getNext();
            while (temp != childMin){
                this.totalTrees += 1;
                temp.setParent(null);
                if (temp.getMarked()){
                    temp.setMarked(false);
                    this.totalMarkedNodes -= 1;
                }
                temp = temp.getNext();
            }
        }
        tempMin.setChild(null);
        tempMin.setNext(null);
        tempMin.setPrev(null);
        this.totalTrees -= 1;
        this.size -= 1;
        if (!this.isEmpty()){
            this.successiveLinking();
        }
    }

    private void successiveLinking(){
        HeapNode[] ranks = new HeapNode[(int) Math.floor(1.5 * (Math.log(this.size)/Math.log(2))) + 1];
        this.first.setPrev(this.first);
        this.first.setNext(this.first);
        ranks[this.first.getRank()] = this.first;
        HeapNode tempNext = this.first.getNext();

        HeapNode tempNextNext;
        while (tempNext != this.first){
            tempNextNext = tempNext.getNext();
            tempNext.setPrev(this.first);
            tempNext.setNext(this.first);
            int rank = tempNext.getRank();
            if (ranks[rank] == null){
                ranks[rank] = tempNext;
            }

            else{
                boolean emptyCell = false;
                HeapNode t1 = tempNext;

                while (!emptyCell){
                    HeapNode linkedTree;
                    HeapNode t2 = ranks[t1.getRank()];
                    ranks[t1.getRank()] = null;

                    if (t1.getKey()  > t2.getKey()){
                        linkedTree = Link(t1, t2);
                    }
                    else{
                        linkedTree = Link(t2, t1);
                    }
                    if (ranks[linkedTree.getRank()] == null){
                        ranks[linkedTree.getRank()] = linkedTree;
                        emptyCell = true;
                    }
                    else{
                        t1 = linkedTree;
                    }
                }
            }

            tempNext = tempNextNext;
        }

        int nullCounter = 0;
        for (HeapNode tree : ranks){
            if (tree == null){
                nullCounter++;
            }
        }
        HeapNode[] allTrees = new HeapNode[ranks.length - nullCounter];
        int index = 0;
        for (HeapNode tree : ranks){
            if (tree != null){
                allTrees[index] = tree;
                index++;
            }
        }
        this.first = allTrees[0];
        this.min = allTrees[0];
        totalTrees = allTrees.length;

        for (int i = 0; i<allTrees.length; i++){
            HeapNode t = allTrees[i];
            if (this.min.getKey() > t.getKey()){
                this.min = t;
            }
            t.setPrev(allTrees[i-1 % allTrees.length]);
            t.setNext(allTrees[i+1 % allTrees.length]);
            }
    }



    /**
     *
     * @param t1
     * @param t2
     * t1.key > t2.key
     * @return
     */
    private HeapNode Link(HeapNode t1, HeapNode t2){
        if (t1.getRank() == 0){
            t2.setChild(t1);
            t1.setParent(t2);
        }
        else{

            HeapNode y = t2;
            HeapNode x = t1;
            while (y.getChild() != null && x != null){
                HeapNode yLastItem = y.getChild().getPrev();
                x.setPrev(yLastItem);
                x.setNext(y.getChild());
                y.getChild().setPrev(x);
                yLastItem.setNext(x);
                y = y.getChild();
                x = x.getChild();
            }
            t1.setParent(t2);
            t2.setChild(t1);
        }

        t2.setRank(t2.getRank()+1);
        totalLinks += 1;
        this.totalTrees -= 1;
        return t2;

    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;// should be replaced by student code
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	if (heap2.isEmpty()){
            return;
        }
        if (this.isEmpty()){
            this.first = heap2.first;
            this.min = heap2.min;
        }
        else{
            HeapNode thisLastItem = this.first.getPrev();
            HeapNode heap2LastItem = heap2.first.getPrev();
            this.first.setPrev(heap2LastItem);
            thisLastItem.setNext(heap2.first);
            heap2.first.setPrev(thisLastItem);
            heap2LastItem.setNext(this.first);
        }

        this.totalTrees += heap2.totalTrees;
        this.size += heap2.size();
        this.totalMarkedNodes += heap2.totalMarkedNodes;
        if (heap2.min.getKey() < this.min.getKey()){
            this.min = heap2.min;
        }
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * 
    */
    public int[] countersRep()
    {
    	if (this.isEmpty()){
            return new int[0];
        }
        int[] arr = new int[(int) Math.floor(1.5 * (Math.log(this.size)/Math.log(2))) + 1];
        arr[this.first.getRank()] += 1;
        HeapNode temp = this.first.getNext();
        while (temp != this.first){
            arr[temp.getRank()] += 1;
            temp = temp.getNext();
        }

        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, x.getKey() - Integer.MIN_VALUE);
        this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey() - delta);
        if (x.getKey() < this.min.getKey()){
            this.min = x;
        }
        if (x.getParent() != null){
            if (x.getKey() < x.getParent().getKey()){
                this.cascadingCuts(x, x.getParent());
            }
        }
        return; // should be replaced by student code
    }

    private void cascadingCuts(HeapNode x, HeapNode y){
        this.cut(x,y);
        if (y.getParent() != null){
            if (!y.Marked){
                y.Marked = true;
                this.totalMarkedNodes += 1;
            }
            else{
                cascadingCuts(y, y.getParent());
            }
        }
    }

    private void cut(HeapNode x, HeapNode y){
        x.setParent(null);
        x.setMarked(false);
        totalMarkedNodes -= 1;
        y.setRank(y.getRank() - 1);
        if (x.getNext() == x){
            y.setChild(null);
        }
        else{
            y.setChild(x.getNext());
            x.getPrev().setNext(x.getNext());
            x.getNext().setParent(x.getPrev());
            x.setPrev(x);
            x.setNext(x);
        }
        totalCuts += 1;
        x.setNext(this.first);
        HeapNode thisLastItem = this.first.getPrev();
        x.setPrev(thisLastItem);
        thisLastItem.setNext(x);
        this.first.setPrev(x);
        this.first = x;

        totalTrees += 1;


    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {    
        return this.size - this.totalMarkedNodes; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return this.totalTrees + 2*this.totalMarkedNodes;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[k];
        if (k == 0){
            return arr;
        }
        FibonacciHeap helpHeap = new FibonacciHeap();
        helpHeap.insert(H.min.getKey());
        helpHeap.first.setPointerToOrigNode(H.min);
        for (int i = 0; i<k ; i++){
            arr[i] = helpHeap.min.getKey();
            HeapNode temp = helpHeap.min.getPointerToOrigNode().getChild();
            if (temp != null){
                helpHeap.insert(temp.getKey());
                helpHeap.first.setPointerToOrigNode(temp);
                HeapNode nextNode = temp.getNext();
                while (nextNode != temp){
                    helpHeap.insert(nextNode.getKey());
                    helpHeap.first.setPointerToOrigNode(nextNode);
                    nextNode = nextNode.getNext();
                }
            }
            helpHeap.deleteMin();
        }
        return arr; // should be replaced by student code
    }

//    Todo do deleteeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
    public HeapNode getFirst(){
        return this.first;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        public int rank;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public boolean Marked;
        public HeapNode pointerToOrigNode;

        public HeapNode(int key) {
    		this.key = key;
            this.rank = 0;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.Marked = false;
            this.pointerToOrigNode = null;
    	}

    	public int getKey() {
    		return this.key;
    	}

        public void setKey(int k){
            this.key -= k;
        }

       public HeapNode getChild(){
           return this.child;
       }
       public void setChild(HeapNode node){
           this.child = node;
       }
       public HeapNode getParent(){
           return this.parent;
       }
       public void setParent(HeapNode node){
           this.parent = node;
       }
       public HeapNode getNext(){
           return this.next;
       }
       public void setNext(HeapNode node){
           this.next = node;
       }
       public HeapNode getPrev(){
           return this.prev;
       }
       public void setPrev(HeapNode node){
           this.prev = node;
       }

       public int getRank(){
           return this.rank;
       }
       public void setRank(int rank){
           this.rank = rank;
       }

       public boolean getMarked(){
           return this.Marked;
       }
       public void setMarked(boolean mark){
           this.Marked = mark;
       }
       public HeapNode getPointerToOrigNode(){
           return this.pointerToOrigNode;
       }
       public void setPointerToOrigNode(HeapNode node){
           this.pointerToOrigNode = node;
        }
    }
}
