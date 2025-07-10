package grillo78.classicexploration.client.util;

import grillo78.classicexploration.entities.Airship;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InputUtil {
    @OnlyIn(Dist.CLIENT)
    public static boolean[] inputToArray(Input input) {
        return new boolean[]{input.up, input.down, input.left, input.right, input.jumping, Minecraft.getInstance().options.keySprint.isDown()};
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean checkAirshipInput(Airship vehicle, Input input) {
        return vehicle.getInput()[0] == input.up
                && vehicle.getInput()[1] == input.down
                && vehicle.getInput()[2] == input.left
                && vehicle.getInput()[3] == input.right
                && vehicle.getInput()[4] == input.jumping
                && vehicle.getInput()[5] == Minecraft.getInstance().options.keySprint.isDown();
    }
}
