package de.dertoaster.classicexploration.client.screen;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.inventory.AirshipMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AirshipScreen extends AbstractContainerScreen<AirshipMenu> {
    private static final ResourceLocation TEX_NO_CHEST = ClassicExplorationMod.prefix("textures/gui/airship_inventory.png");
    private static final ResourceLocation TEX_CHEST = ClassicExplorationMod.prefix("textures/gui/airship_chest_inventory.png");

    public AirshipScreen(AirshipMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private ResourceLocation texture() {
        return this.menu.hasChest() ? TEX_CHEST : TEX_NO_CHEST;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(texture(), x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Barras de combustible (izquierda/derecha) - normalización simple para visualización
        float leftFuelLevel = Math.min(1.0F, this.menu.getLeftFuelLevel() / 1600.0F);
        float rightFuelLevel = Math.min(1.0F, this.menu.getRightFuelLevel() / 1600.0F);

        if (leftFuelLevel > 0.0F) {
            guiGraphics.blit(texture(), x + imageWidth - 19, y + 16 + ((int) ((1 - leftFuelLevel) * 53)), 181, 16 + ((int) ((1 - leftFuelLevel) * 53)), 6, (int) (leftFuelLevel * 53));
        }
        if (rightFuelLevel > 0.0F) {
            guiGraphics.blit(texture(), x + imageWidth - 13, y + 16 + ((int) ((1 - rightFuelLevel) * 53)), 187, 16 + ((int) ((1 - rightFuelLevel) * 53)), 5, (int) (rightFuelLevel * 53));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // GUI textures provide their own labels/artwork; avoid drawing default title/inventory text over them.
    }
}