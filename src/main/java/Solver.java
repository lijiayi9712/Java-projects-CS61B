import java.util.LinkedList;
import java.util.PriorityQueue;


public class Solver {
    PriorityQueue<SearchNode> searchQueue;
    SearchNode initial;
    SearchNode goal;
    LinkedList<Long> soln;


    public class SearchNode implements Comparable<SearchNode> {
        Node node;
        double traveled;
        SearchNode previous;
        double priority;
        double distance;
        Node goal;


        public SearchNode(Node node, Node goal, double traveled, SearchNode previous) {
            this.node = node;
            this.traveled = traveled;
            this.node.gothrough = traveled;
            this.previous = previous;
            this.goal = goal;
            this.distance = estimatedDistanceToGoal();
            this.priority = traveled + this.distance;
        }


        public boolean equals(SearchNode s) {
            return (this.node.equals(s.node));
        }

        /** Provides an estimate of the number of moves to reach the goal.
         * Must be less than or equal to the correct distance. */
        double estimatedDistanceToGoal() {
            double temp = (this.node.lat - this.goal.lat)
                    * (this.node.lat - this.goal.lat) + (this.node.lon - this.goal.lon)
                    * (this.node.lon - this.goal.lon);
            return Math.sqrt(temp);
        }
        boolean isGoal() {
            return estimatedDistanceToGoal() == 0.0;
        }


        @Override
        public int compareTo(SearchNode s) {
            if (this.priority > s.priority) {
                return 1;
            } else if (this.priority < s.priority) {
                return -1;
            } else {
                return 0;
            }
        }

        /**Returns the path traveled so far*/
        public double traveled() {
            return traveled;
        }
    }

    public Solver(Node initial, Node destination) {
        searchQueue = new PriorityQueue<>(1);
        this.initial = new SearchNode(initial, destination, 0, null);
        searchQueue.add(this.initial);
        SearchNode min = searchQueue.poll();
        min.node.visited = true;
        while (!min.isGoal()) {
            Iterable<Node> neighbors = min.node.neighbors();
            for (Node neighbor : neighbors) {
                if (min.previous != null) {
                    if (!neighbor.visited) {
                        SearchNode temp = new SearchNode(neighbor, destination,
                                min.traveled
                                        + min.node.distanceto(neighbor.lon, neighbor.lat), min);
                        searchQueue.add(temp);
                        temp.node.visited = true;
                    } else {
                        double saved = neighbor.gothrough;
                        SearchNode temp = new SearchNode(neighbor, destination,
                                min.traveled
                                        + min.node.distanceto(neighbor.lon, neighbor.lat), min);
                        if (temp.traveled() < saved) {
                            SearchNode tempo = new SearchNode(neighbor, destination,
                                    min.traveled
                                            + min.node.distanceto(neighbor.lon, neighbor.lat), min);
                            searchQueue.add(tempo);
                        }
                    }
                } else {
                    SearchNode temp = new SearchNode(neighbor, destination,
                            min.traveled +  min.node.distanceto(neighbor.lon, neighbor.lat), min);
                    searchQueue.add(temp);
                    temp.node.visited = true;
                }
            }
            min = searchQueue.poll();
        }
        goal = min;
        Node saved = goal.node;
        soln = new LinkedList<>();
        while (goal.previous != null) {
            soln.addFirst(goal.previous.node.id);
            goal = goal.previous;
        }
        soln.addLast(saved.id);
    }



    public Iterable<Long> solution() {
        return soln;
    }
}
