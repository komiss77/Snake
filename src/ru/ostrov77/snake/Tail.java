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
import ru.komiss77.utils.TCUtil;

public class Tail implements Runnable {

    public static final double DEFAULT_SPEED = 0.35D; //SPEED_ORIG=0.23000000417232513
    public static final double SUGAR_BOOSTED_SPEED = 0.45D;//0.55D;
    public static final double FEATHER_BOOSTED_SPEED = 0.55D; //0.65D;

    private int tick = 1; //или все % сработаюи на 0
    public final Arena arena;
    public String playerName;
    private Tail collide;

    private BukkitTask task;

    public final List<Sheep> tail;

    public DyeColor color;
    public int kills, coin;
    private int speedBoost;

    
    public Tail(final Player p, final Arena arena) {
        playerName = p.getName();
        this.arena = arena;
        tail = new ArrayList<>();

    }

    public void start(final Player p) {
        final Location loc = p.getEyeLocation();
        final Location to = arena.arenaLobby;
        final Vector v = to.clone().subtract(loc).toVector().normalize();
        loc.setDirection(v);
        final Entity masterSheep = Tail.this.spawnSheep(loc);
        if (masterSheep != null && masterSheep.isValid()) {
            masterSheep.addPassenger(p);
            task = Bukkit.getScheduler().runTaskTimer(Ostrov.instance, Tail.this, 1, 1);
        } else {
            Bukkit.getLogger().info("Unable to spawn first sheep...");
            Bukkit.getLogger().info("The problem is most likely because you have animals disabled, especially if you\'re running Multiverse.");
        }
    }
    
    
    @Override
    public void run() {

        final Player p = Bukkit.getPlayerExact(playerName);
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
            if (tick > 60 && tick % 2 == 0 && collide == null && arena.state == GameState.ИГРА) {
                Entity sheep;
                final BoundingBox playerBox = p.getBoundingBox().expand(0.5, 0.5, 0.5);
                for (Tail snake : arena.players.values()) {            //перебираем змейки арены
                    if (collide != null) {
                        break; //обработка одного столкновения за тик!
                    }
                    for (int i = 0; i < snake.tail.size(); i++) {
                        if (i < 4 && snake.playerName.equals(playerName)) {
                            continue; //4 своих следующих овцы не учитывать
                        }
                        sheep = snake.tail.get(i);

                        if (playerBox.overlaps(sheep.getBoundingBox())) { //если этот игрок в одном блоке с овцой
                            if (!snake.playerName.equals(playerName)) { //столкнулся НЕ со своими - начисляем вынос
                                kills++;
                            }
                            collide = snake;
                            break;
                        }
                    }
                }

                if (collide != null) { //игрок столкнулся с овцой
                    if (playerName.equals(collide.playerName)) {
                        arena.SendAB(getChatColor()+playerName + " §6стoлкнулся со своей змейкой!");
                    } else {
                        arena.SendAB(getChatColor()+playerName+" §a6врезался в змейку "+collide.getChatColor()+collide.playerName+"§6!");
                    }
                    arena.collide(p);
                    collide = null; //сброс для следующего
                    return; //пропустить действия ниже!
                }
                
                playerBox.expand(0.5, 0.5, 0.5);
                if (!arena.grow.isEmpty() && tick%3==0) {
                    for (int i = arena.grow.size()-1; i>=0; i--) {
                        sheep = arena.grow.get(i);
                        if (playerBox.overlaps(sheep.getBoundingBox())) {
                            sheep.remove();
                            arena.grow.remove(i);
                            addSheep(p);
                            p.playSound(sheep.getLocation(), Sound.BLOCK_HONEY_BLOCK_STEP, 1.0F, 9.9F);
                        }
                    }
                }

            }

            if (speedBoost > 0) {
                --speedBoost;
                if (speedBoost == 0) {
                    updateSpeed(DEFAULT_SPEED);
                }
            }
            
            final Sheep masterSheep = tail.get(0);
            masterSheep.setRotation(p.getLocation().getYaw(), 0); //ориентация головы как у седока, но только вправо-влево
            if (tick % 5 == 0) {
                Location loc = p.getLocation();
                loc.setPitch(0);
                final Vector direction = loc.getDirection();

                direction.multiply(3);
                final Location moveTo = masterSheep.getLocation().add(direction);
                final Mob mob = (Mob) masterSheep;
                mob.getPathfinder().moveTo(moveTo);
            
                Sheep target;
                Sheep sheep;
                for (int i = 1; i < tail.size(); i++) {
                    target = tail.get(i-1);
                    sheep = tail.get(i);
                    sheep.getPathfinder().moveTo(target);
                }
            
            }
           /* final Vector vel = p.getLocation().getDirection().setY(0).normalize().multiply(4);

            Mob before = null;
            for (int i = 0; i < tail.size(); i++) {
                final Mob tailEnt = (Mob) tail.get(i);
                Location loc = p.getLocation().add(vel);
                if (i == 0) {
                    loc = tailEnt.getLocation().add(vel);
                }
                if (before != null) {
                    loc = before.getLocation();
                }
                if (loc.toVector().subtract(tailEnt.getLocation().toVector()).length() > 12.0D) {
                    //final Vector v = tailEnt.getLocation().toVector().subtract(loc.toVector()).setY(0).normalize();
                    //loc = tailEnt.getLocation().add(v.multiply(12));
                    loc = tailEnt.getLocation().add(traj(tailEnt.getLocation(), loc).multiply(12));
                }
                if (before != null) {
                    //final Vector v = before.getLocation().toVector().subtract(tailEnt.getLocation().toVector()).setY(0).normalize();
                    //Location tp = before.getLocation().add(v.multiply(1.4D));
                    Location tp = before.getLocation().add(traj2D(before, tailEnt).multiply(1.4D));
                    tp.setPitch(tailEnt.getLocation().getPitch());
                    tp.setYaw(tailEnt.getLocation().getYaw());
                    tailEnt.teleport(tp);
                }

                tailEnt.getPathfinder().moveTo(loc);

                before = tailEnt;
            }  */

        }

