package es.theedg.hydra;

import java.util.Comparator;

public class HydraPostComparator implements Comparator<HydraPost> {
	@Override
	public int compare(HydraPost lhs, HydraPost rhs) {
		return -1 * lhs.getTimestamp().compareTo(rhs.getTimestamp());
	}
}