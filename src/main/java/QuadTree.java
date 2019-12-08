public class QuadTree {

    /** A node in a Quadtree is a square in the plane; for this project,
    each square node will be called a tile interchangeably, and is defined
     by its upper left and lower right points. Unlike a Binary Tree, a node
     has four children; each child is a subdivided fourth of its paren
     **/

     /** Longitude == x-axis; latitude == y-axis.*/


    public static class Node implements Comparable {
        Node child1;
        Node child2;
        Node child3;
        Node child4;
        double upperleftx;
        double upperlefty;
        double lowerrightx;
        double lowerrighty;
        private String name;
        double LonDPP;
        double centralx;
        double centraly;
        /**Given a query box or image, the LonDPP of that box or image is the
         * (lower right longitude - upper left longitude / (width of the image/box in pixels).
         * For example, for the query box in the example in this section,
         * the LonDPP is (-122.2104604264636 + 122.30410170759153) / (1085) or 0.00008630532
         * units of longitude per pixel.*/

        public Node(double upperleftx, double upperlefty,
                    double lowerrightx, double lowerrighty, String name) {
            this.name = name;
            this.upperleftx = upperleftx;
            this.lowerrightx = lowerrightx;
            this.upperlefty = upperlefty;
            this.lowerrighty = lowerrighty;
            this.LonDPP = (lowerrightx - upperleftx) / 256.0;
            centralx = (lowerrightx + upperleftx) / 2;
            centraly = (upperlefty + lowerrighty) / 2;
        }


        public double originx() {
            return (upperleftx + lowerrightx) / 2;
        }

        public double originy() {
            return (upperlefty + lowerrighty) / 2;
        }

        public String name() {
            return name;
        }

        public void children() {
            child2 = new Node(centralx, upperlefty, lowerrightx, centraly, name + "2");
            child1 = new Node(upperleftx, upperlefty, centralx, centraly, name + "1");
            child3 = new Node(upperleftx, centraly, centralx, lowerrighty, name + "3");
            child4 = new Node(centralx, centraly, lowerrightx, lowerrighty, name + "4");
        }

        @Override
        public int compareTo(Object o) {
            Node other = (Node) o;
            if (this.upperlefty > other.upperlefty) {
                return -1;
            } else if (this.upperlefty == other.upperlefty) {
                if (this.upperleftx > other.upperleftx) {
                    return 1;
                } else if (this.upperleftx == other.upperleftx) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return 1;
            }
        }

        public static boolean intersect(Node query, Node node) {
            return query.upperleftx < node.lowerrightx
                    && query.lowerrightx > node.upperleftx
                    && query.upperlefty > node.lowerrighty
                    && query.lowerrighty < node.upperlefty;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    Node root;

    public QuadTree(double rootupperx, double rootuppery,
                    double rootlowerx, double rootlowery, String name) {
        root = new Node(rootupperx, rootuppery, rootlowerx, rootlowery, name);
    }


    public void construct(Node x) {
        if (x == null) {
            return;
        } else if (x.name.length() == 7) {
            return;
        } else {
            x.child2 = new Node(x.centralx, x.upperlefty, x.lowerrightx, x.centraly, x.name + "2");
            construct(x.child2);
            x.child1 = new Node(x.upperleftx, x.upperlefty, x.centralx, x.centraly, x.name + "1");
            construct(x.child1);
            x.child3 = new Node(x.upperleftx, x.centraly, x.centralx, x.lowerrighty, x.name + "3");
            construct(x.child3);
            x.child4 = new Node(x.centralx, x.centraly, x.lowerrightx, x.lowerrighty, x.name + "4");
            construct(x.child4);
        }
    }
    /**1 in the northwest, 2 in the northeast, 3 in the southwest, and 4 in the southeast.*/


    public int height(Node x) {
        if (x == null) {
            return 0;
        } else {
            return 1 + height(x.child1);
        }
    }
}

