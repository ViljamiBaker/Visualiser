package visualiser.GameTester;

import visualiser.Renderer.Objects.RenderData;

public interface Board extends RenderData{
    public Board[] getPossibleMoves();
    public Board clone();
    public boolean end();
    public int turn();
    public String toString();
    public boolean equals(Object o);
}
