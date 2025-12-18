package visualiser.Data;

import java.util.ArrayList;

public class Organiser<T> {
    public BaseNode[] nodes = new BaseNode[0];
    public BaseConnection[] connections = new BaseConnection[0];
    public Organiser(){}

    @SuppressWarnings("unchecked")
    public Node<T> findNode(T dat){
        for (BaseNode n : nodes) {
            T obj = (T)n.d;
            if(dat.equals(obj)){
                return (Node<T>)n;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Node<T> get(int i){
        Node<T> d = (Node<T>)nodes[i];
        return d;
    }

    // finds AND adds node if not found
    public Node<T> findAndAddNode(T dat){
        Node<T> n = findNode(dat);
        if(n!=null){
            return n;
        }
        //throw new RuntimeException("No node of obj" + dat.toString() + " Exists");
        n = add(dat);
        return n;
    };

    public BaseConnection[] getConnections(){
        return connections;
    }

    public BaseConnection[] getConnections(BaseNode n){
        ArrayList<BaseConnection> conns = new ArrayList<>();
        for (BaseConnection c : connections) {
            if(c.n1.equals(n)||(c.bothway&&c.n2.equals(n))){
                conns.add(c);
            }
        }
        return conns.toArray(new BaseConnection[conns.size()]);
    }

    /*@SuppressWarnings("unchecked")
    public GenericArray<Node<T>> findAllConnectionsNode(Node<T> node){
        BaseNode[] allC = new BaseNode[connections.length];
        int index = 0;
        for (BaseConnection baseConnection : connections) {
            if(baseConnection.n1.d.equals(node.data)){
                allC[index] = baseConnection.n2;
                index++;
            }
            if(baseConnection.n2.d.equals(node.data)){
                allC[index] = baseConnection.n1;
                index++;
            }
        }
        GenericArray<Node<T>> arr = new GenericArray<>(index);
        for (int i = 0; i < index; i++) {
            arr.set(i, (Node<T>)allC[i]);
        }
        return arr;
    }*/

    public BaseNode[] findAllConnections(T dat){
        Node<T> n = findAndAddNode(dat);
        if(n == null) return new BaseNode[0];
        //return findAllConnectionsNode(n);
        return n.connectedNodes;
    }

    public void addInt(BaseNode d){
        BaseNode[] newNodes = new BaseNode[nodes.length+1];
        for (int i = 0; i < nodes.length; i++) {
            newNodes[i] = nodes[i];
        }
        newNodes[nodes.length] = d;
        nodes = newNodes;
    }

    public void addConnInt(Connection<T> c){
        for (BaseConnection ogc : connections) {
            if(ogc.bothway){
                if(
                    (ogc.n1.equals(c.n1)&&ogc.n2.equals(c.n2))||
                    (ogc.n1.equals(c.n2)&&ogc.n2.equals(c.n1))
                ){
                    return;
                }
            }else{
                if(ogc.n1.equals(c.n1)&&ogc.n2.equals(c.n2)){
                    return;
                }else if(ogc.n1.equals(c.n2)&&ogc.n2.equals(c.n1)){
                    ogc.bothway = true;
                    return;
                }
            }
        }
        BaseConnection[] newConnections = new BaseConnection[connections.length+1];
        for (int i = 0; i < connections.length; i++) {
            newConnections[i] = connections[i];
        }
        newConnections[connections.length] = c;
        connections = newConnections;
        c.n1.addConnection(c.n2);
        if(c.bothway){
            c.n2.addConnection(c.n1);
        }
        c.n1.addConnection(c);
        if(c.bothway){
            c.n2.addConnection(c);
        }
    }

    public Node<T> add(T dat){
        Node<T> d = findNode(dat);
        if(d!=null) return d;
        d = new Node<T>(dat);
        addInt(d);
        return d;
    };

    public void addConnection(T dat, T dat2, boolean bothway){
        Connection<T> c = new Connection<T>(findAndAddNode(dat), findAndAddNode(dat2), bothway);
        addConnInt(c);
    };
    public void addConnection(T dat, T dat2){
        addConnection(dat, dat2, true);
    };
}
