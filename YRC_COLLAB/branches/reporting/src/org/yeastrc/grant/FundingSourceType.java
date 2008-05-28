package org.yeastrc.grant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FundingSourceType {
	
	private String type;
	private String displayName;
	private List<SourceName> acceptedSourceNames;
	
	// Federal Source names
	private static final SourceName[] federalSources = new SourceName[] {new SourceName("NASA"),
															new SourceName("NIH"), 
															new SourceName("NSF"), 
															new SourceName("DOE"), 
															new SourceName("DOD"),
															new SourceName("NIST"),
															new SourceName("DVA"),
															new SourceName("OTHER", "Other")};
	
	// Funding source types
	public static final FundingSourceType OTHER = new FundingSourceType("OTHER", "Other");
	public static final FundingSourceType LOCGOV = new FundingSourceType("LOCGOV", "Local Gov.");
	public static final FundingSourceType PROFASSOC = new FundingSourceType("PROFASSOC", "Prof. Assoc.");
	public static final FundingSourceType INDUSTRY = new FundingSourceType("INDUSTRY", "Industry");
	public static final FundingSourceType FOUNDATION = new FundingSourceType("FOUNDATION", "Foundation");
	public static final FundingSourceType FEDERAL = new FundingSourceType("FEDERAL", "U.S. Federal", Arrays.asList(federalSources));

	private static final FundingSourceType[] sourceTypes = new FundingSourceType[]{FEDERAL, FOUNDATION, INDUSTRY, PROFASSOC, LOCGOV, OTHER};
	
	// -------------------------------------------------------------------------------------
	// Private constructors
	// -------------------------------------------------------------------------------------
	private FundingSourceType(String type, String displayName) {
		this(type, displayName, new ArrayList<SourceName>(0));
	}
	
	private FundingSourceType(String type, String displayName, List<SourceName> acceptedValues) {
		this.type = type;
		this.displayName = displayName;
		this.acceptedSourceNames = acceptedValues;
	}

	// -------------------------------------------------------------------------------------
	// BEGIN Static methods
	// -------------------------------------------------------------------------------------
	public static List<FundingSourceType> getFundingSources() {
		return Arrays.asList(sourceTypes);
	}
	
	public static boolean isFederal(String sourceType) {
		return sourceType.equalsIgnoreCase(FEDERAL.getType());
	}
	
	public static boolean isValidSourceName(String sourceType, String sourceName) {
		FundingSourceType source= getSourceType(sourceType);
		if (source == null)	
			return false;
		return source.isValidSourceName(sourceName);
	}
	
	public static FundingSourceType getSourceType(String sourceType) {
		for (FundingSourceType source: sourceTypes) {
			if (source.getType().equals(sourceType))
				return source;
		}
		return null;
	}
	// -------------------------------------------------------------------------------------
	// END Static methods
	// -------------------------------------------------------------------------------------
	
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the acceptedSourceNames
	 */
	public List<SourceName> getAcceptedSourceNames() {
		return acceptedSourceNames;
	}

	/**
	 * @param acceptedSourceNames the acceptedSourceNames to set
	 */
	public void setAcceptedSourceNames(List<SourceName> acceptedSourceNames) {
		this.acceptedSourceNames = acceptedSourceNames;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	public boolean isValidSourceName(String sourceName) {
		if (acceptedSourceNames.size() == 0)
			return true;
		if (sourceName == null)
			return false;
		for (SourceName source: acceptedSourceNames) {
			if (source.getName().equals(sourceName))
				return true;
		}
		return false;
	}
	
	public SourceName getSourceName(String name) {
		if (acceptedSourceNames.size() == 0)
			return new SourceName(name);
		for (SourceName source: acceptedSourceNames) {
			if (source.getName().equals(name))
				return source;
		}
		return null;
	}
	
	public static final class SourceName {
		private String name;
		private String displayName;
		
		public SourceName(String name) {
			this(name, name);
		}
		
		public SourceName(String name, String displayName) {
			this.name = name;
			this.displayName = displayName;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the displayName
		 */
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * @param displayName the displayName to set
		 */
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		
		
	}
}
