package ru.ostrov77.snake.Objects;

import ru.ostrov77.snake.customEntity.CustomSheep;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.EnumColor;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import net.minecraft.server.v1_16_R1.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftSheep;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.ostrov77.snake.Main;
import ru.ostrov77.snake.Manager.Files;



public class Snake {

    Arena a;
    public Player p;
    private Player collided = null;
    private BukkitTask sheepspawn,collisioncheck,sheepcontrol,lostsheep,blockdismount;
    
    private final List <Entity> playerSheep = new ArrayList<>();
    
    private double speedMultipler = 1.0D; 
    private DyeColor color;
    private int speedBoost,kills,tp = 0;
    private boolean sugarBoosted = false;

    
    
    public Snake(Player player, DyeColor color, Arena arena) {
        this.p = player;
        this.a = arena;
        this.color = color;
        
        
        Entity masterSheep = spawnShepp(p.getLocation(), color, p.getLocation().getYaw());

        if (masterSheep != null && masterSheep.isValid()) {
            //masterSheep.setPassenger(player);
            //if (!masterSheep.getPassengers().contains(player)) masterSheep.addPassenger(player);
            masterSheep.addPassenger(player);
            ((Sheep) masterSheep).setColor(color);
            playerSheep.add(masterSheep);       //добавляем первой первую овцу
            
            BlockDismount();
            SheepSpawn();
            CollisionCheck();
            LostSheep();
            SheepControl();
            
        } else {
            Bukkit.getLogger().info("Unable to spawn first sheep...");
            Bukkit.getLogger().info("The problem is most likely because you have animals disabled, especially if you\'re running Multiverse.");
        }

    }
    
   
    


    private void BlockDismount() {                                  //спавнит овец
        blockdismount = (new BukkitRunnable() {
        @Override
        public void run() {

            if (a != null && (a.getState() == GameState.INGAME || a.getState() == GameState.STARTED) ) {

                //if (p != null && playerSheep.get(0) != null && playerSheep.get(0).getPassenger() == null) {
                if (p != null && playerSheep.get(0) != null && playerSheep.get(0).getPassengers().isEmpty() ) {
                    //playerSheep.get(0).setPassenger(p);
                    playerSheep.get(0).addPassenger(p);
                    p.sendMessage("Вы не можете спешиться!");
                }
            }

        }   }).runTaskTimer(Main.getInstance(), 0L, 10L);
    
    }




    
    
    
    
    
    private void SheepSpawn() {                                  //спавнит овец
        this.sheepspawn = (new BukkitRunnable() {
            @Override
            public void run() {

                if (a != null && a.getState() == GameState.INGAME) {
                    //Entity sheep = Main.nmsAccess.spawnX(playerSheep.get(playerSheep.size()-1).getLocation(), color, playerSheep.get(playerSheep.size()-1).getLocation().getYaw());
                        Entity sheep = spawnShepp(playerSheep.get(playerSheep.size()-1).getLocation(), color, playerSheep.get(playerSheep.size()-1).getLocation().getYaw());
                        ((Sheep) sheep).setColor(color);
        //Main.nmsAccess.pathfind(playerSheep.get(playerSheep.size()-1), sheep, p, a);
                        Pathfind(playerSheep.get(playerSheep.size()-1), sheep, p, a);

                        playerSheep.add(sheep);

                } 

            }   
        }).runTaskTimer(Main.getInstance(), 50L, 40L);
    
    }






