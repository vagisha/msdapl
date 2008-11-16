package edu.uwpr.protinfer.database.dto;

public enum ProteinUserValidation {

    UNVALIDATED('U'), ACCEPTED('A'), REJECTED('R');
    
    private char statusChar;
    
    private ProteinUserValidation(char statusChar) {this.statusChar = statusChar;}
    
    public char getStatusChar() {return statusChar;}
    
    public static ProteinUserValidation getStatusForChar(char status) {
        switch (status) {
            case 'U':
                return UNVALIDATED;
            case 'A':
                return ACCEPTED;
            case 'R':
                return REJECTED;
            default:
                return null;
        }
    }
}
