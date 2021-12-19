package ru.ostrov77.snake.Objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import ru.komiss77.Ostrov;
import ru.ostrov77.snake.Manager.Files;
import ru.ostrov77.snake.Manager.FollowGoal;



public class Snake implements Runnable {

    private int tick = 1; //или все % сработаюи на 0
    public final Arena arena;
    public String name;
    private String collided;
    
    private BukkitTask task;
    
    private final List <Entity> playerSheep = new ArrayList<>();
    private final Entity masterSheep;
    
    private final DyeColor color;
    public int kills;
    public int speedBoost;
    public double speed = Files.snakeDefaultSpeed;//1.0D;  //скорость овцы по умолчанию 0,23
    public boolean sugarBoosted = false;

    
    
    public Snake(Player p, DyeColor color, Arena arena) {
        name = p.getName();
        this.arena = arena;
        this.color = color;
        
        
        masterSheep = spawnShepp(p.getLocation(), color, p.getLocation().getYaw());

        if (masterSheep != null && masterSheep.isValid()) {
            
            masterSheep.addPassenger(p);
            ((Sheep) masterSheep).setColor(color);
            playerSheep.add(masterSheep);       //добавляем первой первую овцу
            task = Bukkit.getScheduler().runTaskTimer(Ostrov.instance, this, 1, 1);
            
        } else {
            
            Bukkit.getLogger().info("Unable to spawn first sheep...");
            Bukkit.getLogger().info("The problem is most likely because you have animals disabled, especially if you\'re running Multiverse.");
            
        }

    }
    
    
    
    
    @Override
    public void run() {
        final Player p = Bukkit.getPlayerExact(name);
//System.out.println("run name="+name+" p="+p+" arena="+arena+" state="+arena.getState());        
        if (p==null || !p.isOnline()) {
            this.cancel();
            return;
        }
      
        if (arena==null || (arena.getState() != GameState.INGAME && arena.getState() != GameState.STARTED)) {
            this.cancel();
            return;
        }
        
        
        //поиск столкновений
        if (tick>60 && tick%2==0 && collided==null && arena.getState() == GameState.INGAME) {
            Entity sheep;
            BoundingBox box = p.getBoundingBox();//.expand(0.3, 0.3, 0.3);
            for (Snake snake: arena.playerTracker.values()) {            //перебираем змейки арены
                if (collided!=null) break; //обработка одного столкновения за тик!
                for ( int i=0; i<snake.playerSheep.size(); i++ ) {    
                    if (i<4 && snake.name.equals(name)) continue; //4 своих следующих овцы не учитывать
                    sheep = snake.playerSheep.get(i);

                    if (  box.overlaps(sheep.getBoundingBox()) ) { //если этот игрок в одном блоке с овцой
                        if (!snake.name.equals(name)) { //столкнулся НЕ со своими - начисляем вынос
                            kills++;
                        }
                        collided = snake.name;
                        break;
                    }
                }
            }

            if ( collided != null ) { //игрок столкнулся с овцой
                arena.Collide(p, collided);
                collided = null; //сброс для следующего
                return; //пропустить действия ниже!
            }

        }
        
        
        //не давать спешиться
        if (tick%10==0) {
            if (masterSheep.getPassengers().isEmpty() ) {
                masterSheep.addPassenger(p);
                //p.sendMessage("Вы не можете спешиться!");
            }
        }
        
        
        
        
        //управление мастер-овцой
        if (arena.getState() == GameState.INGAME) {
            
            //Location location = masterSheep.getLocation();
            //location.setDirection(p.getLocation().getDirection());
            //location.setPitch(0.0F);
            //((Mob)masterSheep).setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
            ((Mob)masterSheep).setRotation(p.getLocation().getYaw(), 0);

            if (speedBoost > 0) {
                if (sugarBoosted)  {
                    setSpeed(Files.snakeSugerBoostedSpeed);
                } else {
                    setSpeed(Files.snakeSugerBoostedSpeed);
                }
                --speedBoost;
                if (speedBoost==0) {
                    setSpeed(Files.snakeDefaultSpeed);
                    sugarBoosted = false;
                }
            }// else {
            //    speedMultipler = Files.snakeDefaultSpeed;
            //    sugarBoosted = false;
            //}

            Vector vector = masterSheep.getLocation().getDirection().multiply(speed);
            vector.setY(0);
            //Set_yaw(masterSheep, p);
            //((Mob)masterSheep).lookAt();
//System.out.println("setVelocity "+vector.multiply(speedMultipler));
            masterSheep.setVelocity(vector.multiply(speed));
            
        }
        
        
//if (playerSheep.size()>=2) { tick++; return; } //отладка


        if (tick>50 && tick%40==0 && arena.getState() == GameState.INGAME ) { //SheepSpawn
            Entity last = playerSheep.get(playerSheep.size()-1);
            Entity sheep = spawnShepp(last.getLocation(), color, last.getLocation().getYaw());
//Main.nmsAccess.pathfind(playerSheep.get(playerSheep.size()-1), sheep, p, a);
            Mob mob = (Mob) sheep;
            
            Bukkit.getMobGoals().removeAllGoals(((Sheep)sheep));
            FollowGoal goal = new FollowGoal(mob, (LivingEntity) last);
            //if (!Bukkit.getMobGoals().hasGoal(((Sheep)sheep), goal.getKey())) {
                Bukkit.getMobGoals().addGoal((Sheep)sheep, 1, goal);
            //}

            playerSheep.add(sheep);
//Bukkit.broadcastMessage("SPEED_ORIG="+((LivingEntity)sheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()  );

            setSpeed(speed);
            //Double double1 = 60.0D;
            //Double double2 = 0.0D;
            //Double double3 = 0.0D;
            //double3 = speed * (double1 / 100.0D);
            //double2 = speed - double3;
            //speed = double2;
            //Double double1 = 60.0D;
            //Double double2 = 0.0D;
            //Double double3 = 0.0D;
            //double3 = speed * (double1 / 100.0D);
            //double2 = speed - double3;
            //speed = speed - (speed * (60.0D / 100.0D));
            //if (speed < 0.1D) speed = 0.2D;
            //((LivingEntity)sheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
            //((LivingEntity)sheep).getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue();
            //Pathfind(last, sheep, p, arena);
        }        
        
        


        
        tick++;
        
    }    

    
    private void setSpeed(final double newSpeed) {
        speed = newSpeed;
        
        Double double1 = 60.0D;
        Double double2 = 0.0D;
        Double double3 = 0.0D;
        double3 = speed * (double1 / 100.0D);
        double2 = speed - double3;
        //speed = double2;
            //Double double1 = 60.0D;
            //Double double2 = 0.0D;
            //Double double3 = 0.0D;
            //double3 = speed * (double1 / 100.0D);
            //double2 = speed - double3;
            //speed = speed - (speed * (60.0D / 100.0D));
        if (double2 < 0.1D) double2 = 0.2D;
        
        double2 = speed;
                
//Bukkit.broadcastMessage("setSpeed="+double2 );
        for (Entity sheep : playerSheep) {
            ((LivingEntity)sheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(double2);
        }
    }    

    
    
    private Entity spawnShepp(final Location spawnLoc, final DyeColor color, final float yaw) {
        
        Entity sheep = spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SHEEP);
        ((Sheep)sheep).setColor(color);
        ((LivingEntity)sheep).setRemoveWhenFarAway(false);

       return sheep;
    }    
    
    
    
    
    







    
    public void cancel() {
        if (task!=null) {
            task.cancel();
            task = null;
            
            for (Entity sheep : playerSheep) {
                if (sheep==null || sheep.isDead()) continue;
            
                if (!sheep.getPassengers().isEmpty()) {
                    for (Entity pass : sheep.getPassengers()) {
                        sheep.removePassenger(pass);
                        sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_GHAST_SHOOT , 0.8f, 2.0f); //скорее всего это будет первая, чтобы не брать игрока
                    }
                }
                //sheep.getWorld().playEffect(sheep.getLocation(), Effect.EXPLOSION_LARGE, 0);
                sheep.getWorld().playEffect(sheep.getLocation(), Effect.GHAST_SHOOT, 0);
                
                Item item = sheep.getLocation().getWorld().dropItem(sheep.getLocation(), new ItemStack(Material.GOLD_INGOT, 1) ); 
                //item.setCustomNameVisible(false);
                item.setVelocity(new Vector(0, 1, 0));
                item.setPickupDelay(1);
                item.setGlowing(true);
                
                sheep.remove();
            }
            
        

        playerSheep.clear();

        kills = 0;
        //arena = null;
        
        }
        
    }   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /* private void BlockDismount() {                                  //спавнит овец
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

*/




  /*  private void CollisionCheck() {
        this.collisioncheck = (new BukkitRunnable() {
        @Override
        public void run() {

            if (a != null && a.getState() == GameState.INGAME && collided==null) {


             for (Player check: a.getPlayers() ) {            //перебираем игроков арены
                if (collided!=null) break;

                    if ( check != null && check.isOnline()) {

                        for ( int i=0; i<playerSheep.size(); i++ ) {      //от 0 до последней овцы
                            if (collided!=null) break;


                                if (  (Math.pow(  playerSheep.get(i).getLocation().getX()-check.getLocation().getX(), 2) 
                                                + Math.pow( playerSheep.get(i).getLocation().getZ()- check.getLocation().getZ(), 2)) <3 ) {


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

        }*/
    






    
  /*  private void SheepControl() {
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

            if (  (Math.pow(  playerSheep.get(tp).getLocation().getX()- playerSheep.get(tp+1).getLocation().getX(), 2) 
                            + Math.pow( playerSheep.get(tp).getLocation().getZ()- playerSheep.get(tp+1).getLocation().getZ(), 2)) > 10 ) {

                playerSheep.get(tp+1).teleport(playerSheep.get(tp));
//System.out.println(" телепорт овцы "+playerSheep.get(tp+1).getUniqueId()+" к "+playerSheep.get(tp).getUniqueId());
            }
            tp++;
 

            }}).runTaskTimer(Main.getInstance(), 260L, 6L);
    
    }

    */









    
  /*  public void terminate() {
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
                //item.setCustomName(UUID.randomUUID().toString());
                //item.setCustomNameVisible(false);
                item.setVelocity(new Vector(0, 1, 0));
                item.setPickupDelay(1);
                item.setGlowing(true);
                //item.set
                
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
    }*/

    
    
    
 
        
    //private void stopTrack() {
      //  if (blockdismount != null)   blockdismount.cancel();
      //  if (sheepspawn != null)   sheepspawn.cancel();
      //  if (collisioncheck != null)    collisioncheck.cancel();
       // if (lostsheep != null)    lostsheep.cancel();
       // if (sheepcontrol != null)    sheepcontrol.cancel();
   // }
    
    
    
    
    
    
    
    





/*
    NoSuchFieldException: ay
[13:13:30] [Server thread/WARN]: 	at java.base/java.lang.Class.getDeclaredField(Class.java:2549)
[13:13:30] [Server thread/WARN]: 	at ru.ostrov77.snake.Objects.Snake.Set_yaw(Snake.java:405)
*/
   // public static void Set_yaw(Entity entity, Player player) {
        
        //((LivingEntity)follower).setRotation(0, 0); ??
        
        //((CraftSheep) entity).getHandle().ay = player.getLocation().getYaw(); //yaw
      //  EntitySheep es = ((CraftSheep) entity).getHandle();
        //EntityLiving es = ((CraftSheep) entity).getHandle();
        //es.get
       // es.setPositionRotation(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
      /*  try {
            Field field = es.getClass().getDeclaredField("ay");
            field.setAccessible(true);
            field.set(es, player.getLocation().getYaw());
            field.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException ex) {
            ex.printStackTrace();
        }*/
    //}
  

    
    
    
    
    
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
