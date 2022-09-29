package me.jumper251.replay;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;


import me.jumper251.replay.com.twmacinta.util.MD5;
import me.jumper251.replay.dev.mrflyn.extended.VanillaListeners;
import me.jumper251.replay.dev.mrflyn.extended.WorldHandler;
import me.jumper251.replay.dev.mrflyn.extended.worldmanagers.IWorldManger;
import me.jumper251.replay.dev.mrflyn.extended.worldmanagers.SWMWorldManager;
import me.jumper251.replay.dev.mrflyn.extended.worldmanagers.VanillaWorldManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;



import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.filesystem.saving.DatabaseReplaySaver;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.utils.ReplayCleanup;
import me.jumper251.replay.utils.Metrics;
import me.jumper251.replay.utils.ReplayManager;
import me.jumper251.replay.utils.Updater;


public class ReplaySystem extends JavaPlugin {

	
	public static ReplaySystem instance;
	
	public static Updater updater;
	public static Metrics metrics;
	public IWorldManger worldManger;
	public VanillaWorldManager vanillaWorldManager;
	
	public final static String PREFIX = "§8[§3Replay§8] §r§7";

	
	@Override
	public void onDisable() {
		for (Replay replay : new HashMap<>(ReplayManager.activeReplays).values()) {
		    if (replay.isRecording() && replay.getRecorder().getData().getActions().size() > 0) {
				replay.getRecorder().stop(ConfigManager.SAVE_STOP);
			}
		}

	}
	
	@Override
	public void onEnable() {
		instance = this;


		Long start = System.currentTimeMillis();

		getLogger().info("Loading Replay v" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0));
		
		ConfigManager.loadConfigs();
		ReplayManager.register();
		ReplaySaver.register(ConfigManager.USE_DATABASE ? new DatabaseReplaySaver() : new DefaultReplaySaver());

		updater = new Updater();
		metrics = new Metrics(this, 2188);
		
		if (ConfigManager.CLEANUP_REPLAYS > 0) {
			ReplayCleanup.cleanupReplays();
		}

//		for(World w : Bukkit.getWorlds()){
//			worldManger.onWorldLoad(w);
//		}
		//TODO : FIGURE /\ THis OUT

		if (Bukkit.getServer().getPluginManager().getPlugin("SlimeWorldManager")!=null){
			worldManger = new SWMWorldManager();
			File slimeWorlds = new File("slime_worlds");
			if (slimeWorlds.exists()&&slimeWorlds.isDirectory()) {
				File[] files = slimeWorlds.listFiles();
				getLogger().info(Arrays.toString(files) + " WORLDS");
				if (files!=null) {
					for (File file : files) {
						if (!file.getName().endsWith(".slime"))continue;
						getLogger().info(file.getName() + " WORLDS");
						String hashcode = worldManger.uploadWorld(file);
						if (hashcode==null)continue;
						getLogger().info(file.getName() + " UPLOAD WORLDS");
						WorldHandler.WORLD_NAME_HASHCODE.put(file.getName().substring(0,file.getName().length()-6), hashcode);
					}
				}
			}

		}else {
			worldManger = new VanillaWorldManager();
		}



		vanillaWorldManager = new VanillaWorldManager();

		if (ConfigManager.UPLOAD_WORLDS&&ConfigManager.USE_DATABASE){

			getServer().getPluginManager().registerEvents(worldManger.getListener(), this);

		}

		getLogger().info("Finished (" + (System.currentTimeMillis() - start) + "ms)");

	}
	
	
	public static ReplaySystem getInstance() {
		return instance;
	}
}
