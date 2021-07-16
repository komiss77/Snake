package ru.ostrov77.snake.customEntity;

import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;







public class CustomSheep extends EntitySheep {

  /*  public static final EntityTypeRegistry TYPE = EntityTypeRegistry.SNAKE_SHEEP;

    
@SuppressWarnings("unchecked")
   public CustomSheep(EntityTypes<? extends Entity> entitytypes, World world)
   {
       super((EntityTypes<? extends EntityCreature>) TYPE.getEntityType(), world);
   }*/
   
    public CustomSheep(CraftWorld world) {
        super(EntityTypes.ax, world.getHandle());

        
        
       /*  try {
            Field goalSelectorMap = PathfinderGoalSelector.class.getDeclaredField("c");
            goalSelectorMap.setAccessible(true);
            goalSelectorMap.set(this.goalSelector, new EnumMap<>(PathfinderGoal.Type.class) );
            goalSelectorMap.set(this.targetSelector, new EnumMap<>(PathfinderGoal.Type.class) );
            goalSelectorMap.setAccessible(false);
            
            Field goalSelectorSet = PathfinderGoalSelector.class.getDeclaredField("d");
            goalSelectorSet.setAccessible(true);
            goalSelectorSet.set(this.goalSelector, Sets.newLinkedHashSet());
            goalSelectorSet.set(this.targetSelector, Sets.newLinkedHashSet());
            goalSelectorSet.setAccessible(false);
            
            CustomPathfinderGoalEatTile customGoalEatTile = new CustomPathfinderGoalEatTile(this);
            Field goalSelectorEat = EntitySheep.class.getDeclaredField("bA");
            goalSelectorEat.setAccessible(true);
            goalSelectorEat.set(this.bA, customGoalEatTile);
            goalSelectorEat.setAccessible(false);
            
            
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Main.log_err("Snake_sheep 1 "+ex.getMessage());
        }*/

    }
    
    @Override
    protected void initPathfinder() {
        
      //  this.bA = new PathfinderGoalEatTile(this);
     //   this.goalSelector.a(0, new PathfinderGoalFloat(this));
     //   this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25));
     //   this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0));
     //   this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.1, RecipeItemStack.a(Items.WHEAT), false));
      //  this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1));
      //  this.goalSelector.a(5, this.bA);
       // this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0));
       /// this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0f));
       // this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }
    
    

    @Override
    protected void mobTick() { //без эгото кидает тгдд, т.к. PathfinderGoalEatTile не иниц.
        //this.bz = this.bA.g();
       // super.mobTick();
    }
    
    
    
    
@Override
    public EnumInteractionResult b(final EntityHuman entityhuman, final EnumHand enumhand) {
       /* final ItemStack itemstack = entityhuman.b(enumhand);
        if (itemstack.getItem() != Items.SHEARS || this.isSheared() || this.isBaby()) {
            return super.a(entityhuman, enumhand);
        }
        final PlayerShearEntityEvent event = new PlayerShearEntityEvent((Player)entityhuman.getBukkitEntity(), this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.shear();
        if (!this.world.isClientSide) {
            itemstack.damage(1, entityhuman, entityhuman1 -> entityhuman1.d(enumhand));
        }*/
         return EnumInteractionResult.e;
    }    
    
    
    
    @Override
    public boolean canShear() {
        return false;
    }    
    
   // @Override
   // public void shear(final SoundCategory soundcategory) {
       /* if (!this.world.isClientSide) {
            this.setSheared(true);
            for (int i = 1 + this.random.nextInt(3), j = 0; j < i; ++j) {
                this.forceDrops = true;
                final EntityItem entityitem = this.a(EntitySheep.bx.get(this.getColor()), 1);
                this.forceDrops = false;
                if (entityitem != null) {
                    entityitem.setMot(entityitem.getMot().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
                }
            }
        }
        this.a(SoundEffects.ENTITY_SHEEP_SHEAR, 1.0f, 1.0f);*/
   // }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }












    
    
     

     
     
}
