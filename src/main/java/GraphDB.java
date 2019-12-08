import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */

public class GraphDB {

    final Map<Long, Node> vertexes = new HashMap<>();

    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        LinkedList<Long> removed = new LinkedList<>();
        Iterator keys = vertexes.keySet().iterator();
        while (keys.hasNext()) {
            Node curr = vertexes.get(keys.next());
            if (curr.connected.size() == 0) {
                removed.add(curr.id);
            }
        }
        for (Long remove : removed) {
            vertexes.remove(remove);
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        return vertexes.keySet();
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        HashSet<Long> adj = new HashSet<>();
        Iterator adja = vertexes.get(v).connected.iterator();
        while (adja.hasNext()) {
            Node curr = (Node) adja.next();
            adj.add(curr.id);
        }
        return adj;
    }


    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        Node n1 = vertexes.get(v);
        Node n2 = vertexes.get(w);
        double distance = Math.pow(n1.lat - n2.lat, 2) + Math.pow(n1.lon - n2.lon, 2);
        return Math.sqrt(distance);
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        double min;
        Iterator iter = vertexes.values().iterator();
        Node curr = (Node) iter.next();
        Node res = curr;
        min = curr.distanceto(lon, lat);
        while (iter.hasNext()) {
            curr = (Node) iter.next();
            if (curr.distanceto(lon, lat) < min) {
                min = curr.distanceto(lon, lat);
                res = curr;
            }
        }
        return res.id;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        Node curr = vertexes.get(v);
        return curr.lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        Node curr = vertexes.get(v);
        return curr.lat;
    }

    void addNode(Node curr) {
        vertexes.put(curr.id, curr);
    }

    void addEdge(Node n1, Node n2) {
        n1.connected.add(n2);
        n2.connected.add(n1);
    }


}
