package grillo78.classicexploration.menu;

import grillo78.classicexploration.entities.Airship;
import grillo78.classicexploration.menu.slots.ArrowSlot;
import grillo78.classicexploration.menu.slots.CoalSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class InventoryMenu extends AbstractContainerMenu {

    public InventoryMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Player player) {
        super(pMenuType, pContainerId);
        if(player.getVehicle() != null && player.getVehicle() instanceof Airship){
            Airship ship = (Airship) player.getVehicle();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    this.addSlot(new SlotItemHandler(ship.getItemStackHandler(), j + i * 4, 8 + j * 18, 16 + i * 18));
                }
            }
            addSlot(new CoalSlot(ship.getItemStackHandler(), 12,134,16));
            addSlot(new ArrowSlot(ship.getItemStackHandler(), 13,134,52));
        }
        addPlayerSlots(new InvWrapper(player.getInventory()));
    }

    protected void addPlayerSlots(InvWrapper playerInventory) {
        int yStart = 30 + 18 * 3;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + yStart;
                this.addSlot(new SlotItemHandler(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = yStart + 58;
            this.addSlot(new SlotItemHandler(playerInventory, row, x, y));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack transferred = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        int otherSlots = this.slots.size() - 36;

        if (slot != null && slot.hasItem()) {
            ItemStack current = slot.getItem();
            transferred = current.copy();

            if (index < otherSlots) {
                if (!this.moveItemStackTo(current, otherSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(current, 0, otherSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (current.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return transferred;
    }
}
