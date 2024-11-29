import edu.kit.iti.formal.pathwidth.*;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.util.Iterator;
import java.util.stream.IntStream;

public class MyPathwidthSolver extends PathwidthSolver {
    public MyPathwidthSolver(Graph g){
        super(g);
    }

    @Override
    public Solution solve() {
        final int n = graph.getNumNodes();
        final int p = graph.getPathwidth();

        // defining C
        for (int node = 0; node < n; node++) {
            for (int i = 0; i < n; i++) {
                int cni = getLiteralC(n, node, i);
                int ani = getLiteralA(n, node, i);
                int bni = getLiteralB(n, node, i);
                try {
                    solver.addClause(new VecInt(new int[]{-cni, ani}));
                    solver.addClause(new VecInt(new int[]{-cni, bni}));
                    solver.addClause(new VecInt(new int[]{cni, -ani, -bni}));
                } catch (Exception e) {
                    System.err.println("Defining C: " + e.getMessage());
                }
            }
        }

        // at least 1 number in every interval
        IntStream.range(0, n)
            .mapToObj(node -> new VecInt(IntStream.range(0, n).map(i -> getLiteralC(n, node, i)).toArray()))
            .forEach(vec -> {
                try {
                    solver.addAtLeast(vec, 1);
                } catch (ContradictionException e) {
                    System.err.println("Adding C at least 1 clauses: " + e.getMessage());
                }
            });

        // adding interval clauses
        for (int node = 0; node < n; node++) {
            for (int i = 0; i < n; i++) {
                int ani = getLiteralA(n, node, i);
                int bni = getLiteralB(n, node, i);

                try {
                    solver.addClause(new VecInt(new int[]{ani, bni}));
                    if (i > 0) solver.addClause(new VecInt(new int[]{ani, -getLiteralA(n, node, i - 1)}));
                    if (i < n - 1) solver.addClause(new VecInt(new int[]{bni, -getLiteralB(n, node, i + 1)}));
                } catch (Exception e) {
                    System.err.println("Interval clause for " + node + " " + i + ": " + e.getMessage());
                }
            }
        }

        // adding pathwidth clauses
        IntStream.range(0, n)
            .mapToObj(i -> new VecInt(IntStream.range(0, n).map(node -> getLiteralC(n, node, i)).toArray()))
            .forEach(vec -> {
                try {
                    solver.addAtMost(vec, p + 1);
                } catch (ContradictionException e) {
                    System.err.println("Adding pathwidth clauses: " + e.getMessage());
                }
            });
        
        // adding edge clauses
        Iterator<GraphEdge> edgIterator = graph.getEdgeIterator();
        for (int edgeNum = 0; edgeNum < graph.getNumEdges(); edgeNum++){
            GraphEdge edge = edgIterator.next();
            int u = edge.getEndpoint1();
            int v = edge.getEndpoint2();
            
            for (int i = 0; i < n - 1; i++) {
                try {
                    solver.addClause(new VecInt(new int[]{getLiteralA(n, u, i), getLiteralB(n, v, i + 1)}));
                    solver.addClause(new VecInt(new int[]{getLiteralA(n, v, i), getLiteralB(n, u, i + 1)}));
                } catch (Exception e) {
                    System.err.println("Adding edge clauses for " + i + ": " + e.getMessage());
                }
            }
        }

        try {
            if (solver.isSatisfiable()) {
                int[] model = solver.findModel();
                for (int node = 0; node < n; node++) {
                    int start = 3 * n * node + 2 * n;
                    int end = start + n - 1;
                    int firstPositive = -1, lastPositive = -1;
                    for (int j = start; j <= end; j++) {
                        if (model[j] > 0) {
                            if (firstPositive == -1) firstPositive = j;
                            lastPositive = j;
                        }
                    }
                    solution.setInterval(node, firstPositive - start + 1, lastPositive - start + 1.5f);
                }

                solution.setState(SolutionState.SAT);
            } else {
                solution.setState(SolutionState.UNSAT);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return solution;
    }

    private int getLiteralA(int n, int node, int i){
        return (3 * n * node + i + 1);
    }

    private int getLiteralB(int n, int node, int i){
        return (3 * n * node + n + i + 1);
    }

    private int getLiteralC(int n, int node, int i){
        return (3 * n * node + 2 * n + i + 1);
    }

}
