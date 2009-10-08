/**
 * SequestResultFilterCriteria.java
 * @author Vagisha Sharma
 * Apr 7, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.mascot;

import org.yeastrc.ms.domain.search.ResultFilterCriteria;
import org.yeastrc.ms.domain.search.SORT_BY;

/**
 * 
 */
public class MascotResultFilterCriteria extends ResultFilterCriteria {

    
    private Integer star;
    
    private Double minIonScore;
    private Double minIdentityScore;
    private Double minHomologyScore;
    private Double minExpect;
    
    
    public boolean hasFilters() {
        if(super.hasFilters())
            return true;
        
        return (hasStarFilter() ||
                hasIonScoreFilter() ||
                hasIdentityScoreFilter() ||
                hasHomologyScoreFilter() ||
                hasExpectFilter());
    }
    
    public boolean superHasFilters() {
        return super.hasFilters();
    }
    
    
    //-------------------------------------------------------------
    // STAR FILTER
    //-------------------------------------------------------------
    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }
    
    public boolean hasStarFilter() {
        return star != null;
    }
    
    public String makeStarFilterSql() {
        
        if(!hasStarFilter())
            return "";
        StringBuilder buf = new StringBuilder();
        String starCol = SORT_BY.STAR.getColumnName();
        
        buf.append(" (" +starCol+" = "+star+") ");
        
        return buf.toString();
    }
    
    
    //-------------------------------------------------------------
    // ION_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinIonScore() {
        return minIonScore;
    }

    public void setMinIonScore(Double ionScore) {
        this.minIonScore = ionScore;
    }
    
    public boolean hasIonScoreFilter() {
        return minIonScore != null;
    }
    
    public String makeIonScoreFilterSql() {
        return makeFilterSql(SORT_BY.ION_SCORE.getColumnName(), minIonScore, null);
    }
    
    //-------------------------------------------------------------
    // IDENTITY_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinIdentityScore() {
        return minIdentityScore;
    }
    
    public void setMinIdentityScore(Double identityScore) {
        this.minIdentityScore = identityScore;
    }
    
    public boolean hasIdentityScoreFilter() {
        return this.minIdentityScore != null;
    }
    
    public String makeIdentityScoreFilterSql() {
        return makeFilterSql(SORT_BY.IDENTITY_SCORE.getColumnName(), minIdentityScore, null);
    }
    
    //-------------------------------------------------------------
    // HOMOLOGY_SCORE  FILTER
    //-------------------------------------------------------------
    public Double getMinHomologyScore() {
        return minHomologyScore;
    }
    
    public void setMinHomologyScore(Double homologyScore) {
        this.minHomologyScore = homologyScore;
    }
    
    public boolean hasHomologyScoreFilter() {
        return this.minHomologyScore != null;
    }
    
    public String makeHomologyScoreFilterSql() {
        return makeFilterSql(SORT_BY.HOMOLOGY_SCORE.getColumnName(), minHomologyScore, null);
    }
    
    //-------------------------------------------------------------
    // EXPECT  FILTER
    //-------------------------------------------------------------
    public Double getMinExpect() {
        return minExpect;
    }
    
    public void setMinExpectScore(Double expect) {
        this.minExpect = expect;
    }
    
    public boolean hasExpectFilter() {
        return this.minExpect != null;
    }
    
    public String makeExpectFilterSql() {
        return makeFilterSql(SORT_BY.EXPECT.getColumnName(), minExpect, null);
    }
}
