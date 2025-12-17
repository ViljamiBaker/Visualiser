package visualiser;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

import java.util.ArrayList;

import org.joml.Vector3f;

import visualiser.Renderer.Renderer;
import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.RenderData;
import visualiser.Renderer.Objects.ShadowData;
import visualiser.Data.BaseNode;
import visualiser.Data.Organiser;
import visualiser.Data.Pathfinder;

public class TiltyTiles {
    public static class Board implements RenderData{

        public int[][] board;

        public int turn = 1;

        public Board(){
            board = new int[5][5];
        }

        public Board(Board b){
            board = new int[5][5];
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    set(x, y, b.get(x, y));
                }
            }
            turn = b.turn + 1;
        }

        public boolean check(int x, int y){
            if(x<0||x>=5){
                return false;
            }
            if(y<0||y>=5){
                return false;
            }
            return true;
        }

        public int get(int x, int y){
            if(!check(x, y))
                return 0;
            
            return board[y][x];
        }
        public boolean isfilled(int x, int y){
            if(!check(x, y))
                return true;
            return get(x, y)!=0;
        }
        //  0 == empty
        //  1 == wall
        // -1 == hole
        //  2 == g token
        //  3 == b token
        public void set(int x, int y, int val){
            if(!check(x, y))
                return;
            board[y][x] = val;
        }

        public boolean loose = false;

        public boolean win(){
            if(loose)
                return false;
            
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board.length; y++) {
                    if(get(x, y) == 2){
                        return false;
                    }
                }
            }

            return true;
        }

        public Board[] getPossibleMoves(){
            if(loose||win())
                return new Board[0];
            
            Board[] boards = new Board[4];
            //up
            boards[0] = new Board(this);
            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board.length; y++) {
                    int tile = get(x, y);
                    if(tile == 2||tile == 3){
                        for (int y2 = y-1; y2 >=-1; y2--) {
                            if(boards[0].isfilled(x, y2)){
                                boards[0].set(x, y, 0);
                                if(boards[0].get(x, y2)!=-1){
                                    boards[0].set(x, y2+1, tile);
                                }else if(tile == 3)boards[0].loose = true;
                                break;
                            }
                        }
                    }
                }
            }
            //down
            boards[1] = new Board(this);
            for (int x = 0; x < board.length; x++) {
                for (int y = board.length-1; y >=0; y--) {
                    int tile = get(x, y);
                    if(tile == 2||tile == 3){
                        for (int y2 = y+1; y2 <= board.length; y2++) {
                            if(boards[1].isfilled(x, y2)){
                                boards[1].set(x, y, 0);
                                if(boards[1].get(x, y2)!=-1){
                                    boards[1].set(x, y2-1, tile);
                                }else if(tile == 3)boards[1].loose = true;
                                break;
                            }
                        }
                    }
                }
            }

            
            //left
            boards[2] = new Board(this);
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board.length; x++) {
                    int tile = get(x, y);
                    if(tile == 2||tile == 3){
                        for (int x2 = x-1; x2 >=-1; x2--) {
                            if(boards[2].isfilled(x2, y)){
                                boards[2].set(x, y, 0);
                                if(boards[2].get(x2, y)!=-1){
                                    boards[2].set(x2+1, y, tile);
                                }else if(tile == 3)boards[2].loose = true;
                                break;
                            }
                        }
                    }
                }
            }
            //right
            boards[3] = new Board(this);
            for (int y = 0; y < board.length; y++) {
                for (int x = board.length-1; x >=0; x--) {
                    int tile = get(x, y);
                    if(tile == 2||tile == 3){
                        for (int x2 = x+1; x2 <= board.length; x2++) {
                            if(boards[3].isfilled(x2, y)){
                                boards[3].set(x, y, 0);
                                if(boards[3].get(x2, y)!=-1){
                                    boards[3].set(x2-1, y, tile);
                                }else if(tile == 3)boards[3].loose = true;
                                break;
                            }
                        }
                    }
                }
            }

            return boards;
        }

        @Override
        public String toString(){
            String str = "\n|";
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    str += (get(x,y)) + "|";
                }
                if(y<5-1){
                    str += "\n|";
                }
            }
            str+="\nStatus: ";
            if(win()){
                str+="Wun";
            }else if(loose){
                str+="Loss";
            }else{
                str+="Play";
            }
            str+="\n";
            return str;
        }
        @Override
        public boolean equals(Object o){
            if(o==null||!(o instanceof Board)) return false;
            Board b = (Board) o;
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    if(get(x, y) != b.get(x, y)){
                        return false;
                    }
                }
            }
            return true;
        }

        public static Vector3f BEGIN = new Vector3f(1.0f,0.0f,1.0f);
        public static Vector3f NOWIN = new Vector3f(1.0f);
        public static Vector3f WIN = new Vector3f(0,1.0f,0);
        public static Vector3f FAIL = new Vector3f(1.0f,0.0f,0.0f);

        @Override
        public Vector3f color(){
            if(this.equals(ogBoard)) return BEGIN;
            
            return (loose?FAIL:win()?WIN:NOWIN);
        }
    }

    public static Board ogBoard;

    public static Organiser<Board> solveBoard(Board board){
        Organiser<Board> o = new Organiser<>();
        ArrayList<Board> openBoards = new ArrayList<>();
        
        System.out.println(board);
        openBoards.add(board);
        ArrayList<Board> doneBoards = new ArrayList<>();
        int lastSize = 0;
        while (openBoards.size()>0) {
            Board b = openBoards.get(0);
            //if(b.turn>board.turn+3){
            //    openBoards.remove(0);
            //    doneBoards.add(b);
            //    o.add(b);
            //    continue;
            //}
            for (Board b2 : b.getPossibleMoves()) {
                if(b2.equals(b)) continue;
                o.addConnection(b, b2, false);
                if(openBoards.contains(b2)||doneBoards.contains(b2)){
                    continue;
                }
                if(!b2.win()){
                    openBoards.add(b2);
                }else{
                    o.add(b2);
                    doneBoards.add(b2);
                }
            }
            openBoards.remove(0);
            o.add(b);
            doneBoards.add(b);
            if(lastSize/1000!=o.nodes.length/1000){
                lastSize = o.nodes.length;
                System.out.println(lastSize);
                System.out.println(openBoards.size());
                System.out.println(openBoards.get(0).turn);
                //openBoards.clear();
            }
        }
        System.out.println(o.nodes.length);
        return o;
    }

    public static boolean running = false;
    public static double lastT = 0;

    @SuppressWarnings("unchecked")
    public static void playHelper(int dir){
        Board b = (Board)Renderer.selectedNode1.d;
        Board[] mov = b.getPossibleMoves();
        if(mov.length<4) return;
        Renderer.selectedNode1 = Renderer.o.findNode(mov[dir]);
        System.out.println(Renderer.selectedNode1.d);
    }

    public static void main(String[] args) {
        ogBoard = new Board();
        ogBoard.board = new int[][] {
            { 0, 0, 0, 3, 2},
            { 0, 0, 0, 0, 3},
            { 0, 0,-1, 0, 0},
            { 0, 0, 0, 0, 0},
            { 0, 0, 0, 0, 0},
        };
        //System.out.println(ogBoard);
        //System.out.println(Arrays.toString(ogBoard.getPossibleMoves()));


        Organiser<Board> o = solveBoard(ogBoard);
        System.out.println(o.nodes.length);

        Renderer.start(o, new Function[]{()->{

            if(Renderer.isKeyPressed(GLFW_KEY_KP_8))  
                playHelper(0);
    
            if(Renderer.isKeyPressed(GLFW_KEY_KP_2))  
                playHelper(1);
    
            if(Renderer.isKeyPressed(GLFW_KEY_KP_4))  
                playHelper(2);
    
            if(Renderer.isKeyPressed(GLFW_KEY_KP_6))  
                playHelper(3);

            if(Renderer.isKeyPressed(GLFW_KEY_KP_6))  
                playHelper(3);

            if(Renderer.isKeyPressed(GLFW_KEY_KP_5)){
                Renderer.selectedNode1 = Renderer.o.findNode(ogBoard);

                for (BaseNode n : Renderer.o.nodes) {
                    Board b = (Board)n.d;
                    if(b.win()){
                        Renderer.selectedNode2 = n;
                        break;
                    }
                }

				if(Renderer.selectedNode1 == null || Renderer.selectedNode2 == null ){
					Renderer.path=null;
					Renderer.pathindex = -1;
					return;
				}
				Renderer.path = Pathfinder.calcPath(o, Renderer.selectedNode1, Renderer.selectedNode2);
				Renderer.pathindex=0;
                running = true;
            }
            
            if(Renderer.path==null) return;
            
            if(Renderer.isKeyPressed(GLFW_KEY_G))  
                running = !running;

            if(running&&glfwGetTime()>lastT+2){
                Renderer.pathindex++;
                Renderer.pathindex = Math.clamp(Renderer.pathindex, 0, Renderer.path.nodes().length-1);
                System.out.println(Renderer.path.nodes()[Renderer.path.nodes().length-1-Renderer.pathindex].d);
                lastT = glfwGetTime();
            }


        }}, 2, -1, new ShadowData(0.15,-1,0.3));
    }
}
