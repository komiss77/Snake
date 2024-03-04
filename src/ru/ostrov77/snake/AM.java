package ru.ostrov77.snake;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Config;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.GameState;
import ru.komiss77.objects.CaseInsensitiveMap;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.OstrovConfig;
import ru.komiss77.utils.OstrovConfigManager;
import ru.ostrov77.minigames.MG;

public class AM {

    public static final OstrovConfigManager manager;
    private static final OstrovConfig config;
    public static final CaseInsensitiveMap<Arena> arenas;
    public static final List<File> songs = new ArrayList<>();
    public static boolean save;
    
    static {
        manager = new OstrovConfigManager(Main.getInstance());
        config = manager.getNewConfig("config.yml");
        arenas = new CaseInsensitiveMap();
        File[] files = new File(Main.getInstance().getDataFolder().getPath() + "/songs/").listFiles();
        for (File f : files) {
            if (f.getName().contains(".nbs")) {
                songs.add(f);
            }
        }   
    }


    
    public static void saveAll() {
            
        for ( Arena a : AM.arenas.values()) {

            config.set( "arenas." + a.arenaName+ ".arenaLobby" , LocationUtil.toDirString(a.arenaLobby) );
            config.set( "arenas." + a.arenaName+ ".boundsLow" , LocationUtil.toDirString(a.boundsLow) );
            config.set( "arenas." + a.arenaName+ ".boundsHigh", LocationUtil.toDirString(a.boundsHigh) );

            final List<String> list = new ArrayList<>();
            for (Location spawnpoint : a.spawns) {
                list.add(LocationUtil.toDirString(spawnpoint));
            }
            config.set("arenas." + a.arenaName+ ".spawnPoints", ApiOstrov.toString(list, false));
        }
        config.saveConfig();
    }



    public static void loadAll() {

        if (config.getConfigurationSection("arenas")!=null) {
            config.getConfigurationSection("arenas").getKeys(false).stream().forEach( arenaName -> {
                
                final List<Location> spawnPoints = new ArrayList<>();
                for (String s : config.getString("arenas."+arenaName+".spawnPoints").split(",")) {
                    spawnPoints.add(LocationUtil.stringToLoc(s, true, true));
                }
                
                final Arena arena = new Arena(
                        spawnPoints, 
                        arenaName,
                        LocationUtil.stringToLoc(config.getString("arenas."+arenaName+".arenaLobby"), false, true),
                        LocationUtil.stringToLoc(config.getString("arenas."+arenaName+".boundsLow"), false, true),
                        LocationUtil.stringToLoc(config.getString("arenas."+arenaName+".boundsHigh"), false, true)
                );
                
                if (spawnPoints.isEmpty() || arena.arenaLobby==null || arena.boundsLow==null || arena.boundsHigh==null) {
                    Main.log_err("Арена "+arenaName+" - проблема с локациями.");
                } else {
                    arena.state = GameState.ОЖИДАНИЕ;
                }
                arena.sendArenaData();
                arenas.put(arenaName, arena);
                MG.arenas.put(arenaName, arena);
            });
        }

    }


    public static Arena createArena(Location firstspawn, String name) {
        Arena arena = new Arena(Arrays.asList(firstspawn), name, (Location) null, (Location) null, (Location) null);
        arenas.put(name, arena);
        MG.arenas.put(name, arena);
        save = true;
        return arena;
    }



    public static Arena getArena(String s) {
        return arenas.get(s);
    }

    public static boolean ArenaExist(String s) {
        return arenas.containsKey(s);
    }


    public static Arena getArenaByWorld(String w) {
        for (Arena a : arenas.values()) {
            if (a.arenaLobby.getWorld().getName().equals(w)) {
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



}
