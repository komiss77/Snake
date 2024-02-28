package ru.ostrov77.snake;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.GameState;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;


public class SnakeLst implements Listener {

    
    @EventHandler (priority = EventPriority.MONITOR)
    public void PlayerQuitEvent (PlayerQuitEvent e) {
        final Arena a = AM.getArena(e.getPlayer());
        if (a!=null) {
            a.removePlayer(e.getPlayer());
        }
    }    
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemMerge (final ItemMergeEvent e) {
        Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
        if (a!=null && a.state != GameState.ОЖИДАНИЕ) {
             e.setCancelled(true);
        }
    }    
    

     
    @EventHandler
    public void onHostileSpawn(CreatureSpawnEvent e) {
        if ( !e.getLocation().getWorld().getName().equals(Bukkit.getServer().getWorlds().get(0).getName()) && e.getEntity().getType() != EntityType.SHEEP )
            e.setCancelled(true);

    }

     
    @EventHandler
    public void Damage (EntityDamageEvent e) {

        final Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);

        if (arena != null) {
            e.setDamage(0);
            p.setFallDistance(0);

            switch (arena.state) {
                
                case ОЖИДАНИЕ, СТАРТ, ФИНИШ ->
                    p.teleport(arena.arenaLobby);
                    

                case ЭКИПИРОВКА, ИГРА -> {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID ) {
                        arena.removePlayer(p);
                    } else if ( e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        //
                    }
                }

                default ->
                    p.teleport(p.getWorld().getSpawnLocation());
            }
        }

    }

     
    @EventHandler
    public void damm(EntityDamageByEntityEvent e) {
            if (!e.getDamager().isOp()) e.setCancelled(true);
    }
    

     
    
    @EventHandler( priority = EventPriority.MONITOR)
    public void speedBoostManager(PlayerInteractEvent e) {
        if ( (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem()!=null && e.getMaterial() == Material.FEATHER) {
            Player p = e.getPlayer();
            final Arena arena = AM.getArena(p);
            if (arena != null && arena.state==GameState.ИГРА) {
                Snake snake = arena.players.get(p.getName());
                if (snake!=null ) {
                    snake.speedBoost = Files.speedboostTimeTicks;
                }
                if (e.getItem().getAmount()==1) {
                    if (e.getHand()==EquipmentSlot.HAND) p.getInventory().getItemInMainHand().setType(Material.AIR);
                    else p.getInventory().getItemInOffHand().setType(Material.AIR);
                } else {
                    if (e.getHand()==EquipmentSlot.HAND) p.getInventory().getItemInMainHand().setAmount(e.getItem().getAmount()-1);
                    else p.getInventory().getItemInOffHand().setAmount(e.getItem().getAmount()-1);
                }
            }
        }
    }

    
    
    @EventHandler
    public void sugarPickupEventSpeed(EntityPickupItemEvent e) {
        if (e.getEntityType()!=EntityType.PLAYER) return;
        Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);
        if (arena != null) {
            final Item i = e.getItem();
            Snake snake = arena.players.get(p.getName());
            if (snake!=null ) {
                if (arena.state==GameState.ИГРА) {
                    if (i.getItemStack().getType() == Material.SUGAR) {
                        if (i.isGlowing()) return;
                        i.setVelocity(new Vector(0, 1.0, 0));
                        Ostrov.sync( ()->i.remove(), 5);
                        i.setGlowing(true);
                        i.getWorld().playEffect(i.getLocation(), Effect.BOW_FIRE, 5);
                        snake.speedBoost = Files.speedboostTimeTicks;
                        snake.sugarBoosted = true;
                    }
                } else if (arena.state==GameState.ФИНИШ) { //золотишко подбирается только на финише
                    if (i.getItemStack().getType() == Material.SUNFLOWER) {
                        if (i.isGlowing()) return;
                        i.setVelocity(new Vector(0, 1.0, 0));
                        Ostrov.sync( ()->i.remove(), 5);
                        i.setGlowing(true);
                        i.getWorld().playEffect(i.getLocation(), Effect.BOW_FIRE, 5);
                        arena.pickupGold++;
                        p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 9.9F);
                        p.setLevel(p.getLevel()+1);
                        Oplayer op;
                        for (Player pl : arena.getPlayers()) {
                            op = PM.getOplayer(pl);
                            op.score.getSideBar().update(p.getName(), arena.getChatColor(p.getName()) + p.getName() + " §7"+p.getLevel());
                        }
                    }
                }
            }
            e.setCancelled(true);
        }
    }



    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }

    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }

    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
       final Player p = pde.getEntity(); 
       p.teleport (Bukkit.getWorlds().get(0).getSpawnLocation());
    }   
    
        
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }  
   

    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }
 
   
/*
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onOpenInv (InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

            if ( AM.isInGame(player) ) {
                e.setCancelled(true);
                player.closeInventory();
                player.sendMessage("Инвентарь заблокирован!");
            }

    }
*/

    
}
