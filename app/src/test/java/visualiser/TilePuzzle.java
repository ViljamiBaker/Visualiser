package visualiser;

import org.joml.Vector3f;

import visualiser.GameTester.Board;
import visualiser.GameTester.BoardSolver;

public class TilePuzzle {
    public static class TileBoard implements Board{

        private static final int[][] WINARR = new int[][]{{1,2,3},{4,0,5},{6,7,8}};

        public int turn;

        public int[][] tiles;

        public String tilestring;

        public TileBoard(String tilestring){
            this.tilestring = tilestring;
            tiles = new int[][]{
                {1,2,3},
                {4,0,5},
                {6,7,8}
            };
        }

        public TileBoard clone(){
            TileBoard b = new TileBoard(tilestring);
            for (int y = 0; y < tiles.length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    b.setTile(x, y, getTile(x, y));
                }
            }
            b.tilestring = this.tilestring;
            b.turn = this.turn + 1;
            return b;
        }

        public Board[] getPossibleMoves(){
            int zerox = -1;
            int zeroy = -1;
            for (int y = 0; y < tiles.length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    if(getTile(x, y) == 0){
                        zerox = x;
                        zeroy = y;
                    }
                }
            }

            TileBoard[] boards = new TileBoard[4];
            int movecount = 0;
            if(checkTile(zerox+1, zeroy)){
                boards[0] = this.clone();
                boards[0].setTile(zerox, zeroy, this.getTile(zerox+1, zeroy));
                boards[0].setTile(zerox+1, zeroy, 0);
                movecount++;
            }
            if(checkTile(zerox-1, zeroy)){
                boards[1] = this.clone();
                boards[1].setTile(zerox, zeroy, this.getTile(zerox-1, zeroy));
                boards[1].setTile(zerox-1, zeroy, 0);
                movecount++;
            }
            if(checkTile(zerox, zeroy+1)){
                boards[2] = this.clone();
                boards[2].setTile(zerox, zeroy, this.getTile(zerox, zeroy+1));
                boards[2].setTile(zerox, zeroy+1, 0);
                movecount++;
            }
            if(checkTile(zerox, zeroy-1)){
                boards[3] = this.clone();
                boards[3].setTile(zerox, zeroy, this.getTile(zerox, zeroy-1));
                boards[3].setTile(zerox, zeroy-1, 0);
                movecount++;
            }
            int index = 0;
            TileBoard[] moves = new TileBoard[movecount];
            for (int i = 0; i < 4; i++) {
                if(boards[i]!=null){
                    moves[index] = boards[i];
                    index++;
                }
            }
            return moves;
        }

        public boolean end(){
            for (int y = 0; y < tiles.length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    if(getTile(x, y)!=WINARR[y][x])
                        return false;
                }
            }
            return true;
        }

        public int turn(){
            return turn;
        }

        public boolean checkTile(int x, int y){
            if(y<0||y>=tiles.length)
                return false;
            if(x<0||x>=tiles[0].length)
                return false;
            return true;
        }

        public void setTile(int x, int y, int set){
            if(!checkTile(x, y))
                return;
            tiles[y][x] = set;
        }

        public int getTile(int x, int y){
            if(!checkTile(x, y))
                return -1;
            return tiles[y][x];
        }

        public String toString(){
            String str = "";
            for (int y = 0; y < tiles.length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    str += tilestring.charAt(getTile(x, y));
                    if(x!=tiles[0].length-1){
                        str += ", ";
                    }
                }
                if(y!=tiles.length-1){
                    str += "\n";
                }
            }
            return str;
        }

        public boolean equals(Object o){
            if(!(o instanceof TileBoard)){
                return false;
            }
            TileBoard t = (TileBoard)o;
            for (int y = 0; y < tiles.length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    if(getTile(x, y)!=t.getTile(x, y)){
                        return false;
                    }
                }
            }
            return t.tilestring.equals(tilestring);
        }

        private static final Vector3f WINCOLOR = new Vector3f(0.0f,1.0f,0.0f);
        private static final Vector3f NOWIN = new Vector3f(1.0f);

        public Vector3f color(){
            if(end())
                return WINCOLOR;

            return NOWIN;
        }
    }

    public static void main(String[] args){
        BoardSolver.startVisualisation(new TileBoard("012345678"));
    }
}
