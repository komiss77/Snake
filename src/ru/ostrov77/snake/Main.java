package ru.ostrov77.snake;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.GameState;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.player.profile.Section;
import ru.komiss77.utils.ItemBuilder;



public class Main extends JavaPlugin implements Listener {

    private static Main instance;       
    public static MenuItem colorChoice;
            
@Override
    public void onLoad() {
        instance = this;
    }

    public static final Main getInstance() { 
        return Main.instance; 
    }
       
       
@Override
    public void onEnable() {

        log_ok("Super Snake startup....");
        
        if (!this.getDataFolder().exists())  this.getDataFolder().mkdir();
        else if (this.getConfig() == null)   this.saveDefaultConfig();
        else {
            Files.loadAll();
            Messages.loadAll();
        }

        Shop.startup();
        
        Bukkit.getServer().getPluginManager().registerEvents(new SnakeLst(), this);
        
        final ItemStack is1=new ItemBuilder(Material.NAME_TAG)
            .name("§aВыбор цвета")
            .build();
        colorChoice = new MenuItemBuilder("colorChoice", is1)
            .slot(0)
            .giveOnJoin(false)
            .giveOnRespavn(false)
            .giveOnWorld_change(false)
            .anycase(true)
            .canDrop(false)
            .canPickup(false)
            .canMove(false)
            .interact( e -> {
                    if (e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        //
                    }
                }
            )
            .create();
        Bukkit.getLogger().info("Змейка готова!");
        
    }


    
    

    
@Override
    public void onDisable() {
////////////////////////////////////////////////////////////////////////////////

            AM.arenas.values().stream().forEach((ar) -> {
                ApiOstrov.sendArenaData(
                        ar.arenaName,
                        GameState.ВЫКЛЮЧЕНА,
                        "§4█████████",
                        "§2§l§oЗмейка",
                        "§5"+ar.arenaName,
                        "§4█████████",
                        "выключена",
                        0
                );
            });
                
                 
////////////////////////////////////////////////////////////////////////////////

        Shop.disable();
        Files.saveAll();
        AM.stopAllArena();
      //  EntityTypeRegistry.unregisterEntities();
    }

    
    
    
@Override
    public boolean onCommand(CommandSender commandsender, Command command, String s, String[] astring) {
            boolean flag = Commands.handleCommand(commandsender, command, s, astring);
            return flag;
    }
    
    

    

public static void log_ok(String s) {   Bukkit.getConsoleSender().sendMessage("§fSnake: §2"+ s); }
public static void log_err(String s) {   Bukkit.getConsoleSender().sendMessage("§fSnake: §c"+ s); }
    
    public static void sendBsignMysql(final String name, final String line2, final String line3, final GameState state, final int players) {
        ////////////////////////////////////////////////////////////////////////////////
             ApiOstrov.sendArenaData(
                    name,
                        GameState.РАБОТАЕТ,
                    "§2§l§oЗмейка",
                    "§5"+name,
                    line2,
                    line3,
                    "работает",
                    players
            );
        ////////////////////////////////////////////////////////////////////////////////

    }
    public static void sendBsignChanel(final String name, final String line2, final String line3, final GameState state, final int players) {
        ////////////////////////////////////////////////////////////////////////////////
            ApiOstrov.sendArenaData(
                    name,
                    state,
                    "§2§l§oЗмейка",
                    "§5"+name,
                    line2,
                    line3,
                    "работает",
                    players
            );
        ////////////////////////////////////////////////////////////////////////////////

    }

    
}
