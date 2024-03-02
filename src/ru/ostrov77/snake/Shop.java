package ru.ostrov77.snake;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.komiss77.modules.player.PM;



public class Shop {

    public static HashMap colorChooser = new HashMap();
    public static HashMap owned = new HashMap();
    public static HashMap selected = new HashMap();
    static File customYml = new File(Main.getInstance().getDataFolder() + "/shopdata.yml");
    static FileConfiguration customConfig = YamlConfiguration.loadConfiguration(Shop.customYml);

    
    private static void saveCustomYml(FileConfiguration fileconfiguration, File file) {
        try { fileconfiguration.save(file);
        } catch (IOException ioexception) {}
    }

    
    
    public static void startup() {
        HashMap hashmap = new HashMap();
        String s;
        Iterator iterator;

        try {
            iterator = Shop.customConfig.getConfigurationSection("colorChooser").getKeys(false).iterator();
            while (iterator.hasNext()) {
                s = (String) iterator.next();
                hashmap.put(UUID.fromString(s), Shop.customConfig.getBoolean("colorChooser." + s));
            }
            if ( hashmap.size() > 0)   Shop.colorChooser = hashmap;
        } catch (NullPointerException nullpointerexception) {}

        try {
            hashmap = new HashMap();
            iterator = Shop.customConfig.getConfigurationSection("speedboosts").getKeys(false).iterator();
            while (iterator.hasNext()) {
                s = (String) iterator.next();
                List list = Shop.customConfig.getList("speedboosts." + s);

                hashmap.put(UUID.fromString(s), list);
            }
            if ( hashmap.size() > 0)    Shop.owned = hashmap;
        } catch (NullPointerException nullpointerexception1) {}

    }

    
    
    
    public static boolean findPlayerInOwned(UUID uuid, String s) {
        Iterator iterator = Shop.owned.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((UUID) entry.getKey()).toString().equals(uuid.toString())) {
                List list = (List) entry.getValue();
                
                Iterator iterator1 = list.iterator();
                while (iterator1.hasNext()) {
                    String s1 = (String) iterator1.next();
                    if (s1.contains(s))    return true;
                }
            }
        }

        return false;
    }

    
    public static boolean findPlayerInColorChooser(String nik) {
        return PM.getOplayer(nik).hasGroup("warior");
     /*   Iterator iterator = Shop.colorChooser.entrySet().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext())     return false;
            entry = (Entry) iterator.next();
        } while (!((UUID) entry.getKey()).equals(uuid) || !((Boolean) entry.getValue()));

        return true;*/
    }

    
    
    public static void disable() {
        Iterator iterator = Shop.colorChooser.entrySet().iterator();

        Entry entry;

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            Shop.customConfig.set("colorChooser." + entry.getKey(), entry.getValue());
        }

        iterator = Shop.owned.entrySet().iterator();

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            Shop.customConfig.set("speedboosts." + entry.getKey(), entry.getValue());
        }

        saveCustomYml(Shop.customConfig, Shop.customYml);
    }

    
    
    public boolean purchaseColorChooser(Player player) {
       // if (Shop.economy == null) {
            return PM.getOplayer(player).hasGroup("warior");
       /* } else if (Shop.economy.getBalance(player) < (double) FileManager.priceColorChooser) {
            return false;
        } else {
            EconomyResponse economyresponse = Shop.economy.withdrawPlayer(player, (double) FileManager.priceColorChooser);

            if (economyresponse.transactionSuccess()) {
                Shop.colorChooser.put(player.getUniqueId(), Boolean.valueOf(true));
                return true;
            } else {
                return false;
            }
        }*/
    }

    public boolean doesHaveColorChooser(Player player) {
        return Shop.colorChooser.containsKey(player.getUniqueId());
    }
}
