package visualiser;
import java.util.Arrays;

import visualiser.Data.Organiser;

public class OrganiserTest {
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
        o.add(d1);
        o.add(d2);
        o.add(d3);
        o.addConnection(d2, d3);
        o.addConnection(d1, d3);
        System.out.println(Arrays.toString(o.connections));
    }
}
