package me.jumper251.replay.dev.mrflyn.extended.worldmanagers;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.plugin.loaders.LoaderUtils;
import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.database.DatabaseRegistry;
import me.jumper251.replay.dev.mrflyn.extended.*;
import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.replaysystem.data.types.LocationData;
import me.jumper251.replay.replaysystem.data.types.SpawnData;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import me.jumper251.replay.utils.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static me.jumper251.replay.dev.mrflyn.extended.WorldHandler.worldWatcherIncrement;

public class SWMWorldManager implements IWorldManger {

    SlimePlugin slime;
    int query = 0;
    private final SWMListeners listener;

    public SWMWorldManager(){
        slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        listener = new SWMListeners(slime);
    }

    @Override
    public void onWorldLoad(World world) {
//        if (query==2){
//            query = 0;
//        }

        if(!ConfigManager.UPLOAD_WORLDS)return;
        if (ConfigManager.BLACKLISTED_UPLOAD_WORDLS.contains(world.getName()))return;
//        Bukkit.getScheduler().runTaskAsynchronously(ReplaySystem.getInstance(), ()->{
            String hashcode = ReplaySystem.getInstance().worldManger.uploadWorld(world.getName());
//            if(hashcode==null)return;
            WorldHandler.putNewWorldWithHashcode(world.getName(), hashcode);
//        try {
//            ReplaySystem.getInstance().getLogger().info("Sleeping");
//            Thread.sleep(3000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        });
//        query++;
    }