        //if (tick > 50 && tick % 40 == 0 && arena.state == GameState.ИГРА) {
            //final Entity lastSheep = tail.get(tail.size() - 1);
        //    addSheep(p);
        //}

        tick++;

    }

    private Vector traj2D(final Entity a, final Entity b) {
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
    }

    private Vector traj(final Location a, final Location b) {
        return b.toVector().subtract(a.toVector()).setY(0).normalize();
    }

    private Vector traj(final Entity a, final Entity b) {
        return b.getLocation().toVector().subtract(a.getLocation().toVector()).setY(0).normalize();
    }
    
    public void addSheep(final Player p) {
        final Entity lastSheep = tail.get(tail.size() - 1);
        Location loc = lastSheep.getLocation();//p.getLocation();
        if (tail.size()>1) {
            loc.add(traj(tail.get(tail.size() - 2), lastSheep));
        } else {
            loc.subtract(lastSheep.getLocation().getDirection().setY(0));//loc.subtract(p.getLocation().getDirection().setY(0));
        }
        loc.setDirection(lastSheep.getLocation().getDirection());
        spawnSheep(loc);
    }
    

    private Entity spawnSheep(final Location loc) {
        
        final Sheep newSheep = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
        newSheep.setInvulnerable(true);
        newSheep.setColor(color);
        newSheep.setNoDamageTicks(Integer.MAX_VALUE);
        
        Bukkit.getMobGoals().removeAllGoals(newSheep);
        ((LivingEntity) newSheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(DEFAULT_SPEED);
        
        if (!tail.isEmpty()) {//для всех, кроме первой
            final Entity lastSheep = tail.get(tail.size() - 1);
            final FollowGoal goal = new FollowGoal((Mob) newSheep, (LivingEntity) lastSheep, arena);
            //Bukkit.getMobGoals().addGoal(newSheep, 1, goal);
        }
        tail.add(newSheep);
        return newSheep;
    }
    



    public void stop(final boolean drop) {
        if (task == null) return;
        task.cancel();
        task = null;

        for (Sheep sheep : tail) {
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
                item.setGlowing(true);
            } else {
               sheep.setAI(false);
               
            }
            //sheep.remove();
        }

        tail.clear();
        kills = 0;

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

    public String getChatColor() {
        return color == null ? "§7" : TCUtil.toChat(color);
    }



}

    /* UC
        public void onUpdate() {
        if (getPlayer() == null) return;
        Vector vel = getPlayer().getLocation().getDirection().setY(0).normalize().multiply(4);

        Creature before = null;
        for (int i = 0; i < tail.size(); i++) {
            Creature tailEnt = tail.get(i);
            Location loc = getPlayer().getLocation().add(vel);
            if (i == 0) {
                loc = tailEnt.getLocation().add(vel);
            }
            if (before != null) {
                loc = before.getLocation();
            }
            if (loc.toVector().subtract(tailEnt.getLocation().toVector()).length() > 12.0D) {
                loc = tailEnt.getLocation().add(traj(tailEnt.getLocation(), loc).multiply(12));
            }
            if (before != null) {
                Location tp = before.getLocation().add(traj2D(before, tailEnt).multiply(1.4D));
                tp.setPitch(tailEnt.getLocation().getPitch());
                tp.setYaw(tailEnt.getLocation().getYaw());
                tailEnt.teleport(tp);
            }

            BukkitBrain.getBrain(tailEnt).getController().moveTo(loc);

            before = tailEnt;
        }
    }
    */
        
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