package edu.uwpr.protinfer.database.dao;

import java.util.List;

import edu.uwpr.protinfer.database.dto.GenericProteinferRun;
import edu.uwpr.protinfer.database.dto.ProteinferInput;

public interface GenericProteinferRunDAO <S extends ProteinferInput, T extends GenericProteinferRun<S>>{

    public abstract int save(GenericProteinferRun<?> run);
    
    public abstract void update(GenericProteinferRun<?> run);

    public abstract T loadProteinferRun(int proteinferId);

    public abstract List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds);

    public abstract void delete(int pinferId);

}