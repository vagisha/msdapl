package edu.uwpr.protinfer.database.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.ProteinInferenceProgram;


public class GenericProteinferRun<T extends ProteinferInput> {

    private int id;
    private Date date;
//    private ProteinferStatus status;
    private ProteinInferenceProgram program;
    private String programVersion;
    private Program inputGenerator;
    private String comments;
    private List<T> inputSummaryList;

    
    public GenericProteinferRun() {
        inputSummaryList = new ArrayList<T>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<T> getInputList() {
        return inputSummaryList;
    }

    public void setInputSummaryList(List<T> inputList) {
        this.inputSummaryList = inputList;
    }

    public ProteinInferenceProgram getProgram() {
        if(this.programVersion != null)
            program.setVersion(this.programVersion);
        return this.program;
    }

    public String getProgramString() {
        return program.name();
    }
    
    public String getProgramVersion() {
        if(this.programVersion == null)
            return program.getVersion();
        else
            return this.programVersion;
    }
    
    public void setProgramVersion(String programVersion) {
        this.programVersion = programVersion;
    }
    
    public void setProgram(ProteinInferenceProgram program) {
        this.program = program;
    }

    public Program getInputGenerator() {
        return inputGenerator;
    }
    
    public String getInputGeneratorString() {
        return inputGenerator.name();
    }
    
    public void setInputGenerator(Program inputGenerator) {
        this.inputGenerator = inputGenerator;
    }
    
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}