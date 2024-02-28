package ru.ostrov77.snake;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.komiss77.enums.GameState;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.ostrov77.minigames.MG;

public class AM {

    public static final CaseInsensitiveMap<Arena> arenas;
    public static final List<File> songs = new ArrayList<>();

    static {
        arenas = new CaseInsensitiveMap();
        File[] files = new File(Main.getInstance().getDataFolder().getPath() + "/songs/").listFiles();
        for (File f : files) {
            if (f.getName().contains(".nbs")) {
                songs.add(f);
            }
        }   
    }

    public static Arena createArena(Location firstspawn, String name) {

        ArrayList spawns = new ArrayList();
        spawns.add(firstspawn);

        Arena arena = new Arena(spawns, name, (Location) null, (Location) null, (Location) null, 2);
        arenas.put(name, arena);
        MG.arenas.put(name, arena);
        return arena;
    }

    public static boolean CanCreate(Player p) {
        boolean can = true;
        for (Entry<String, Arena> e : arenas.entrySet()) {
            if (e.getValue().getSpawns().get(0).getWorld().getName().equals(p.getWorld().getName())) {
                can = false;
            }
        }
        return can;
    }

    public static void LoadArena(List spawns, String name, Location arenaLobby, Location boundsLow, Location boundsHigh, int minPlayers) {
//System.out.println("Load: "+name+" spawns:"+spawns +" arenaLobby:"+arenaLobby+" boundsLow:"+boundsLow+" boundsHigh:"+boundsHigh+" minPlayers:"+minPlayers);  
        Arena arena = new Arena(spawns, name, arenaLobby, boundsLow, boundsHigh, minPlayers);
        arenas.put(name, arena);
        MG.arenas.put(name, arena);
        Main.sendBsignMysql(name, arena.state.displayColor+arena.state.name(), "", GameState.ОЖИДАНИЕ, 0);
    }


    public static Arena getArena(String s) {
        return arenas.get(s);
    }

    public static boolean ArenaExist(String s) {
        return arenas.containsKey(s);
    }


    public static Arena getArenaByWorld(String w) {
        for (Arena a : arenas.values()) {
            if (a.getArenaLobby().getWorld().getName().equals(w)) {
                return a;
            }

        }
        return null;
    }

    public static void stopArena(String s, Player player) {
        Arena arena = AM.getArena(s);
        if (arena != null) {
            arena.resetGame();
        }
    }

    public static void stopAllArena() {
        arenas.entrySet().stream().forEach((e) -> {
            e.getValue().resetGame();
        });
    }

    public static Arena getArena(Player p) {
        for (Entry<String, Arena> e : arenas.entrySet()) {
            if (e.getValue().hasPlayer(p)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static boolean isInside(Location location, Vector vector, Vector vector1) {
        int i = Math.min(vector.getBlockX(), vector1.getBlockX());
        int j = Math.min(vector.getBlockZ(), vector1.getBlockZ());
        int k = Math.max(vector.getBlockX(), vector1.getBlockX());
        int l = Math.max(vector.getBlockZ(), vector1.getBlockZ());

        return location.getX() >= (double) i && location.getX() <= (double) k && location.getZ() >= (double) j && location.getZ() <= (double) l;
    }

    public static void addSpawn(Location location, String s) {
        Arena arena = AM.getArena(s);
        if (arena != null) {
            arena.addSpawn(location);
        }
    }

    public static void setArenaLobby(Location location, String s) {
        Arena arena = AM.getArena(s);
        arena.setArenaLobby(location);
    }

    public static void setBoundsHigh(Location location, String s) {
        Arena arena = AM.getArena(s);
        if (arena != null) {
            arena.setBoundsHigh(location);
        }
    }

    public static void setBoundsLow(Location location, String s) {
        Arena arena = AM.getArena(s);
        if (arena != null) {
            arena.setBoundsLow(location);
        }
    }


}
