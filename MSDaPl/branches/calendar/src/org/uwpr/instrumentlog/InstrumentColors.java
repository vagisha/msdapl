/**
 * InstrumentColors.java
 * @author Vagisha Sharma
 * May 21, 2009
 * @version 1.0
 */
package org.uwpr.instrumentlog;

/**
 * 
 */
public class InstrumentColors {

    private InstrumentColors() {}
    
    public static final String[] INSTRUMENT_COLORS = new String[] { 
        "e0c240",   // dirty yellow
        "006400",  // dark green
        "9932CC",   // Dark Orchid
        "8CBF40",  // green
        "1e90ff",  // blue
        "8b008b",  // purple
        "ffa500",  // amber
        "20b2aa",  // light teal
        "dd4477",  // pink
        "FF4500",  // OrangeRed (http://en.wikipedia.org/wiki/Web_colors)
        "4682B4",  // SteelBlue (http://en.wikipedia.org/wiki/Web_colors)
        // "DC143C"   // Crimson
        "dc143c"  // red
        
        };

    // green #8CBF40
    // blue #3366CC
    public static String getColor(int instrumentId) {
        return INSTRUMENT_COLORS[instrumentId % INSTRUMENT_COLORS.length];
    }
}
