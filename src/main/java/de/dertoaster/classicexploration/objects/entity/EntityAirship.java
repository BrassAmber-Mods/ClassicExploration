package de.dertoaster.classicexploration.objects.entity;

import javax.annotation.Nullable;

import de.dertoaster.classicexploration.init.CEItemTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EntityAirship extends Entity implements IInventory, INamedContainerProvider, IAnimatable {

	enum E_AIRSHIP_EQUIPMENT {
		NONE, CHEST, CANNON, LAMP
	}

	protected Inventory inventory;
	protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);

	private boolean inputLeft = false;
	private boolean inputRight = false;
	private boolean inputForward = false;
	private boolean inputBackward = false;
	private boolean inputAscend = false;
	private boolean inputDescend = false;

	private float airshipSpeed = 0.0F;

	private static final DataParameter<Boolean> DATA_ID_HAS_FUEL = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DATA_ID_HAS_CHEST = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Boolean> DATA_ID_ENGINE_LEFT_ACTIVE = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DATA_ID_ENGINE_RIGHT_ACTIVE = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Integer> DATA_ID_BALLOON_COLOR = EntityDataManager.defineId(EntityAirship.class, DataSerializers.INT);

	// TODO: SPlitu into fuel engine left and create engine class that stores all engine related things
	private int remainingFuelBurnTime = 0;

	public EntityAirship(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_ID_HAS_FUEL, false);
		this.entityData.define(DATA_ID_BALLOON_COLOR, DyeColor.WHITE.getColorValue());
		this.entityData.define(DATA_ID_HAS_CHEST, false);
		this.entityData.define(DATA_ID_ENGINE_LEFT_ACTIVE, false);
		this.entityData.define(DATA_ID_ENGINE_RIGHT_ACTIVE, false);
	}

	public boolean hasChest() {
		return this.entityData.get(DATA_ID_HAS_CHEST);
	}

	public void setHasChest(boolean value) {
		if (!this.level.isClientSide) {
			this.entityData.set(DATA_ID_HAS_CHEST, value);
		}
	}

	public DyeColor getBalloonColor() {
		return DyeColor.byId(this.entityData.get(DATA_ID_BALLOON_COLOR));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt) {
		this.entityData.set(DATA_ID_BALLOON_COLOR, nbt.getInt("balloonColor"));
		this.setHasChest(nbt.getBoolean("hasChest"));

		ItemStackHelper.loadAllItems(nbt, this.itemStacks);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		nbt.putInt("balloonColor", this.getBalloonColor().getColorValue());
		nbt.putBoolean("hasChest", this.hasChest());

		ItemStackHelper.saveAllItems(nbt, this.itemStacks);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public boolean canCollideWith(Entity p_241849_1_) {
		return canVehicleCollide(this, p_241849_1_);
	}

	public static boolean canVehicleCollide(Entity p_242378_0_, Entity p_242378_1_) {
		return (p_242378_1_.canBeCollidedWith() || p_242378_1_.isPushable()) && !p_242378_0_.isPassengerOfSameVehicle(p_242378_1_);
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean isPushable() {
		return true;
	}

	protected Vector3d getRelativePortalPosition(Direction.Axis p_241839_1_, TeleportationRepositioner.Result p_241839_2_) {
		return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(p_241839_1_, p_241839_2_));
	}

	public void push(Entity p_70108_1_) {
		if (p_70108_1_ instanceof BoatEntity || p_70108_1_ instanceof EntityAirship) {
			if (p_70108_1_.getBoundingBox().minY < this.getBoundingBox().maxY) {
				super.push(p_70108_1_);
			}
		} else if (p_70108_1_.getBoundingBox().minY <= this.getBoundingBox().minY) {
			super.push(p_70108_1_);
		}

	}

	@Override
	public void tick() {
		super.tick();

		if (this.level.isClientSide) {
			// Apply inputs
			EntityAirshipControl.updateClientControls(this);
		}

		// DONE: Check each engine if it is active, then per engine add to the fuel consumption
		if (this.getControllingPassenger() == null) {
			// If we have no pilot, we just fall down slowly

			return;
		}

		if (this.remainingFuelBurnTime > 0) {
			if (!this.entityData.get(DATA_ID_HAS_FUEL)) {
				this.entityData.set(DATA_ID_HAS_FUEL, true);
			}
			// Consume fuel if the airship burns anything => descelerating or ascelerating and at least one engine is not stopped
			if (this.inputForward || this.inputBackward && !(this.inputLeft && this.inputRight)) {
				if (!this.inputLeft) {
					this.remainingFuelBurnTime--;
				}
				if (!this.inputRight) {
					this.remainingFuelBurnTime--;
				}
			}
			if (this.remainingFuelBurnTime <= 0) {
				this.entityData.set(DATA_ID_HAS_FUEL, false);
			} else {
				// Update velocity / direction
				if (this.inputLeft ^ this.inputRight) {
					// If only one is active, the other one brakes. We will turn around the braking side
					// DONE: Update rotation
					if (this.inputLeft) {
						this.yRot += 0.5F;
					} else {
						this.yRot -= 0.5F;
					}
					// Now, apply some velocity, but only if we accelerate or decellerate
				}
				// Accelerating
				if (this.inputForward && this.airshipSpeed <= this.getMaxSpeed()) {
					this.airshipSpeed += 0.05F;
				}
				// Decelerating
				if (this.inputBackward && this.airshipSpeed >= (-0.5F * this.getMaxSpeed())) {
					this.airshipSpeed -= 0.05F;
				}

				double movementVert = 0.0D;
				if (this.inputAscend ^ this.inputDescend) {
					if (this.inputAscend) {
						movementVert = 0.05D;
					}
					if (this.inputDescend) {
						movementVert = -0.05D;
					}
				}

				// Now, apply the speed
				if (this.inputForward || this.inputBackward) {
					Vector3d newVelocity = Vector3d.directionFromRotation(0.0F, this.yRot);
					newVelocity = newVelocity.normalize().scale(this.airshipSpeed).add(0, movementVert, 0);

					// Now apply to the current velocity
					this.move(MoverType.SELF, newVelocity);
				}
			}
		}
	}

	@Override
	public ActionResultType interact(PlayerEntity player, Hand hand) {
		ActionResultType ret = super.interact(player, hand);
		if (ret.consumesAction())
			return ret;
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem().is(CEItemTags.CHESTS)) {
			if (!this.hasChest()) {
				this.setHasChest(true);
				stack.shrink(1);
			}
		} else if (!player.isCrouching()) {
			if (player.isSecondaryUseActive()) {
				return ActionResultType.PASS;
			} else if (this.isVehicle()) {
				return ActionResultType.PASS;
			} else if (!this.level.isClientSide) {
				return player.startRiding(this) ? ActionResultType.CONSUME : ActionResultType.PASS;
			} else {
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.sidedSuccess(this.level.isClientSide);
	}

	private float getMaxSpeed() {
		return 1.0F;
	}

	protected int getBurnDuration(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		} else {
			// Item item = fuel.getItem();
			return net.minecraftforge.common.ForgeHooks.getBurnTime(fuel, IRecipeType.BLASTING);
		}
	}

	public void processControlInputs(boolean[] controlValues) {
		boolean accelerate = controlValues[0];
		boolean decelerate = controlValues[3];

		boolean stopRightEngine = controlValues[2];
		boolean stopLeftEngine = controlValues[1];

		boolean ascend = controlValues[4];
		boolean descend = controlValues[5];

		this.inputForward = accelerate;
		this.inputBackward = decelerate;
		this.inputLeft = stopLeftEngine;
		this.inputRight = stopRightEngine;
		this.inputAscend = ascend;
		this.inputDescend = descend;
	}

	// Inventory shit

	@Override
	public void clearContent() {
		this.itemStacks.clear();
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		// TODO Auto-generated method stub
		return null;
	}

	// 29 for chest, 3 for cannon, 2 for none (2 fuel slots
	@Override
	public int getContainerSize() {
		int defaultSize = 2;
		if (this.hasChest()) {
			defaultSize += 27;
		}
		return defaultSize;
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
	public ItemStack getItem(int p_70301_1_) {
		return this.itemStacks.get(p_70301_1_);
	}

	@Override
	public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
		return ItemStackHelper.removeItem(this.itemStacks, p_70298_1_, p_70298_2_);
	}

	@Override
	public ItemStack removeItemNoUpdate(int p_70304_1_) {
		ItemStack itemstack = this.itemStacks.get(p_70304_1_);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.itemStacks.set(p_70304_1_, ItemStack.EMPTY);
			return itemstack;
		}
	}

	@Override
	public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
		this.itemStacks.set(p_70299_1_, p_70299_2_);
		if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getMaxStackSize()) {
			p_70299_2_.setCount(this.getMaxStackSize());
		}
	}

	@Override
	public void setChanged() {

	}

	@Override
	public boolean stillValid(PlayerEntity p_70300_1_) {
		if (!this.isAlive()) {
			return false;
		} else {
			return !(p_70300_1_.distanceToSqr(this) > 64.0D);
		}
	}

	private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing) {
		if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return itemHandler.cast();
		return super.getCapability(capability, facing);
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		itemHandler.invalidate();
	}

	private AnimationFactory factory = new AnimationFactory(this);

	@Override
	public void registerControllers(AnimationData data) {
		// Engine tilting => up, down, neutral
		// Engine activity => left and right
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

}
