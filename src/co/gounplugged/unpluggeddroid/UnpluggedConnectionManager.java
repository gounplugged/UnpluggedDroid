package co.gounplugged.unpluggeddroid;

import java.util.UUID;

import es.theedg.hydra.HydraPostDb;

public interface UnpluggedConnectionManager {
	public void ping();
	public String getServiceName();
	public UUID getUuid();
	public HydraPostDb getHydraPostDb();
	public boolean isEnabled();
	public void resumeConnection();
	public void stop();
}
