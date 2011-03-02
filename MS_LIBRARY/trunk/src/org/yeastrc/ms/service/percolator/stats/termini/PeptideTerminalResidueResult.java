/**
 * PeptideAAFrequencyResult.java
 * @author Vagisha Sharma
 * Feb 25, 2011
 */
package org.yeastrc.ms.service.percolator.stats.termini;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.general.MsEnzyme;

/**
 * 
 */
public class PeptideTerminalResidueResult {

	private Map<Character, AminoAcidCount> aaCounts = new HashMap<Character, AminoAcidCount>();
	private int totalResultCount;
	private MsEnzyme enzyme;
	private int numResultsWithEnzTerm_0;
	private int numResultsWithEnzTerm_1;
	private int numResultsWitnEnzTerm_2;
	

	private static final Logger log = Logger.getLogger(PeptideTerminalResidueResult.class);
	
	void setEnzyme(MsEnzyme enzyme) {
		this.enzyme = enzyme;
	}
	
	void addEnzymaticTerminiCount(int numEnzymaticTermini) {
		switch (numEnzymaticTermini) {
			case 0:
				numResultsWithEnzTerm_0++;
				break;
			case 1:
				numResultsWithEnzTerm_1++;
				break;
			case 2:
				numResultsWitnEnzTerm_2++;
				break;
		}
	}
	
