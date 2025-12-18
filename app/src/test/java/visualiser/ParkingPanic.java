package visualiser;

import java.util.ArrayList;

import org.joml.Vector3f;

import visualiser.Renderer.Renderer;
import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.RenderData;
import visualiser.Renderer.Objects.ShadowData;
import visualiser.Renderer.Util.RendererUpdateType;
import visualiser.Data.Organiser;
import visualiser.ParkingPanic.Board.Car;
import visualiser.ParkingPanic.Board.Car.Dir;

public class ParkingPanic {
    public static class Board implements RenderData{
        public class Car{
            public static enum Dir{
                VERT, // +y
                HORI  // +x
            }
            public Dir dir;
            public int length;
            public String color;
            public int xpos;
            public int ypos;

            public Car(Dir dir, int length, String color, int xpos, int ypos){
                this.dir = dir;
                this.length = length;
                this.color = color;
                this.xpos = xpos;
                this.ypos = ypos;
            }

            public Car(Car c){
                this.dir = c.dir;
                this.length = c.length;
                this.color = c.color;
                this.xpos = c.xpos;
                this.ypos = c.ypos;
            }

            public boolean isCoordWithin(int x, int y){
                if(dir == Dir.VERT){
                    return this.xpos == x && (y>=this.ypos&&y<this.ypos+length);
                }else{
                    return this.ypos == y && (x>=this.xpos&&x<this.xpos+length);
                }
            }
            public int[][] getPossibleMoves(){
                ArrayList<Integer[]> moves = new ArrayList<>();
                if(dir == Dir.VERT){
                    boolean colliding = false;
                    for (int i = 0; i < length; i++) {
                        if(isfilled(xpos, ypos+1+i, this)){
                            colliding = true;
                            break;
                        }
                    }
                    if(!colliding){
                        moves.add(new Integer[] {xpos,ypos+1});
                    }
                    colliding = false;
                    for (int i = 0; i < length; i++) {
                        if(isfilled(xpos, ypos-1+i, this)){
                            colliding = true;
                            break;
                        }
                    }
                    if(!colliding){
                        moves.add(new Integer[] {xpos,ypos-1});
                    }
                }else{
                    boolean colliding = false;
                    for (int i = 0; i < length; i++) {
                        if(isfilled(xpos+1+i, ypos, this)){
                            colliding = true;
                            break;
                        }
                    }
                    if(!colliding){
                        moves.add(new Integer[] {xpos+1,ypos});
                    }
                    colliding = false;
                    for (int i = 0; i < length; i++) {
                        if(isfilled(xpos-1+i, ypos, this)){
                            colliding = true;
                            break;
                        }
                    }
                    if(!colliding){
                        moves.add(new Integer[] {xpos-1,ypos});
                    }
                }
                int[][] m = new int[moves.size()][2];
                for (int i = 0; i < moves.size(); i++) {
                    m[i][0] = moves.get(i)[0];
                    m[i][1] = moves.get(i)[1];
                }
                return m;
            }
            @Override
            public boolean equals(Object o){
                if(o==null||!(o instanceof Car)) return false;
                Car c = (Car) o;
                return xpos == c.xpos&&ypos == c.ypos&&dir == c.dir&&length == c.length;
            }
        }

        public int xsize;
        public int ysize;

        public int[][] board;

        ArrayList<Car> cars = new ArrayList<>();

        Car winningCar = null;

        public int winx;
        public int winy;
        public int turn = 1;

        public Board(int xsize,int ysize, int winx, int winy){
            board = new int[ysize][xsize];
            this.xsize = xsize;
            this.ysize = ysize;
            this.winx = winx;
            this.winy = winy;
        }

        public Board(Board b){
            board = new int[b.ysize][b.xsize];
            this.xsize = b.xsize;
            this.ysize = b.ysize;
            this.winx = b.winx;
            this.winy = b.winy;
            for (Car c : b.cars) {
                Car c2 = new Car(c);
                cars.add(c2);
                if(c.equals(b.winningCar)){
                    winningCar = c2;
                }
            }
            this.turn = b.turn+1;
        }

        public boolean check(int x, int y){
            if(x<0||x>=xsize){
                return false;
            }
            if(y<0||y>=ysize){
                return false;
            }
            return true;
        }

        public String get(int x, int y){
            if(!check(x, y))
                return " ";
            for (Car c : cars) {
                if(c.isCoordWithin(x, y)){
                    if(c.equals(winningCar)){
                        return "W";
                    }
                    return c.color;
                }
            }
            return " ";
        }
        public boolean isfilled(int x, int y, Car car){
            if(!check(x, y))
                return true;
            for (Car c : cars) {
                if(c.equals(car)) continue;
                if(c.isCoordWithin(x, y)){
                    return true;
                }
            }
            return false;
        }
        public void set(int x, int y, int val){
            if(!check(x, y))
                return;
            board[y][x] = val;
        }

