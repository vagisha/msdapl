package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;

import edu.uwpr.protinfer.ProteinInferenceProgram;
import edu.uwpr.protinfer.ProteinInferenceProgram.ProgramParam;
import edu.uwpr.protinfer.ProteinInferenceProgram.ProgramParam.TYPE;

public class ProgramParameters {

    private String programName;
    private String progDisplayName;
    private List<Param> paramList;
    
    public ProgramParameters() {
        paramList = new ArrayList<Param>();
    }
    
    public ProgramParameters(ProteinInferenceProgram program) {
        this.programName = program.name();
        this.progDisplayName = program.getDisplayName();
        this.paramList = new ArrayList<Param>(program.getProgramParams().length);
        for(ProgramParam p: program.getProgramParams())
            this.addParam(new Param(p));
    }
    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDisplayName() {
        return progDisplayName;
    }
    public void setProgramDisplayName(String progDisplayName) {
        this.progDisplayName = progDisplayName;
    }
    
    public List<Param> getParamList() {
        return paramList;
    }
    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }
    // to be used by struts indexed properties
    public Param getParam(int index) {
        while(index >= paramList.size())
            paramList.add(new Param());
        return paramList.get(index);
    }
    public void addParam(Param param) {
        paramList.add(param);
    }
    
    
    public static final class Param {
        private String name;
        private String displayName;
        private String type;
        private String tooltip;
        private String notes;
        private String value;
        private String[] values; // used for radiobox / list options
        
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String[] getOptions() {
            return values;
        }
        
        public Param(){}
        
        public Param(ProgramParam param) {
            this.name = param.getName();
            this.displayName = param.getDisplayName();
            this.tooltip = param.getDescription();
            if(param.getType() == TYPE.BOOLEAN)
                this.type = "checkbox";
            else if(param.getType() == TYPE.CHOICE)
                this.type = "radio";
            else 
                this.type="text";
            this.value = param.getDefaultValue();
            this.values = param.getValues();
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getTooltip() {
            return tooltip;
        }
        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }
        public String getNotes() {
            return notes;
        }
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
    
}
