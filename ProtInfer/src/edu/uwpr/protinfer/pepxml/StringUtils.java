package edu.uwpr.protinfer.pepxml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class StringUtils {

    private StringUtils(){}
    
    public static final int getCoveredSequenceLength(String sequence, List<String> subseqList) {
        
        List<Coordinates> coords = new ArrayList<Coordinates>(subseqList.size());
        for (String subseq: subseqList) {
            int nextStart = 0;
            int idx;
            while((idx = sequence.indexOf(subseq, nextStart)) >= 0) {
                nextStart = idx+subseq.length();
                coords.add(new Coordinates(idx, nextStart - 1));
            }
        }

        Collections.sort(coords, new Comparator<Coordinates>(){
            public int compare(Coordinates o1, Coordinates o2) {
                return Integer.valueOf(o1.start).compareTo(Integer.valueOf(o2.start));
            }});
            
        int coveredLength = 0;
        Coordinates lastCoord = null;
        for (Coordinates coord: coords) {
            if (lastCoord == null) {
                lastCoord = coord;
                continue;
            }
            if (lastCoord.overlap(coord)) {
                lastCoord.end = Math.max(lastCoord.end, coord.end);
            }
            else {
                coveredLength += lastCoord.length();
                lastCoord = coord;
            }
        }
        if (lastCoord != null)
            coveredLength+= lastCoord.length();
        
        return coveredLength;
    }
    
    private static final class Coordinates {
        private int start;
        private int end;
        public Coordinates(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        public boolean overlap(Coordinates coord) {
            if (this.start < coord.start) {
                return coord.start <= this.end;
            }
            else {
                return this.start <= coord.end;
            }
        }
        
        public int length() {
            return end - start + 1;
        }
    }
}
