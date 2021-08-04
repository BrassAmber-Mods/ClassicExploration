package de.dertoaster.classicexploration.objects.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class EntityAirship extends Entity implements IInventoryChangedListener {

	enum E_AIRSHIP_EQUIPMENT {
		NONE, CHEST, CANNON, LAMP
	}

	protected Inventory inventory;

	private static final DataParameter<Boolean> DATA_ID_HAS_FUEL = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> DATA_ID_STEERING_UP_OR_DOWN = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BYTE); 
	private static final DataParameter<Boolean> DATA_ID_ENGINE_LEFT_ACTIVE = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DATA_ID_ENGINE_RIGHT_ACTIVE = EntityDataManager.defineId(EntityAirship.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Integer> DATA_ID_EQUIPMENT = EntityDataManager.defineId(EntityAirship.class, DataSerializers.INT);

	private int remainingFuelBurnTime = 0;
	
	public EntityAirship(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);

		this.createInventory();
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {

	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return new SSpawnObjectPacket(this);
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

	private int getInventorySize() {
		// TODO: Check for chest attachment
		// 28 for chest, 2 for cannon, 1 for none
		return 1;
	}

	private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

	protected void createInventory() {
		Inventory inventory = this.inventory;
		this.inventory = new Inventory(this.getInventorySize());
		if (inventory != null) {
			inventory.removeListener(this);
			int i = Math.min(inventory.getContainerSize(), this.inventory.getContainerSize());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = inventory.getItem(j);
				if (!itemstack.isEmpty()) {
					this.inventory.setItem(j, itemstack.copy());
				}
			}
		}

		this.inventory.addListener(this);
		this.updateContainerEquipment();
		this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
	}

	private void updateContainerEquipment() {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing) {
		if (this.isAlive() && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler != null)
			return itemHandler.cast();
		return super.getCapability(capability, facing);
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		if (itemHandler != null) {
			net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
			itemHandler = null;
			oldHandler.invalidate();
		}
	}

	@Override
	public void tick() {
		super.tick();
		
		
		//TODO: Check each engine if it is active, then per engine add to the fuel consumption
		if(this.getControllingPassenger() == null) {
			//If we have no pilot, we just fall down slowly
			
			return;
		}
		
		if(this.remainingFuelBurnTime > 0) {
			boolean leftActive = this.entityData.get(DATA_ID_ENGINE_LEFT_ACTIVE);
			boolean rightActive = this.entityData.get(DATA_ID_ENGINE_RIGHT_ACTIVE);
			if(!this.entityData.get(DATA_ID_HAS_FUEL)) {
				this.entityData.set(DATA_ID_HAS_FUEL, true);
			}
			if(leftActive) {
				this.remainingFuelBurnTime--;
			}
			if(rightActive) {
				this.remainingFuelBurnTime--;
			}
			if(this.remainingFuelBurnTime <= 0) {
				this.entityData.set(DATA_ID_HAS_FUEL, false);
			} else {
				//TODO: Add in turning behavior and accelleration
				//Update velocity / direction
				if(leftActive ^ rightActive) {
					//If only one is active, the other one breaks. We will turn around the breaking side
					if(leftActive) {
						
					} else {
						
					}
				}
				//Both are active
				else if(leftActive) {
					
				}
				//None are active
				else {
					
				}
			}
		} 
	}
	
	protected int getBurnDuration(ItemStack fuel) {
	      if (fuel.isEmpty()) {
	         return 0;
	      } else {
	         Item item = fuel.getItem();
	         return net.minecraftforge.common.ForgeHooks.getBurnTime(fuel);
	      }
	   }

	@Override
	public void containerChanged(IInventory p_76316_1_) {
		// TODO Auto-generated method stub
		//TODO: Handle if we got new fuel, if yes => Play ignition sound

	}
	
}
