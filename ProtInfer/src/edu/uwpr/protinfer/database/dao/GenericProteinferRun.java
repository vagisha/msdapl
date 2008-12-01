package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.BaseProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinInferenceProgram;
import edu.uwpr.protinfer.database.dto.ProteinferInput;

public interface GenericProteinferRun <S extends ProteinferInput, T extends BaseProteinferRun<S>>{

    public abstract int saveNewProteinferRun(ProteinInferenceProgram program);

    public abstract int save(BaseProteinferRun<?> run);
    
    public abstract void update(BaseProteinferRun<?> run);

    public abstract T getProteinferRun(int proteinferId);

    public abstract List<Integer> getProteinferIdsForRunSearches(List<Integer> runSearchIds);

    public abstract void delete(int pinferId);

}