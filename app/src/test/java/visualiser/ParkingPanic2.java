package visualiser;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.util.ArrayList;

import visualiser.Renderer.Renderer;
import visualiser.Renderer.Objects.Function;
import visualiser.Renderer.Objects.ShadowData;
import visualiser.Data.Organiser;
import visualiser.ParkingPanic.Board;
import visualiser.ParkingPanic.Board.Car;
import visualiser.ParkingPanic.Board.Car.Dir;

public class ParkingPanic2 {

    public static int count = 1;
    
    @SuppressWarnings("unchecked")
    public static class Fun implements Function{
        public void run(){
            if(Renderer.isKeyPressed(GLFW_KEY_UP))
                count++;
            if(Renderer.isKeyPressed(GLFW_KEY_DOWN))
                count--;
            count = Math.clamp(count, 1, 20);
            for (int i2 = 0; i2 < count; i2++) {
                if(openBoards.size()==0)return;
                Board b = openBoards.get(0);
                for (int i = 0; i<b.cars.size(); i++) {
                    Car c = b.cars.get(i);
                    for (int[] is : c.getPossibleMoves()) {
                        Board b2 = new Board(b);
                        Car c2 = b2.cars.get(i);
                        c2.xpos = is[0];
                        c2.ypos = is[1];
                        Renderer.o.addConnection(b, b2, true);
                        Renderer.o.resetNodePosition(b2,10,0.1f);
                        if(openBoards.contains(b2)||doneBoards.contains(b2)){
                            continue;
                        }
                        if(!b2.win()){
                            openBoards.add(b2);
                        }else{
                            //Renderer.o.add(b2);
                            doneBoards.add(b2);
                        }
                    }
                }
                openBoards.remove(0);
                //Renderer.o.add(b);
                Renderer.o.resetNodePosition(b,10,0.1f);
                doneBoards.add(b);
            }
        }
    }

    public static Board ogBoard;
    public static Organiser<Board> o;

    public static ArrayList<Board> openBoards = new ArrayList<>();
    public static ArrayList<Board> doneBoards = new ArrayList<>();

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

        ogBoard.winningCar = c;

        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",0,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",1,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",2,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",3,0));
        //ogBoard.cars.add(ogBoard.new Car(Dir.VERT,2,"1",4,0));
        ParkingPanic.ogBoard = ogBoard;
        o = new Organiser<>();
        openBoards.add(ogBoard);
        System.out.println(o.nodes.length);
        Renderer.start(o, new Function[]{new Fun()}, 100, -2, new ShadowData());
    }
}
