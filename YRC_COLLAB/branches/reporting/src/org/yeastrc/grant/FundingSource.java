package org.yeastrc.grant;

import org.yeastrc.grant.FundingSourceType.SourceName;

public class FundingSource {

	private FundingSourceType type;
	private SourceName name;
	
	public FundingSource(FundingSourceType type, SourceName name) {
		this.type = type;
		this.name = name;
	}
	
	public boolean isFederal() {
		return FundingSourceType.isFederal(type.getType());
	}
	
	public String getTypeName() {
		return type.getType();
	}
	
	public String getTypeDisplayName() {
		return type.getDisplayName();
	}
	
	public String getName() {
		return name.getName();
	}
	
	public String getDisplayName() {
		return name.getDisplayName();
	}
}
