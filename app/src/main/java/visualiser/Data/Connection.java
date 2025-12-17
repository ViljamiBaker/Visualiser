package visualiser.Data;

public class Connection<T> extends BaseConnection{
    public Node<T> node1;
    public Node<T> node2;

    public Connection(Node<T> node1,Node<T> node2, boolean bothway){
        super(node1, node2, bothway);
        this.node1 = node1;
        this.node2 = node2;
    }
}