    @Override
    public String uploadWorld(String name) {
        try {
            String hashcode = null;
            byte[] data = null;
            if (slime.getLoader("file").worldExists(name)){
                data = slime.getLoader("file").loadWorld(name, true);
                hashcode = WorldUtils.hash(data);
            } else if (slime.getLoader("mysql").worldExists(name)) {
                data = slime.getLoader("mysql").loadWorld(name, true);
                hashcode = WorldUtils.hash(data);
            } else if (slime.getLoader("mongodb").worldExists(name)) {
                data = slime.getLoader("mongodb").loadWorld(name, true);
                hashcode = WorldUtils.hash(data);
            }
            if (hashcode == null) return null;
            if (data == null)return null;
            if (DatabaseRegistry.getDatabase().getService().hasWorld(hashcode)) return hashcode;
            DatabaseRegistry.getDatabase().getService().setWorld(hashcode, name, data, "slime");
            return hashcode;
        }catch (Exception e){
            if (!(e instanceof NullPointerException))
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String uploadWorld(File file) {
        System.out.println("UP WORLD");
        try {
            String hashcode = null;
            byte[] data = null;

            data = Files.readAllBytes(file.toPath());
            hashcode = WorldUtils.hash(data);

            if (hashcode == null){
                System.out.println("HASH NULL");
                return null;}
            if (data == null){
                System.out.println("data NULL");
                return null;}
            if (DatabaseRegistry.getDatabase().getService().hasWorld(hashcode)) return hashcode;
            DatabaseRegistry.getDatabase().getService().setWorld(hashcode, file.getName().substring(0,file.getName().length()-6), data, "slime");
            return hashcode;
        }catch (Exception e){
            if (!(e instanceof IOException)&&!(e instanceof NullPointerException))
                e.printStackTrace();
        }
        return null;
    }

    @Override
    public File downloadWorld(String hashcode, String name) {
        try {
            File dir = new File(ReplaySystem.getInstance().getDataFolder() + "/downloadedWorlds/");
            if (!dir.exists()||!dir.isDirectory()){
                dir.mkdirs();
            }
            for(File file : dir.listFiles()){
                if (!file.getName().endsWith(".slime"))continue;
                if (file.getName().contains(hashcode))return file;
            }
            RawWorld worldData = DatabaseRegistry.getDatabase().getService().getWorld(hashcode);
            if (worldData == null) {
                LogUtils.log("(downloadWorld) Unable to load world from database. (Doesn't exist)");
                return null;
            }
            if (worldData.data == null || worldData.name == null || worldData.hashcode == null || worldData.type == null) {
                LogUtils.log("(downloadWorld) Unable to load world from database. (Missing data)");
                return null;
            }

            String destination = ReplaySystem.getInstance().getDataFolder() + "/downloadedWorlds" + "/" + name + "_" + worldData.hashcode;
            File file = new File(Paths.get(destination + ".slime").toUri());
            if (!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Files.write(Paths.get(destination + ".slime"), worldData.data);
            return file;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public File downloadWorldFromName(String name) {
        try {
            RawWorld worldData = DatabaseRegistry.getDatabase().getService().getWorldFromName(name);
            if (worldData == null) {
                LogUtils.log("(downloadWorldFromName) Unable to load world from database. (Doesn't exist)");
                return null;
            }
            if (worldData.data == null || worldData.name == null || worldData.hashcode == null || worldData.type == null) {
                LogUtils.log("(downloadWorldFromName) Unable to load world from database. (Missing data");
                return null;
            }
            String destination = ReplaySystem.getInstance().getDataFolder() + "/downloadedWorlds" + "/" + worldData.name + "_" + worldData.hashcode;
            return new File(Files.write(Paths.get(destination + ".slime"), worldData.data).toUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public SlimeWorld loadWorldSlime(File file, String nameWithHashcode) {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            SlimePropertyMap spm = new SlimePropertyMap();
            spm.setString(SlimeProperties.WORLD_TYPE, "flat");
            spm.setInt(SlimeProperties.SPAWN_X, 0);
            spm.setInt(SlimeProperties.SPAWN_Y, 64);
            spm.setInt(SlimeProperties.SPAWN_Z, 0);
            spm.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            spm.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            spm.setString(SlimeProperties.DIFFICULTY, "easy");
            spm.setBoolean(SlimeProperties.PVP, true);
            //TODO: BUG
            if (nameWithHashcode==null)nameWithHashcode = file.getName().replace(".slime", "");
            return LoaderUtils.deserializeWorld(slime.getLoader("file"), nameWithHashcode, data, spm, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void unloadWorld(String name) {
        ReplaySystem.getInstance().getLogger().info("Swm UnloadWorld: called");
        if (Bukkit.getWorld(name)==null)return;
        ReplaySystem.getInstance().getLogger().info("Swm UnloadWorld: true "+name);
//        Bukkit.unloadWorld(name, true);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "swm unload "+name);
    }

    public Listener getListener(){
        return this.listener;
    }

    @Override
    public void onReplayStart(Replayer replayer, SpawnData spawnData) {
        String destination = ReplaySystem.getInstance().getDataFolder()+"/downloadedWorlds";
        String replayWorld = spawnData.getLocation().getWorld()+"_"+spawnData.getLocation().getWorldHashCode();
        replayer.setPaused(true);
        if(replayer.isAllWorldsFound()){
            //TODO :POSSIBLE ERR REPLAY PAUSE

            for(String world : replayer.getReplay().getData().getUsedWorlds()){
                if (world.equals(replayWorld))continue;

                //check if world is already loaded
                if (Bukkit.getWorld(world)!=null){
                    worldWatcherIncrement(world, 1);
                    continue;
                }
                Bukkit.getScheduler().runTaskAsynchronously(ReplaySystem.getInstance(), ()->{
                    SlimeWorld sWorld = loadWorldSlime(new File(destination,world+".slime"), null);
                    Bukkit.getScheduler().runTask(ReplaySystem.getInstance(), ()->{
                        slime.generateWorld(sWorld);
                        worldWatcherIncrement(world, 1);
                    });
                });

            }


        }



        ReplaySystem.getInstance().getLogger().info("onReplayStart");

        //check if world is already loaded

        if (Bukkit.getWorld(replayWorld)!=null){
            //TODO: add number to watching replays in this world.
            worldWatcherIncrement(replayWorld, 1);
            ReplaySystem.getInstance().getLogger().info("onReplayStart: worldLoadedAlready");
            replayer.getWatchingPlayer().teleport(LocationData.toLocation(replayer, spawnData.getLocation()));
            replayer.setPaused(false);
            return;
        }
        //check if world is already downloaded
        if(WorldUtils.doesFolderExists(replayWorld+".slime", destination)){
            //TODO: loadWorld
            ReplaySystem.getInstance().getLogger().info("onReplayStart: loadWorldSlime");
            Bukkit.getScheduler().runTaskAsynchronously(ReplaySystem.getInstance(), ()->{
                SlimeWorld world = loadWorldSlime(new File(destination,replayWorld+".slime"), null);
                Bukkit.getScheduler().runTask(ReplaySystem.getInstance(), ()->{
                    slime.generateWorld(world);
                    replayer.getWatchingPlayer().teleport(LocationData.toLocation(replayer,spawnData.getLocation()));
                    worldWatcherIncrement(replayWorld, 1);
                    replayer.setPaused(false);
                });
            });
            return;
        }
        //download world from database async
        Bukkit.getScheduler().runTaskAsynchronously(ReplaySystem.getInstance(), () -> {
            ReplaySystem.getInstance().getLogger().info("onReplayStart: downloadWorld");
            File file = ReplaySystem.getInstance().worldManger.downloadWorld(spawnData.getLocation().getWorldHashCode(), spawnData.getLocation().getWorld());
            ReplaySystem.getInstance().getLogger().info("onReplayStart: downloadWorld done");
            if (file==null){
                //TODO: attempt unsafe world loading
                Bukkit.getScheduler().runTask(ReplaySystem.getInstance(), ()->{
                    replayer.getWatchingPlayer().teleport(LocationData.toLocation(replayer, spawnData.getLocation()));
                    replayer.setPaused(false);
                });
                return;
            }
            ReplaySystem.getInstance().getLogger().info("onReplayStart: downloadWorld done slime");
            SlimeWorld w = loadWorldSlime(file, replayWorld);
            ReplaySystem.getInstance().getLogger().info("onReplayStart: downloadWorld done slime done");
            Bukkit.getScheduler().runTask(ReplaySystem.getInstance(), () -> {
                ReplaySystem.getInstance().getLogger().info("onReplayStart: loadWorld slime");
                slime.generateWorld(w);
                ReplaySystem.getInstance().getLogger().info("onReplayStart: loadWorld slime done");
                worldWatcherIncrement(replayWorld, 1);
                replayer.getWatchingPlayer().teleport(LocationData.toLocation(replayer, spawnData.getLocation()));
                replayer.setPaused(false);
            });
        });
    }

}
