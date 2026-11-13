package de.dertoaster.classicexploration.objects.entity;

import javax.annotation.Nullable;

import de.dertoaster.classicexploration.init.CEItemTags;
import de.dertoaster.classicexploration.init.CEItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EntityAirship extends Entity implements Container, MenuProvider, GeoEntity {

    enum E_AIRSHIP_EQUIPMENT {
        NONE, CHEST, CANNON, LAMP
    }

    protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);

    private boolean inputLeft = false;
    private boolean inputRight = false;
    private boolean inputForward = false;
    private boolean inputBackward = false;
    private boolean inputAscend = false;
    private boolean inputDescend = false;

    private float airshipSpeed = 0.0F;
    private float deltaRotation = 0.0F;

    private static final EntityDataAccessor<Boolean> DATA_ID_HAS_FUEL = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_HAS_CHEST = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_ENGINE_LEFT_ACTIVE = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_ENGINE_RIGHT_ACTIVE = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_BALLOON_COLOR = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_LEFT_HAS_FUEL = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_RIGHT_HAS_FUEL = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_LEFT_FUEL_BURN_TIME = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_RIGHT_FUEL_BURN_TIME = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ID_BALLOON_DESTROYED = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ID_BALLOON_HEALTH = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_HEALTH = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURT_TIME = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_HURT_DIR = SynchedEntityData.defineId(EntityAirship.class, EntityDataSerializers.FLOAT);

    private int leftFuelBurnTime = 0;
    private int rightFuelBurnTime = 0;
    private int arrowFireCooldown = 0;
    private float health = 20.0F;
    private float hurtDirO = 0.0F;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Client-side interpolation (similar a barcos)
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    private final EntityAirshipPart balloonPart;
    private final PartEntity<?>[] parts;

    public EntityAirship(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.balloonPart = new EntityAirshipPart(this, "balloon", 3.0F, 3.0F);
        this.parts = new PartEntity<?>[]{this.balloonPart};
        this.tickPartPositions();
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        this.tickPartPositions();
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.parts.length; i++) {
            this.parts[i].setId(id + i + 1);
        }
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.parts;
    }

    private void tickPartPositions() {
        if (this.balloonPart == null || this.isRemoved() || this.isBalloonDestroyed()) {
            return;
        }
        double yOffset = 1.8D; // aproximación: globo sobre la base de la nave
        double x = this.getX();
        double y = this.getY() + yOffset;
        double z = this.getZ();

        this.balloonPart.xo = this.balloonPart.getX();
        this.balloonPart.yo = this.balloonPart.getY();
        this.balloonPart.zo = this.balloonPart.getZ();
        this.balloonPart.yRotO = this.balloonPart.getYRot();
        this.balloonPart.xRotO = this.balloonPart.getXRot();
        this.balloonPart.setPos(x, y, z);
        this.balloonPart.hasImpulse = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_HAS_FUEL, false);
        builder.define(DATA_ID_BALLOON_COLOR, DyeColor.WHITE.getId());
        builder.define(DATA_ID_HAS_CHEST, false);
        builder.define(DATA_ID_ENGINE_LEFT_ACTIVE, false);
        builder.define(DATA_ID_ENGINE_RIGHT_ACTIVE, false);
        builder.define(DATA_ID_LEFT_HAS_FUEL, false);
        builder.define(DATA_ID_RIGHT_HAS_FUEL, false);
        builder.define(DATA_ID_LEFT_FUEL_BURN_TIME, 0);
        builder.define(DATA_ID_RIGHT_FUEL_BURN_TIME, 0);
        builder.define(DATA_ID_BALLOON_DESTROYED, false);
        builder.define(DATA_ID_BALLOON_HEALTH, 4);
        builder.define(DATA_ID_HEALTH, 20.0F);
        builder.define(DATA_ID_HURT_TIME, 0);
        builder.define(DATA_ID_HURT_DIR, 0.0F);
    }

    public boolean hasChest() {
        return this.entityData.get(DATA_ID_HAS_CHEST);
    }

    public void setHasChest(boolean value) {
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_ID_HAS_CHEST, value);
        }
    }

    public DyeColor getBalloonColor() {
        return DyeColor.byId(this.entityData.get(DATA_ID_BALLOON_COLOR));
    }

    public void setBalloonColor(DyeColor color) {
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_ID_BALLOON_COLOR, color.getId());
        }
    }

    public int getBalloonHealth() {
        return this.entityData.get(DATA_ID_BALLOON_HEALTH);
    }

    public boolean isBalloonDestroyed() {
        return this.entityData.get(DATA_ID_BALLOON_DESTROYED);
    }

    public void damageBalloon() {
        if (this.isBalloonDestroyed()) {
            return;
        }
        int currentHealth = this.getBalloonHealth();
        int newHealth = Math.max(0, currentHealth - 1);
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_ID_BALLOON_HEALTH, newHealth);
        }
        if (newHealth <= 0) {
            this.destroyBalloon();
        }
    }

    public void destroyBalloon() {
        if (this.isBalloonDestroyed()) {
            return;
        }
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_ID_BALLOON_DESTROYED, true);
            this.entityData.set(DATA_ID_BALLOON_HEALTH, 0);
        }
        this.setNoGravity(false);
        if (this.balloonPart != null) {
            this.balloonPart.setPos(this.getX(), this.getY() - 1000.0D, this.getZ());
        }
    }

    public void restoreBalloon() {
        if (!this.isBalloonDestroyed()) {
            return;
        }
        if (!this.level().isClientSide) {
            this.entityData.set(DATA_ID_BALLOON_DESTROYED, false);
            this.entityData.set(DATA_ID_BALLOON_HEALTH, 4);
        }
        this.setNoGravity(true);
        this.tickPartPositions();
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.isRemoved() || this.level().isClientSide) {
            return false;
        }
        this.health = Math.max(0.0F, this.health - amount);
        this.entityData.set(DATA_ID_HEALTH, this.health);
        if (this.health <= 0.0F) {
            this.die(source);
            return true;
        }
        // Balanceo al ser golpeado, como el barco
        this.hurtDirO = this.getHurtDir();
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        return true;
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT_TIME);
    }

    public void setHurtTime(int time) {
        this.entityData.set(DATA_ID_HURT_TIME, time);
    }

    public float getHurtDir() {
        return this.entityData.get(DATA_ID_HURT_DIR);
    }

    public void setHurtDir(float dir) {
        this.entityData.set(DATA_ID_HURT_DIR, dir);
    }

    public float getHurtDirO() {
        return this.hurtDirO;
    }

    private void die(net.minecraft.world.damagesource.DamageSource source) {
        this.remove(Entity.RemovalReason.KILLED);
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            if (!this.isBalloonDestroyed()) {
                // Globo intacto: suelta el ítem de la nave
                this.spawnAtLocation(CEItems.ITEM_AIRSHIP.get());
            } else {
                // Globo destruido: suelta dispenser, 2 motores, barco de roble y 4 hilos
                this.spawnAtLocation(Blocks.DISPENSER.asItem());
                this.spawnAtLocation(CEItems.ITEM_ENGINE.get());
                this.spawnAtLocation(CEItems.ITEM_ENGINE.get());
                this.spawnAtLocation(Items.OAK_BOAT);
                for (int i = 0; i < 4; i++) {
                    this.spawnAtLocation(Items.STRING);
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.entityData.set(DATA_ID_BALLOON_COLOR, tag.getInt("balloonColor"));
        if (tag.contains("balloonDestroyed")) {
            this.entityData.set(DATA_ID_BALLOON_DESTROYED, tag.getBoolean("balloonDestroyed"));
        }
        if (tag.contains("balloonHealth")) {
            this.entityData.set(DATA_ID_BALLOON_HEALTH, Math.max(0, Math.min(4, tag.getInt("balloonHealth"))));
        }
        if (tag.contains("health")) {
            this.health = tag.getFloat("health");
            this.entityData.set(DATA_ID_HEALTH, this.health);
        }
        this.setHasChest(tag.getBoolean("hasChest"));
        if (tag.contains("leftFuelBurnTime") || tag.contains("rightFuelBurnTime")) {
            this.leftFuelBurnTime = tag.getInt("leftFuelBurnTime");
            this.rightFuelBurnTime = tag.getInt("rightFuelBurnTime");
        } else {
            // Compatibilidad con datos antiguos: repartir el combustible previo
            int legacy = tag.getInt("fuelBurnTime");
            this.leftFuelBurnTime = legacy / 2;
            this.rightFuelBurnTime = legacy - this.leftFuelBurnTime;
        }
        this.airshipSpeed = tag.getFloat("airshipSpeed");
        this.entityData.set(DATA_ID_LEFT_HAS_FUEL, this.leftFuelBurnTime > 0);
        this.entityData.set(DATA_ID_RIGHT_HAS_FUEL, this.rightFuelBurnTime > 0);
        this.entityData.set(DATA_ID_LEFT_FUEL_BURN_TIME, this.leftFuelBurnTime);
        this.entityData.set(DATA_ID_RIGHT_FUEL_BURN_TIME, this.rightFuelBurnTime);
        this.entityData.set(DATA_ID_HAS_FUEL, this.leftFuelBurnTime > 0 || this.rightFuelBurnTime > 0);
        ContainerHelper.loadAllItems(tag, this.itemStacks, this.registryAccess());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("balloonColor", this.getBalloonColor().getId());
        tag.putBoolean("balloonDestroyed", this.isBalloonDestroyed());
        tag.putInt("balloonHealth", this.getBalloonHealth());
        tag.putFloat("health", this.health);
        tag.putBoolean("hasChest", this.hasChest());
        tag.putInt("leftFuelBurnTime", this.leftFuelBurnTime);
        tag.putInt("rightFuelBurnTime", this.rightFuelBurnTime);
        tag.putFloat("airshipSpeed", this.airshipSpeed);
        ContainerHelper.saveAllItems(tag, this.itemStacks, this.registryAccess());
    }

    public boolean canCollideWith(Entity other) {
        return canVehicleCollide(this, other);
    }

    public static boolean canVehicleCollide(Entity a, Entity b) {
        return (b.canBeCollidedWith() || b.isPushable()) && !a.isPassengerOfSameVehicle(b);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    protected int getMaxPassengers() {
        return 2;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < this.getMaxPassengers();
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof Boat || entity instanceof EntityAirship) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();

        if (this.isBalloonDestroyed() || this.getBalloonHealth() <= 0) {
            this.setNoGravity(false);
            if (!this.level().isClientSide || this.isControlledByLocalInstance()) {
                Vec3 motion = this.getDeltaMovement();
                if (!this.onGround()) {
                    motion = motion.add(0.0D, -0.04D, 0.0D);
                }
                motion = new Vec3(motion.x * 0.98D, motion.y * 0.98D, motion.z * 0.98D);
                this.setDeltaMovement(motion);
                this.move(MoverType.SELF, motion);
            }
            return;
        }

        boolean controlledByLocal = this.isControlledByLocalInstance();

        if (this.level().isClientSide) {
            EntityAirshipControl.updateClientControls(this);
            // Como los barcos: el cliente que controla también simula el movimiento localmente.
            // Los clientes remotos solo interpolan (lerp).
            if (!controlledByLocal) {
                this.tickPartPositions();
                return;
            }
        }

        this.deltaRotation = 0.0F;

        if (!this.level().isClientSide) {
            this.tryRefuelFromInventory();
            if (this.arrowFireCooldown > 0) {
                this.arrowFireCooldown--;
            }
        }

        boolean hasPilot = this.getControllingPassenger() instanceof Player;
        boolean leftHasFuel = (!this.level().isClientSide ? this.leftFuelBurnTime > 0 : this.entityData.get(DATA_ID_LEFT_HAS_FUEL));
        boolean rightHasFuel = (!this.level().isClientSide ? this.rightFuelBurnTime > 0 : this.entityData.get(DATA_ID_RIGHT_HAS_FUEL));
        boolean anyFuel = leftHasFuel || rightHasFuel;

        // Sin piloto: velocidad a 0, sin consumo de combustible y caída lenta
        if (!hasPilot) {
            this.airshipSpeed = 0.0F;
            double fall = this.onGround() ? 0.0D : -0.02D;
            if (fall != 0.0D) {
                this.move(MoverType.SELF, new Vec3(0.0D, fall, 0.0D));
            }
            if (!this.level().isClientSide) {
                this.entityData.set(DATA_ID_ENGINE_LEFT_ACTIVE, false);
                this.entityData.set(DATA_ID_ENGINE_RIGHT_ACTIVE, false);
                this.entityData.set(DATA_ID_LEFT_HAS_FUEL, this.leftFuelBurnTime > 0);
                this.entityData.set(DATA_ID_RIGHT_HAS_FUEL, this.rightFuelBurnTime > 0);
                this.entityData.set(DATA_ID_LEFT_FUEL_BURN_TIME, this.leftFuelBurnTime);
                this.entityData.set(DATA_ID_RIGHT_FUEL_BURN_TIME, this.rightFuelBurnTime);
                this.entityData.set(DATA_ID_HAS_FUEL, this.leftFuelBurnTime > 0 || this.rightFuelBurnTime > 0);
            }
            this.tickPartPositions();
            return;
        }

        // Con piloto pero sin combustible en ambos motores: cae lentamente y no responde a controles
        if (!anyFuel) {
            this.airshipSpeed = 0.0F;
            double fall = this.onGround() ? 0.0D : -0.02D;
            if (fall != 0.0D) {
                this.move(MoverType.SELF, new Vec3(0.0D, fall, 0.0D));
            }
            if (!this.level().isClientSide) {
                this.entityData.set(DATA_ID_ENGINE_LEFT_ACTIVE, false);
                this.entityData.set(DATA_ID_ENGINE_RIGHT_ACTIVE, false);
                this.entityData.set(DATA_ID_LEFT_HAS_FUEL, this.leftFuelBurnTime > 0);
                this.entityData.set(DATA_ID_RIGHT_HAS_FUEL, this.rightFuelBurnTime > 0);
                this.entityData.set(DATA_ID_LEFT_FUEL_BURN_TIME, this.leftFuelBurnTime);
                this.entityData.set(DATA_ID_RIGHT_FUEL_BURN_TIME, this.rightFuelBurnTime);
                this.entityData.set(DATA_ID_HAS_FUEL, this.leftFuelBurnTime > 0 || this.rightFuelBurnTime > 0);
            }
            this.tickPartPositions();
            return;
        }

        boolean hasInput = this.inputForward || this.inputBackward || this.inputLeft || this.inputRight || this.inputAscend || this.inputDescend;

        // Motor disponible = tiene combustible (y no se apaga por input de giro lateral, igual que antes)
        boolean leftAvailable = leftHasFuel && !this.inputLeft;
        boolean rightAvailable = rightHasFuel && !this.inputRight;

        // Giro por input de dirección: solo si el motor de ese lado está encendido (tiene combustible)
        if (this.inputLeft ^ this.inputRight) {
            if (this.inputLeft && leftHasFuel) {
                this.deltaRotation += -1.5F;
            } else if (this.inputRight && rightHasFuel) {
                this.deltaRotation += 1.5F;
            }
        }

        // Si intenta avanzar y un motor está apagado por falta de combustible, gira hacia el lado del motor apagado
        if (this.inputForward) {
            if (!leftHasFuel && rightHasFuel) {
                this.deltaRotation += -1.5F; // motor izquierdo apagado -> gira a la izquierda
            } else if (leftHasFuel && !rightHasFuel) {
                this.deltaRotation += 1.5F; // motor derecho apagado -> gira a la derecha
            }
        }

        if (this.deltaRotation != 0.0F) {
            this.setYRot(this.getYRot() + this.deltaRotation);
        }

        int thrustEngines = (leftAvailable ? 1 : 0) + (rightAvailable ? 1 : 0);

        // Control de velocidad: solo si hay empuje disponible
        if (thrustEngines > 0) {
            float accelFactor = thrustEngines / 2.0F;
            if (this.inputForward) {
                this.airshipSpeed = Math.min(this.getMaxSpeed() * accelFactor, this.airshipSpeed + 0.02F * accelFactor);
            } else if (this.inputBackward) {
                this.airshipSpeed = Math.max(-0.5F * this.getMaxSpeed() * accelFactor, this.airshipSpeed - 0.02F * accelFactor);
            } else {
                this.airshipSpeed *= 0.99F;
            }
        } else {
            this.airshipSpeed *= 0.98F;
        }
        if (Math.abs(this.airshipSpeed) < 0.005F) this.airshipSpeed = 0.0F;

        double movementVert = 0.0D;
        if (this.inputAscend ^ this.inputDescend) {
            if (thrustEngines > 0) {
                movementVert = (this.inputAscend ? 0.08D : -0.08D) * (thrustEngines / 2.0D);
            }
        }

        boolean leftEngineActive = leftAvailable && hasInput && thrustEngines > 0;
        boolean rightEngineActive = rightAvailable && hasInput && thrustEngines > 0;
        // Si solo uno tiene empuje por falta de combustible del otro, mantener su hélice activa con input
        if (hasInput) {
            leftEngineActive = leftHasFuel && !this.inputLeft && (leftHasFuel || rightHasFuel);
            rightEngineActive = rightHasFuel && !this.inputRight && (leftHasFuel || rightHasFuel);
            // Si no hay empuje efectivo (p.ej. ambos apagados por giro) y no hay movimiento vertical posible, parar hélices
            if (thrustEngines == 0 && movementVert == 0.0D && !this.inputForward && !this.inputBackward) {
                // mantener lógica básica por input salvo que no haya combustible
                leftEngineActive = leftHasFuel && hasInput && !this.inputLeft;
                rightEngineActive = rightHasFuel && hasInput && !this.inputRight;
            }
        }

        // Ajuste final: hélices giran con input de teclado si el motor tiene combustible y no está desactivado por giro
        leftEngineActive = leftHasFuel && hasInput && !this.inputLeft;
        rightEngineActive = rightHasFuel && hasInput && !this.inputRight;

        if (!this.level().isClientSide) {
            this.entityData.set(DATA_ID_ENGINE_LEFT_ACTIVE, leftEngineActive);
            this.entityData.set(DATA_ID_ENGINE_RIGHT_ACTIVE, rightEngineActive);
        }

        if (!this.level().isClientSide) {
            if (hasPilot) {
                if (this.leftFuelBurnTime > 0) {
                    this.leftFuelBurnTime = Math.max(0, this.leftFuelBurnTime - 1);
                }
                if (this.rightFuelBurnTime > 0) {
                    this.rightFuelBurnTime = Math.max(0, this.rightFuelBurnTime - 1);
                }
            }
            this.entityData.set(DATA_ID_LEFT_HAS_FUEL, this.leftFuelBurnTime > 0);
            this.entityData.set(DATA_ID_RIGHT_HAS_FUEL, this.rightFuelBurnTime > 0);
            this.entityData.set(DATA_ID_LEFT_FUEL_BURN_TIME, this.leftFuelBurnTime);
            this.entityData.set(DATA_ID_RIGHT_FUEL_BURN_TIME, this.rightFuelBurnTime);
            this.entityData.set(DATA_ID_HAS_FUEL, this.leftFuelBurnTime > 0 || this.rightFuelBurnTime > 0);
        }

        if (this.airshipSpeed != 0.0F || movementVert != 0.0D) {
            Vec3 horizontal = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize().scale(this.airshipSpeed);
            this.move(MoverType.SELF, horizontal.add(0.0D, movementVert, 0.0D));
        }

        // Resetear fallDistance de pasajeros para evitar daño por caída al bajarse
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof net.minecraft.world.entity.LivingEntity living) {
                living.fallDistance = 0.0F;
            }
        }

        this.tickPartPositions();
    }

    private void tryRefuelFromInventory() {
        // Slot 0 (arriba) -> motor izquierdo, Slot 1 (abajo) -> motor derecho
        this.tryRefuelMotorFromSlot(0, true);
        this.tryRefuelMotorFromSlot(1, false);
    }

    private void tryRefuelMotorFromSlot(int slot, boolean leftMotor) {
        int current = leftMotor ? this.leftFuelBurnTime : this.rightFuelBurnTime;
        if (current > 0 || slot < 0 || slot >= this.itemStacks.size()) {
            return;
        }
        ItemStack stack = this.itemStacks.get(slot);
        if (stack.isEmpty()) return;
        int burn = this.getBurnDuration(stack);
        if (burn <= 0) return;

        if (leftMotor) {
            this.leftFuelBurnTime += burn;
        } else {
            this.rightFuelBurnTime += burn;
        }

        ItemStack remainder = stack.getCraftingRemainingItem();
        stack.shrink(1);
        if (stack.isEmpty()) {
            this.itemStacks.set(slot, remainder);
        } else if (!remainder.isEmpty()) {
            if (!this.tryInsertStack(remainder)) {
                this.spawnAtLocation(remainder);
            }
        }
        this.setChanged();
    }

    private boolean tryInsertStack(ItemStack stack) {
        for (int i = 0; i < this.itemStacks.size(); i++) {
            ItemStack existing = this.itemStacks.get(i);
            if (existing.isEmpty()) {
                this.itemStacks.set(i, stack.copy());
                return true;
            }
            if (ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize()) {
                int canAdd = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(canAdd);
                stack.shrink(canAdd);
                if (stack.isEmpty()) return true;
            }
        }
        return stack.isEmpty();
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity living ? living : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions passengerDimensions, float partialTick) {
        // Sentar al jugador más abajo, a 0.1 bloques desde la base de la entidad
        return new Vec3(0.0D, 0.1D, 0.0D).yRot(-this.getYRot() * ((float) Math.PI / 180F));
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        int passengerIndex = this.getPassengers().indexOf(passenger);
        if (passengerIndex > 0) {
            // Segundo pasajero: sentarse a la derecha del piloto
            passenger.moveRelative(0.0F, new Vec3(0.6D, 0.0D, 0.0D).yRot(-this.getYRot() * ((float) Math.PI / 180F)));
        }
        if (!passenger.getType().is(EntityTypeTags.CAN_TURN_IN_BOATS)) {
            passenger.setYRot(passenger.getYRot() + this.deltaRotation);
            passenger.setYHeadRot(passenger.getYHeadRot() + this.deltaRotation);
            this.clampRotation(passenger);
        }
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        this.clampRotation(passenger);
    }

    protected void clampRotation(Entity passenger) {
        passenger.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        passenger.yRotO += f1 - f;
        passenger.setYRot(passenger.getYRot() + f1 - f);
        passenger.setYHeadRot(passenger.getYRot());
    }

    // Passenger positioning can be refined for 1.21.1 attachment points if needed.

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) {
            return ret;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (this.isBalloonDestroyed() && stack.is(CEItems.ITEM_BALLOON.get())) {
            if (!this.level().isClientSide) {
                this.restoreBalloon();
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor dyeColor = dyeItem.getDyeColor();
            if (this.getBalloonColor() != dyeColor) {
                if (!this.level().isClientSide) {
                    this.setBalloonColor(dyeColor);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.CONSUME;
        }

        if (stack.is(CEItemTags.CHESTS)) {
            if (!this.hasChest()) {
                this.setHasChest(true);
                stack.shrink(1);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        if (!this.isBalloonDestroyed() && stack.getItem() instanceof DyeItem) {
            // Tinte aplicado encima (ya manejado más arriba); no hace falta nada más aquí
        }

        if (player.isSecondaryUseActive()) {
            // Shift + click: open inventory/menu when possible
            if (!this.level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                this.openInventoryFor(serverPlayer);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (!player.isCrouching()) {
            if (this.isVehicle()) {
                return InteractionResult.PASS;
            } else if (!this.level().isClientSide) {
                return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                return InteractionResult.SUCCESS;
            }
        }

        // Crouching with empty/other hand: try to open menu as convenience
        if (!this.level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            this.openInventoryFor(serverPlayer);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public void openInventoryFor(net.minecraft.server.level.ServerPlayer player) {
        player.openMenu(this, buf -> {
            buf.writeInt(this.getId());
            buf.writeBoolean(this.hasChest());
        });
    }

    private float getMaxSpeed() {
        return 1.0F;
    }

    protected int getBurnDuration(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        int burnTime = fuel.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        return Math.max(0, burnTime);
    }

    public int getLeftFuelBurnTime() {
        return this.level().isClientSide ? this.entityData.get(DATA_ID_LEFT_FUEL_BURN_TIME) : this.leftFuelBurnTime;
    }

    public int getRightFuelBurnTime() {
        return this.level().isClientSide ? this.entityData.get(DATA_ID_RIGHT_FUEL_BURN_TIME) : this.rightFuelBurnTime;
    }

    public void processControlInputs(boolean[] controlValues) {
        this.inputForward = controlValues[0];
        this.inputLeft = controlValues[1];
        this.inputRight = controlValues[2];
        this.inputBackward = controlValues[3];
        this.inputAscend = controlValues[4];
        this.inputDescend = controlValues[5];
    }

    public boolean shootArrow() {
        if (this.level().isClientSide) {
            return false;
        }
        if (this.arrowFireCooldown > 0) {
            return false;
        }
        LivingEntity pilot = this.getControllingPassenger();
        if (!(pilot instanceof Player player)) {
            return false;
        }

        for (int slot = 0; slot < this.getContainerSize(); slot++) {
            ItemStack stack = this.getItem(slot);
            if (stack.isEmpty() || !stack.is(ItemTags.ARROWS)) {
                continue;
            }

            ItemStack arrowStack = stack.copyWithCount(1);
            ArrowItem arrowItem = stack.getItem() instanceof ArrowItem ai ? ai : (ArrowItem) Items.ARROW;
            AbstractArrow arrow = arrowItem.createArrow(this.level(), arrowStack, player, null);
            Vec3 shootingPos = position().add(new Vec3(0,0,1).yRot((float) Math.toRadians(-getYRot())));
            arrow.setPos(shootingPos.x, shootingPos.y, shootingPos.z);
            arrow.shootFromRotation(this, 0.0F, this.getYRot(), 0.0F, 3.0F, 1.0F);

            stack.shrink(1);
            if (stack.isEmpty()) {
                this.setItem(slot, ItemStack.EMPTY);
            }
            this.setChanged();
            this.level().addFreshEntity(arrow);
            this.arrowFireCooldown = 20;
            return true;
        }
        return false;
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new de.dertoaster.classicexploration.inventory.AirshipMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    public static final int FUEL_SLOT_COUNT = 2;
    public static final int CHEST_SLOT_COUNT = 12; // 4 columns x 3 rows (según GUI con cofre)

    @Override
    public int getContainerSize() {
        return FUEL_SLOT_COUNT + (this.hasChest() ? CHEST_SLOT_COUNT : 0);
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.itemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= this.itemStacks.size()) {
            return ItemStack.EMPTY;
        }
        return this.itemStacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.itemStacks, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = this.itemStacks.get(slot);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.itemStacks.set(slot, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.itemStacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
            return;
        }

        if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            this.lerpSteps--;
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = steps;
    }

    @Override
    public double lerpTargetX() {
        return this.lerpSteps > 0 ? this.lerpX : this.getX();
    }

    @Override
    public double lerpTargetY() {
        return this.lerpSteps > 0 ? this.lerpY : this.getY();
    }

    @Override
    public double lerpTargetZ() {
        return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
    }

    @Override
    public float lerpTargetXRot() {
        return this.lerpSteps > 0 ? (float) this.lerpXRot : this.getXRot();
    }

    @Override
    public float lerpTargetYRot() {
        return this.lerpSteps > 0 ? (float) this.lerpYRot : this.getYRot();
    }

    private static final RawAnimation LEFT_MOTOR_ROTATE = RawAnimation.begin().thenLoop("animation.airship.rotarLeft.rotateClockwise");
    private static final RawAnimation RIGHT_MOTOR_ROTATE = RawAnimation.begin().thenLoop("animation.airship.rotarRight.rotateClockwise");
    private static final RawAnimation ENGINE_NEUTRAL = RawAnimation.begin().thenLoop("animation.airship.enginePose.neutral");
    private static final RawAnimation ENGINE_TILT_UP = RawAnimation.begin().thenLoop("animation.airship.rotarLeft.enginePose.tiltUp");
    private static final RawAnimation ENGINE_TILT_DOWN = RawAnimation.begin().thenLoop("animation.airship.rotarLeft.enginePose.tiltDown");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "left_motor", 0, state -> {
            if (this.entityData.get(DATA_ID_ENGINE_LEFT_ACTIVE)) {
                state.setAndContinue(LEFT_MOTOR_ROTATE);
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "right_motor", 0, state -> {
            if (this.entityData.get(DATA_ID_ENGINE_RIGHT_ACTIVE)) {
                state.setAndContinue(RIGHT_MOTOR_ROTATE);
                return PlayState.CONTINUE;
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>(this, "inclination", 0, state -> {
            boolean tiltUp = this.inputAscend;
            boolean tiltDown = this.inputDescend;
            if (this.inputBackward) {
                boolean tmp = tiltUp;
                tiltUp = tiltDown;
                tiltDown = tmp;
            }

            if (tiltUp) {
                state.setAndContinue(ENGINE_TILT_UP);
                return PlayState.CONTINUE;
            }
            if (tiltDown) {
                state.setAndContinue(ENGINE_TILT_DOWN);
                return PlayState.CONTINUE;
            }
            state.setAndContinue(ENGINE_NEUTRAL);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

