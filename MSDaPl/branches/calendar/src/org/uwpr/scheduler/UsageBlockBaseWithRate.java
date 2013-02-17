/**
 * 
 */
package org.uwpr.scheduler;

import java.math.BigDecimal;

import org.uwpr.costcenter.InstrumentRate;
import org.uwpr.instrumentlog.UsageBlockBase;

/**
 * UsageBlockBaseWithRate.java
 * @author Vagisha Sharma
 * Jul 19, 2011
 * 
 */
public class UsageBlockBaseWithRate extends UsageBlockBase {

	private InstrumentRate rate;

	public InstrumentRate getRate() {
		return rate;
	}

	public void setRate(InstrumentRate rate) {
		this.rate = rate;
	}
	
	public UsageBlockBaseWithRate copy() {
        
		UsageBlockBaseWithRate blk = new UsageBlockBaseWithRate();
        super.copy(blk);
        blk.setRate(this.rate);
        return blk;
    }
	
	public BigDecimal getFee()
	{
		return getRate().getRate().multiply(new BigDecimal(getNumHours()));
	}
}
