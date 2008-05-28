package org.yeastrc.grant;

import java.util.Comparator;

public class GrantUtils {

	private GrantUtils() {}
	
	public static class GrantTitleComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getTitle().length() == 0)
				return 1;
			if (g2 == null || g2.getTitle().length() == 0)
				return -1;
			return g1.getTitle().compareTo(g2.getTitle());
		}
	}
	
	public static class GrantPIComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getPILastName().length() == 0)
				return 1;
			if (g2 == null || g2.getPILastName().length() == 0)
				return -1;
			return g1.getPILastName().compareTo(g2.getPILastName());
		}
	}
	
	public static class GrantSourceTypeComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null)
				return 1;
			if (g2 == null)
				return -1;
			return g1.getFundingSource().getTypeDisplayName().compareTo(g2.getFundingSource().getTypeDisplayName());
		}
	}
	
	public static class GrantSourceNameComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getFundingSource().getDisplayName().length() == 0)
				return 1;
			if (g2 == null || g2.getFundingSource().getDisplayName().length() == 0)
				return -1;
			return g1.getFundingSource().getDisplayName().compareTo(g2.getFundingSource().getDisplayName());
		}
	}
	
	public static class GrantNumberComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getGrantNumber().length() == 0)
				return 1;
			if (g2 == null || g2.getGrantNumber().length() == 0)
				return -1;
			return g1.getGrantNumber().compareTo(g2.getGrantNumber());
		}
	}
	
	public static class GrantAmountComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getGrantAmount().length() == 0)
				return 1;
			if (g2 == null || g2.getGrantAmount().length() == 0)
				return -1;
			return g1.getGrantAmount().compareTo(g2.getGrantAmount());
		}
	}
}
