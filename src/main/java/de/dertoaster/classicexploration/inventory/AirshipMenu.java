package de.dertoaster.classicexploration.inventory;

import de.dertoaster.classicexploration.init.CEMenus;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AirshipMenu extends AbstractContainerMenu {

    private final Container airshipContainer;
    private final EntityAirship airship;

    // Slot positions derived from GUI textures (airship_inventory.png / airship_chest_inventory.png, 256x256)
    // Positions are slot box top-left coordinates as detected from the textures, with +1px offset on both axes.
    public static final int FUEL_X = 134;
    public static final int FUEL_Y_TOP = 16;
    public static final int FUEL_Y_BOTTOM = 52;

    public static final int CHEST_X_START = 8;
    public static final int CHEST_Y_START = 16;
    public static final int CHEST_COLS = 4;
    public static final int CHEST_ROWS = 3;

    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 84;
    public static final int HOTBAR_Y = 142;

    public static AirshipMenu fromNetwork(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        boolean hasChest = buf.readBoolean();
        int size = EntityAirship.FUEL_SLOT_COUNT + (hasChest ? EntityAirship.CHEST_SLOT_COUNT : 0);
        EntityAirship airship = null;
        if (playerInventory.player.level().getEntity(entityId) instanceof EntityAirship found) {
            airship = found;
        }
        Container container = airship != null ? airship : new SimpleContainer(Math.max(size, EntityAirship.FUEL_SLOT_COUNT));
        return new AirshipMenu(containerId, playerInventory, container, airship, hasChest);
    }

    public AirshipMenu(int containerId, Inventory playerInventory, EntityAirship airship) {
        this(containerId, playerInventory, airship, airship, airship.hasChest());
    }

    private AirshipMenu(int containerId, Inventory playerInventory, Container airshipContainer, EntityAirship airship, boolean hasChest) {
        super(CEMenus.AIRSHIP_MENU.get(), containerId);
        this.airshipContainer = airshipContainer;
        this.airship = airship;

        checkContainerSize(airshipContainer, EntityAirship.FUEL_SLOT_COUNT + (hasChest ? EntityAirship.CHEST_SLOT_COUNT : 0));
        airshipContainer.startOpen(playerInventory.player);

        // Fuel slots (entity slots 0,1)
        this.addSlot(new Slot(airshipContainer, 0, FUEL_X, FUEL_Y_TOP));
        this.addSlot(new Slot(airshipContainer, 1, FUEL_X, FUEL_Y_BOTTOM));

        // Chest slots (entity slots 2..13) only when chest is installed
        if (hasChest) {
            int index = EntityAirship.FUEL_SLOT_COUNT;
            for (int row = 0; row < CHEST_ROWS; row++) {
                for (int col = 0; col < CHEST_COLS; col++) {
                    this.addSlot(new Slot(airshipContainer, index++, CHEST_X_START + col * 18, CHEST_Y_START + row * 18));
                }
            }
        }

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * 18, HOTBAR_Y));
        }
    }

    public boolean hasChest() {
        return this.airship != null ? this.airship.hasChest() : this.airshipContainer.getContainerSize() > EntityAirship.FUEL_SLOT_COUNT;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.airshipContainer.stillValid(player);
    }

    public float getLeftFuelLevel() {
        return this.airship != null ? this.airship.getLeftFuelBurnTime() : 0.0F;
    }

    public float getRightFuelLevel() {
        return this.airship != null ? this.airship.getRightFuelBurnTime() : 0.0F;
    }

    public int getBalloonHealth() {
        return this.airship != null ? this.airship.getBalloonHealth() : 0;
    }

    public boolean isBalloonDestroyed() {
        return this.airship != null && this.airship.isBalloonDestroyed();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            int entitySlots = this.hasChest() ? EntityAirship.FUEL_SLOT_COUNT + EntityAirship.CHEST_SLOT_COUNT : EntityAirship.FUEL_SLOT_COUNT;

            if (index < entitySlots) {
                if (!this.moveItemStackTo(stack, entitySlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Try fuel slots first, then chest slots
                if (!this.moveItemStackTo(stack, 0, entitySlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.airshipContainer.stopOpen(player);
    }
}