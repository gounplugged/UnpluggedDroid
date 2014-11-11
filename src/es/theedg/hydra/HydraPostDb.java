package es.theedg.hydra;

import java.util.ArrayList;

public interface HydraPostDb {
	public ArrayList<HydraPost> getHydraPosts();
	public void newHydraPost(int msgCode, HydraPost p);
}
