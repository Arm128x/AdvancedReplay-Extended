package me.jumper251.replay.replaysystem.data.types;

import java.io.Serializable;

import me.jumper251.replay.dev.mrflyn.extended.WorldHandler;
import me.jumper251.replay.replaysystem.data.ReplayData;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -849472505875330147L;
	
	private double x, y, z;
	
	private float yaw, pitch;
	
	private String world;

	private String worldHashCode;
	
	public LocationData(ReplayData rData, double x, double y, double z, String world, String worldHashcode) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.worldHashCode = worldHashcode;
		if (rData!=null)
			rData.addUsedWorld(world+"_"+this.worldHashCode);
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public String getWorld() {
		return world;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public double getZ() {
		return z;
	}

	public String getWorldHashCode(){
		return this.worldHashCode;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	
	public static LocationData fromLocation(ReplayData rData, Location loc) {
		return new LocationData(rData,loc.getX(), loc.getY(), loc.getZ(), WorldHandler.WORLD_FALSE_NAME_REAL_NAME.get(loc.getWorld().getName()), WorldHandler.WORLD_NAME_HASHCODE.get(loc.getWorld().getName()));
	}

	public static LocationData fromLocation0(ReplayData rData, Location loc) {
		return new LocationData(rData, loc.getX(), loc.getY(), loc.getZ(), loc.getWorld().getName(), WorldHandler.WORLD_NAME_HASHCODE.get(loc.getWorld().getName()));
	}
	
	public static Location toLocation(Replayer replayer, LocationData locationData) {
		return new Location(Bukkit.getWorld(replayer.isAllWorldsFound()?locationData.getWorld()+"_"+locationData.getWorldHashCode():locationData.getWorld()), locationData.getX(), locationData.getY(), locationData.getZ());
		//TODO FIX
	}


	@Override
	public String toString() {
		return "LocationData{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", yaw=" + yaw +
				", pitch=" + pitch +
				", worldHashCode=" + worldHashCode +
				", world='" + world + '\'' +
				'}';
	}
}
