package visualiser;

import visualiser.Data.Organiser;
import visualiser.Renderer.Renderer;

public class RendererTest {


    public static class Data {
        public int x;
        public int y;
        public Data(int x,int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString(){
            return "x: " + x + ", y: " + y;
        }
        @Override
        public boolean equals(Object o){
            if(!(o instanceof Data)) return false;
            Data d = (Data)o;
            return d.x == this.x && d.y == this.y;
        }
    }

    public static void main(String[] args) {
        Organiser<Data> o = new Organiser<Data>();
        Data d1 = new Data(1, 0);
        Data d2 = new Data(1, 1);
        Data d3 = new Data(-1, 3);
        Data d4 = new Data(4,6);
        Data d5 = new Data(2, 0);
        Data d6 = new Data(3, 1);
        Data d7 = new Data(-4, 3);
        Data d8 = new Data(5,6);
        o.add(d1);
        o.add(d2);
        o.add(d3);
        o.add(d4);
        o.add(d5);
        o.add(d6);
        o.add(d7);
        o.add(d8);
        o.addConnection(d1, d2);
        o.addConnection(d2, d3);
        o.addConnection(d3, d4);
        o.addConnection(d1, d4);
        o.addConnection(d5, d6);
        o.addConnection(d6, d7);
        o.addConnection(d7, d8);
        o.addConnection(d5, d8);
        Renderer.start(o);
    }
}
