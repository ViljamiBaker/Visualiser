package visualiser.Renderer.Physical;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import org.joml.Vector3f;

import visualiser.Data.BaseConnection;
import visualiser.Data.BaseNode;
import visualiser.Data.Connection;
import visualiser.Data.Node;
import visualiser.Data.Organiser;

public class PhysicalOrganiser<T> extends Organiser<T>{
    public PhysicalOrganiser(){}

    double time = 0.0;

    @SuppressWarnings("unchecked")
    public PhysicalOrganiser(Organiser<T> cln){
        for (BaseNode baseNode : cln.nodes) {
            addInt(new PhysicalNode<T>(((Node<T>)baseNode).data));
        }
        for (BaseConnection baseConnection : cln.connections) {
            Connection<T> conn = ((Connection<T>)baseConnection);
            addConnInt(new Connection<>(findAndAddNode(conn.node1.data), findAndAddNode(conn.node2.data),conn.bothway));
        }
    }

    public PhysicalNode<T> getP(int index){
        return (PhysicalNode<T>)super.get(index);
    }

    public PhysicalNode<T> findP(T dat){
        return (PhysicalNode<T>)super.findAndAddNode(dat);
    }

    public int getNodeCount(){
        return nodes.length;//Math.min(Math.min(nodes.length,(int)(time*100.0f)),417);
    }

    @Override
    public Node<T> add(T dat){
        PhysicalNode<T> n = (PhysicalNode<T>)findNode(dat);
        if(n!=null) return n;
        n = new PhysicalNode<T>(dat);
        //resetNodePosition(n,10,0.1f);
        addInt(n);
        return n;
    };

    public void resetNodePosition(T dat, int settleCount,float deltaTime){
        resetNodePosition((PhysicalNode<T>)findAndAddNode(dat), settleCount, deltaTime);
    }

    @SuppressWarnings("unchecked")
    public void resetNodePosition(PhysicalNode<T> n, int settleCount,float deltaTime){
        if(n==null||n.connections.length==0) return;
        Vector3f avgPos = new Vector3f();
        for (int i2 = 0; i2 < n.connections.length; i2++) {
            PhysicalNode<T> n2 = (PhysicalNode<T>)n.connectedNodes[i2];
            avgPos.add(n2.pos);
        }
        int index = 0;
        for (int i = 0; i<nodes.length; i++) {
            BaseNode n2 = nodes[i];
            T obj = (T)n2.d;
            if(n.d.equals(obj)){
                index = i;
            }
        }
        avgPos.mul(1.0f/(float)(n.connections.length));
        avgPos.add((float)Math.random(),(float)Math.random(),(float)Math.random());
        n.pos = avgPos;
        for (int i = 0; i < settleCount; i++) {
            updateNodeFull(deltaTime, n, -1, index);
        }
    }

    @SuppressWarnings("unchecked")
    public void resetNodePositions(){
        for (int i = 0; i < nodes.length; i++) {
            PhysicalNode<T> n = getP(i);
            n.pos = new Vector3f();
        }
        for (int i = 0; i < nodes.length; i++) {
            PhysicalNode<T> n = getP(i);
            Vector3f avgPos = new Vector3f();
            for (int i2 = 0; i2 < n.connections.length; i2++) {
                PhysicalNode<T> n2 = (PhysicalNode<T>)n.connectedNodes[i2];
                avgPos.add(n2.pos);
            }
            avgPos.mul(1.0f/(float)(i+1));
            avgPos.add((float)Math.random()*10,(float)Math.random()*10,(float)Math.random()*10);
            n.pos = avgPos;
        }
    }

    Vector3f temp = new Vector3f();

    private void updateNodeFull(float deltaTime, PhysicalNode<T> n, int i){
        updateNodeFull(deltaTime, n, i, i);
    }

    @SuppressWarnings("unchecked")
    private void updateNodeFull(float deltaTime, PhysicalNode<T> n, int i, int index){
        for (int i2 = 0; i2 < n.connections.length; i2++) {
            PhysicalNode<T> n2 = (PhysicalNode<T>)n.connectedNodes[i2];
            n.pos.sub(n2.pos,temp);
            Vector3f diff = temp;
            float l = diff.lengthSquared();
            //if(l==0)continue;
            float pow;
            pow = 0.1f/(l+0.05f)-Math.min((float)Math.pow(1.02f, l)*0.3f,1);//-0.22f/(float)Math.sqrt(l);
            diff.normalize(pow);
            n.vel.add(diff);
            diff.mul(-1);
            n2.vel.add(diff);
        }
        for (int i2 = i+1; i2 < nodes.length; i2++) {
            if(i2==index)continue;
            PhysicalNode<T> n2 = getP(i2);
            n.pos.sub(n2.pos,temp);
            Vector3f diff = temp;
            float l = diff.lengthSquared();
            float pow;
            pow = 0.1f/(l+0.075f);
            diff.normalize(pow);
            n.vel.add(diff);
            diff.mul(-1);
            n2.vel.add(diff);
        }
        Vector3f diff = n.pos;
        diff.normalize(-0.02f,temp);
        n.vel.add(temp);
        n.vel.mul(0.5f);
        n.pos.add(n.vel.mul(deltaTime,temp));
        //n.pos=new Vector3f(i,i,i);
    }

    public void update(float deltaTime,double time){
        this.time = time;

		for (int i = 0; i < nodes.length; i++) {
			PhysicalNode<T> n = getP(i);
            updateNodeFull(deltaTime, n, i);
		}
    }
    
    public boolean update(float deltaTime,double time, int startIndex, int count){
        this.time = time;

		for (int i = startIndex; i < Math.min(nodes.length, startIndex+count); i++) {
			PhysicalNode<T> n = getP(i);
            updateNodeFull(deltaTime, n, i);
		}
        return nodes.length<=startIndex+count;
    }
    
    
    public int update(float deltaTime,double time, int startIndex, int attemptCount, double maxTime){
        this.time = time;
        boolean end = false;
        boolean wrap = false;
        int index = startIndex;
        while (!end) {
            for (int i = index; i < Math.min(nodes.length, index+attemptCount); i++) {
                PhysicalNode<T> n = getP(i);
                updateNodeFull(deltaTime, n, i);
            }
            index = Math.min(nodes.length, index+attemptCount);
            if(glfwGetTime()-time>maxTime){
                end = true;
                break;
            }
            if(index == nodes.length){
                wrap = true;
            }
        }
        if(wrap){
            index = 0;
            end = false;
            while (!end) {
                for (int i = index; i < Math.min(nodes.length, index+attemptCount); i++) {
                    PhysicalNode<T> n = getP(i);
                    updateNodeFull(deltaTime, n, i);
                }
                index = Math.min(nodes.length, index+attemptCount);
                if(glfwGetTime()-time>maxTime){
                    end = true;
                    break;
                }
                if(index == nodes.length){
                    wrap = true;
                }
            }
        }
        return index;
    }
}
