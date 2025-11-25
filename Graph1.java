import java.util.*;

public class Graph1<T> {
    private static final int initial_cap = 10;
    private Vertex<T>[] nodes;
    private int size;
    private boolean directed;

    private static class Vertex<T> {
        T data;
        Edge<T>[] connections;
        int degree;


        Vertex(T value) {
            this.data = value;
            this.connections = new Edge[initial_cap];
            this.degree = 0;
        }

        void addConnection(Vertex<T> to, int weight) {
            if (degree == connections.length) {
                // Увеличиваем массив в 2 раза при заполнении
                connections = Arrays.copyOf(connections, connections.length * 2);
            }
            connections[degree++] = new Edge<>(to, weight);
        }

        //Удаление ребра со сдвигом элементов
        void removeEdge(Vertex<T> to) {
            for (int i = 0; i < degree; i++) {
                if (connections[i] != null && connections[i].to.equals(to)) {
                    for (int j = i; j < degree - 1; j++) {
                        connections[j] = connections[j + 1];
                    }
                    connections[degree - 1] = null;
                    degree--;
                    break;
                }
            }
        }

        boolean hasEdge(Vertex<T> to) {
            for (int i = 0; i < degree; i++) {
                if (connections[i] != null && connections[i].to.equals(to)) {
                    return true;
                }
            }
            return false;
        }

        Vertex<T>[] getAdjacentVertices() {

            // Создаем массив смежных вершин текущего размера
            Vertex<T>[] adjacent = new Vertex[degree];
            for (int i = 0; i < degree; i++) {
                adjacent[i] = connections[i].to;
            }
            return adjacent;
        }


        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Vertex<?> vertex = (Vertex<?>) obj;
            return Objects.equals(data, vertex.data);
        }


