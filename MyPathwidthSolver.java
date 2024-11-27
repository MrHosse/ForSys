import edu.kit.iti.formal.pathwidth.*;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.util.Arrays;

public class MyPathwidthSolver extends PathwidthSolver {
    public MyPathwidthSolver(Graph g){
        super(g);
    }

    @Override
    public Solution solve() {

        
        try{
            solver.addClause(new VecInt(new int[]{-1,2}));
            solver.addClause(new VecInt(new int[]{1,-3,-2}));
            Arrays.stream(solver.findModel()).forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("LOL");
        }
        // TODO: Implement solution
        solution.setState(SolutionState.UNKNOWN);
        return solution;
    }
}
