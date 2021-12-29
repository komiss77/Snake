package ru.ostrov77.snake.Manager;

import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import java.util.EnumSet;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;
import ru.komiss77.Ostrov;
import ru.ostrov77.snake.Objects.Arena;
import ru.ostrov77.snake.Objects.GameState;








    public class FollowGoal implements Goal<Sheep> {
        
        public static final GoalKey<Sheep> key = GoalKey.of(Sheep.class, new NamespacedKey(Ostrov.instance, "snake"));
        private final Mob mob;
        private final LivingEntity target;
        private final Arena arena;
        private int tick;
        
        public FollowGoal(Mob mob, LivingEntity target, final Arena arena) {
//Bukkit.broadcastMessage("FollowGoal "+target.getName());
            //this.key = GoalKey.of(Sheep.class, new NamespacedKey(Ostrov.instance, "snake"));
            this.mob = mob;
            this.target = target;
            this.arena = arena;
        }
 
        @Override
        public boolean shouldActivate() {
            return true;
            //if (cooldown > 0) {
            ///    --cooldown;
            //    return false;
            //}
            //closestPlayer = getClosestPlayer();
            //if (closestPlayer == null) {
           //     return false;
           // }
           // return isHoldingEmerald(closestPlayer);
        }
 
        @Override
        public boolean shouldStayActive() {
            return shouldActivate();
        }
 
        @Override
        public void start() {
        }
 
        @Override
        public void stop() {
            mob.getPathfinder().stopPathfinding();
            mob.setTarget(null);
 //Bukkit.broadcastMessage("!!!!!!!!!!!!!!!!!!!! stop "+target.getName());
           //cooldown = 100;
        }
 
        
        
        
        @Override
        public void tick() {
            
            if (target==null || target.isDead()) {
                mob.getPathfinder().stopPathfinding();
 //Bukkit.broadcastMessage("!!!!!!!!!!!!!!!!!!!! stop222 "+target.getName());
                return;
            }
//Bukkit.broadcastMessage("tick "+target.getName()+" "+mob.getTicksLived()+" "+arena.getState());
            if (arena.getState()!=GameState.INGAME) return;
            
            if (tick%5==0) { //моб тикает через раз, так что реально будет 10
//Bukkit.broadcastMessage("tick%10 "+target.getName()+" "+mob.getTicksLived());
                
                final PathResult path =  mob.getPathfinder().findPath(target);
                
               // if (target.getType()==EntityType.PLAYER) { //для первой овцы
                    
                    //mob.setRotation(target.getLocation().getYaw(), 0);
                    
                   // final Vector direction = target.getLocation().getDirection();
                    
//Bukkit.broadcastMessage(target.getName()+" direction="+direction);

                   // direction.setY(0);
                   // direction.multiply(3);
                    
//Bukkit.broadcastMessage("   direction="+direction);
                    
                   // final Location moveTo = mob.getEyeLocation().add(direction);
//Bukkit.broadcastMessage(target.getName()+LocationUtil.StringFromLoc(mob.getLocation()).replaceFirst("map2:", "")+"->"+LocationUtil.StringFromLoc(moveTo).replaceFirst("map2:", ""));
///if (target.getName().equals("komiss77")) Bukkit.broadcastMessage(LocationUtil.StringFromLoc(mob.getLocation()).replaceFirst("map2:", "")+
//        "->"+LocationUtil.StringFromLoc(moveTo).replaceFirst("map2:", "")
//+"   direction="+direction);

                   // path = mob.getPathfinder().findPath(moveTo); //направляем на точку 2 блока впереди игрока
                    
                //} else {
                    
                   // path = mob.getPathfinder().findPath(target); //направляем просто за целью
                    
                //}
                
                if (path!=null) {
//Bukkit.broadcastMessage(cc+"path==null"  );
                   boolean done = mob.getPathfinder().moveTo( path);
//if(target.getType()==EntityType.PLAYER) Bukkit.broadcastMessage( target.getName()+" moveTo="+LocationUtil.StringFromLoc(path.getFinalPoint())+(done ? "+":"-"));
//Bukkit.broadcastMessage("       setPathResult?"+done+" "+Arrays.toString(path.getPoints().toArray())  );
                } else {
//if(target.getType()==EntityType.PLAYER) Bukkit.broadcastMessage( target.getName()+" path=null");
                }
//Bukkit.broadcastMessage("");         
            }
            
            tick++;
                //final PathResult path = mob.getPathfinder().findPath(target);
               // mob.getPathfinder().moveTo( path);
            //}
            
//if (mob.getPathfinder().getCurrentPath()==null) {
//    Bukkit.broadcastMessage("getCurrentPath == null!!"  );
//} else {
//    Bukkit.broadcastMessage("final point:" + mob.getPathfinder().getCurrentPath().getFinalPoint().toString() );
//}
    //Bukkit.broadcastMessage("available goals:" + Arrays.toString( mob.getPathfinder().getCurrentPath(). ) );
    //Bukkit.broadcastMessage("available targets:" + Arrays.toString( ((CraftMob) mob).getHandle().targetSelector.availableGoals.toArray() ) );
    //Bukkit.broadcastMessage("available goals:" + Arrays.toString(((CraftMob) mob).getHandle().goalSelector.availableGoals.toArray()) );
    //Bukkit.broadcastMessage("available targets:" + Arrays.toString(((CraftMob) mob).getHandle().targetSelector.availableGoals.toArray()) );
       
            
            
            
            
            
//System.out.println("distanceSquared="+mob.getLocation().distanceSquared(target.getLocation()));
            if (mob.getLocation().distanceSquared(target.getLocation()) > 30) {
                //mob.getPathfinder().stopPathfinding();
            //} else {
                //mob.getPathfinder().moveTo(closestPlayer, 1.0D);
                Vector inverseDirectionVec = target.getLocation().getDirection().normalize().multiply(-1);
//Bukkit.broadcastMessage("tp dist>30");
                mob.teleport(target.getLocation().add(inverseDirectionVec));
            }
        }
 
        
        
        
        
        
        
        
        @Override
        public GoalKey<Sheep> getKey() {
            return key;
        }
 
        @Override
        public EnumSet<GoalType> getTypes() {
            return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
        }
 
     /*   private Player getClosestPlayer() {
            Collection<Player> nearbyPlayers = mob.getWorld().getNearbyPlayers(mob.getLocation(), 10.0, player ->
                    !player.isDead() && player.getGameMode() != GameMode.SPECTATOR && player.isValid());
            double closestDistance = -1.0;
            Player closestPlayer = null;
            for (Player player : nearbyPlayers) {
                double distance = player.getLocation().distanceSquared(mob.getLocation());
                if (closestDistance != -1.0 && !(distance < closestDistance)) {
                    continue;
                }
                closestDistance = distance;
                closestPlayer = player;
            }
            return closestPlayer;
        }
 
        private boolean isHoldingEmerald(Player target) {
            PlayerInventory inv = target.getInventory();
            return isEmerald(inv.getItemInMainHand()) || isEmerald(inv.getItemInOffHand());
        }
 
        private boolean isEmerald(ItemStack stack) {
            switch (stack.getType()) {
                case EMERALD:
                case EMERALD_BLOCK:
                case EMERALD_ORE:
                    return true;
                default:
                    return false;
            }
        }*/
    }