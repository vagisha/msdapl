/**
 * UploadException.java
 * @author Vagisha Sharma
 * Jul 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

/**
 * 
 */
public class UploadException extends Exception {

    private static final long serialVersionUID = 1L;

    public static int WARN = 0;
    private static int ERROR = 1;
    
    public static enum ERROR_CODE {
        EMPTY_DIRECTORY     ("No files found to upload.", ERROR),
        MISSING_MS2         ("Missing required ms2 files.", ERROR),
        READ_ERROR_MS2      ("Error reading MS2 file", ERROR),
        UNKNOWN_ERROR       ("", ERROR),
        
        UNSUPPORTED_SQT     ("Non-SEQUEST sqt files are not supported", WARN),
        INVALID_SQT_HEADER  ("Invalid SQT header", WARN),
        READ_ERROR_SQT      ("Error reading MS2 file", WARN),
        
        
        ;
      
        private String message = "";
        private int errType;
        
        private ERROR_CODE(String message, int errType) {
            this.message = message;
            this.errType = errType;
        }
        public String getMessage() {
            return message;
        }
        public boolean isError() {
            return errType == ERROR;
        }
    }
    
    private final ERROR_CODE errCode;
    private String directory; 
    private String file;
    private int lineNum = -1;
    private String errorMessage;
    
    public UploadException(ERROR_CODE error) {
        this.errCode = error;
    }
    
    public ERROR_CODE getErrorCode() {
        return errCode;
    }
    
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        if (errCode.isError()) 
            buf.append("ERROR: upload failed\n");
        else
            buf.append("WARNING: uploaded with warnings\n");
        buf.append(errCode.getMessage());
        buf.append("\n");
        if (directory != null)
            buf.append("\tDirectory: "+directory+"\n");
        if (file != null)
            buf.append("\tFile: "+file+"\n");
        if (lineNum != -1)
            buf.append("\tLineNum: "+file+"\n");
        if (errorMessage != null)
            buf.append("\t"+errorMessage);
        return buf.toString();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
    
    public void setFile(String file) {
        this.file = file;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
