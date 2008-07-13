/**
 * PeakUtils.java
 * @author Vagisha Sharma
 * Jul 12, 2008
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 */
public class PeakUtils {

    private PeakUtils() {}
    
    public static byte[] encodePeakString(String peakString) throws IOException {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(peakString);
            oos.flush();
            return baos.toByteArray();
        }
        
        finally {
            if (oos != null) {
                try {oos.close();}
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }
    
    public static String decodePeakString(byte[] peakData) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(peakData);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return ((String) ois.readObject());
        }
        
        finally {
            if (ois != null) {
                try {ois.close();} 
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }
}
