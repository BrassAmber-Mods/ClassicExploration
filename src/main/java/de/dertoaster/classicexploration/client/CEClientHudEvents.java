package de.dertoaster.classicexploration.client;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = ClassicExplorationMod.MODID, value = Dist.CLIENT)
public class CEClientHudEvents {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (!(minecraft.player.getVehicle() instanceof EntityAirship airship)) {
            return;
        }

        int health = airship.getBalloonHealth();
        if (health <= 0 || airship.isBalloonDestroyed()) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        ResourceLocation balloonTexture = ClassicExplorationMod.prefix("textures/gui/balloon.png");

        int heartX = width / 2 + 8;
        int heartY = height - 57;
        int heartSize = 16;

        for (int i = 0; i < airship.getBalloonHealth(); i++) {
            int xPos = heartX;
            int yPos = heartY;
            guiGraphics.blit(balloonTexture, xPos + i*(heartSize), yPos,0,0, heartSize, heartSize, heartSize, heartSize);
        }

//        for (int i = 0; i < 4; i++) {
//            boolean filled = i < health;
//            int color = filled ? 0xFFFF0000 : 0xFF333333;
//            guiGraphics.fill(heartX + i * (heartSize + spacing), heartY, heartX + i * (heartSize + spacing) + heartSize, heartY + heartSize, color);
//            if (filled) {
//                guiGraphics.fill(heartX, heartY, heartX + 16, heartY + 16, 0xFFFF5555);
//            }
//        }
    }
}