        public int hashCode() {
            return Objects.hash(data);
        }
    }

    private static class Edge<T> {
        Vertex<T> to;
        int weight;

        Edge(Vertex<T> to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }


    public Graph1() {
        this(false);
    }


    public Graph1(boolean isDirected) {
        this.nodes = new Vertex[initial_cap];
        this.size = 0;
        this.directed = isDirected;
    }

    public void addVertex(T v) {
        if (v == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }

        if (findVertex(v) != null) {
            return;
        }

        // Динамическое расширение массива вершин
        if (size == nodes.length) {
            nodes = Arrays.copyOf(nodes, nodes.length * 2);
        }
        nodes[size++] = new Vertex<>(v);
    }

    public void addEdge(T from, T to, int weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }

        Vertex<T> fromVertex = findVertex(from);
        Vertex<T> toVertex = findVertex(to);

        if (fromVertex == null || toVertex == null) {
            throw new IllegalArgumentException("Vertices do not exist");
        }

        fromVertex.addConnection(toVertex, weight);

        if (!directed) {
            toVertex.addConnection(fromVertex, weight);
        }
    }
    //Удаление вершины со всеми связанными рёбрами
    public void removeVertex(T v) {
        Vertex<T> vertexToRemove = findVertex(v);

        if (vertexToRemove == null) {
            return;
        }

        for (int i = 0; i < size; i++) {
            Vertex<T> vertex = nodes[i];

            if (vertex != null) {
                vertex.removeEdge(vertexToRemove);
            }
        }

        int indexToRemove = -1;
        for (int i = 0; i < size; i++) {
            Vertex<T> vertex = nodes[i];

            if (vertex != null && vertex.equals(vertexToRemove)) {
                indexToRemove = i;
                break;
            }
        }

        //Удаление вершины из массива со сдвигом
        if (indexToRemove != -1) {
            for (int i = indexToRemove; i < size - 1; i++) {
                nodes[i] = nodes[i + 1];
            }
            nodes[size - 1] = null;
            size--;
        }
    }

    public void removeEdge(T from, T to) {
        Vertex<T> fromVertex = findVertex(from);
        Vertex<T> toVertex = findVertex(to);

        if (fromVertex != null && toVertex != null) {
            fromVertex.removeEdge(toVertex);

            if (!directed) {
                toVertex.removeEdge(fromVertex);
            }
        }
    }

    //Рекурсивный обход в глубину (DFS)
    public List<T> getAdjacent(T v) {
        Vertex<T> vertex = findVertex(v);

        if (vertex == null) {
            return new ArrayList<>();
        }
        Vertex<T>[] adjacentVertices = vertex.getAdjacentVertices();
        List<T> result = new ArrayList<>();
        for (Vertex<T> adj : adjacentVertices) {
            result.add(adj.data);
        }
        return result;
    }

    public List<T> dfs(T start) {
        Vertex<T> startVertex = findVertex(start);

        if (startVertex == null) {
            throw new IllegalArgumentException("Start vertex does not exist");
        }
        boolean[] visitedNodes = new boolean[size];
        List<T> result = new ArrayList<>();
        dfsRecursive(startVertex, visitedNodes, result);
        return result;
    }

    private void dfsRecursive(Vertex<T> current, boolean[] visited, List<T> result) {
        int currentIndex = getVertexIndex(current);
        visited[currentIndex] = true;
        result.add(current.data);

        Vertex<T>[] adjacent = current.getAdjacentVertices();
        for (Vertex<T> neighbor : adjacent) {
            int neighborIndex = getVertexIndex(neighbor);

            if (!visited[neighborIndex]) {
                dfsRecursive(neighbor, visited, result);
            }
        }
    }

    //Обход в ширину (BFS) с использованием очереди на массиве
    public List<T> bfs(T start) {
        Vertex<T> startVertex = findVertex(start);

        if (startVertex == null) {
            throw new IllegalArgumentException("Start vertex does not exist");
        }

        boolean[] visited = new boolean[size];
        List<T> result = new ArrayList<>();

        Vertex<T>[] queue = new Vertex[size];
        int front = 0, rear = 0;
        int startIndex = getVertexIndex(startVertex);
        visited[startIndex] = true;
        queue[rear++] = startVertex;

        while (front < rear) {
            Vertex<T> current = queue[front++];
            result.add(current.data);
            Vertex<T>[] adjacent = current.getAdjacentVertices();
            for (Vertex<T> neighbor : adjacent) {
                int neighborIndex = getVertexIndex(neighbor);

                if (!visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    queue[rear++] = neighbor;
                }
            }
        }
        return result;
    }

    private Vertex<T> findVertex(T value) {
        for (int i = 0; i < size; i++) {
            Vertex<T> vertex = nodes[i];

            if (vertex != null && vertex.data.equals(value)) {
                return vertex;
            }
        }
        return null;
    }

    private int getVertexIndex(Vertex<T> vertex) {
        for (int i = 0; i < size; i++) {
            Vertex<T> v = nodes[i];

            if (v != null && v.equals(vertex)) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsVertex(T v) {
        return findVertex(v) != null;
    }

    public int getVertexCount() {
        return size;
    }

    public void printGraph() {
        System.out.println("\nТекущая структура графа:");
        System.out.println("Всего вершин: " + size);
        for (int i = 0; i < size; i++) {
            Vertex<T> vertex = nodes[i];
            System.out.print(vertex.data + " -> ");
            List<T> adjacent = getAdjacent(vertex.data);

            if (adjacent.isEmpty()) {
                System.out.print("нет смежных вершин");
            } else {
                for (T adj : adjacent) {
                    System.out.print(adj + " ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph1<String> graph = null;

        System.out.println("Программа для работы с графами");

        while (graph == null) {
            System.out.print("Выберите тип графа (1 - неориентированный, 2 - ориентированный): ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                graph = new Graph1<>(false);
                System.out.println("Создан неориентированный граф");
            } else if (choice == 2) {
                graph = new Graph1<>(true);
                System.out.println("Создан ориентированный граф");
            } else {
                System.out.println("Неверный выбор! Попробуйте снова.");
            }
        }

        boolean running = true;
        while (running) {
            System.out.println("\n МЕНЮ");
            System.out.println("1. Добавить вершину");
            System.out.println("2. Добавить ребро");
            System.out.println("3. Показать смежные вершины");
            System.out.println("4. Обход в глубину (DFS)");
            System.out.println("5. Обход в ширину (BFS)");
            System.out.println("6. Показать структуру графа");
            System.out.println("7. Выход");
            System.out.print("Выберите действие: ");

            int action = scanner.nextInt();

            switch (action) {
                case 1:
                    System.out.print("Введите имя вершины: ");
                    String vertex = scanner.next();
                    graph.addVertex(vertex);
                    System.out.println("Вершина '" + vertex + "' добавлена");
                    break;

                case 2:
                    System.out.print("Введите начальную вершину: ");
                    String from = scanner.next();
                    System.out.print("Введите конечную вершину: ");
                    String to = scanner.next();
                    System.out.print("Введите вес ребра: ");
                    int weight = scanner.nextInt();

                    if (!graph.containsVertex(from)) {
                        System.out.println("Ошибка: Вершина '" + from + "' не существует!");
                        System.out.println("Сначала добавьте вершины через пункт меню 1");
                    } else if (!graph.containsVertex(to)) {
                        System.out.println("Ошибка: Вершина '" + to + "' не существует!");
                        System.out.println("Сначала добавьте вершины через пункт меню 1");
                    } else {
                        try {
                            graph.addEdge(from, to, weight);
                            System.out.println("Ребро между '" + from + "' и '" + to + "' добавлено");
                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    }
                    break;

                case 3:
                    System.out.print("Введите вершину: ");
                    String checkVertex = scanner.next();
                    if (!graph.containsVertex(checkVertex)) {
                        System.out.println("Вершина '" + checkVertex + "' не существует!");
                    } else {
                        List<String> adjacent = graph.getAdjacent(checkVertex);
                        System.out.println("Смежные с '" + checkVertex + "': " + adjacent);
                    }
                    break;

                case 4:
                    System.out.print("Введите стартовую вершину для DFS: ");
                    String dfsStart = scanner.next();
                    if (!graph.containsVertex(dfsStart)) {
                        System.out.println("Вершина '" + dfsStart + "' не существует!");
                    } else {
                        try {
                            List<String> dfsResult = graph.dfs(dfsStart);
                            System.out.println("DFS результат: " + dfsResult);
                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    }
                    break;

                case 5:
                    System.out.print("Введите стартовую вершину для BFS: ");
                    String bfsStart = scanner.next();
                    if (!graph.containsVertex(bfsStart)) {
                        System.out.println("Вершина '" + bfsStart + "' не существует!");
                    } else {
                        try {
                            List<String> bfsResult = graph.bfs(bfsStart);
                            System.out.println("BFS результат: " + bfsResult);
                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    }
                    break;

                case 6:
                    if (graph.getVertexCount() == 0) {
                        System.out.println("Граф пустой! Добавьте вершины.");
                    } else {
                        graph.printGraph();
                    }
                    break;

                case 7:
                    running = false;
                    System.out.println("Выход из программы");
                    break;

                default:
                    System.out.println("Неверный выбор!");
            }
        }

        scanner.close();
    }
}