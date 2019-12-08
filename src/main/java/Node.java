import java.util.LinkedList;

public class Node  {
    double lat;
    double lon;
    long id;
    LinkedList<Node> connected;
    String name;
    boolean visited;
    double gothrough;


    public Node(double lat, double lon, long id) {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
        this.connected = new LinkedList();
        visited = false;
    }






    double distanceto(double locallon, double locallat) {
        return Math.sqrt(Math.pow(this.lat - locallat, 2) + Math.pow(this.lon - locallon, 2));
    }


    /** Provides an iterable of all the neighbors of this Node. */
    Iterable<Node> neighbors() {
        return connected;
    }


    @Override
    public boolean equals(Object n) {
        Node node = (Node) n;
        return (node.id == this.id);
    }

    @Override
    public int hashCode() {
        return (int) this.id;
    }
}
