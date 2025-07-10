package grillo78.classicexploration.entities;

import grillo78.classicexploration.menu.InventoryMenu;
import grillo78.classicexploration.menu.ModMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

public class Airship extends Entity {
    private static final EntityDataAccessor<Boolean> UP_KEY = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DOWN_KEY = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LEFT_KEY = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RIGHT_KEY = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> JUMP_KEY = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CROUCHING = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FUEL_TIME = SynchedEntityData.defineId(Airship.class, EntityDataSerializers.INT);

    private ItemStackHandler itemStackHandler = new ItemStackHandler(14);
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private ResourceLocation lootTable;

    public Airship(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(UP_KEY, false);
        this.entityData.define(DOWN_KEY, false);
        this.entityData.define(LEFT_KEY, false);
        this.entityData.define(RIGHT_KEY, false);
        this.entityData.define(JUMP_KEY, false);
        this.entityData.define(CROUCHING, false);
        this.entityData.define(FUEL_TIME, 0);
    }

    public void setInput(boolean[] input) {
        this.entityData.set(UP_KEY, input[0]);
        this.entityData.set(DOWN_KEY, input[1]);
        this.entityData.set(LEFT_KEY, input[2]);
        this.entityData.set(RIGHT_KEY, input[3]);
        this.entityData.set(JUMP_KEY, input[4]);
        this.entityData.set(CROUCHING, input[5]);
    }

    public boolean[] getInput() {
        return new boolean[]{this.entityData.get(UP_KEY), this.entityData.get(DOWN_KEY), this.entityData.get(LEFT_KEY), this.entityData.get(RIGHT_KEY), this.entityData.get(JUMP_KEY), this.entityData.get(CROUCHING)};
    }

    @Override
    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            double d0 = this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset();
            pCallback.accept(pPassenger, this.getX(), d0, this.getZ());
        }
    }

    public double getFuelTime(){
        return entityData.get(FUEL_TIME);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.025;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (entityData.get(FUEL_TIME) > 0)
                entityData.set(FUEL_TIME, entityData.get(FUEL_TIME) - 1);
            else if (!itemStackHandler.getStackInSlot(12).isEmpty()) {
                entityData.set(FUEL_TIME, getMaxFuelTime());
                itemStackHandler.getStackInSlot(12).shrink(1);
            }
        }
            applyMovement();
        this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        this.move(MoverType.SELF, getDeltaMovement());
    }

    public int getMaxFuelTime() {
        return 1600;
    }

    private void applyMovement() {
        this.setDeltaMovement(getDeltaMovement().multiply(0.9, 0.9, 0.9));
        if (getDeltaMovement().length() < 0.001)
            this.setDeltaMovement(Vec3.ZERO);
        if (this.getPassengers().size() != 0 && entityData.get(FUEL_TIME) > 0) {
            double verticalMovement = getInput()[4] ? 0.5F : getInput()[0]
                    || getInput()[1]
                    || getInput()[2]
                    || getInput()[4] ? 0.0F : getInput()[5] ? -0.5 : 0;
            double forwardVectorScale = 0;
            float rotationY = 0;
            if (getInput()[0])
                forwardVectorScale = 0.25;
            if (getInput()[2])
                rotationY = -5F;
            if (getInput()[3])
                rotationY += 5;
            for (Entity passenger : getPassengers()) {
                passenger.setYRot(passenger.getYRot() + rotationY);
            }
            Vec3 movingVector = new Vec3(0, verticalMovement, forwardVectorScale).yRot((float) Math.toRadians(-getYRot())).add(getDeltaMovement()).scale(0.5);
            this.setDeltaMovement(movingVector);
            this.setYRot(getYRot() + rotationY);
        } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0,-0.025F,0));
        }

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        itemStackHandler.deserializeNBT(compoundTag.getCompound("inventory"));
        entityData.set(FUEL_TIME, compoundTag.getInt("fuel_time"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put("inventory", itemStackHandler.serializeNBT());
        compoundTag.putInt("fuel_time", entityData.get(FUEL_TIME));
    }

    public void lerpTo(double p_38299_, double p_38300_, double p_38301_, float p_38302_, float p_38303_, int p_38304_, boolean p_38305_) {
        this.lerpX = p_38299_;
        this.lerpY = p_38300_;
        this.lerpZ = p_38301_;
        this.lerpYRot = (double) p_38302_;
        this.lerpXRot = (double) p_38303_;
        this.lerpSteps = 10;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interact(Player p_38330_, InteractionHand p_38331_) {
        if (p_38330_.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!this.level().isClientSide) {
            if (p_38330_.startRiding(this)) {
                return InteractionResult.CONSUME;
            } else
                return InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    public void createMenu(Player pPlayer) {
        pPlayer.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> new InventoryMenu(ModMenus.INVENTORY_CONTAINER.get(), id, pPlayer), Component.empty()));
    }
}
