// Program to fully test the Renderer
// Allows playing on a connect 4 board by pressing 1-7
// Board is printed into out after each turn

package visualiser;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;

import java.util.ArrayList;

import org.joml.Vector3f;

import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.RenderData;
import visualiser.Renderer.Physical.PhysicalOrganiser;
import visualiser.Data.Organiser;
import visualiser.Renderer.Renderer;

public class TestProgram {
    public static class Board implements RenderData{
        //[x][y] || red is 1, yellow is 2
        //{(0,0),(1,0),(2,0)}
        //{(0,1),(1,1),(2,1)}
        //{(0,2),(1,2),(2,2)}
        int[][] arr = new int[6][7];
        boolean redPlaying = true;
        int turn = 0;

        public static Vector3f NOWIN = new Vector3f(1.0f);
        public static Vector3f REDWIN = new Vector3f(1.0f,0,0);
        public static Vector3f YELWIN = new Vector3f(1.0f,1.0f,0);

        public Board(){}
        public Board(Board c){
            for (int i = 0; i < arr.length; i++) {
                for (int i2 = 0; i2 < arr[0].length; i2++) {
                    arr[i][i2] = c.arr[i][i2];
                }
            }
            redPlaying = c.redPlaying;
            turn = c.turn;
        }
        public boolean play(int col){
            redPlaying = !redPlaying;
            turn++;

            for (int i = arr.length-1; i>=0; i--) {
                if(arr[i][col]==0){
                    arr[i][col] = (redPlaying?2:1);
                    return true;
                }
            }
            return false;
        }
        public void set(int col,int row, int piece){
            set(col, row, piece, true);
        }
        public void set(int col,int row, int piece, boolean red){
            redPlaying = red;
            arr[row][col] = piece;
        }
        public int won(){
            //horizontal
            for (int i = 0; i < arr.length; i++) {
                for (int i2 = 0; i2 < arr[0].length-3; i2++) {
                    if(
                        arr[i][i2]==arr[i][i2]&&
                        arr[i][i2]==arr[i][i2+1]&&
                        arr[i][i2]==arr[i][i2+2]&&
                        arr[i][i2]==arr[i][i2+3]&&
                        arr[i][i2]!=0
                    ){
                        return arr[i][i2];
                    }
                }
            }
            //vertical
            for (int i = 0; i < arr.length-3; i++) {
                for (int i2 = 0; i2 < arr[0].length; i2++) {
                    if(
                        arr[i][i2]==arr[i][i2]&&
                        arr[i][i2]==arr[i+1][i2]&&
                        arr[i][i2]==arr[i+2][i2]&&
                        arr[i][i2]==arr[i+3][i2]&&
                        arr[i][i2]!=0
                    ){
                        return arr[i][i2];
                    }
                }
            }
            //diag ++
            for (int i = 0; i < arr.length-3; i++) {
                for (int i2 = 0; i2 < arr[0].length-3; i2++) {
                    if(
                        arr[i][i2]==arr[i][i2]&&
                        arr[i][i2]==arr[i+1][i2+1]&&
                        arr[i][i2]==arr[i+2][i2+2]&&
                        arr[i][i2]==arr[i+3][i2+3]&&
                        arr[i][i2]!=0
                    ){
                        return arr[i][i2];
                    }
                }
            }
            //diag +-
            for (int i = 0; i < arr.length-3; i++) {
                for (int i2 = 3; i2 < arr[0].length; i2++) {
                    if(
                        arr[i][i2]==arr[i][i2]&&
                        arr[i][i2]==arr[i+1][i2-1]&&
                        arr[i][i2]==arr[i+2][i2-2]&&
                        arr[i][i2]==arr[i+3][i2-3]&&
                        arr[i][i2]!=0
                    ){
                        return arr[i][i2];
                    }
                }
            }

            return 0;
        }

        @Override
        public Vector3f color(){
            return (won()==0?NOWIN:(won()==1?REDWIN:YELWIN));
        }

