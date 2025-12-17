package visualiser.Data;

public class BaseNode {
    public Object d;

    public BaseNode[] connectedNodes = new BaseNode[0];
    public BaseConnection[] connections = new BaseConnection[0];

    public BaseNode(Object d){
        this.d = d;
    }

    public void addConnection(BaseNode other){
        BaseNode[] newNodeConnections = new BaseNode[connections.length+1];

        for (int i = 0; i < connections.length; i++) {
            newNodeConnections[i] = connectedNodes[i];
        }

        newNodeConnections[connections.length] = other;

        connectedNodes = newNodeConnections;
    }

    public void addConnection(BaseConnection other){
        BaseConnection[] newConnections = new BaseConnection[connections.length+1];

        for (int i = 0; i < connections.length; i++) {
            newConnections[i] = connections[i];
        }

        newConnections[connections.length] = other;

        connections = newConnections;
    }

    @Override
    public String toString(){
        return "[" + d.toString() + "]";
    }
    @Override
    public boolean equals(Object o){
        if(o==null||!(o instanceof BaseNode)) return false;
        BaseNode n = (BaseNode) o;
        return d.equals(n.d);
    }
}
