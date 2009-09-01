/**
 * FileUtils.java
 * @author Vagisha Sharma
 * Aug 3, 2009
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 */
public class FileUtils {

    private FileUtils() {}

    public static void copyFile (File src, File dest) throws IOException {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);

            // Transfer bytes from in to out
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        finally {
            in.close();
            out.close();
        }
    }
    
    public static void deleteFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f: files)
                deleteFile(f);
        }
        else {
            file.delete();
        }
    }
}
