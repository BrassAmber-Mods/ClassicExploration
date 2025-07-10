package grillo78.classicexploration.client.screen;

import grillo78.classicexploration.ClassicExploration;
import grillo78.classicexploration.entities.Airship;
import grillo78.classicexploration.menu.InventoryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InventoryScreen  extends AbstractContainerScreen<InventoryMenu> {
    private static final ResourceLocation SHIP_INVENTORY_LOCATION = new ResourceLocation(ClassicExploration.MOD_ID, "textures/gui/airship_inventory.png");

    public InventoryScreen(InventoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);
        renderTooltip(pGuiGraphics, pMouseX,pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(SHIP_INVENTORY_LOCATION, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        double progress = ((Airship)Minecraft.getInstance().player.getVehicle()).getFuelTime()/((Airship)Minecraft.getInstance().player.getVehicle()).getMaxFuelTime();
        pGuiGraphics.blit(SHIP_INVENTORY_LOCATION, relX+157, (int) (relY+16+52*(1-progress)),181, (int) (16+52*(1-progress)), 11, (int) (52*progress));
    }
}
