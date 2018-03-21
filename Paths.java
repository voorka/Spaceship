
package student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Edge;
import models.Node;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {

    /** Return the shortest path from start to end, or the empty list if a path
     * does not exist.
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath(Node start, Node end) {
        /* TODO Read note A7 FAQs on the course piazza for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in lecture slides

        // map contains an entry for each node in S or F. Thus,
        // |map| = |S| + |F|.
        // For each such key-node, the value part contains the shortest known
        // distance to the node and the node's backpointer on that shortest path.
        HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();

        F.add(start, 0);
        map.put(start, new SFdata(0, null, 0));
        // invariant: as in lecture slides, together with def of F and map
    
        while (F.size() != 0) {
        	
            Node f= F.poll();
          if(map.get(f).hostilecount > 1){
           	while(f.isHostile()){ f= F.poll();}}
            
            if (f == end) return constructPath(end, map);   
            int fDist= map.get(f).distance;
         
            for (Edge e : f.getExits()) {// for each neighbor w of f
                Node w= e.getOther(f);              
                int newWdist= fDist + e.length;
                int newhostilecount = map.get(f).hostilecount + (w.isHostile()? 1:0);
                
                if (w.isHostile()){newWdist= (int) ((fDist + e.length)*(1.2));}
              
                if (w.hasSpeedUpgrade()){
                	newWdist = (int)(fDist + e.length*(.8));
                }
               
                SFdata wData= map.get(w);
                if (newhostilecount < 2){                                              
                	if (wData == null) { //if w not in S or F
                		map.put(w, new SFdata(newWdist, f, newhostilecount));
                		F.add(w, newWdist);
                	} else if (newWdist < wData.distance) {
                		wData.distance= newWdist;
                		wData.backPointer= f;  
                		wData.hostilecount=newhostilecount;
                		F.updatePriority(w, newWdist);
                	}
                }
           }
      } // if no path
        // no path from start to end
        
        return shortestPath1(start,end);
        //return new LinkedList<Node>();
    }
    /** helper function to solve any paths that shortestPath cannot solve */
    public static List<Node> shortestPath1(Node start, Node end) {
        /* TODO Read note A7 FAQs on the course piazza for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in lecture slides

        // map contains an entry for each node in S or F. Thus,
        // |map| = |S| + |F|.
        // For each such key-node, the value part contains the shortest known
        // distance to the node and the node's backpointer on that shortest path.
        HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();

        F.add(start, 0);
        map.put(start, new SFdata(0, null,0));
        int hostilecount = 0;
        // invariant: as in lecture slides, together with def of F and map
        while (F.size() != 0) {
            Node f= F.poll();
            
            
            if (f == end) {
            	return constructPath(end, map);
            }
           
            int fDist= map.get(f).distance;           
            for (Edge e : f.getExits()) {// for each neighbor w of f
                Node w= e.getOther(f);
                int newWdist= fDist + e.length;
                if (w.isHostile()){
                	if (hostilecount>1){
                		newWdist=newWdist+1000000;
                	}else{
                		newWdist=newWdist+1000;
                	}             	
                }
                if (w.hasSpeedUpgrade()){
                	newWdist=newWdist- 100;
                }         
                SFdata wData= map.get(w);                        
                if (wData == null) { //if w not in S or F
                    map.put(w, new SFdata(newWdist, f,0));
                    F.add(w, newWdist);
                } else if (newWdist < wData.distance) {
                    wData.distance= newWdist;
                    wData.backPointer= f;
                    F.updatePriority(w, newWdist);
                }
            } }
        
        return new LinkedList<Node>();
        }
    


    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
        LinkedList<Node> path= new LinkedList<Node>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        //            path, in reverse order.
        while (p != null) {
            path.addFirst(p);
            p= nData.get(p).backPointer;
        }
        return path;
    }

    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getConnect(q).length;
                p= q;
            }
            return s;
        }}

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private int distance; // distance from start node to this one
        private int hostilecount; // number of hostile nodes from start node to this one

        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(int d, Node p, int h) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
            hostilecount = h;    // Hostile count on this path        
           
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer;
        }
    }}