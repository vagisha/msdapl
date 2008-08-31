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
        DIRECTORY_NOT_FOUND     ("Directory not found.", ERROR),
        EMPTY_DIRECTORY         ("No files found to upload.", ERROR),
        MISSING_MS2             ("Missing required ms2 files.", ERROR),
        READ_ERROR_MS2          ("Error reading MS2 file", ERROR),
        INVALID_MS2_SCAN        ("Invalid MS2 scan", ERROR),
        SHA1SUM_CALC_ERROR      ("Error calculating sha1sum", ERROR),
        
        RUNTIME_MS2_ERROR       ("", ERROR),
        
        UNSUPPORTED_SQT         ("Unsupported sqt file found", WARN),
        MULTIPLE_SQT_TYPES      ("More than one sqt file types found", WARN),
        INVALID_SQT_HEADER      ("Invalid SQT header", WARN),
        INVALID_SQT_SCAN        ("Invalid SQT scan", WARN),
        NO_SCANID_FOR_SQT_SCAN  ("No database scanID found for SQT scan", WARN),
        READ_ERROR_SQT          ("Error reading SQT file", ERROR),
        
        MISSING_SEQUEST_PARAMS  ("Missing sequest.params file.", WARN),
        MISSING_PROLUCID_PARAMS ("Missing search.xml files.", WARN),
        UNKNOWN_PARAMS          ("Unknown parameters file for search.", WARN),
        PARAM_PARSING_ERROR     ("Error parsing parameters file", WARN),
        NO_RUN_SEARCHES_UPLOADED("No run searches were uploaded", WARN),
        
        PROTEIN_NOT_FOUND       ("Protein not found in database", WARN),
        
        RUNTIME_SQT_ERROR       ("Runtime exception.", WARN)
        
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
    private String errorMessage;
    
    public UploadException(ERROR_CODE error) {
        this.errCode = error;
    }
    
    public UploadException(ERROR_CODE error, Exception e) {
        super(e);
        this.errCode = error;
    }
    
    public ERROR_CODE getErrorCode() {
        return errCode;
    }
    
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        if (errCode.isError()) 
            buf.append("ERROR: ");
        else
            buf.append("WARNING: ");
        buf.append(errCode.getMessage());
        if (file != null)
            buf.append("\n\tFile: "+file);
        if (directory != null)
            buf.append("\n\tDirectory: "+directory);
        if (errorMessage != null)
            buf.append("\n\t"+errorMessage);
        buf.append("\n");
        
        return buf.toString();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
    
    public void setFile(String file) {
        this.file = file;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void appendErrorMessage(String toAppend) {
        if (errorMessage == null || errorMessage.length() == 0) {
            errorMessage = toAppend;
        }
        else {
            errorMessage = errorMessage+"\n\t"+toAppend;
        }
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