        public boolean win(){
            if(winningCar == null)
                return false;
            if(winningCar.dir==Dir.VERT){
                return winningCar.isCoordWithin(winx, winy)||winningCar.isCoordWithin(winx-winningCar.length+1, winy);
            }else{
                return winningCar.isCoordWithin(winx, winy)||winningCar.isCoordWithin(winx, winy-winningCar.length+1);
            }
        }

        @Override
        public String toString(){
            String str = "|";
            for (int y = 0; y < ysize; y++) {
                for (int x = 0; x < xsize; x++) {
                    str += (x==winx&&y==winy?"#":get(x,y)) + "|";
                }
                if(y<ysize-1){
                    str += "\n|";
                }
            }
            str+="\nWin: " + win();
            return str;
        }
        @Override
        public boolean equals(Object o){
            if(o==null||!(o instanceof Board)) return false;
            Board b = (Board) o;
            if(
                b.cars.size()!=cars.size()&&
                b.xsize!=xsize&&
                b.ysize!=ysize&&
                b.winx!=winx&&
                b.winy!=winy
            ) return false;
            for (int i = 0; i < cars.size(); i++) {
                if(!b.cars.get(i).equals(cars.get(i)))return false;
            }
            return true;
        }

        public static Vector3f BEGIN = new Vector3f(1.0f,0.0f,1.0f);
        public static Vector3f NOWIN = new Vector3f(1.0f);
        public static Vector3f WIN = new Vector3f(0,1.0f,0);

        @Override
        public Vector3f color(){
            if(this.equals(ogBoard)) return BEGIN;
            return (win()?WIN:NOWIN);
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
            for (int i = 0; i<b.cars.size(); i++) {
                Car c = b.cars.get(i);
                for (int[] is : c.getPossibleMoves()) {
                    Board b2 = new Board(b);
                    Car c2 = b2.cars.get(i);
                    c2.xpos = is[0];
                    c2.ypos = is[1];
                    if(openBoards.contains(b2)||doneBoards.contains(b2)){
                        o.addConnection(b, b2, true);
                        continue;
                    }
                    o.addConnection(b, b2, true);
                    if(!b2.win()){
                        openBoards.add(b2);
                    }else{
                        o.add(b2);
                        doneBoards.add(b2);
                    }
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

    public static void main(String[] args) {
        ogBoard = new Board(6,6, 0,3);

        //Car c = ogBoard.new Car(Dir.HORI, 2,"1",3,3);
        //ogBoard.cars.add(c);
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"2",0,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"3",2,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"4",2,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"5",4,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"6",3,2));
        
        //Car c = ogBoard.new Car(Dir.HORI, 2,"W",2,3);
        //ogBoard.cars.add(c);
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"1",0,2));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"2",1,2));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"3",0,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"4",1,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"5",3,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"6",2,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"7",3,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"8",4,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"9",5,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"A",2,4));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"B",4,4));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"C",1,5));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"D",3,5));

        //Car c = ogBoard.new Car(Dir.HORI, 2,"W",3,3);
        //ogBoard.cars.add(c);
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"1",0,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"2",0,2));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"3",0,4));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 3,"4",0,5));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 3,"5",2,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"6",2,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"7",4,1));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"8",5,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"9",2,2));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"0",3,4));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"A",5,3));

        Car c = ogBoard.new Car(Dir.HORI, 2,"W",3,3);
        ogBoard.cars.add(c);
        ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"1",0,0));
        ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"2",0,1));
        ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"3",0,2));
        ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"4",0,5));
        ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"5",2,0));
        ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"6",4,0));
        ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"C",1,2));
        ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 3,"8",2,2));
        ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"9",5,1));
        ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"0",3,4));
        ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"A",4,4));
        ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"B",4,5));

        //Car c = ogBoard.new Car(Dir.HORI, 2,"W",4,3);
        //ogBoard.cars.add(c);
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 2,"1",4,2));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 3,"1",1,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"1",0,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 2,"1",0,4));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI, 3,"1",1,5));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT, 3,"1",3,2));

        ogBoard.winningCar = c;

        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",2,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"2",3,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"3",2,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.HORI,2,"4",4,3));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",4,0));

        Organiser<Board> o = solveBoard(ogBoard);
        System.out.println(o.nodes.length);
        Renderer.start(o, new Function[0], 100, RendererUpdateType.UpdateToTime, new ShadowData(0.1,5,0.3));
    }
}
