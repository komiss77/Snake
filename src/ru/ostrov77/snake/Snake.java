package ru.ostrov77.snake;

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
import ru.komiss77.enums.GameState;

public class Snake implements Runnable {

    public static final double DEFAULT_SPEED = 0.35D; //SPEED_ORIG=0.23000000417232513
    public static final double SUGAR_BOOSTED_SPEED = 0.45D;//0.55D;
    public static final double FEATHER_BOOSTED_SPEED = 0.55D; //0.65D;

    private int tick = 1; //или все % сработаюи на 0
    public final Arena arena;
    public String name;
    private String collided;

    private BukkitTask task;

    public final List<Entity> tail;
    protected final Entity masterSheep;

    private final DyeColor color;
    public int kills;
    private int speedBoost;

    
    public Snake(Player p, DyeColor color, Arena arena) {
        name = p.getName();
        this.arena = arena;
        this.color = color;
        tail = new ArrayList<>();
        masterSheep = spawnSheep(p);

        if (masterSheep != null && masterSheep.isValid()) {

            masterSheep.addPassenger(p);
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
        if (p == null || !p.isOnline()) {
            stop(false);
            return;
        }

        if (arena == null || (arena.state != GameState.ИГРА && arena.state != GameState.ЭКИПИРОВКА)) {
            stop(false);
            return;
        }

        if (arena.state == GameState.ИГРА) {
            
            //поиск столкновений
            if (tick > 60 && tick % 2 == 0 && collided == null && arena.state == GameState.ИГРА) {
                Entity sheep;
                BoundingBox box = p.getBoundingBox();//.expand(0.3, 0.3, 0.3);
                for (Snake snake : arena.players.values()) {            //перебираем змейки арены
                    if (collided != null) {
                        break; //обработка одного столкновения за тик!
                    }
                    for (int i = 0; i < snake.tail.size(); i++) {
                        if (i < 4 && snake.name.equals(name)) {
                            continue; //4 своих следующих овцы не учитывать
                        }
                        sheep = snake.tail.get(i);

                        if (box.overlaps(sheep.getBoundingBox())) { //если этот игрок в одном блоке с овцой
                            if (!snake.name.equals(name)) { //столкнулся НЕ со своими - начисляем вынос
                                kills++;
                            }
                            collided = snake.name;
                            break;
                        }
                    }
                }

                if (collided != null) { //игрок столкнулся с овцой
                    arena.collide(p, collided);
                    collided = null; //сброс для следующего
                    return; //пропустить действия ниже!
                }

            }


            if (speedBoost > 0) {
                --speedBoost;
                if (speedBoost == 0) {
                    updateSpeed(DEFAULT_SPEED);
                }
            }

          /*  masterSheep.setRotation(p.getLocation().getYaw(), 0); //ориентация головы как у седока, но только вправо-влево
            if (tick % 10 == 0) {
                Location loc = p.getLocation();
                loc.setPitch(0);
                final Vector direction = loc.getDirection();

                direction.multiply(3);
                final Location moveTo = masterSheep.getLocation().add(direction);
                final Mob mob = (Mob) masterSheep;
                mob.getPathfinder().moveTo(moveTo);
            }*/
            
            Vector vel = p.getLocation().getDirection().setY(0).normalize().multiply(4);

            Mob before = null;
            for (int i = 0; i < tail.size(); i++) {
                Mob tailEnt = (Mob) tail.get(i);
                Location loc = p.getLocation().add(vel);
                if (i == 0) {
                    loc = tailEnt.getLocation().add(vel);
                }
                if (before != null) {
                    loc = before.getLocation();
                }
                if (loc.toVector().subtract(tailEnt.getLocation().toVector()).length() > 12.0D) {
                    final Vector v = tailEnt.getLocation().toVector().subtract(loc.toVector()).setY(0).normalize();
                    loc = tailEnt.getLocation().add(v.multiply(12));
                }
                if (before != null) {
                    final Vector v = before.getLocation().toVector().subtract(tailEnt.getLocation().toVector()).setY(0).normalize();
                    Location tp = before.getLocation().add(v.multiply(1.4D));
                    tp.setPitch(tailEnt.getLocation().getPitch());
                    tp.setYaw(tailEnt.getLocation().getYaw());
                    tailEnt.teleport(tp);
                }

                tailEnt.getPathfinder().moveTo(loc);

                before = tailEnt;
            }  

        }

        //if (tick > 50 && tick % 40 == 0 && arena.state == GameState.ИГРА) {
       //     final Entity lastSheep = tail.get(tail.size() - 1);
        //    spawnSheep(lastSheep);
        //}

        tick++;

    }
    
   // private Vector traj2D(final Entity a, final Entity b) {
   //     return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
   // }

    //private Vector traj(final Location a, final Location b) {
    //    return b.toVector().subtract(a.toVector()).setY(0).normalize();
   // }

    //private Vector traj(final Entity a, final Entity b) {
    //    return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
    //}

    
    private Entity spawnSheep(final Entity followTo) {
        Location loc = followTo.getLocation();
        
        if (tail.size()>1) {
            final Vector v = tail.get(tail.size() - 2).getLocation().toVector().subtract(followTo.getLocation().toVector()).setY(0).normalize();
            loc.add(v);
        } else {
            loc.subtract(followTo.getLocation().getDirection().setY(0));
        }
        
        final Sheep newSheep = (Sheep) followTo.getWorld().spawnEntity(loc, EntityType.SHEEP);
        newSheep.setInvulnerable(true);
        newSheep.setColor(color);
        newSheep.setNoDamageTicks(Integer.MAX_VALUE);
        
        Bukkit.getMobGoals().removeAllGoals(newSheep);
        ((LivingEntity) newSheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(DEFAULT_SPEED);
        
        //if (target.getType() != EntityType.PLAYER) { //для всех, кроме первой
            //final FollowGoal goal = new FollowGoal((Mob) newSheep, (LivingEntity) target, arena);
            //Bukkit.getMobGoals().addGoal(newSheep, 1, goal);
       // }
        tail.add(newSheep);
        return newSheep;
    }
    

        /* UC
        public void addSheepToTail(int amount) {
        Player player = getPlayer();
        for (int i = 0; i < amount; i++) {
            Location loc = player.getLocation();
            if (!tail.isEmpty()) {
                loc = lastTail().getLocation();
            }
            if (tail.size() > 1) {
                loc.add(traj(tail.get(tail.size() - 2), lastTail()));
            } else {
                loc.subtract(player.getLocation().getDirection().setY(0));
            }
            Sheep tailEnt = (loc.getWorld().spawn(loc, Sheep.class));
            tailEnt.setNoDamageTicks(Integer.MAX_VALUE);
            tailEnt.setRemoveWhenFarAway(false);
            tailEnt.teleport(loc);
            tail.add(tailEnt);
            tailEnt.setColor(DyeColor.values()[color]);
        }
    }
    */


    public void stop(final boolean drop) {
        if (task != null) {
            task.cancel();
            task = null;

            for (Entity sheep : tail) {
                if (sheep == null || sheep.isDead()) {
                    continue;
                }

                if (!sheep.getPassengers().isEmpty()) {
                    for (Entity pass : sheep.getPassengers()) {
                        sheep.removePassenger(pass);
                        sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.8f, 2.0f); //скорее всего это будет первая, чтобы не брать игрока
                    }
                }
                sheep.getWorld().playEffect(sheep.getLocation(), Effect.GHAST_SHOOT, 0);

                //золото выпадать только из 2+ змейки, в одиночке выпадать не будет!
                if (drop) { 
                    Item item = sheep.getLocation().getWorld().dropItem(sheep.getLocation(), new ItemStack(Material.SUNFLOWER, 1));
                    item.setVelocity(new Vector(0, 1, 0));
                    item.setPickupDelay(1);
                    //item.setGlowing(true);
                }
                sheep.remove();
            }

            tail.clear();
            kills = 0;

        }

    }


    public void allpePickup() {
        final Entity lastSheep = tail.get(tail.size() - 1);
        spawnSheep(lastSheep);
    }
    
    
    public void speedBoost(final int speedboostTicks, final Material booster) {
        if (booster == Material.SUGAR) {
            updateSpeed(SUGAR_BOOSTED_SPEED);
            speedBoost = speedboostTicks;
        } else  if (booster == Material.FEATHER) {
            updateSpeed(FEATHER_BOOSTED_SPEED);
            speedBoost = speedboostTicks;
        }    
    }
    
    private void updateSpeed(final double newSpeed) {
        for (Entity sheep : tail) {
            ((LivingEntity) sheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        }
    }



}
