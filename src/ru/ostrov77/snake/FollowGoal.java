package ru.ostrov77.snake;

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
import ru.komiss77.enums.GameState;

public class FollowGoal implements Goal<Sheep> {

    public static final GoalKey<Sheep> key = GoalKey.of(Sheep.class, new NamespacedKey(Ostrov.instance, "snake"));
    private final Mob mob;
    private final LivingEntity target;
    private final Arena arena;
    private int tick;

    public FollowGoal(Mob mob, LivingEntity target, final Arena arena) {
        this.mob = mob;
        this.target = target;
        this.arena = arena;
    }

    @Override
    public boolean shouldActivate() {
        return true;
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
    }

    @Override
    public void tick() {

        if (target == null || target.isDead()) {
            mob.getPathfinder().stopPathfinding();
            return;
        }
        if (arena.state != GameState.ИГРА) {
            return;
        }

        if (tick % 5 == 0) { //моб тикает через раз, так что реально будет 10

            final PathResult path = mob.getPathfinder().findPath(target);

            if (path != null) {
                boolean done = mob.getPathfinder().moveTo(path);
            } else {
            }
        }

        tick++;

        if (mob.getLocation().distanceSquared(target.getLocation()) > 30) {
            Vector inverseDirectionVec = target.getLocation().getDirection().normalize().multiply(-1);
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

}
