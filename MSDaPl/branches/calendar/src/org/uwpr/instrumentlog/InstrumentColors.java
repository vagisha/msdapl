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
        "006400",   	// dark green
	"1E90FF",   // dodger blue

        "20b2aa",  	// light sea green
        "8b008b",   // dark magenta
	"9932CC",  // Dark Orchid


        "ffa500",  // amber
        "fa8072",  // salmon
        "9932CC",  // Dark Orchid
        "20b2aa",  // light teal
        "dd4477",  // pink
        "FF4500",  // OrangeRed (http://en.wikipedia.org/wiki/Web_colors)
        "4682B4",  // SteelBlue (http://en.wikipedia.org/wiki/Web_colors)
        "9370DB",   	// medium purple
        "FFD700",   	// gold
        "d2691e",   	// chocolate
	"00BFFF",  // deep sky blue
        "DC143C",   // Crimson
        "8b008b",   // purple
        "EE82EE",   // violet
        "800080",   // purple
	"FF4500",   // orange red
	"ffa500",   // orange
        "228B22"   // forest green
        };

    // green #8CBF40
    // blue #3366CC
    public static String getColor(int instrumentId) {
        return INSTRUMENT_COLORS[instrumentId % INSTRUMENT_COLORS.length];
    }
}
