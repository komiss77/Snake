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

    public static double snakeDefaultSpeed = 0.35D; //SPEED_ORIG=0.23000000417232513
    public static double snakeBoostedSpeed = 0.55D; //0.65D;
    public static double snakeSugerBoostedSpeed = 0.45D;//0.55D;

    private int tick = 1; //или все % сработаюи на 0
    public final Arena arena;
    public String name;
    private String collided;

    private BukkitTask task;

    public final List<Entity> playerSheep = new ArrayList<>();
    protected final Entity masterSheep;

    private final DyeColor color;
    public int kills;
    public int speedBoost;
    public double speed = snakeDefaultSpeed; //Files.snakeDefaultSpeed;//1.0D;  //скорость овцы по умолчанию 0,23
    public boolean sugarBoosted = false;

    public Snake(Player p, DyeColor color, Arena arena) {
        name = p.getName();
        this.arena = arena;
        this.color = color;
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
            this.stop(false);
            return;
        }

        if (arena == null || (arena.state != GameState.ИГРА && arena.state != GameState.ЭКИПИРОВКА)) {
            this.stop(false);
            return;
        }

        //поиск столкновений
        if (tick > 60 && tick % 2 == 0 && collided == null && arena.state == GameState.ИГРА) {
            Entity sheep;
            BoundingBox box = p.getBoundingBox();//.expand(0.3, 0.3, 0.3);
            for (Snake snake : arena.players.values()) {            //перебираем змейки арены
                if (collided != null) {
                    break; //обработка одного столкновения за тик!
                }
                for (int i = 0; i < snake.playerSheep.size(); i++) {
                    if (i < 4 && snake.name.equals(name)) {
                        continue; //4 своих следующих овцы не учитывать
                    }
                    sheep = snake.playerSheep.get(i);

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

        //не давать спешиться
        if (tick % 10 == 0) {
            if (masterSheep.getPassengers().isEmpty()) {
                masterSheep.addPassenger(p);
                //p.sendMessage("Вы не можете спешиться!");
            }
        }

        //управление мастер-овцой
        if (arena.state == GameState.ИГРА) {

            if (speedBoost > 0) {
                if (sugarBoosted) {
                    updateSpeed(snakeSugerBoostedSpeed);
                } else {
                    updateSpeed(snakeSugerBoostedSpeed);
                }
                --speedBoost;
                if (speedBoost == 0) {
                    updateSpeed(snakeDefaultSpeed);
                    sugarBoosted = false;
                }
            }

            //carrot.teleport(getCarrotLoc(p));
            //final Vector vector = masterSheep.getLocation().getDirection().multiply(speed);
            masterSheep.setRotation(p.getLocation().getYaw(), 0); //ориентация головы как у седока, но только вправо-влево

            if (tick % 10 == 0) {
                Location loc = p.getLocation();
                loc.setPitch(0);
                final Vector direction = loc.getDirection();

                direction.multiply(3);
                final Location moveTo = masterSheep.getLocation().add(direction);
                final Mob mob = (Mob) masterSheep;
                mob.getPathfinder().moveTo(moveTo);

            }

        }

        if (tick > 50 && tick % 40 == 0 && arena.state == GameState.ИГРА) { //SheepSpawn
            final Entity lastSheep = playerSheep.get(playerSheep.size() - 1);
            spawnSheep(lastSheep);
        }

        tick++;

    }

    private Entity spawnSheep(final Entity target) {
        final Entity newSheep = target.getWorld().spawnEntity(target.getLocation(), EntityType.SHEEP);
        newSheep.setInvulnerable(true);
        ((Sheep) newSheep).setColor(color);
        //final Mob mob = (Mob) newSheep;
        Bukkit.getMobGoals().removeAllGoals(((Sheep) newSheep));
        if (target.getType() != EntityType.PLAYER) {
            final FollowGoal goal = new FollowGoal((Mob) newSheep, (LivingEntity) target, arena);
            Bukkit.getMobGoals().addGoal((Sheep) newSheep, 1, goal);
        }
        playerSheep.add(newSheep);
        ((LivingEntity) newSheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        return newSheep;
    }

    private void updateSpeed(final double newSpeed) {
        for (Entity sheep : playerSheep) {
            ((LivingEntity) sheep).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        }
    }

    public void stop(final boolean drop) {
        if (task != null) {
            task.cancel();
            task = null;

            for (Entity sheep : playerSheep) {
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

            playerSheep.clear();
            kills = 0;

        }

    }

}
