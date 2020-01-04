package ru.ostrov77.snake.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.komiss77.Enums.UniversalArenaState;
import ru.ostrov77.snake.Main;

import ru.ostrov77.snake.Objects.Arena;






public class AM {

    private static HashMap <String, Arena> arenas;

public static void Init() {
    arenas = new HashMap();
}    
    
/*
public static void MySign() {
    
    
     (new BukkitRunnable() {
            @Override
            public void run() {

                final List<String> list = new ArrayList<>(am.arenas.keySet());
                    curr++;
            if (curr >= list.size() ) curr = 0; 
////////////////////////////////////////////////////////////////////////////////
                
                API.sendDataToServer("§b§l§oЗмейка<:>"
                        + "§5§l"+am.arenas.get(list.get(curr)).getName()+"<:>"
                        + "§1"+am.arenas.get(list.get(curr)).getPlayers().size()+" / "+am.arenas.get(list.get(curr)).getMaxPlayers()+"<:>"
                        + "§5§l"+am.arenas.get(list.get(curr)).getStateAsString()+"<:>"
                        + "5");
                
////////////////////////////////////////////////////////////////////////////////
            }
        }).runTaskTimer(Main.GetInstance(), 100L, 100L);      
}
*/ 
    
    
public static Arena createArena( Location firstspawn, String name ) {

        ArrayList spawns = new ArrayList();
        spawns.add(firstspawn);
        
        Arena arena = new Arena ( spawns, name, (Location) null, (Location) null, (Location) null, 2 );
        arenas.put(name,arena);

        return arena;
    }

public static boolean CanCreate ( Player p) {
    boolean can=true;
    for (Entry <String, Arena> e : arenas.entrySet()) {
        if (e.getValue().getSpawns().get(0).getWorld().getName().equals(p.getWorld().getName())) can=false;
    } 
    return can;
}

    
public static void LoadArena(List spawns, String name, Location arenaLobby, Location boundsLow, Location boundsHigh, int minPlayers ) {
//System.out.println("Load: "+name+" spawns:"+spawns +" arenaLobby:"+arenaLobby+" boundsLow:"+boundsLow+" boundsHigh:"+boundsHigh+" minPlayers:"+minPlayers);  
    Arena arena = new Arena( spawns, name, arenaLobby,  boundsLow, boundsHigh, minPlayers );
        arenas.put(name,arena);
        Main.sendBsignMysql(name, arena.getStateAsString(), "", UniversalArenaState.ОЖИДАНИЕ, 0);
      }
/*    
public AM reloadArena( List spawns, String name ) {
        AM arena = new AM( spawns, name,   new Location(Bukkit.getWorld("world"), 0.0D, 0.0D, 0.0D), new Location(Bukkit.getWorld("world"), 0.0D, 0.0D, 0.0D), new Location(Bukkit.getWorld("world"), 0.0D, 0.0D, 0.0D), 2 );
        this.arenas.put(name,arena);
        return arena;
    }
*/
    
public static HashMap<String,Arena> getAllArenas() { 
    return arenas;
    }
    

public static Arena getArena(String s) {
         if (  !arenas.containsKey(s) ) return null;
         else return arenas.get(s);
    }
     
     
public static boolean ArenaExist (String s) {
        return arenas.containsKey(s);
    }
    
 
public static void startArenaByName(String s) {
        Arena arena = getArena(s);
        arena.ForceStart();
    }

public static Arena getArenaByWorld(String w) {
    
    for (Entry <String, Arena> e : arenas.entrySet()) {
        if ( e.getValue().getArenaLobby().getWorld().getName().equals(w)) return e.getValue();
        
    }
      return null;
        
    }
    
    
public static void stopArena(String s, Player player) {
        Arena arena = getArena(s);
        if (arena != null) {
            arena.resetGame();
        }
    }

public static void stopAllArena() {
    
    arenas.entrySet().stream().forEach((e) -> {
        e.getValue().resetGame();
    });

}
   
    
    
    
    
    
    
    
    
    
    
    
    
    
public static void addPlayer(Player player, String s) {
    
        Arena arena = getArena(s);

        if (arena == null)  player.sendMessage("§сНет такой арены!");
        else if ( !arena.IsJonable() )   player.sendMessage("§4На арене идёт игра!");
        else if ( arena.getPlayers().size() + 1 > arena.getSpawns().size() )   player.sendMessage("§4Арена заполнена!");
        else if (isInArena(player))   player.sendMessage("§сВы уже в игре!");
        else if (arena.getName() == null)   player.sendMessage("§4Арена испортилась - нет названия..");
        else if (arena.getArenaLobby() == null)  player.sendMessage("§4Арена испортилась - нет лобби ожидания..");
        else if (arena.getSpawns() == null || arena.getSpawns().isEmpty())   player.sendMessage("§4Арена испортилась - нет стартовых точек..");
            
        else  arena.addPlayers(player);
                    
    }

    
 
    
public static boolean isInGame(Player p) {
    return arenas.entrySet().stream().anyMatch((e) -> (e.getValue().IsInThisGame(p)));
}

public static boolean isInArena(Player p) {
    return arenas.entrySet().stream().anyMatch((e) -> (e.getValue().isInThisArena(p)));
}

    
    
    
    
public static void GlobalPlayerExit(Player p ) {
    arenas.entrySet().stream().forEach((e) -> {  e.getValue().PlayerExit(p); });
    }



public static Arena getPlayersArena(Player p) {
    for (Entry <String, Arena> e : arenas.entrySet()) {
        if (e.getValue().isInThisArena(p)) return e.getValue();
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
        Arena arena = getArena(s);
        if (arena != null)  arena.addSpawn(location);
    }

    public static void setArenaLobby(Location location, String s) {
        Arena arena = getArena(s);
        arena.setArenaLobby(location);
    }
    

    public static void setBoundsHigh(Location location, String s) {
        Arena arena = getArena(s);
        if (arena != null)   arena.setBoundsHigh(location);
    }

    public static void setBoundsLow(Location location, String s) {
        Arena arena = getArena(s);
        if (arena != null)   arena.setBoundsLow(location);
    }


    
    
    
    
    
}
