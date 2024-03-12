package ru.ostrov77.snake;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.events.FigureActivateEntityEvent;
import ru.komiss77.events.FigureClickEvent;
import ru.komiss77.modules.games.GM;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.world.Cuboid;
import ru.komiss77.utils.TCUtils;


public class SnakeLst implements Listener {

   
    private static Sheep sh1, sh2;
    private static Location loc1, loc2;
    private static final Cuboid cuboid;
    private static BukkitTask task;
    private static World world;
    
    static {
        cuboid = new Cuboid (3, 3, 3);
    }
    
    @EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFigureActivateEntity (final FigureActivateEntityEvent e) {
        if (e.getFigure().getTag().startsWith("snake")) {
            if (e.getFigure().getTag().equals("snake1")) {
                sh1 = (Sheep) e.getFigure().entity;
                sh1.setNoDamageTicks(Integer.MAX_VALUE);
                sh1.setAI(true);
                sh1.setGravity(true);
                loc1 = sh1.getLocation();
                Bukkit.getMobGoals().removeAllGoals(sh1);
                if (world == null) {
                    cuboid.allign(e.getFigure().spawnLoc);
                    world = sh1.getWorld();
                }
            } else if (e.getFigure().getTag().equals("snake2")) {
                sh2 = (Sheep) e.getFigure().entity;
                sh2.setNoDamageTicks(Integer.MAX_VALUE);
                sh2.setAI(true);
                sh2.setGravity(true);
                loc2 = sh2.getLocation();
                Bukkit.getMobGoals().removeAllGoals(sh1);
                if (world == null) {
                    cuboid.allign(e.getFigure().spawnLoc);
                    world = sh2.getWorld();
                }
            }
            
            if (sh1 == null || sh2==null) return;
            
            if (!sh1.getLocation().getChunk().equals(sh2.getLocation().getChunk())) {
                Ostrov.log_warn("Фигуры змейки в разных чанках!");
                return;
            }

            if (task!=null) {
                task.cancel();
                task = null;
            }
            

            task=new BukkitRunnable() {
                int s=0;
                boolean dir = true;
                
                @Override
                public void run() {

                    for (final Player p : world.getPlayers()) {
                        if (cuboid.contains(p.getLocation())) {
                            GM.randomPlay(p, Game.SN, Ostrov.MOT_D);
                            //continue;
                        }
                    }

                    if (sh1 == null || sh1.isDead() || !sh1.isValid()) {
                        return;
                    }
                    
                    if (sh2==null || sh2 == null || sh2.isDead() || !sh2.isValid()) {
                        return;
                    }
                    
                    if (s%5==0) {
                        if (dir) {
                            sh1.getPathfinder().moveTo(loc2);
                            sh2.getPathfinder().moveTo(loc1);
                        } else {
                            sh1.getPathfinder().moveTo(loc1);
                            sh2.getPathfinder().moveTo(loc2);
                        }
                        dir = !dir;
                    }
                    
                    sh1.setColor(DyeColor.values()[Ostrov.random.nextInt(16)]);
                    sh2.setColor(DyeColor.values()[Ostrov.random.nextInt(16)]);
                    sh1.customName(Component.text(TCUtils.randomColor() + "ЗМЕЙКА"));
                    sh1.customName(Component.text(TCUtils.randomColor() + "ЗМЕЙКА"));
                    
                   s++;

                }
            }.runTaskTimer(Ostrov.instance, 1, 11);
                    
        }
    }

    
    @EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFigureClick (final FigureClickEvent e) {
        if (e.getFigure().getTag().equals("snake1") || e.getFigure().getTag().equals("snake2")) {
            e.getPlayer().sendMessage("§bПодходи ближе, поиграем!");
        }
    }     
    




    
    @EventHandler (priority = EventPriority.MONITOR)
    public void PlayerQuitEvent (PlayerQuitEvent e) {
        final Arena a = AM.getArena(e.getPlayer());
        if (a!=null) {
            a.removePlayer(e.getPlayer());
        }
    }    
     
    
    @EventHandler
    public void onHostileSpawn(CreatureSpawnEvent e) {
        final Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
        if (a!=null && e.getEntity().getType() != EntityType.SHEEP ) e.setCancelled(true);
    }

    
    //@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
   // public void onRemove (final EntityRemoveFromWorldEvent e) {
    //    Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
    //    if (a!=null && a.state == GameState.ИГРА) {
//Ostrov.log("onRemove!!!! "+e.getEntity().getType());
     //   }
    //}  
        
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDismount(final EntityDismountEvent e) {
//Ostrov.log("onDismount getEntity="+e.getEntity()+" getDismounted="+e.getDismounted()+" isCancellable?"+e.isCancellable());
        if (e.getEntityType() == EntityType.PLAYER && e.getDismounted().getType() == EntityType.SHEEP) {
            final Player p = (Player) e.getEntity();
            final Arena arena = AM.getArena(p);
//Ostrov.log("p="+p+" arena="+arena);
            if (arena != null) {
                e.setCancelled(true);
//Ostrov.log("onDismount setCancelled!!!!");
            }

        }
    }
    
        
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemMerge (final ItemMergeEvent e) {
        final Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
        if (a!=null && a.state != GameState.ОЖИДАНИЕ) {
             e.setCancelled(true);
        }
    }    
    


     
    @EventHandler
    public void Damage (EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;
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

     
    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            final Arena arena = AM.getArena(p);
            if (arena != null) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }
    }
    
     
    /*
    @EventHandler( ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(final PlayerInteractEvent e) {
        if ( (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                && e.getItem()!=null && e.getMaterial() == Material.FEATHER) {
            Player p = e.getPlayer();
            final Arena arena = AM.getArena(p);
            if (arena != null && arena.state==GameState.ИГРА) {
                Tail snake = arena.players.get(p.getName());
                if (snake!=null ) {
                    snake.speedBoost(70, Material.FEATHER);
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
    }*/

    
    
    @EventHandler( ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickup(final EntityPickupItemEvent e) {
        if (e.getEntityType()!=EntityType.PLAYER) return;
        Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);
        if (arena != null) {
            final Item i = e.getItem();
            final Tail snake = arena.players.get(p.getName());
            if (snake!=null ) {
                if (arena.state==GameState.ИГРА) {
                    if (i.getItemStack().getType() == Material.SUGAR) {
                        //if (i.isGlowing()) return;
                        pickupEffect(i);
                        snake.speedBoost(70, Material.SUGAR);
                    }// else if (i.getItemStack().getType() == Material.ENCHANTED_GOLDEN_APPLE) {
                    //    pickupEffect(i);
                    //    snake.addSheep(p);
                    //}
                } else if (arena.state==GameState.ФИНИШ) { //золотишко подбирается только на финише
                    if (i.getItemStack().getType() == Material.SUNFLOWER) {
                        //if (i.isGlowing()) return;
                        pickupEffect(i);
                        arena.pickupGold++;
                        snake.coin++;
                        p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 9.9F);
                        Oplayer op;
                        for (Player pl : arena.getPlayers()) {
                            op = PM.getOplayer(pl);
                            op.score.getSideBar().update(p.getName(), snake.getChatColor()+p.getName()+" §6§l"+snake.coin);
                        }
                    }
                }
            }
            e.setCancelled(true);
        }
    }

    private static void pickupEffect(final Item i) {
        //i.setGlowing(true);
        i.setPickupDelay(Integer.MAX_VALUE);
        i.setVelocity(new Vector(0, 1.0, 0));
        Ostrov.sync( ()->i.remove(), 6);
        i.getWorld().playEffect(i.getLocation(), Effect.BOW_FIRE, 5);  
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

    
   // @EventHandler
  //  public void onPlayerDeath(PlayerDeathEvent pde) {
  //     final Player p = pde.getEntity(); 
  ////     p.teleport (Bukkit.getWorlds().get(0).getSpawnLocation());
  //  }   
    
        
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
