import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        int n = 2;
        Test obj = new Test();
        System.out.println(obj.getEdgeClauses(n, 2, 0, 1));
    }

    private int getLiteralA(int n, int node, int i){
        return (3 * n * node + i + 1);
    }

    private int getLiteralB(int n, int node, int i){
        return (3 * n * node + n + i + 1);
    }

    private List<List<Integer>> getEdgeClauses(int n, int count, int u, int v) {
        List<List<Integer>> clauses = new ArrayList<>();
        if (count == 0) {
            List<Integer> singleClause1 = new ArrayList<>();
            singleClause1.add(getLiteralA(n, u, count));
            clauses.add(singleClause1);
    
            List<Integer> singleClause2 = new ArrayList<>();
            singleClause2.add(getLiteralB(n, v, count));
            clauses.add(singleClause2);
    
            return clauses;
        }
        for (List<Integer> list : getEdgeClauses(n, count - 1, u, v)) {
            List<Integer> list1 = new ArrayList<>(list);
            list1.add(getLiteralA(n, u, count));
            clauses.add(list1);
    
            List<Integer> list2 = new ArrayList<>(list);
            list2.add(getLiteralB(n, v, count));
            clauses.add(list2);
        }
        return clauses;
    }
    
}
