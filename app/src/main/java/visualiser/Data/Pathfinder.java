package visualiser.Data;

import java.util.ArrayList;

public class Pathfinder {
    
    public static class PNode {
        BaseNode node;
        int dist;
        PNode lastNode;
        BaseConnection conn;
        public PNode(BaseNode node, BaseConnection conn, int dist, PNode lastNode){
            this.node = node;
            this.dist = dist;
            this.lastNode = lastNode;
            this.conn = conn;
        }
        @Override
        public boolean equals(Object o){
            if(o==null) return false;
            if(o instanceof PNode){
                PNode n = (PNode) o;
                return (n.node.equals(node)&&n.dist==dist);
            }else if(o instanceof BaseNode){
                return node.equals(((BaseNode)o));
            }
            return false;

        }
    }

    @SuppressWarnings("unlikely-arg-type")
    public static int iofgood(ArrayList<PNode> nodes, BaseNode n2){
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).equals(n2)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings({"rawtypes"})
    private static Path calcPathint(Organiser org, BaseNode start, BaseNode end, boolean goodatt){
        ArrayList<PNode> openNodes = new ArrayList<>();
        openNodes.add(new PNode(end, null, 0, null));
        ArrayList<PNode> closedNodes = new ArrayList<>();
        while (openNodes.size()>0&&(Pathfinder.iofgood(closedNodes, start)==-1)) {
            PNode pn = openNodes.get(0);
            BaseNode n = pn.node;
            BaseConnection[] conns = n.connections;
            for (BaseConnection c : conns) {
                BaseNode n2;
                if(n.equals(c.n1)){
                    n2 = c.n2;
                }else{
                    n2 = c.n1;
                }
                int index = Pathfinder.iofgood(closedNodes, n2);
                if(index>=0){
                    PNode pn2 = closedNodes.get(index);
                    if(pn2.dist>pn.dist+1){
                        pn2.lastNode = pn;
                        pn2.conn = c;
                        pn2.dist = pn.dist+1;
                        closedNodes.set(index, pn2);
                    }
                }else{
                    int index2 = Pathfinder.iofgood(openNodes, n2);
                    if(index2>=0)continue;
                    PNode pn2 = new PNode(n2, c, pn.dist+1, pn);
                    openNodes.add(pn2);
                }
            }
            openNodes.remove(pn);
            closedNodes.add(pn);
        }
        if(closedNodes.size()==1){
            if(goodatt){
                return calcPathint(org, end, start, false);
            }else{
                return null;
            }
        }
        if(Pathfinder.iofgood(closedNodes, start)>=0){
            ArrayList<PNode> p = new ArrayList<>();
            p.add(closedNodes.get(iofgood(closedNodes,start)));
            PNode pn = p.get(0);
            while (pn.lastNode!=null) {
                p.add(pn.lastNode);
                pn = pn.lastNode;
            }
            BaseNode[] nodes = new BaseNode[p.size()];
            BaseConnection[] conns = new BaseConnection[p.size()-1];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i]=p.get(goodatt?p.size()-1-i:i).node;
            }
            for (int i = 0; i < conns.length; i++) {
                conns[i]=p.get(goodatt?p.size()-2-i:i).conn;
            }
            if(nodes.length==0||conns.length==0)
                return null;
            return new Path(nodes, conns);
        }else{
            return new Path(new BaseNode[0], new BaseConnection[0]);
        }
    }

    @SuppressWarnings({"rawtypes"})
    public static Path calcPath(Organiser org, BaseNode start, BaseNode end){
        return calcPathint(org, start, end, true);
    }
}
