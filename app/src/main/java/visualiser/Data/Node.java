package visualiser.Data;

public class Node <T> extends BaseNode{
    public T data;
    public Node(T data){
        super(data);
        this.data = data;
    }
}
