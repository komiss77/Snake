package ru.ostrov77.snake;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class Files {

    static Plugin plugin = Main.getPlugin(Main.class);
    static File customYml = new File( Files.plugin.getDataFolder() + "/arenas.yml" );
    static FileConfiguration customConfig = YamlConfiguration.loadConfiguration(Files.customYml);
    
    public static int priceColorChooser = 450;
    public static int creditOnWin = 30;
    public static int creditOnHitSomeoneOut = 5;
    public static int creditForParticipation = 2;
    public static int speedboostTimeTicks = 70;
    //public static double snakeDefaultSpeed = 0.45D;
    //public static double snakeBoostedSpeed = 0.65D;
    //public static double snakeSugerBoostedSpeed = 0.55D;
    public static int chanceOfSugerSpawning = 5;
    public static int maxSugarOnGround = 7;
    public static boolean allowHostileMobSpawningInArena;
    public static int fastSnakePrice = 200;
    public static int ferrariSnakePrice = 600;
    public static int ferrariKitBoosts = 10;
    public static int fastKitBoosts = 5;


    
    
    

    public static void loadAll() {
        try {
            loadBasic();
            Files.customConfig.options().copyDefaults(true);
            Files.plugin.getConfig().options().copyDefaults(true);
            Files.plugin.saveConfig();
            if (Files.customConfig.getConfigurationSection("Arenas") ==null)    return;
            
            ConfigurationSection cconf = Files.customConfig.getConfigurationSection("Arenas");
            
            cconf.getKeys(false).stream().forEach((name) -> {
                
//System.out.print(" ---- name = "+name);
                List<String> spawnlist = new ArrayList<>(Arrays.asList( (cconf.getString( name + ".spawns" )).split(",")));
                
                List<Location> allspawn = new ArrayList();
                
                for (String point : spawnlist) {
                    final Location loc = Utils.stringToLoc(point);
                    if (loc!=null) allspawn.add(loc);
//System.out.print(" ---- point = "+point+" loc="+loc);
                }
                //spawnlist.stream().forEach((point) -> {
                //    allspawn.add ( Utils.stringToLoc(point) );
               // });
                
                if (!allspawn.isEmpty()) {
                    AM.LoadArena(
                            allspawn,
                            name, 
                            Utils.stringToLoc ( cconf.getString ( name + ".arenaLobby" ) ),
                            Utils.stringToLoc ( cconf.getString ( name + ".boundsLow" ) ),
                            Utils.stringToLoc ( cconf.getString ( name + ".boundsHigh" ) ),
                            cconf.getInt( name + ".minPlayers" )
                    );
//System.out.print(" ---- +ARENA = "+name);
                } else {
                    Main.log_err("Арена "+name+" не загружена, нет точек спавна.");
                }
                
                
            });
            
        } catch (NullPointerException e) {
            Main.log_err("loadAll "+e.getMessage());
        }

    }
    
    
    
    
    
    public static void loadBasic() {
        
        Files.plugin.getConfig().addDefault("creditOnWin", 30);
        Files.plugin.getConfig().addDefault("priceColorChooser", 450);
        Files.plugin.getConfig().addDefault("creditOnHitSomeoneOut", 5);
        Files.plugin.getConfig().addDefault("creditForParticipation", 2);
        Files.plugin.getConfig().addDefault("fastSnakePrice", 200);
        Files.plugin.getConfig().addDefault("ferrariSnakePrice", 600);
        Files.plugin.getConfig().addDefault("ferrariKitBoosts", 10);
        Files.plugin.getConfig().addDefault("fastKitBoosts", 5);
        Files.plugin.getConfig().addDefault("speedboostTimeTicks", 70);
        Files.plugin.getConfig().addDefault("snakeDefaultSpeed", 0.45D);
        Files.plugin.getConfig().addDefault("snakeBoostedSpeed", 0.65D);
        Files.plugin.getConfig().addDefault("chanceOfSugerSpawning", 5);
        Files.plugin.getConfig().addDefault("maxSugarOnGround", 7);
        Files.plugin.getConfig().addDefault("snakeSugerBoostedSpeed", 0.55D);
        Files.plugin.getConfig().addDefault("allowHostileMobSpawningInArena", false);
        Files.plugin.getConfig().options().copyDefaults(true);
        Files.plugin.saveConfig();
        
        Files.creditOnWin = Files.plugin.getConfig().getInt("creditOnWin");
        Files.priceColorChooser = Files.plugin.getConfig().getInt("priceColorChooser");
        Files.creditOnHitSomeoneOut = Files.plugin.getConfig().getInt("creditOnHitSomeoneOut");
        Files.creditForParticipation = Files.plugin.getConfig().getInt("creditForParticipation");
        Files.ferrariSnakePrice = Files.plugin.getConfig().getInt("ferrariSnakePrice");
        Files.fastSnakePrice = Files.plugin.getConfig().getInt("fastSnakePrice");
        Files.ferrariKitBoosts = Files.plugin.getConfig().getInt("ferrariKitBoosts");
        Files.fastKitBoosts = Files.plugin.getConfig().getInt("fastKitBoosts");
        Files.speedboostTimeTicks = Files.plugin.getConfig().getInt("speedboostTimeTicks");
        //Files.snakeDefaultSpeed = Files.plugin.getConfig().getDouble("snakeDefaultSpeed");
        //Files.snakeBoostedSpeed = Files.plugin.getConfig().getDouble("snakeBoostedSpeed");
        Files.chanceOfSugerSpawning = Files.plugin.getConfig().getInt("chanceOfSugerSpawning");
        //Files.snakeSugerBoostedSpeed = Files.plugin.getConfig().getDouble("snakeSugerBoostedSpeed");
        Files.maxSugarOnGround = Files.plugin.getConfig().getInt("maxSugarOnGround");
        Files.allowHostileMobSpawningInArena = Files.plugin.getConfig().getBoolean("allowHostileMobSpawningInArena");

    }
    
    
    
    
    
    
    public static void saveAll() {

          //  SignsListener.savefile();

            Arena arena;
           // List allspawn = new ArrayList();
            //String spawns;
            
            for (Entry <String, Arena> e : AM.arenas.entrySet()) {
                
                arena = e.getValue();
                
                String spawns="";
                //allspawn.clear();
                List allspawn = new ArrayList();
                    for (Location spawnpoint : arena.getSpawns()) {
                        allspawn.add(Utils.locToString(spawnpoint));
                    }
                spawns = allspawn.toString().replaceAll("\\[|\\]|\\s", "");
               // ConfigurationSection cs = Files.customConfig.getConfigurationSection("AM." + arena.getName());
                Files.customConfig.set( "Arenas." + arena.arenaName+ ".arenaLobby" , Utils.locToString(arena.getArenaLobby()) );
                Files.customConfig.set( "Arenas." + arena.arenaName+ ".boundsLow" , Utils.locToString(arena.getBoundsLow()) );
                Files.customConfig.set( "Arenas." + arena.arenaName+ ".boundsHigh", Utils.locToString(arena.getBoundsHigh()) );
                Files.customConfig.set( "Arenas." + arena.arenaName+ ".spawns", spawns);
                Files.customConfig.set( "Arenas." + arena.arenaName+ ".minPlayers", arena.getMinPlayers() );
               
            }
                //cs.set( ".arenaLobby" , Utils.locToString(arena.getArenaLobby()) );
               // cs.set( ".boundsLow" , Utils.locToString(arena.getBoundsLow()) );
               // cs.set( ".boundsHigh", Utils.locToString(arena.getBoundsHigh()) );
 

            Files.plugin.saveConfig();
            Utils.saveCustomYml(Files.customConfig, Files.customYml);
    }
    
    
    
    
}
