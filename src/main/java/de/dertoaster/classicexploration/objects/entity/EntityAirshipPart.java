package de.dertoaster.classicexploration.objects.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.entity.PartEntity;

public class EntityAirshipPart extends PartEntity<EntityAirship> {
    public final String name;
    private final EntityDimensions size;

    public EntityAirshipPart(EntityAirship parent, String name, float width, float height) {
        super(parent);
        this.name = name;
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public boolean isPickable() {
        EntityAirship parent = this.getParent();
        return parent != null && !parent.isBalloonDestroyed();
    }

    @Override
    public boolean canBeCollidedWith() {
        EntityAirship parent = this.getParent();
        return parent != null && !parent.isBalloonDestroyed();
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        EntityAirship parent = this.getParent();
        if (parent == null || this.isInvulnerableTo(source) || parent.isBalloonDestroyed()) {
            return false;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            if (!this.level().isClientSide) {
                parent.damageBalloon();
                parent.hurt(source, amount);
            }
            return true;
        }
        return parent.hurt(source, amount);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
