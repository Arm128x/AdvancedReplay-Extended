package me.jumper251.replay.dev.mrflyn.extended;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.replaysystem.data.types.LocationData;
import me.jumper251.replay.replaysystem.data.types.SpawnData;
import me.jumper251.replay.replaysystem.replaying.Replayer;

import java.io.File;
import java.util.HashMap;

public class WorldHandler {
    public static HashMap<String, String> WORLD_NAME_HASHCODE = new HashMap<>();
    public static HashMap<String, String> WORLD_FALSE_NAME_REAL_NAME = new HashMap<>();
    public static HashMap<String, Integer> WORLD_WATCHER = new HashMap<>();
    public static void onReplayStart(Replayer replayer, SpawnData spawnData){
        if (!ConfigManager.USE_DATABASE){
            replayer.setPaused(true);
            replayer.getWatchingPlayer().teleport(LocationData.toLocation(replayer, spawnData.getLocation()));
            replayer.setPaused(false);
            return;
        }

        ReplaySystem.getInstance().worldManger.onReplayStart(replayer, spawnData);


    }

    public static void putNewWorldWithHashcode(String displayName, String hashCode){
        String worldName = displayName;
        if(ReplaySystem.getInstance().bw1058Support!=null){

            worldName = ReplaySystem.getInstance().bw1058Support.getRealWorldName(displayName);

            if (!displayName.equals(worldName)&&WORLD_NAME_HASHCODE.containsKey(worldName)){
                WORLD_NAME_HASHCODE.put(displayName, WORLD_NAME_HASHCODE.get(worldName));
                return;
            }

        }
        WORLD_NAME_HASHCODE.put(worldName, hashCode);
    }

    public static void putNewWorldFalseName(String falseName) {
        String realName = falseName;
        if (ReplaySystem.getInstance().bw1058Support != null) {

            realName = ReplaySystem.getInstance().bw1058Support.getRealWorldName(falseName);

        }
        WORLD_FALSE_NAME_REAL_NAME.put(falseName, realName);
    }

    public static void worldWatcherIncrement(String name, int number){
        if (WORLD_WATCHER.containsKey(name))WORLD_WATCHER.put(name, WORLD_WATCHER.get(name)+number);
        else WORLD_WATCHER.put(name, number);
    }

}
