import java.util.*;

public class Main {

    static class Node {
        String state;
        Node parent;

        Node(String state, Node parent) {
            this.state = state;
            this.parent = parent;
        }
    }

    interface Frontier {
        void add(Node node);
        Node remove();
        boolean contains(String state);
        boolean isEmpty();

        void addChildren(List<Node> children);
    }

    static class StackFrontier implements Frontier {
        private final Deque<Node> stack = new ArrayDeque<>();

        public void add(Node node) {
            stack.push(node);
        }

        public Node remove() {
            return stack.pop();
        }

        public boolean contains(String state) {
            return stack.stream()
                    .anyMatch(node -> node.state.equals(state));
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public void addChildren(List<Node> children) {
            for (int i = children.size() - 1; i >= 0; i--) {
                add(children.get(i));
            }
        }
    }

    static class QueueFrontier implements Frontier {
        private final Queue<Node> queue = new ArrayDeque<>();

        public void add(Node node) {
            queue.add(node);
        }

        public Node remove() {
            return queue.remove();
        }

        public boolean contains(String state) {
            return queue.stream()
                    .anyMatch(node -> node.state.equals(state));
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void addChildren(List<Node> children) {
            for (Node child : children) {
                add(child);
            }
        }
    }

    static class SearchResult {
        List<String> path;
        List<String> explored;

        SearchResult(List<String> path, List<String> explored) {
            this.path = path;
            this.explored = explored;
        }
    }

    static SearchResult graphSearch(
            String start,
            String goal,
            Frontier frontier,
            Map<String, List<String>> graph) {

        Node startNode = new Node(start, null);
        frontier.add(startNode);

        Set<String> exploredSet = new HashSet<>();
        List<String> exploredOrder = new ArrayList<>();

        while (!frontier.isEmpty()) {
            Node node = frontier.remove();

            if (node.state.equals(goal)) {
                List<String> path = new ArrayList<>();

                Node current = node;
                while (current != null) {
                    path.add(current.state);
                    current = current.parent;
                }

                Collections.reverse(path);
                return new SearchResult(path, exploredOrder);
            }

            exploredSet.add(node.state);
            exploredOrder.add(node.state);

            List<String> neighbors = new ArrayList<>(
                    graph.getOrDefault(node.state, List.of())
            );
            Collections.sort(neighbors);

            List<Node> children = new ArrayList<>();

            for (String neighbor : neighbors) {
                if (!frontier.contains(neighbor)
                        && !exploredSet.contains(neighbor)) {
                    children.add(new Node(neighbor, node));
                }
            }

            frontier.addChildren(children);
        }

        return new SearchResult(List.of(), exploredOrder);
    }

    public static void main(String[] args) {
        Map<String, List<String>> graph = new HashMap<>();
        
        graph.put("Rumah", List.of("Kini Kuliner"));
        graph.put("Kini Kuliner", List.of("Rumah", "Hotel Colonial"));
        graph.put("Hotel Colonial", List.of("Kini Kuliner", "Akkarena"));
        graph.put("Akkarena", List.of("Hotel Colonial", "Trans Studio Mall"));
        graph.put("Trans Studio Mall", List.of("Akkarena", "Upperhills"));
        graph.put("Upperhills", List.of("Trans Studio Mall", "Bola Dunia CPI"));
        graph.put("Bola Dunia CPI", List.of("Upperhills", "Kampus"));
        graph.put("Kampus", List.of("Bola Dunia CPI"));

        SearchResult dfs = graphSearch(
                "Rumah", "Kampus", new StackFrontier(), graph);

        SearchResult bfs = graphSearch(
                "Rumah", "Kampus", new QueueFrontier(), graph);

        System.out.println("DFS path: " + dfs.path);
        System.out.println("DFS explored: " + dfs.explored);

        System.out.println("BFS path: " + bfs.path);
        System.out.println("BFS explored: " + bfs.explored);
    }
}