    private void CollisionCheck() {
        this.collisioncheck = (new BukkitRunnable() {
        @Override
        public void run() {

            if (a != null && a.getState() == GameState.INGAME && collided==null) {


             for (Player check: a.getPlayers() ) {            //перебираем игроков арены
                if (collided!=null) break;

                    if ( check != null && check.isOnline()) {

                        for ( int i=0; i<playerSheep.size(); i++ ) {      //от 0 до последней овцы
                            if (collided!=null) break;


                                if ( (short) (Math.pow( (short) playerSheep.get(i).getLocation().getX()-(short)check.getLocation().getX(), 2) 
                                                + Math.pow((short) playerSheep.get(i).getLocation().getZ()-(short) check.getLocation().getZ(), 2)) <3 ) {


                                    if ( p == check ) {       //если это овца хозяина
                                        if ( i>4 ) {            //по счёту больше 4, то столкнулся с собой
                                            collided=check;
                                        }             
                                    } else {
                                        collided = check;                   //если не хозяина, кто-то столкнулся c овцой хозяина
                                        kills++;
                                    }                   

                                }
                        }
                    }
             }

             if ( collided != null ) {
                 Player t=collided;
                 collided=null;
                 a.Collide(p, t);
             }



                    } 

                }}).runTaskTimer(Main.getInstance(), 60L, 7L);

        }
    






    
    private void SheepControl() {
        sheepcontrol = (new BukkitRunnable() {
        @Override
        public void run() {

        if (a != null && a.getState() == GameState.INGAME) {
                    
//     ------- управление, ускорения --------------
            Location location = playerSheep.get(0).getLocation();
            location.setDirection(p.getLocation().getDirection());
            location.setPitch(0.0F);

            if (speedBoost > 0) {
                if (sugarBoosted)  speedMultipler = Files.snakeSugerBoostedSpeed;
                else speedMultipler = Files.snakeBoostedSpeed;
                --speedBoost;
            } else {
                speedMultipler = Files.snakeDefaultSpeed;
                sugarBoosted = false;
            }

            Vector vector = location.getDirection().multiply(speedMultipler);
            vector.setY(0);
            Set_yaw(playerSheep.get(0), p);
//System.out.println("setVelocity "+vector.multiply(speedMultipler));
            playerSheep.get(0).setVelocity(vector.multiply(speedMultipler));
 //--------------------------------------
        } 
                
            }}).runTaskTimer(Main.getInstance(), 0L, 1L);
        
        
    }


