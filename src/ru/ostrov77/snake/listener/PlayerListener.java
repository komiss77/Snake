package ru.ostrov77.snake.listener;

import me.clip.deluxechat.DeluxeChat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.ApiOstrov;

import ru.komiss77.Ostrov;
import ru.komiss77.enums.Data;
import ru.komiss77.events.BsignLocalArenaClick;
import ru.komiss77.events.BungeeDataRecieved;
import ru.komiss77.events.FriendTeleportEvent;
import ru.komiss77.modules.player.PM;
import ru.ostrov77.snake.Main;
import ru.ostrov77.snake.Manager.AM;
import ru.ostrov77.snake.Manager.Files;
import ru.ostrov77.snake.Objects.Arena;
import ru.ostrov77.snake.Objects.GameState;
import ru.ostrov77.snake.Objects.Snake;




public class PlayerListener implements Listener {

    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemMerge (final ItemMergeEvent e) {
        Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
        if (a!=null) {
//System.out.println(" ---- onItemMerge --- "+e.getEntity().getItemStack());
            if (a.getState()!=GameState.WAITING) e.setCancelled(true);
        }
        
        
    }    
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBsignLocalArenaClick (final BsignLocalArenaClick e) {
        AM.addPlayer( e.player, e.arenaName);
//System.out.println(" ---- BsignLocalArenaClick --- "+e.player.getName()+" "+e.arenaName);
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBungeeStatRecieved(final BungeeDataRecieved e) {  
        final String wantToArena = e.getOplayer().getDataString(Data.WANT_ARENA_JOIN);
        if (wantToArena.isEmpty() || wantToArena.equals("any")) return;
         AM.addPlayer( e.getPlayer(), wantToArena);
    }    
    

    
    @EventHandler
    public void FriendTeleport(FriendTeleportEvent e) {
        e.Set_canceled(true, "§e!");
    }
   
    
    
    
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        JoinToLobby (e.getPlayer());
        if (!e.getPlayer().hasPlayedBefore()) {
            ApiOstrov.sendTitle(e.getPlayer(), "§2Игра §aЗмейка", "§fВыберите арену с помощью табличек!");
        }
    }

    public void JoinToLobby (Player p) {
        if (!p.isOp()) p.setGameMode(GameMode.ADVENTURE);
        p.setFireTicks(0);
        
        p.getInventory().clear();
        p.getInventory().setItem(7, GuiListener.toLobby);
        p.updateInventory();
        
        p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    }
  
    
    
    
     
    @EventHandler
    public void onHostileSpawn(CreatureSpawnEvent e) {
        
        if ( !e.getLocation().getWorld().getName().equals(Bukkit.getServer().getWorlds().get(0).getName()) && e.getEntity().getType() != EntityType.SHEEP )
            e.setCancelled(true);
        
        if ( e.getEntity() instanceof Monster ) {
            e.setCancelled(true);
        }
        
    }
   
    
    
    
     
    @EventHandler
    public void PlayerQuitEvent (PlayerQuitEvent playerquitevent) {
        Player player = playerquitevent.getPlayer();
        AM.GlobalPlayerExit (player);
    }

 
    @EventHandler
    public void GoToLobby (PlayerChangedWorldEvent e) {
        if (e.getPlayer().getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) JoinToLobby(e.getPlayer());
    }




    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onOpenInv (InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

            if ( AM.isInGame(player) ) {
                e.setCancelled(true);
                player.closeInventory();
                player.sendMessage("Инвентарь заблокирован!");
            }

    }


                
                
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWorldChange (final PlayerChangedWorldEvent e) {
//System.out.println("PlayerChangedWorldEvent from="+e.getFrom().getName());
        //final Player p = e.getPlayer();
        new BukkitRunnable() {
            final Player p = e.getPlayer();
            @Override
            public void run() {
                switchLocalGlobal(p, true);
                perWorldTabList(e.getPlayer());
                if (PM.nameTagManager!=null && !e.getPlayer().getWorld().getName().equals("lobby")) {  
                    PM.nameTagManager.setNametag(e.getPlayer().getName(), "", "");
                }
                if (PM.exist(p.getName()))PM.getOplayer(p).score.getSideBar().reset();
            }
        }.runTaskLater(Main.getInstance(), 1);
    }
        

    
    public static void switchLocalGlobal(final Player p, final boolean notify) {
        if (p.getWorld().getName().equalsIgnoreCase("lobby")) { //оказались в лобби, делаем глобальный
            if ( DeluxeChat.isLocal(p.getUniqueId().toString()) ){
                if (notify) p.sendMessage("§fЧат переключен на глобальный");
                Ostrov.deluxechatPlugin.setGlobal(p.getUniqueId().toString());
            }
        } else {
            if ( !DeluxeChat.isLocal(p.getUniqueId().toString()) )  {
                if (notify) p.sendMessage("§fЧат переключен на Игровой");
                Ostrov.deluxechatPlugin.setLocal(p.getUniqueId().toString());
            }
        }
    }
    
    
    
    
    
