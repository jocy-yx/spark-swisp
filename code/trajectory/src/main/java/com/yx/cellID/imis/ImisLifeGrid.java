package com.yx.cellID.imis;

import java.util.ArrayList;
import java.util.List;

public class ImisLifeGrid {
    
    private double LON_EAST = 42.0;
    
    private double LON_WEST = 34.5;
    
    private double LAT_NORTH = 30.0;
    
    private double LAT_SOUTH = 19.0;

    
    private double DELTA_LON;

    private long NUMBER_OF_GRID_X ;
    private long NUMBER_OF_GRID_Y ;

    private int[][] directions = {{-1,0},{-1,1},{-1,-1},
                                            {0,1},{0,-1},
                                            {1,0},{1,-1},{1,1}};

    public ImisLifeGrid(double e) {
        this.DELTA_LON = e;
        this.NUMBER_OF_GRID_X = (long) Math.ceil((LON_EAST-LON_WEST)/DELTA_LON);
        this.NUMBER_OF_GRID_Y = (long) Math.ceil((LAT_NORTH-LAT_SOUTH)/DELTA_LON);
    }

   
    public long mapToGridCell(double lon, double lat) {
        
        long xIndex = (long)((Math.abs(lon-LON_WEST)) / DELTA_LON);
        
        long yIndex = (long)(Math.abs((LAT_NORTH - lat)) / DELTA_LON);
        return xIndex + (yIndex * NUMBER_OF_GRID_X);
    }


    
    public List<Long> closeId(long id){
        ArrayList<Long> res = new ArrayList<>();
        long row = id / NUMBER_OF_GRID_X;
        long col = id % NUMBER_OF_GRID_X;
        res.add(id);

        for (int[] dir:directions){
            long nextRow = row+dir[0];
            long nextCol = col+dir[1];
            if (inArea(nextRow,nextCol)){
                long newID = nextRow*NUMBER_OF_GRID_X+nextCol;
                res.add(newID);
            }
        }

        return res;
    }

    private boolean inArea(long row, long col) {
        return row>=0 && row<NUMBER_OF_GRID_Y && col>=0 && col<NUMBER_OF_GRID_X;
    }


}
