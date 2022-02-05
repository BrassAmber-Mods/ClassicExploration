package de.dertoaster.classicexploration.objects.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
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
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityAirship extends Entity implements IInventory, INamedContainerProvider {

	enum E_AIRSHIP_EQUIPMENT {
		NONE, CHEST, CANNON, LAMP
	}

	protected Inventory inventory;

	// TODO: Copy over behavior from EntityBoat
	private float deltaRotation;

	private boolean inputLeft = false;
	private boolean inputRight = false;
	private boolean inputForward = false;
	private boolean inputBackward = false;
	private boolean inputAscend = false;
	private boolean inputDescend = false;
	
	private float airshipSpeed = 0.0F;

	private static final DataParameter<Boolean> DATA_ID_HAS_FUEL = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);
	
	private static final DataParameter<Integer> DATA_ID_BALLOON_COLOR = EntityDataManager.defineId(EntityAirship.class, DataSerializers.INT);
	private static final DataParameter<Integer> DATA_ID_EQUIPMENT = EntityDataManager.defineId(EntityAirship.class, DataSerializers.INT);

	//TODO: SPlitu into fuel engine left and create engine class that stores all engine related things
	private int remainingFuelBurnTime = 0;

	public EntityAirship(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.set(DATA_ID_HAS_FUEL, false);
		this.entityData.set(DATA_ID_BALLOON_COLOR, DyeColor.WHITE.getColorValue());
	}
	
	public DyeColor getBalloonColor() {
		return DyeColor.byId(this.entityData.get(DATA_ID_BALLOON_COLOR));
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt) {
		this.entityData.set(DATA_ID_BALLOON_COLOR, nbt.getInt("balloonColor"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
		nbt.putInt("balloonColor", this.getBalloonColor().getColorValue());
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
			//Consume fuel if the airship burns anything => descelerating or ascelerating and at least one engine is not stopped
			if(this.inputForward || this.inputBackward && !(this.inputLeft && this.inputRight)) {
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
				// TODO: Add in turning behavior and accelleration
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
				if(this.inputAscend ^ this.inputDescend) {
					if(this.inputAscend) {
						movementVert = 0.05D;
					}
					if(this.inputDescend) {
						movementVert = -0.05D;
					}
				}
				
				//Now, apply the speed
				if(this.inputForward || this.inputBackward) {
					Vector3d newVelocity = Vector3d.directionFromRotation(0.0F, this.yRot);
					newVelocity = newVelocity.normalize().scale(this.airshipSpeed).add(0, movementVert, 0);
					
					//Now apply to the current velocity
					this.setDeltaMovement(this.getDeltaMovement().add(newVelocity));
				}
			}
		}
	}

	private float getMaxSpeed() {
		return 1.0F;
	}

	private void controlAirship() {
		if (this.isVehicle()) {
			float f = 0.0F;
			if (this.inputLeft) {
				--this.deltaRotation;
			}

			if (this.inputRight) {
				++this.deltaRotation;
			}

			if (this.inputRight != this.inputLeft && !this.inputForward && !this.inputBackward) {
				f += 0.005F;
			}

			this.yRot += this.deltaRotation;
			if (this.inputForward) {
				f += 0.04F;
			}

			if (this.inputBackward) {
				f -= 0.005F;
			}

			double dy = 0.0D;
			if (this.inputAscend) {
			}
			if (this.inputDescend) {
			}

			this.setDeltaMovement(this.getDeltaMovement().add((double) (MathHelper.sin(-this.yRot * ((float) Math.PI / 180F)) * f), dy, (double) (MathHelper.cos(this.yRot * ((float) Math.PI / 180F)) * f)));
		}
	}

	protected int getBurnDuration(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		} else {
			//Item item = fuel.getItem();
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
		
		this.inputForward= accelerate;
		this.inputBackward = decelerate;
		this.inputLeft = stopLeftEngine;
		this.inputRight = stopRightEngine;
		this.inputAscend = ascend;
		this.inputDescend = descend;
	}

	//Inventory shit
	
	@Override
	public void clearContent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		// TODO Auto-generated method stub
		return null;
	}

	// 29 for chest, 3 for cannon, 2 for none (2 fuel slots
	@Override
	public int getContainerSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getItem(int p_70301_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack removeItemNoUpdate(int p_70304_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
		// TODO Auto-generated method stub
		
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
	
}
