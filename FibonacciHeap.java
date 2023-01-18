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

    /**
     * FibonacciHeap constructor
     */
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
    * Complexity: O(1)
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
    * Returns the newly created node.
    * Complexity: O(1)
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode(key);
        
    	if (this.isEmpty()){ // special case: insert when tree is empty.
            this.min = newNode;
            this.first = newNode;
        }
        else{
            //changing pointers to add new node as a new tree in heap: 
        	HeapNode tempFirst = this.first;
            HeapNode last = this.first.getPrev();
            newNode.setPrev(last);
            newNode.setNext(tempFirst);
            tempFirst.setPrev(newNode);
            last.setNext(newNode);
            this.first = newNode;
            
            //updating min pointer if needed:
            if (newNode.getKey() < this.min.getKey()){
                this.min = newNode;
            }
        }
        // updating heap fields: 
    	this.totalTrees += 1;
        this.size += 1;
        return newNode;
    }
   /**
    * public void deleteMin()
    * Deletes the node containing the minimum key.
    * Performs successive linking if FibonacciHeap contains more than 1 Node before deletion
    * Complexity: O(n)
    */
    public void deleteMin()
    {
        if (this.isEmpty()){ // if heap is empty, do nothing. 
          return;
        }
        
        // heap is not empty: 
        HeapNode tempMin = this.min;

        //case1: min has no children: 
        if (tempMin.getChild() == null){ 
        	
        	//updating tree min and first: 
        	if (this.totalTrees == 1){  
                this.first = null;
                this.min = null;
            }

            if (this.first == tempMin){
                this.first = tempMin.getNext();
            }
            //changing pointers to remove min from tree:
            HeapNode prevMin = tempMin.getPrev();
            HeapNode nextMin = tempMin.getNext();
            prevMin.setNext(nextMin);
            nextMin.setPrev(prevMin);
        }
        
        //case2: min has children:
        else{
            HeapNode childMin = tempMin.getChild();
            if (this.first == tempMin){
                this.first = childMin;
            }

          //changing pointers to remove min from tree:
            if (this.totalTrees != 1){
                HeapNode prevMin = tempMin.getPrev();
                HeapNode nextMin = tempMin.getNext();
                HeapNode lastChildMin = childMin.getPrev();
                prevMin.setNext(childMin);
                childMin.setPrev(prevMin);
                nextMin.setPrev(lastChildMin);
                lastChildMin.setNext(nextMin);
            }
            
            // add min's children as new trees in heap: 
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
        
        //update heap fields:
        this.totalTrees -= 1;
        this.size -= 1;
        
        //do successive linking to heap:
        if (!this.isEmpty()){
            this.successiveLinking();
        }
    }

    /**
     * private void successiveLinking()
     *
     * Performs successive linking - Iterate over all trees in FibonacciHeap links trees of same size.
     * Splits the FibonacciHeap to trees based on their rank.
     * Puts a tree with rank i to array in position i, if array[i] is empty,
     * Else performs link with the tree in array[i] and put it in array[i+1], or performs another link,
     * until array[j] (i<j) is empty
     * Complexity: O(n)
     */
    private void successiveLinking(){
        //create array representing "Buckets" of tree ranks to help us link: 
    	HeapNode[] ranks = new HeapNode[(int) Math.floor(1.5 * (Math.log(this.size)/Math.log(2))) + 1];

       // place first tree of heap in bucket. 
        ranks[this.first.getRank()] = this.first;
        HeapNode tempNext = this.first.getNext();
        this.first.setPrev(this.first);
        this.first.setNext(this.first);
        HeapNode tempNextNext;
        
        //iterate over heap roots and link trees of same rank using the 'buckets' method.  
        while (tempNext != this.first){ 
            tempNextNext = tempNext.getNext();
            tempNext.setPrev(tempNext);
            tempNext.setNext(tempNext);
            int rank = tempNext.getRank();
            
            
            if (ranks[rank] == null){ //we dont have a tree with same rank in bucket. 
                ranks[rank] = tempNext;
            }

            else{
                boolean emptyCell = false; // we have a tree with same rank in bucket. 
                HeapNode t1 = tempNext;

             // after first link, continue succesive linking with outcome tree:
                while (!emptyCell){ 
                    HeapNode linkedTree;
                    HeapNode t2 = ranks[t1.getRank()];
                    ranks[t1.getRank()] = null;
                    
                    // calc which tree has min node to determine how to link:
                    if (t1.getKey()  > t2.getKey()){
                        linkedTree = Link(t1, t2);
                    }
                    else{
                        linkedTree = Link(t2, t1);
                    }
                    
                    //we found an empty bucket. place there outcome tree and stop:
                    if (ranks[linkedTree.getRank()] == null){
                        ranks[linkedTree.getRank()] = linkedTree;
                        emptyCell = true;
                    }
                    else{
                        t1 = linkedTree;
                    }
                }
            }

            tempNext = tempNextNext; // continue to next tree root. 
        }
        
       // turn the buckets array of trees roots to our heap: 
        
       // Create same array but without empty cells: 
        int nullCounter = 0;
        for (HeapNode tree : ranks){
            if (tree == null){ //skip empty cells. 
                nullCounter++;
            }
        }
        
        HeapNode[] allTrees = new HeapNode[ranks.length - nullCounter]; 
        int index = 0;
        for (HeapNode tree : ranks){ // 
            if (tree != null){
                allTrees[index] = tree;
                index++;
            }
        }
        
        // connect the new roots of the heap: 
        this.first = allTrees[0];
        this.min = allTrees[0];
        totalTrees = allTrees.length;

        for (int i = 0; i < allTrees.length; i++){
            HeapNode t = allTrees[i];
            if (this.min.getKey() > t.getKey()){
                this.min = t;
            }
            t.setPrev(allTrees[Math.floorMod((i-1),(allTrees.length))]);
            t.setNext(allTrees[Math.floorMod(i+1, allTrees.length)]);
        }
    }



    /**
     * private HeapNode Link(HeapNode t1, HeapNode t2)
     *
     * Performs a link between two HeapNodes that are trees with the same rank.
     * t2's root will be the root of the new tree.
     * pre: t1.key > t2.key
     * Returns HeapNode that is the root of the new tree.
     * Complexity: O(1)
     */
    private HeapNode Link(HeapNode t1, HeapNode t2){
        if (t1.getRank() == 0){
            t2.setChild(t1);
            t1.setParent(t2);
        }
        else{
            t2.getChild().getPrev().setNext(t1);
            t1.setPrev(t2.getChild().getPrev());
            t1.setNext(t2.getChild());
            t2.getChild().setPrev(t1);
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
    * Complexity: O(1)
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Complexity: O(1)
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
        //if both heaps are not empty, add heap2 roots to heap1
        else{
            HeapNode thisLastItem = this.first.getPrev();
            HeapNode heap2LastItem = heap2.first.getPrev();
            this.first.setPrev(heap2LastItem);
            thisLastItem.setNext(heap2.first);
            heap2.first.setPrev(thisLastItem);
            heap2LastItem.setNext(this.first);
        }
        //update heap fields:
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
    * Complexity: O(1)
    */
    public int size()
    {
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of the array depends on the maximum order of a tree.)
    * Complexity: 0(n)
    */
    public int[] countersRep()
    {
    	if (this.isEmpty()){
            return new int[0];
        }
    	//create countersRep array:
        int[] arr = new int[(int) Math.floor(1.5 * (Math.log(this.size)/Math.log(2))) + 1];
        
        //iterate over heap roots and update countersRep:
        arr[this.first.getRank()] += 1;
        HeapNode temp = this.first.getNext();
        while (temp != this.first){
            arr[temp.getRank()] += 1;
            temp = temp.getNext();
        }
        
        //return array with no zeros at the end: 
        if (arr[arr.length-1] != 0){
            return arr;
        }
        int i = arr.length -1;
        while (arr[i] == 0 && i >= 0){
            i--;
        }
        int[] arrNoZero = new int[i+1];
        for (int j = 0; j < i+1; j++){
            arrNoZero[j] = arr[j];
        }
        return arrNoZero;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    * Performs decreaseKey and then deleteMin
    * Complexity: O(n)
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
    * Performs cascadingCuts if x.getKey() < x.getParent().getKey()
    * Complexity: O(n)
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey() - delta); //decrease x key
        
    	
    	if (x.getKey() < this.min.getKey()){ // update heap min if needed
            this.min = x;
        }
        if (x.getParent() != null){ //if x key is smaller then his parent, preform cascadingCuts to keep heap legal
            if (x.getKey() < x.getParent().getKey()){
                this.cascadingCuts(x, x.getParent());
            }
        }
    }

    /**
     * private void cascadingCuts(HeapNode x, HeapNode y)
     *
     * Gets HeapNode x and HeapNode y, y = x.getParent()
     * Performs Cuts until reaching root or until y is not marked
     * Complexity: O(n)
     */
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

    /**
     * private void cut(HeapNode x, HeapNode y)
     *
     * Gets HeapNode x and HeapNode y, y = x.getParent()
     * Cut HeapNode from its parent, and place the new created tree in the first position of the FibonacciHeap
     * Complexity: O(1)
     */
    private void cut(HeapNode x, HeapNode y){
        if (x.Marked){ // beacause x will become root, unmark him. 
            totalMarkedNodes -= 1;
        }
        x.setParent(null);
        x.setMarked(false);
        y.setRank(y.getRank() - 1);
        if (x.getNext() == x){ // if x is the only child of his parent
            y.setChild(null);
        }

        else{
            if (y.getChild() == x){ // if x is the most left child of his parent
                y.setChild(x.getNext());
            }
            HeapNode xPrev = x.getPrev();
            HeapNode xNext = x.getNext();
            xPrev.setNext(xNext);
            xNext.setPrev(xPrev);
            x.setPrev(x);
            x.setNext(x);
        }
        //update heap fields:
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
    * Complexity: O(1)
    */
    public int nonMarked() 
    {    
        return this.size - this.totalMarkedNodes; 
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
    * Compelxity: O(1)
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
    * Complexity: 0(1)
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
    * Complexity: 0(1)
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H.
    * Complexity: O(k*deg(H))
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[k]; //create array for k min vals
        
        if (k == 0){
            return arr;
        }
        
        FibonacciHeap helpHeap = new FibonacciHeap(); // create help heap
        
        // insert node with H.min key to help heap, and keep pointer back to the node in H
        helpHeap.insert(H.min.getKey()); 
        helpHeap.first.setPointerToOrigNode(H.min);
        
        for (int i = 0; i<k ; i++){
            arr[i] = helpHeap.min.getKey(); // insert the min node in help heap to the array
            HeapNode temp = helpHeap.min.getPointerToOrigNode().getChild();
         // if the orig node in H has children, insert them to help heap (they are the potential next min value):
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
            helpHeap.deleteMin(); // delete the min node from help heap
        }
        return arr; 
    }

    
	//  Todo do deleteeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
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

        /**
        * public HeapNode(int key)
        *
        * HeapNode Constructor
        *
        */
        public HeapNode(int key) {
    		this.key = key;
            this.rank = 0;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.Marked = false;
            this.pointerToOrigNode = null; // help field for kmin method. 
    	}

         /**
        * public int getKey()
        *
        * Returns the key of the HeapNode
        * Complexity: O(1)
        */
        public int getKey() {
    		return this.key;
    	}

        /**
        * public void setKey(int k)
        *
        * Sets the ket of the HeapNode to k
        * Complexity: O(1)
        */
        public void setKey(int k){
            this.key = k;
        }

        /**
        * public HeapNode getChild()
        *
        * Returns the Child of the HeapNode
        * Complexity: O(1)
        */
       public HeapNode getChild(){
           return this.child;
       }

        /**
        * public void setChild(HeapNode node)
        *
        * Sets the Child of the HeapNode to be node
        * Complexity: O(1)
        */
       public void setChild(HeapNode node){
           this.child = node;
       }

        /**
        * public HeapNode getParent()
        *
        * Returns the Parent of the HeapNode
        * Complexity: O(1)
        */
       public HeapNode getParent(){
           return this.parent;
       }

        /**
        * public void setParent(HeapNode node)
        *
        * Sets the Parent of the HeapNode to be node
        * Complexity: O(1)
        */
       public void setParent(HeapNode node){
           this.parent = node;
       }

        /**
        * public HeapNode getNext()
        *
        * Returns the next sibling of the HeapNode
        * Complexity: O(1)
        */
       public HeapNode getNext(){
           return this.next;
       }

        /**
        * public void setNext(HeapNode node)
        *
        * Sets the next sibling of the HeapNode to be node
        * Complexity: O(1)
        */
       public void setNext(HeapNode node){
           this.next = node;
       }

        /**
        * public HeapNode getPrev()
        *
        * Returns the previous sibling of the HeapNode
        * Complexity: O(1)
        */
       public HeapNode getPrev(){
           return this.prev;
       }

        /**
        * public void setPrev(HeapNode node)
        *
        * Sets the previous sibling of the HeapNode to be node
        * Complexity: O(1)
        */
       public void setPrev(HeapNode node){
           this.prev = node;
       }

        /**
        * public int getRank()
        *
        * Returns the rank of the HeapNode
        * Complexity: O(1)
        */
       public int getRank(){
           return this.rank;
       }

        /**
        * public void setRank(int rank)
        *
        * Sets the rank of the HeapNode to be rank
        * Complexity: O(1)
        */
       public void setRank(int rank){
           this.rank = rank;
       }

        /**
        * public boolean getMarked()
        *
        * Returns true if the HeapNode is marked, else false
        * Complexity: O(1)
        */

       public boolean getMarked(){
           return this.Marked;
       }

        /**
        * public void setMarked(boolean mark)
        *
        * Sets Marked field to be true or false based of mark value
        * Complexity: O(1)
        */
       public void setMarked(boolean mark){
           this.Marked = mark;
       }

        /**
        * public HeapNode getPointerToOrigNode()
        *
        * Returns a pointer with the id of the HeapNode in its origin FibonacciHeap
        * Complexity: O(1)
        */
       public HeapNode getPointerToOrigNode(){
           return this.pointerToOrigNode;
       }

        /**
        * public void setPointerToOrigNode(HeapNode node)
        *
        * Sets pointerToOrigNode field to be the id of node
        * Complexity: O(1)
        */
       public void setPointerToOrigNode(HeapNode node){
           this.pointerToOrigNode = node;
        }
    }
}
