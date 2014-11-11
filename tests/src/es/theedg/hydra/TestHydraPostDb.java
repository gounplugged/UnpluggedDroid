package es.theedg.hydra;

import java.util.ArrayList;

public class TestHydraPostDb implements HydraPostDb{
	private ArrayList<HydraPost> hydraPosts;
	
	public TestHydraPostDb() {
		hydraPosts = new ArrayList<HydraPost>();
	}
	public ArrayList<HydraPost> getHydraPosts() {
		return hydraPosts;		
	}
	
	public void newHydraPost(int msgCode, HydraPost p) {
		hydraPosts.add(p);
	}
}