    public static void perWorldTabList(final Player player) {
        for (Player other:Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(other.getWorld().getName())) {
                player.showPlayer(Main.getInstance(), other);
                other.showPlayer(Main.getInstance(), player);
            } else {
                player.hidePlayer(Main.getInstance(), other);
                other.hidePlayer(Main.getInstance(), player);
            }
        }

    }
    
     /*   
 @EventHandler
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
                if (p.getItemInHand()!=null && p.getItemInHand().getType() == Material.SKULL_ITEM ) return;
                if (!p.isOp()) e.setCancelled(true);
        } else e.setCancelled(true);
    }        
 */  
    
    @EventHandler
    public void Damage (EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            
            e.setDamage(0);
            p.setFireTicks(0);

            if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                p.setFallDistance(0);
                    if ( AM.isInGame(p) )  AM.GlobalPlayerExit(p);      //если где-то играет
                    else JoinToLobby (p);
            }  
            
        }
    }

     
    @EventHandler
    public void damm(EntityDamageByEntityEvent e) {
            if (!e.getDamager().isOp()) e.setCancelled(true);
    }
    
    @EventHandler
    public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e){
        if (!e.getPlayer().isOp()) e.setCancelled(true);
    }    
   
    
    
     
    
    @EventHandler( priority = EventPriority.MONITOR)
    public void speedBoostManager(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if ( (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem()!=null && e.getMaterial() == Material.FEATHER) {
            
            Snake snake = AM.getSnake(p);
            if (snake!=null && snake.arena.getState()==GameState.INGAME ) {
                snake.speedBoost = Files.speedboostTimeTicks;
            }
            //if (!AM.isInGame(p)) return;
            //AM.getPlayersArena(p).SetSpeedBoost(p, Files.speedboostTimeTicks); 
            if (e.getItem().getAmount()==1) {
                if (e.getHand()==EquipmentSlot.HAND) p.getInventory().getItemInMainHand().setType(Material.AIR);
                else p.getInventory().getItemInOffHand().setType(Material.AIR);
            } else {
                if (e.getHand()==EquipmentSlot.HAND) p.getInventory().getItemInMainHand().setAmount(e.getItem().getAmount()-1);
                else p.getInventory().getItemInOffHand().setAmount(e.getItem().getAmount()-1);
            }
            
            //if (p.getItemInHand().getAmount() == 1)   p.setItemInHand(new ItemStack(Material.AIR));
            //else p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
        }
    }

    
    
    @EventHandler
    public void sugarPickupEventSpeed(EntityPickupItemEvent e) {
        if (e.getEntityType()!=EntityType.PLAYER) return;
            Player p = (Player) e.getEntity();
            
            
            Snake snake = AM.getSnake(p);
            if (snake!=null ) {
                if (e.getItem().getItemStack().getType() == Material.SUGAR  && snake.arena.getState()==GameState.INGAME) {
                    //e.setCancelled(true);
                    //e.getItem().remove();
                    //if ( !AM.getPlayersArena(p).hasStarted() ) return;
                    if (AM.getPlayersArena(p).UseSugar(e.getItem())) {
                        snake.speedBoost = Files.speedboostTimeTicks;
                        snake.sugarBoosted = true;
                        //AM.getPlayersArena(p).SetSpeedBoost(p, Files.speedboostTimeTicks); 
                        //AM.getPlayersArena(p).SetSugarBoosted(p, true); 
                    }
                    
                } else if (e.getItem().getItemStack().getType() == Material.GOLD_INGOT  && snake.arena.getState()==GameState.ENDING) {
                    //e.setCancelled(true);
                    //e.getItem().remove();
                    //if ( AM.getPlayersArena(p).getState() != GameState.ENDING) return;
                    AM.getPlayersArena(p).AddGold();
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 9.9F);
                    //p.getWorld().playEffect(p.getLocation(), Effect.LAVA_POP, 0);
         //ParticleEffect.LAVA.display(0.1F, 0.1F, 0.1F, 0.1F, 5, p.getEyeLocation(), 5.0D);
                }
            } //else {
            
            final Arena a = AM.getPlayersArena(p);
            if (a!=null && a.getState()==GameState.ENDING && e.getItem().getItemStack().getType() == Material.GOLD_INGOT) {
                AM.getPlayersArena(p).AddGold();
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 9.9F);
         //ParticleEffect.LAVA.display(0.1F, 0.1F, 0.1F, 0.1F, 5, p.getEyeLocation(), 5.0D);
            }
            
            e.setCancelled(true);
            e.getItem().remove();
           // }
            
            
            //if ( !AM.isInGame(p) ) {
           //     e.setCancelled(true);
            //    e.getItem().remove();
            //    return;
            //}
                
              /*  if (e.getItem().getItemStack().getType() == Material.SUGAR) {
                    e.setCancelled(true);
                    e.getItem().remove();
                    if ( !AM.getPlayersArena(p).hasStarted() ) return;
                    if (AM.getPlayersArena(p).UseSugar(e.getItem())) {
                        AM.getPlayersArena(p).SetSpeedBoost(p, Files.speedboostTimeTicks); 
                        AM.getPlayersArena(p).SetSugarBoosted(p, true); 
                    }
                    
                } else if (e.getItem().getItemStack().getType() == Material.GOLD_INGOT) {
                    e.setCancelled(true);
                    e.getItem().remove();
                    if ( AM.getPlayersArena(p).getState() != GameState.ENDING) return;
                    AM.getPlayersArena(p).AddGold();
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 9.9F);
                    //p.getWorld().playEffect(p.getLocation(), Effect.LAVA_POP, 0);
         //ParticleEffect.LAVA.display(0.1F, 0.1F, 0.1F, 0.1F, 5, p.getEyeLocation(), 5.0D);
                }*/
                        


    }

    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            //if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.WHEAT || e.getClickedBlock().getType() == Material.SOIL)) {
            if (e.getClickedBlock() != null) {
                e.setCancelled(true);
            }
        }
    }   
    
    @EventHandler  
    public void onHungerChange(FoodLevelChangeEvent e) {e.setCancelled(true); ((Player)e.getEntity()).setFoodLevel(20);}
        
    
    @EventHandler
    public void onPlace(BlockPlaceEvent e) { if (!e.getPlayer().isOp()) e.setCancelled(true);}
    
    @EventHandler
    public void onBreak(BlockBreakEvent e) {if (!e.getPlayer().isOp()) e.setCancelled(true);}
    

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
       final Player p = pde.getEntity(); 
       p.teleport (Bukkit.getWorlds().get(0).getSpawnLocation());
    }   
   
    
        
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().isOp()) return;
        e.setCancelled(true);
        //e.getItemDrop().remove();
        //ItemStack droped = e.getPlayer().getItemInHand().clone();
        //droped.setAmount(1);
        //e.getPlayer().setItemInHand(droped);
        //e.getPlayer().updateInventory();
    }     
    
    
    
    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent event) {
        if (event.getOffHandItem() != null  )  event.setCancelled(true);
    }    
  
    
    
    @EventHandler
    public void blockGrow(BlockGrowEvent event) {
         event.setCancelled(true);
    }    
    
    
    @EventHandler
    public void strucGrow(StructureGrowEvent event) {
          event.setCancelled(true);
    }    
    
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
          event.setCancelled(true);
    }    

    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
     
        boolean rain = event.toWeatherState();
        if(rain)
            event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onForm(BlockFormEvent form) {
	    form.setCancelled(true);
	}
	
 
    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
     
        boolean storm = event.toThunderState();
        if(storm)
            event.setCancelled(true);
    } 

    
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }
    
    
    
    
    
    
    
}
