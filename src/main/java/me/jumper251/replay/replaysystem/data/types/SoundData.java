package me.jumper251.replay.replaysystem.data.types;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Sound;

public class SoundData extends PacketData {

	/**
	 *
	 */
	private static final long serialVersionUID = -5227638148471461255L;

	private String sound;

	private float volume;

	private int seed;

	private int x;
	private int y;
	private int z;

	public SoundData(String sound, int x, int y, int z, float v, int seed) {
		this.sound = sound;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = v;
		this.seed = seed;

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

	public String getSound() {
		return sound;
	}

	public float getVolume() {
		return volume;
	}

	public float getSeed() {
		return seed;
	}

	public PacketContainer getPacket(){
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.NAMED_SOUND_EFFECT);
		packet.getStrings().write(0, sound);
		packet.getIntegers().write(0, x)
				.write(1, y)
				.write(2, z)
				.write(3, seed);
		packet.getFloat().write(0, volume);
		return packet;
	}


}
