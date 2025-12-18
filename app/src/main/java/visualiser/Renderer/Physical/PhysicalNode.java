package visualiser.Renderer.Physical;

import org.joml.Math;
import org.joml.Vector3f;

import visualiser.Data.Node;

public class PhysicalNode<T> extends Node<T>{
    public Vector3f pos = new Vector3f((float)Math.random()*100.0f,(float)Math.random()*100.0f,(float)Math.random()*100.0f);
    public Vector3f vel = new Vector3f((float)Math.random(),(float)Math.random(),(float)Math.random());
    public PhysicalNode(T data){
        super(data);
    }
}