    private void LostSheep() {
        this.lostsheep = (new BukkitRunnable() {
        @Override
        public void run() {
                        
            if ( tp > playerSheep.size()-2 ) tp=0;

            if ( (short) (Math.pow( (short) playerSheep.get(tp).getLocation().getX()-(short) playerSheep.get(tp+1).getLocation().getX(), 2) 
                            + Math.pow((short) playerSheep.get(tp).getLocation().getZ()-(short) playerSheep.get(tp+1).getLocation().getZ(), 2)) > 10 ) {

                playerSheep.get(tp+1).teleport(playerSheep.get(tp));
//System.out.println(" телепорт овцы "+playerSheep.get(tp+1).getUniqueId()+" к "+playerSheep.get(tp).getUniqueId());
            }
            tp++;
 

            }}).runTaskTimer(Main.getInstance(), 260L, 6L);
    
    }

    








    
    public void terminate() {
        stopTrack();
        
        //try { 
        //    if (this.playerSheep.get(0).getPassenger()!=null) this.playerSheep.get(0).getPassenger().eject();
        //} catch (NullPointerException e) {}
        
        
            if ( !playerSheep.isEmpty()) {
                
               try {
                   //if (playerSheep.get(0).getPassenger()!=null)  playerSheep.get(0).getPassenger().eject(); 
                   if (playerSheep.get(0).getPassengers().contains(p))  playerSheep.get(0).removePassenger(p); 
               } catch (NullPointerException e) {
               Main.log_err("terminate:"+e.getMessage());}   
                
            playerSheep.stream().forEach((sheep) -> {
                //sheep.getWorld().playEffect(sheep.getLocation(), Effect.EXPLOSION_LARGE, 0);
                sheep.getWorld().playEffect(sheep.getLocation(), Effect.GHAST_SHOOT, 0);
                
                Item item = sheep.getLocation().getWorld().dropItem(sheep.getLocation(), new ItemStack(Material.GOLD_INGOT, 1) ); 
                item.setCustomName(UUID.randomUUID().toString());
                item.setCustomNameVisible(false);
                item.setVelocity(new Vector(0, 1, 0));
                item.setPickupDelay(1);
                
                //sheep.getLocation().getWorld().dropItemNaturally(sheep.getLocation(),new ItemStack(Material.GOLD_INGOT, 1) );
                try { 
                    sheep.remove();
                } catch (NullPointerException e ) { 
                    Main.log_err("terminate: "+e.getMessage()); 
                }  
            });
            }
        

        playerSheep.clear();

        kills = 0;
        tp = 0;
        a = null;
        
        if (p!=null && p.isOnline()) p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT , 0.8f, 2.0f);
        p = null;
    }

    
    private void stopTrack() {
        if (blockdismount != null)   blockdismount.cancel();
        if (sheepspawn != null)   sheepspawn.cancel();
        if (collisioncheck != null)    collisioncheck.cancel();
        if (lostsheep != null)    lostsheep.cancel();
        if (sheepcontrol != null)    sheepcontrol.cancel();
    }
    
    
    
    
    
    
    
    
    public Entity GetFirstShhep() {
        if (this.playerSheep.size() >=1) return this.playerSheep.get(0);
        else return null;
    }
    

    public int getKills() {
        return kills;
    }
    
    
    
    public int GetSpeedBoost() {
        return speedBoost;
    }

    public void SetSpeedBoost(int speed) {
         if (a != null && a.getState() == GameState.INGAME)
                speedBoost=speed;
    }
    
    public void SetSugarBoosted(boolean b) {
         if (a != null && a.getState() == GameState.INGAME)
                sugarBoosted=b;
    }
    
    public boolean IsSugarBoost() {
        return sugarBoosted;
    }




    
    public static void Pathfind(final Entity target, final Entity follower, final Player owner, final Arena minigame) {
        (new BukkitRunnable() {
            @Override
            public void run() {
                if (!minigame.hasStarted())   this.cancel();
                if (!target.isValid())   this.cancel();


                
                final net.minecraft.server.v1_16_R1.EntityInsentient nms_insentient = (EntityInsentient) ((CraftEntity)follower).getHandle();
                final PathEntity pathEntity = nms_insentient.getNavigation().a(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 1);
                if (pathEntity != null) {
                    nms_insentient.getNavigation().a(pathEntity, 1.0);
                    nms_insentient.getNavigation().a(2.0);
                }
                
                follower.getLocation().setDirection(target.getLocation().getDirection());

                Double double1 = 60.0D;
                Double double2 = 0.0D;
                Double double3 = 0.0D;
                
                Double speed = Files.snakeDefaultSpeed;
                if (minigame.HasSpeedBoost(owner)) {
                    if (minigame.HasSugarBoosted(owner)) {
                        speed = Files.snakeSugerBoostedSpeed;
                    } else {
                        speed = Files.snakeBoostedSpeed;
                    }
                }
                double3 = speed * (double1 / 100.0D);
                double2 = speed - double3;
                speed = double2;
                if (double2 < 0.1D)   speed = 0.2D;
                nms_insentient.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double)speed);   
                
                
            }
        }).runTaskTimer(Main.getInstance(), 0L, 2L);
    }


    
    public static void Set_yaw(Entity entity, Player player) {
        ((CraftSheep) entity).getHandle().yaw = player.getLocation().getYaw();
    }

    
    
    private Entity spawnShepp(final Location location, final DyeColor color, final float yaw) {
        final CraftWorld mcWorld = (CraftWorld) location.getWorld();
        final CustomSheep custom_sheep = new CustomSheep (mcWorld);

        custom_sheep.setLocation(location.getX(), location.getY(), location.getZ(), yaw, location.getPitch());
        ((CraftLivingEntity) custom_sheep.getBukkitEntity()).setRemoveWhenFarAway(false);
        //custom_sheep.setColor(numToEcc(color));
        custom_sheep.setColor(EnumColor.valueOf(color.toString()));
        
        mcWorld.addEntity(custom_sheep, CreatureSpawnEvent.SpawnReason.CUSTOM);
        
        return custom_sheep.getBukkitEntity();
    }
    
    
    
    
    
  /*   public static EnumColor numToEcc(final int n) {
        if (n == 0) {
            return EnumColor.WHITE;
        }
        if (n == 1) {
            return EnumColor.ORANGE;
        }
        if (n == 2) {
            return EnumColor.MAGENTA;
        }
        if (n == 3) {
            return EnumColor.LIGHT_BLUE;
        }
        if (n == 4) {
            return EnumColor.YELLOW;
        }
        if (n == 5) {
            return EnumColor.LIME;
        }
        if (n == 6) {
            return EnumColor.PINK;
        }
        if (n == 7) {
            return EnumColor.GRAY;
        }
        if (n == 8) {
            return EnumColor.SILVER;
        }
        if (n == 9) {
            return EnumColor.CYAN;
        }
        if (n == 10) {
            return EnumColor.PURPLE;
        }
        if (n == 11) {
            return EnumColor.BLUE;
        }
        if (n == 12) {
            return EnumColor.BROWN;
        }
        if (n == 13) {
            return EnumColor.GREEN;
        }
        if (n == 14) {
            return EnumColor.RED;
        }
        if (n == 15) {
            return EnumColor.BLACK;
        }
        if (n > 15) {
            return EnumColor.WHITE;
        }
        return EnumColor.WHITE;
    }*/
    
     
     
     
}
