package me.jumper251.replay.dev.mrflyn.extended;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;


public class Bw1058Support {

    public Bw1058Support(){
    }

    public String getRealWorldName(String worldName){

        IArena arena = BedWars.getAPI().getArenaUtil().getArenaByIdentifier(worldName);

        if (arena==null)return worldName;

        return arena.getArenaName();

    }


}