	void setNtermMinusOneCount(char aa, int num) {
		if(aa == '-')
			return;
		AminoAcidCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidCount();
			aaCounts.put(aa, count);
		}
		count.ntermMinusOneCount = num;
	}
	
	void setCtermCount(char aa, int num) {
		if(aa == '-')
			return;
		AminoAcidCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidCount();
			aaCounts.put(aa, count);
		}
		count.ctermCount = num;
	}
	
	void addNtermMinusOneCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidCount();
			aaCounts.put(aa, count);
		}
		count.addNtermMinusOneCount();
	}
	
	void addNtermCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidCount();
			aaCounts.put(aa, count);
		}
		count.addNtermCount();
	}
	
	void addCtermCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidCount();
			aaCounts.put(aa, count);
		}
		count.addCtermCount();
	}
	
	void addCtermPlusOneCount(char aa) {
		if(aa == '-')
			return;
		AminoAcidCount count = aaCounts.get(aa);
		if(count == null) {
			count = new AminoAcidCount();
			aaCounts.put(aa, count);
		}
		count.addCtermPlusOneCount();
	}
	
	AminoAcidCount getCounts(char aa) {
		return aaCounts.get(aa);
	}
	
	void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}
	
	public Set<Character> getAminoAcids() {
		return aaCounts.keySet();
	}
	
	public Set<Character> getTopThreeAminoAcidsNtermMinusOne() {
		
		List<Character> sorted = new ArrayList<Character>();
		sorted.addAll(getAminoAcids());
		Collections.sort(sorted, new Comparator<Character>() {
			
			@Override
			public int compare(Character o1, Character o2) {
				return Integer.valueOf(getNtermMinusOneCountForAA(o2)).compareTo(getNtermMinusOneCountForAA(o1));
			}
		});
		
		Set<Character> topThree = new HashSet<Character>();
		topThree.add(sorted.get(0));
		topThree.add(sorted.get(1));
		topThree.add(sorted.get(2));
		
		return topThree;
	}
	
	public Set<Character> getTopThreeAminoAcidsCterm() {
		
		List<Character> sorted = new ArrayList<Character>();
		sorted.addAll(getAminoAcids());
		Collections.sort(sorted, new Comparator<Character>() {
			
			@Override
			public int compare(Character o1, Character o2) {
				return Integer.valueOf(getCtermCountForAA(o2)).compareTo(getCtermCountForAA(o1));
			}
		});
		
		Set<Character> topThree = new HashSet<Character>();
		topThree.add(sorted.get(0));
		topThree.add(sorted.get(1));
		topThree.add(sorted.get(2));
		
		return topThree;
	}
	
	public int getTotalResultCount() {
		return this.totalResultCount;
	}
	
	public int getNtermMinusOneCountForAA(char aa) {
		AminoAcidCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getNtermMinusOneCount();
	}
	
	public double getNtermMinusOneFreqForAA(char aa) {
		
		return roundOne((getNtermMinusOneCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	public int getNtermCountForAA(char aa) {
		AminoAcidCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getNtermCount();
	}
	
	public double getNtermFreqForAA(char aa) {
		return roundOne((getNtermCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	public int getCtermCountForAA(char aa) {
		AminoAcidCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getCtermCount();
	}
	
	public double getCtermFreqForAA(char aa) {
		return roundOne((getCtermCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	public int getCtermPlusOneCountForAA(char aa) {
		AminoAcidCount aaCount = aaCounts.get(aa);
		if(aaCount == null) {
			return 0;
		}
		else
			return aaCount.getCtermPlusOneCount();
	}
	
	public double getCtermPlusOneFreqForAA(char aa) {
		return roundOne((getCtermPlusOneCountForAA(aa) * 100.0)/(double)totalResultCount);
	}
	
	public int getNumResultsWithEnzTerm_0() {
		return numResultsWithEnzTerm_0;
	}
	
	public double getPercResultsWithEnzTerm_0() {
		return roundOne((numResultsWithEnzTerm_0 * 100.0) / (double)totalResultCount);
	}

	public int getNumResultsWithEnzTerm_1() {
		return numResultsWithEnzTerm_1;
	}
	
	public double getPercResultsWithEnzTerm_1() {
		return roundOne((numResultsWithEnzTerm_1 * 100.0) / (double)totalResultCount);
	}

	public int getNumResultsWitnEnzTerm_2() {
		return numResultsWitnEnzTerm_2;
	}
	
	public double getPercResultsWithEnzTerm_2() {
		return roundOne((numResultsWitnEnzTerm_2 * 100.0) / (double)totalResultCount);
	}
	
	public MsEnzyme getEnzyme() {
		return this.enzyme;
	}
	
	private double roundOne(double num) {
        return Math.round(num*10.0)/10.0;
    }
	
	void combineWith(PeptideTerminalResidueResult anotherResult) {
		
		this.totalResultCount += anotherResult.getTotalResultCount();
		
		for(Character aa: anotherResult.getAminoAcids()) {
			AminoAcidCount myCount = this.getCounts(aa);
			if(myCount == null) {
				myCount = new AminoAcidCount();
				this.aaCounts.put(aa, myCount);
			}
			
			AminoAcidCount theirCount = anotherResult.getCounts(aa);
			if(theirCount != null) {
				myCount.combineWith(theirCount);
			}
		}
	}
	
	static final class AminoAcidCount {
		
		private char aa;
		private int ntermMinusOneCount;
		private int ntermCount;
		private int ctermCount;
		private int ctermPlusOneCount;
		
		public char getAa() {
			return aa;
		}
		public void setAa(char aa) {
			this.aa = aa;
		}
		public int getNtermMinusOneCount() {
			return ntermMinusOneCount;
		}
		void addNtermMinusOneCount() {
			this.ntermMinusOneCount++;
		}
		public int getNtermCount() {
			return ntermCount;
		}
		void addNtermCount() {
			this.ntermCount++;
		}
		public int getCtermCount() {
			return ctermCount;
		}
		void addCtermCount() {
			this.ctermCount++;
		}
		public int getCtermPlusOneCount() {
			return ctermPlusOneCount;
		}
		void addCtermPlusOneCount() {
			this.ctermPlusOneCount++;
		}
		
		void combineWith(AminoAcidCount other) {
			if(other == null) 
				return;
			if(other.getAa() != this.aa){
				log.error("Cannot combine with counts for amino acid: "+this.aa+" with "+other.getAa());
				return;
			}
			this.ntermMinusOneCount += other.getNtermMinusOneCount();
			this.ntermCount += other.getNtermCount();
			this.ctermCount += other.getCtermCount();
			this.ctermPlusOneCount += other.getCtermPlusOneCount();
		}
	}
}
