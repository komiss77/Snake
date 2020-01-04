package ru.ostrov77.snake.customEntity;
/*
import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.server.v1_15_R1.Block;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.BlockStatePredicate;
import net.minecraft.server.v1_15_R1.Blocks;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.GameRules;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.PathfinderGoal;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory;

public class CustomPathfinderGoalEatTile extends PathfinderGoal
{
    private static final Predicate<IBlockData> a;
    private final EntityInsentient b;
    private final World c;
    private int d;
    
    static {
        a = BlockStatePredicate.a(Blocks.GRASS);
    }
    
    public CustomPathfinderGoalEatTile(final EntityInsentient entityinsentient) {
        this.b = entityinsentient;
        this.c = entityinsentient.world;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK, Type.JUMP));
    }
    
    @Override
    public boolean a() {
        //if (this.b.getRandom().nextInt(this.b.isBaby() ? 50 : 1000) != 0) {
            return false;
       // }
       // final BlockPosition blockposition = new BlockPosition(this.b);
       // return CustomPathfinderGoalEatTile.a.test(this.c.getType(blockposition)) || this.c.getType(blockposition.down()).getBlock() == Blocks.GRASS_BLOCK;
    }
    
    @Override
    public void c() {
        this.d = 40;
        this.c.broadcastEntityEffect(this.b, (byte)10);
        this.b.getNavigation().o();
    }
    
    @Override
    public void d() {
        this.d = 0;
    }
    
    @Override
    public boolean b() {
        return this.d > 0;
    }
    
    public int g() {
        return this.d;
    }
    
    @Override
    public void e() {
        this.d = Math.max(0, this.d - 1);
        if (this.d == 4) {
            final BlockPosition blockposition = new BlockPosition(this.b);
            if (CustomPathfinderGoalEatTile.a.test(this.c.getType(blockposition))) {
                if (!CraftEventFactory.callEntityChangeBlockEvent(this.b, blockposition, Blocks.AIR.getBlockData(), !this.c.getGameRules().getBoolean(GameRules.MOB_GRIEFING)).isCancelled()) {
                    this.c.b(blockposition, false);
                }
                this.b.blockEaten();
            }
            else {
                final BlockPosition blockposition2 = blockposition.down();
                if (this.c.getType(blockposition2).getBlock() == Blocks.GRASS_BLOCK) {
                    if (!CraftEventFactory.callEntityChangeBlockEvent(this.b, blockposition, Blocks.AIR.getBlockData(), !this.c.getGameRules().getBoolean(GameRules.MOB_GRIEFING)).isCancelled()) {
                        this.c.triggerEffect(2001, blockposition2, Block.getCombinedId(Blocks.GRASS_BLOCK.getBlockData()));
                        this.c.setTypeAndData(blockposition2, Blocks.DIRT.getBlockData(), 2);
                    }
                    this.b.blockEaten();
                }
            }
        }
    }
}
*/