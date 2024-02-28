package ru.ostrov77.snake;
/*

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.TCUtils;




public class GuiListener implements Listener {
    
    public static ItemStack colorChoice;         
    
    public GuiListener(Main aThis) {
        
        colorChoice = new ItemBuilder(Material.NAME_TAG)
                .name("§aВыбор цвета")
                .build();

    }


    @EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = false)
    public static void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        final ItemStack item = e.getItem();
        
        if (AM.isInGame(e.getPlayer()) ) return;


        if (item == null || !item.hasItemMeta() ||  !item.getItemMeta().hasDisplayName() ) return;

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

        final String itemName = item.getItemMeta().getDisplayName();


        if ( itemName.equals (colorChoice.getItemMeta().getDisplayName())) {
            e.setCancelled(true);
            if ( !PM.getOplayer(p).hasGroup("warior") ) {
                p.sendMessage("§cУ вас не куплена привилегия Воин!");
                return;
            }
             
            Inventory inventory = Bukkit.createInventory( null, 18, colorChoice.getItemMeta().getDisplayName());

            for (byte col=0; col<=15; col++) {
                ItemStack is = new ItemStack(Material.WHITE_WOOL);
                is = TCUtils.changeColor(is, col);
                inventory.addItem(is);
            }

            p.openInventory(inventory);
        }

            }

    }
    
    
  
    
    
    @EventHandler(  priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
            
        if (AM.isInGame(player)) {
            e.setCancelled(true);
            player.closeInventory();
            return;
        }
                
        final ItemStack item = e.getCurrentItem();

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            final String itemName = item.getItemMeta().getDisplayName();
            if ( itemName.equals (colorChoice.getItemMeta().getDisplayName()) ) {
                  //  itemName.equals (toLobby.getItemMeta().getDisplayName()) ) {
                e.setCancelled(true);
                return;
            }
        }
        
        
        if ( item == null || item.getType() == null ) return;
        
                    
        if ( e.getView().getTitle().equals("§aВыбор цвета") ) {
            e.setCancelled(true);
            if ( !item.getType().toString().endsWith("_WOOL") ) return;

            DyeColor dc=DyeColor.valueOf( item.getType().toString().replace("_WOOL",""));

            AM.getArena(player).SetSheepColor( player.getName(), dc);

            //player.sendMessage("§fВаши овцы будут "+Main.EnumColor( itemstack.getData().getData() ) +"§lТАКОГО §fцвета!");
            player.sendMessage("§fЦвет ваших овец будет "+TCUtils.dyeDisplayName(dc));

            player.closeInventory();
        }

                    
                    
    }
   

    
    
    
    @EventHandler
    public void cancelMove(InventoryDragEvent event) {
        if ( ((Player) event.getWhoClicked()).isOp()) return;
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
    }
    
    
    
    
}

*/
   
    