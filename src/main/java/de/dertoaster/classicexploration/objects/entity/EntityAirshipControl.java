package de.dertoaster.classicexploration.objects.entity;

import de.dertoaster.classicexploration.network.packet.CPacketAirshipControls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityAirshipControl {

    @OnlyIn(Dist.CLIENT)
    static void updateClientControls(EntityAirship airship) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        if (airship.getControllingPassenger() == minecraft.player) {
            Options options = minecraft.options;

            boolean forward = options.keyUp.isDown();
            boolean left = options.keyLeft.isDown();
            boolean right = options.keyRight.isDown();
            boolean backward = options.keyDown.isDown();
            boolean ascend = options.keyJump.isDown();
            boolean descend = options.keySprint.isDown();

            // Aplicar localmente para predicción/simulación en cliente (como los barcos)
            airship.processControlInputs(new boolean[]{forward, left, right, backward, ascend, descend});

            PacketDistributor.sendToServer(new CPacketAirshipControls(
                    airship.getId(), forward, left, right, backward, ascend, descend
            ));
        }
    }
}
