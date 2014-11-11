package es.theedg.hydra;

import java.util.Comparator;

public class HydraPostComparator implements Comparator<HydraPost> {
	@Override
	public int compare(HydraPost lhs, HydraPost rhs) {
		return lhs.getTimestamp() < rhs.getTimestamp() ? 1 : lhs.getTimestamp() == rhs.getTimestamp() ? 0 : -1;
	}
}