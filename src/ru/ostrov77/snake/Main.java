package ru.ostrov77.snake;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.modules.games.GM;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.SmartInventory;



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

        AM.loadAll();
        
        Bukkit.getServer().getPluginManager().registerEvents(new SnakeLst(), this);
        instance.getCommand("snake").setExecutor(new SnakeCmd());
        
        final ItemStack is1=new ItemBuilder(Material.ORANGE_GLAZED_TERRACOTTA)
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
                        SmartInventory
                            .builder()
                            //.id(op.nik+op.menu.section.name())
                            .provider(new ColorChoiceMenu())
                            .size(2, 9)
                            .title("§6Выбор цвета змейки")
                            .build()
                            .open(e.getPlayer());
                    }
                }
            )
            .create();
        
        Bukkit.getLogger().info("Змейка готова!");
        
    }


    
    

    
@Override
    public void onDisable() {

        if (AM.save) {
            AM.saveAll();
        }
        
        AM.arenas.values().stream().forEach( ar -> {
            ar.resetGame();
            GM.sendArenaData(
                    Game.SN, 
                    ar.arenaName, 
                    GameState.ВЫКЛЮЧЕНА, 
                    0, 
                    "§4█████████", 
                    "§2§l§oЗмейка",
                    "§5"+ar.arenaName, 
                    "§4█████████"
            );
        });
        
    }

    
    

    
    

    

public static void log_ok(String s) {   Bukkit.getConsoleSender().sendMessage("§fSnake: §2"+ s); }
public static void log_err(String s) {   Bukkit.getConsoleSender().sendMessage("§fSnake: §c"+ s); }
    


    
}
