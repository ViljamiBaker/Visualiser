package visualiser.GameTester;

import java.util.ArrayList;

import visualiser.Renderer.Renderer;
import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.ShadowData;
import visualiser.Renderer.Physical.PhysicalOrganiser;
import visualiser.Renderer.Util.RendererStartData;


public class BoardSolver {

public static PhysicalOrganiser<Board> solveBoard(Board board){
    return BoardSolver.solveBoard(board, Integer.MAX_VALUE);
}
public static PhysicalOrganiser<Board> solveBoard(Board board, int maxTurn){
        PhysicalOrganiser<Board> o = new PhysicalOrganiser<>();
        ArrayList<Board> openBoards = new ArrayList<>();
        
        System.out.println(board);
        openBoards.add(board);
        ArrayList<Board> doneBoards = new ArrayList<>();
        int lastSize = 0;
        while (openBoards.size()>0) {
            Board b = openBoards.get(0);
            if(b.turn()>board.turn()+maxTurn){
                openBoards.remove(0);
                doneBoards.add(b);
                o.add(b);
                continue;
            }
            for (Board b2 : b.getPossibleMoves()) {
                if(openBoards.contains(b2)||doneBoards.contains(b2)){
                    o.addConnection(b, b2, b.isBothWay());
                    continue;
                }
                openBoards.add(b2);
                o.addConnection(b, b2, b.isBothWay());
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
    public static void startVisualisation(Board b, RendererStartData dat){
        BoardSolver.startVisualisation(b, dat, Integer.MAX_VALUE);
    }
    public static void startVisualisation(Board b, RendererStartData dat, int maxTurn){
        PhysicalOrganiser<Board> o = solveBoard(b,maxTurn);
        System.out.println(o.nodes.length);
        Renderer.start(o, dat);
    }
}
