package visualiser.GameTester;

import java.util.ArrayList;

import visualiser.Renderer.Renderer;
import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.ShadowData;
import visualiser.Renderer.Physical.PhysicalOrganiser;


public class BoardSolver {

public static PhysicalOrganiser<Board> solveBoard(Board board){
        PhysicalOrganiser<Board> o = new PhysicalOrganiser<>();
        ArrayList<Board> openBoards = new ArrayList<>();
        
        System.out.println(board);
        openBoards.add(board);
        ArrayList<Board> doneBoards = new ArrayList<>();
        int lastSize = 0;
        while (openBoards.size()>0) {
            Board b = openBoards.get(0);
            for (Board b2 : b.getPossibleMoves()) {
                if(openBoards.contains(b2)||doneBoards.contains(b2)){
                    o.addConnection(b, b2, false);
                    continue;
                }
                openBoards.add(b2);
                o.addConnection(b, b2, false);
                o.add(b2);
            }
            openBoards.remove(0);
            o.add(b);
            doneBoards.add(b);
            if(lastSize/1000!=o.nodes.length/1000){
                lastSize = o.nodes.length;
                System.out.println(lastSize);
                System.out.println(openBoards.size());
                System.out.println(openBoards.get(0).turn());
            }
        }
        System.out.println(o.nodes.length);
        return o;
    }
    public static void startVisualisation(Board b){
        PhysicalOrganiser<Board> o = solveBoard(b);
        System.out.println(o.nodes.length);
        Renderer.start(o, new Function[0], 100, -2, new ShadowData(0.1,5,0.3));
    }
}