        @Override
        public String toString(){
            String str = "|";
            for (int i = 0; i < arr.length; i++) {
                for (int i2 = 0; i2 < arr[2].length; i2++) {
                    str += (arr[i][i2]==0?" ":(arr[i][i2]==1?"R":"Y")) + "|";
                }
                if(i<arr.length-1){
                    str += "\n|";
                }
            }
            str += "\nMove:" + turn;
            str += "\nWin:" + won();
            str += "\nRed:" + redPlaying;
            return str;
        }
        @Override
        public boolean equals(Object o){
            if(!(o instanceof Board)) return false;
            Board b = (Board)o;
            for (int i = 0; i < arr.length; i++) {
                for (int i2 = 0; i2 < arr[0].length; i2++) {
                    if(arr[i][i2] != b.arr[i][i2]) return false;
                }
            }
            return true;
        }
    }

    public static class Fun implements Function{
        public void run(){
            if(Renderer.isKeyPressed(GLFW_KEY_0))
                Renderer.o.resetNodePositions();


            if(Renderer.isKeyPressed(GLFW_KEY_1)){
                og.play(0);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
            if(Renderer.isKeyPressed(GLFW_KEY_2)){
                og.play(1);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
            if(Renderer.isKeyPressed(GLFW_KEY_3)){
                og.play(2);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
            if(Renderer.isKeyPressed(GLFW_KEY_4)){
                og.play(3);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
            if(Renderer.isKeyPressed(GLFW_KEY_5)){
                og.play(4);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
            if(Renderer.isKeyPressed(GLFW_KEY_6)){
                og.play(5);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
            if(Renderer.isKeyPressed(GLFW_KEY_7)){
                og.play(6);
                Renderer.o = new PhysicalOrganiser<Board>(solveBoard(og));
                Renderer.o.resetNodePositions();
            }
        }
    }

    public static Board og;

    public static Organiser<Board> solveBoard(Board board){
        Organiser<Board> o = new Organiser<>();
        ArrayList<Board> openBoards = new ArrayList<>();
        
        System.out.println(board);
        openBoards.add(board);
        ArrayList<Board> doneBoards = new ArrayList<>();
        int lastSize = 0;
        while (openBoards.size()>0) {
            Board b = openBoards.get(0);
            if(b.turn>board.turn+5){
                openBoards.remove(0);
                doneBoards.add(b);
                o.add(b);
                continue;
            }
            for (int i = 0; i < 7; i++) {
                Board b2 = new Board(b);
                boolean play = b2.play(i);
                if(!play)continue;
                if(openBoards.contains(b2)||doneBoards.contains(b2)){}
                o.addConnection(b, b2, false);
                if(b2.won()==0){
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
            }
        }
        System.out.println(o.nodes.length);
        return o;
    }

    public static void main(String[] args) {
        og = new Board();
        og.set(0, 5, 2);
        og.set(1, 5, 2);
        og.set(2, 5, 1);

        og.set(5, 5, 1);
        og.set(6, 5, 1);

        og.set(0, 4, 2);
        og.set(1, 4, 2);
        og.set(2, 4, 2);

        og.set(5, 4, 1);
        og.set(6, 4, 1);

        og.set(0, 3, 1);
        og.set(1, 3, 1);
        og.set(2, 3, 2);

        og.set(6, 3, 1);

        og.set(0, 2, 1);
        og.set(1, 2, 2);
        og.set(2, 2, 2);

        og.set(6, 2, 2);

        og.set(0, 1, 2);

        og.set(2, 1, 1);

        og.play(5);
        og.play(5);

        og.play(2);

        og.play(5);
        og.play(1);
        og.play(6);
        og.play(4);
        og.play(3);

        og.play(3);
        //og.play(4);
        //og.play(4);
        //og.play(3);
        //og.play(3);

        Renderer.start(solveBoard(og), new Function[] {new Fun()});
    }
}
