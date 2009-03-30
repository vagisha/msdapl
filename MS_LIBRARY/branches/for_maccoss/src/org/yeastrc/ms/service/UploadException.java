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
        
        
        PREUPLOAD_CHECK_FALIED  ("Pre-upload check failed", ERROR),
        
        EXPT_NOT_FOUND          ("Experiment not found in the database", ERROR),
        DIRECTORY_NOT_FOUND     ("Directory not found.", ERROR),
        EMPTY_DIRECTORY         ("No files found to upload.", ERROR),
        MISSING_SCAN_DATA_FILE  ("Missing required scan data files.", ERROR),
        MULTI_SCAN_DATA_FORMATS ("More than one scan data file types found", ERROR),
        NO_SCAN_DATA_FORMATS    ("No supported scan data file types found", ERROR),
        UNSUPPORTED_SCAN_DATA_FORMAT ("Unsupported scan data format", ERROR),
        
        
        CREATE_EXPT_ERROR       ("Error creating experiment.", ERROR),
        
        READ_ERROR_MS2          ("Error reading MS2 file", ERROR),
        INVALID_MS2_SCAN        ("Invalid MS2 scan", ERROR),
        SHA1SUM_CALC_ERROR      ("Error calculating sha1sum", ERROR),
        
        RUNTIME_MS2_ERROR       ("", ERROR),
        
        NO_SQT_TYPE             ("No sqt type found", ERROR),
        UNSUPPORTED_SQT         ("Unsupported sqt file found", ERROR),
        MULTIPLE_SQT_TYPES      ("More than one sqt file types found", ERROR),
        NO_RUNID_FOR_SQT        ("No runID found for sqt file", WARN),
        INVALID_SQT_HEADER      ("Invalid SQT header", WARN),
        INVALID_SQT_SCAN        ("Invalid SQT scan", WARN),
        NO_SCANID_FOR_SQT_SCAN  ("No database scanID found for SQT scan", WARN),
        DUPLICATE_SCAN_CHARGE   ("Duplicate result found for scan + charge combination", ERROR),
        READ_ERROR_SQT          ("Error reading SQT file", ERROR),
        
        MOD_LOOKUP_FAILED       ("Modification lookup failed", WARN),
        
        MISSING_SEQUEST_PARAMS  ("Missing sequest.params file.", ERROR),
        MISSING_PROLUCID_PARAMS ("Missing search.xml files.", ERROR),
        UNKNOWN_PARAMS          ("Unknown parameters file for search.", ERROR),
        PARAM_PARSING_ERROR     ("Error parsing parameters file", ERROR),
        NO_RUN_SEARCHES_UPLOADED("No run searches were uploaded", ERROR),
        AMBIG_PROG_VERSION      ("Ambiguous analysis program version in sqt files", ERROR),
        
        SEARCHDB_NOT_FOUND      ("No matching search database found", ERROR),
        PROTEIN_NOT_FOUND       ("Protein not found in database", ERROR),
        
        SCAN_CHARGE_NOT_FOUND   ("MS2 scan charge information not found", ERROR),
        
        // For Percolator uploads
        NO_RUNSEARCHID_FOR_SQT  ("No runSearchID found for Percolator sqt file", WARN),
        NO_PERC_ANALYSIS_UPLOADED("No Percolator analysis files were uploaded", ERROR),
        NOT_MATCHING_SEARCH_RESULT("No matching search result was found for the Percolator result", WARN),
        NOT_MATCHING_SEARCH_SCAN("No matching search scan was found for the Percolator search scan", WARN),
        
        // General
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
