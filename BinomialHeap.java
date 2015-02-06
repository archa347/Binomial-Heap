    import java.util.Comparable;
    import java.util.PriorityQueue;
    import java.util.Map;
    import java.util.HashMap;
    
    private class BinomialHeap<E extends Comparable<E>> {

        //Binomial Heap node helper class
        private class BinomialHeapNode<T extends Comparable<T>> implements Comparable<BinomialHeapNode<T>> {

            private T key;
            private int size;
            private int order;
            private PriorityQueue<BinomialHeapNode<T>> children;

            public BinomialHeapNode(T k){
                this.key = k;
                this.order = 0;
                this.size = 1;
                this.children = new PriorityQueue<BinomialHeapNode<T>>();
            }

            public void addChild(BinomialHeapNode<T> node) {
                children.add(node);
                this.order++;
                this.size += node.size;
            }

            public BinomialHeapNode<T> getMin() {
                BinomialHeapNode<T> child = this.children.poll();
                if (child != null) this.size -= child.size;
                return child;
            }

            @Override
            public int compareTo(BinomialHeapNode<T> o) {
                return this.getKey().compareTo(o.getKey());
            }
            public T getKey() {
                return key;

            }

            public void setKey(T key) {
                this.key = key;
            }

            public void setOrder(int order) {
                this.order = order;
            }

            public PriorityQueue<BinomialHeapNode<T>> getChildren() {
                return children;
            }

            public void setChildren(PriorityQueue<BinomialHeapNode<T>> children) {
                this.children = children;
            }

            public int getOrder() {
                return order;
            }

            public int getSize() {
                return this.size;
            }
        }
        //End Binomial Heap Node Helper class

        private Map<Integer,BinomialHeapNode<E>> forest;
        private PriorityQueue<BinomialHeapNode<E>> min;
        private int size;

        //Public API

        public BinomialHeap() {
            this.forest = new HashMap<Integer, BinomialHeapNode<E>>();
            this.size = 0;
        }
        public BinomialHeap(E k) {
            BinomialHeapNode<E> node = new BinomialHeapNode<E>(k);
            this.forest = new HashMap<Integer,BinomialHeapNode<E>>();
            forest.put(0, node);
            this.size = 1;
            findMin();
        }

        public BinomialHeap(BinomialHeapNode<E> tree) {
            this.forest = new HashMap<Integer,BinomialHeapNode<E>>();
            forest.put(tree.getOrder(),tree);
            this.size = tree.getSize();
            findMin();
        }

        public E getMin() {
            if (this.size == 0) return null;
            return this.min.peek().getKey();
        }

        public E deleteMin() {
            if (this.size == 0) return null;
            BinomialHeapNode<E> ret = this.min.poll();
            this.size -= ret.getSize();
            this.forest.remove(ret.getOrder());
            BinomialHeapNode<E> tree;
            while ( (tree = ret.getMin()) != null){
                this.mergeHeap(new BinomialHeap<E>(tree));
            }
            return ret.getKey();
        }

        public void insert(E k) {
            BinomialHeap<E> q = new BinomialHeap<E>(k);
            this.mergeHeap(q);
        }

        private void mergeHeap(BinomialHeap<E> heap) {
            //Iterate through the merging heap's forest
            for (int order : heap.forest.keySet()){
                //check if my forest contains tree of this order
                if (this.forest.containsKey(order)) {
                    //Merge merging heap k-order tree with my k-order tree
                    BinomialHeapNode<E> newTree = mergeTree(this.forest.get(order), heap.forest.get(order));
                    //check if my forest contains a k+1 order tree
                    while (this.forest.containsKey(newTree.getOrder())) {
                        int newOrder = newTree.getOrder(); //order k+1
                        //Merge newTree with existing k+1 order tree
                        newTree = mergeTree(newTree,this.forest.get(newOrder));
                        //remove old k+1-order tree
                        this.forest.remove(newOrder);
                        //repeat until unused order found
                    }
                    //remove old k-order tree
                    this.forest.remove(order);
                    //add new tree to my forest
                    this.forest.put(newTree.getOrder(),newTree);
                }
                // if no k-order tree, add directly to forest
                else this.forest.put(order,heap.forest.get(order));
            }
            this.size += heap.size;
            findMin(); //rebuild min list
        }

        //End public API

        //Private helper methods
        private BinomialHeapNode<E> mergeTree(BinomialHeapNode<E> p, BinomialHeapNode<E> q) {
            //If p is less than q
            if (p.compareTo(q) <= 0) {
                p.addChild(q);
                return p;
            }
            else {
                q.addChild(p);
                return q;
            }
        }

        private void findMin() {
            min = new PriorityQueue<BinomialHeapNode<E>>();
            for (BinomialHeapNode<E> tree : forest.values()) {
                min.add(tree);
            }
        }

    }
    
