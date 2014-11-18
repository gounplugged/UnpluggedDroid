package es.theedg.hydra;

import java.util.ArrayList;

public interface HydraPostDb {
	public ArrayList<HydraPost> getHydraPosts();
	public void addHydraPost(int msgCode, HydraPost p);
}
