package ru.ostrov77.snake;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import ru.komiss77.modules.world.WorldManager;

public class Utils {

    public static Location stringToLoc(String s) {
        if (s != null && !s.trim().equals("")) {
            String[] astring = s.split("<>");

            if (astring.length == 4) {
                
                final String worldName = astring[0];
                World world = Bukkit.getServer().getWorld(worldName);
                
                if (world==null && WorldManager.load(Bukkit.getConsoleSender(), worldName, World.Environment.NORMAL, WorldManager.Generator.Empty)!=null) {
                    Main.log_ok("§eЗагружен мир "+worldName+" для арены ");
                    world=Bukkit.getWorld(worldName);
               }
                
                if (world==null) return null;
                
                Double d = Double.parseDouble(astring[1]);
                Double double1 = Double.parseDouble(astring[2]);
                Double double2 = Double.parseDouble(astring[3]);

                return new Location(world, d, double1, double2);
                
             }
        } 
        
        return null;
    }




    public static void saveCustomYml(FileConfiguration fileconfiguration, File file) {
        try {
            fileconfiguration.save(file);
        } catch (IOException ioexception) {
           // ioexception.printStackTrace();
        }

    }

    public static String locToString(Location location) {
        return location == null ? "" : location.getWorld().getName() + "<>" + location.getBlockX() + "<>" + location.getBlockY() + "<>" + location.getBlockZ();
    }




}
