package org.yeastrc.ms.dao;

import java.util.List;

import org.yeastrc.ms.dto.MsProteinMatch;

public interface MsProteinMatchDAO {

    public abstract List<MsProteinMatch> loadResultProteins(int resultId);

    public abstract void save(MsProteinMatch proteinMatch);

    public abstract void delete(int resultId);

}