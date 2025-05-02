public class WeightedQuickUnion {

    int[] array;

    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public WeightedQuickUnion(int N) {
        array = new int[N];
        for (int i = 0; i < N; i++) {
            array[i] = -1;
        }
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        if (parent(v) < 0) {
            return -1 * array[v];
        }
        return sizeOf(parent(v));
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        return array[v]; //the element itself is where it goes / or its the negative size
    }

    /* Returns true if nodes V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        return find(v1) == find(v2); //if they have the same root
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v >= array.length) {
            throw new IllegalArgumentException();
        }
        if (parent(v) < 0) {
            return v; //once we get to the final index which stores all the values we return the index
        }//for every recursive call we need to point the node back to the new
        int root = find(parent(v));
        array[v] = root; //optimization to point everything to the root
        return root;
    }

    private int findRoot(int v){
        if (parent(v) < 0) {
            return v; //once we get to the final index which stores all the values we return the index
        }//for every recursive call we need to point the node back to the new
        return findRoot(parent(v));
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing a item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        if (v1 == v2) {
            return;
        }
        if (connected(v1, v2)) {
            return;
        }
        //the tree with the greater weight points to the one with less
        int sizeV1 = sizeOf(v1);
        int sizeV2 = sizeOf(v2);
        if (sizeV1 <= sizeV2) { //connect v1 to v2's root
            int rootV1 = find(v1); //connect v1's root to v2s root
            int rootV2 = find(v2);
            array[rootV1] = rootV2;
            array[rootV2] = -1 * (sizeV1 + sizeV2);
        } else { //v1 is greater than v2, points v2 to v1
            int rootV1 = find(v1); //connect v1's root to v2s root
            int rootV2 = find(v2);
            array[rootV2] = rootV1;
            array[rootV1] = -1 * (sizeV1 + sizeV2); //update root size
        }
    }
}
