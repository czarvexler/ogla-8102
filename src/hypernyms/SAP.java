package hypernyms;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

import java.util.Collections;

/**
 * @author aleksander.veksler
 * @since Apr-2018
 */
public class SAP {

    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(final Digraph G) {
        this.digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(final int v, final int w) {
        return length(Collections.singleton(v), Collections.singleton(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(final int v, final int w) {
        return ancestor(Collections.singleton(v), Collections.singleton(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(final Iterable<Integer> v, final Iterable<Integer> w) {
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsV = new BreadthFirstDirectedPaths(this.digraph, v);
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsW = new BreadthFirstDirectedPaths(this.digraph, w);

        int length = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (breadthFirstDirectedPathsV.hasPathTo(i) && breadthFirstDirectedPathsW.hasPathTo(i)) {
                final int l = breadthFirstDirectedPathsV.distTo(i) + breadthFirstDirectedPathsW.distTo(i);
                if (length == -1 || l < length) {
                    length = l;
                }
            }
        }

        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(final Iterable<Integer> v, final Iterable<Integer> w) {
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsV = new BreadthFirstDirectedPaths(this.digraph, v);
        final BreadthFirstDirectedPaths breadthFirstDirectedPathsW = new BreadthFirstDirectedPaths(this.digraph, w);

        int length = -1;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (breadthFirstDirectedPathsV.hasPathTo(i) && breadthFirstDirectedPathsW.hasPathTo(i)) {
                final int l = breadthFirstDirectedPathsV.distTo(i) + breadthFirstDirectedPathsW.distTo(i);
                if (length == -1 || l < length) {
                    length = l;
                    ancestor = i;
                }
            }
        }

        return ancestor;
    }

    // do unit testing of this class
    public static void main(final String[] args) {

    }
}
