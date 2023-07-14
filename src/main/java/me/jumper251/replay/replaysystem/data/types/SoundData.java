package me.jumper251.replay.replaysystem.data.types;

import org.bukkit.Sound;

public class SoundData extends PacketData {

	/**
	 *
	 */
	private static final long serialVersionUID = -5227638148471461255L;

	private Sound sound;

	private float volume;

	private float pitch;

	private double x;
	private double y;
	private double z;

	public SoundData(Sound sound, double x, double y, double z, float v, float p) {
		this.sound = sound;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = v;
		this.pitch = p;

	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Sound getSound() {
		return sound;
	}

	public float getVolume() {
		return volume;
	}

	public float getPitch() {
		return pitch;
	}
}
