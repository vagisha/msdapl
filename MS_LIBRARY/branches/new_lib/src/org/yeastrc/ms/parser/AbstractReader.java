package org.yeastrc.ms.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractReader {

    protected int currentLineNum = 0;
    protected int warnings = 0;
    protected String currentLine;
    protected BufferedReader reader;
    protected String fileName;

    public AbstractReader() {
        super();
    }

    public int getWarningCount() {
        return warnings;
    }

    public String getFileName() {
        return fileName;
    }
    
    public void open(String filePath) throws IOException{
        reader = new BufferedReader(new FileReader(filePath));
        fileName = new File(filePath).getName();
        advanceLine();
    }

    public void open(String fileName, InputStream inStream) throws IOException {
        this.fileName = fileName;
        reader = new BufferedReader(new InputStreamReader(inStream));
        advanceLine();
    }
    
    /**
     * This method should be called explicitly after the file has been read.
     */
    public void close() {
        currentLine = null;
        if (reader != null) 
            try {reader.close();}
        catch (IOException e) {}
    }

    protected void advanceLine() throws IOException {
        currentLineNum++;
        currentLine = reader.readLine(); // advance first
        // skip over blank lines and line that don't start with valid character
        while(currentLine != null && !isValidLine(currentLine)) {
            // log.warn("!!!LINE# "+currentLineNum+" Invalid line; skipping ... \n"+currentLine);
            currentLineNum++;
            currentLine = reader.readLine();
        }
        // remove any leading or trailing white spaces
        if (currentLine != null)
            currentLine = currentLine.trim();
    }

    protected final String[] parseNameValueLine(String line, Pattern pattern) {
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String val = match.group(2);
            if (val != null && val.length() == 0)
                return new String[]{match.group(1)};
            else
                return new String[]{match.group(1), val};
        }
        else
            return new String[0];
    }
    
    protected abstract boolean isValidLine(String line);
}