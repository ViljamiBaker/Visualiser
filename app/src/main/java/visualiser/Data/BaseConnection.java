package visualiser.Data;

public class BaseConnection {
    public BaseNode n1;
    public BaseNode n2;
    public boolean bothway;

    public BaseConnection(BaseNode n1,BaseNode n2, boolean bothway){
        this.n1 = n1;
        this.n2 = n2;
        this.bothway = bothway;
    }

    @Override
    public String toString(){
        return n1.toString() + " <-> " + n2.toString();
    }
}
