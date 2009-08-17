package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.ProteinferInput;

public interface GenericProteinferInputDAO <T extends ProteinferInput>{

    public abstract List<T> loadProteinferInputList(int pinferId);
    
    public abstract int saveProteinferInput(T input);
    
    public abstract List<Integer> loadInputIdsForProteinferRun(int pinferId);
    
    public abstract void deleteProteinferInput(int pinferId);
}
