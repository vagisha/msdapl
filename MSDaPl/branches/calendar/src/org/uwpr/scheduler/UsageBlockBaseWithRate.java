/**
 * 
 */
package org.uwpr.scheduler;

import java.math.BigDecimal;

import org.uwpr.costcenter.CostCenterConstants;
import org.uwpr.costcenter.InstrumentRate;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.project.Affiliation;

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
		BigDecimal fee = getRate().getRate().multiply(new BigDecimal(getNumHours()));

		MsInstrument instrument = MsInstrumentUtils.instance().getMsInstrument(getInstrumentID());
		if(instrument != null && instrument.getName().equals("Bravo"))
		{
			// No setup cost for the bravo
			return fee;
		}
		if(CostCenterConstants.ADD_SETUP_COST)
		{
			boolean external = rate.getRateType().getName().equals(Affiliation.external.getName());
			fee = external? fee.add(CostCenterConstants.SETUP_COST_EXTERNAL) :
			                fee.add(CostCenterConstants.SETUP_COST_INTERNAL);
		}
		return fee;
	}
}
