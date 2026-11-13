package de.dertoaster.classicexploration.objects.item;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ItemLore extends Item {

    public ItemLore(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
        if (id == null) {
            return;
        }
        boolean shiftDown = Minecraft.getInstance().getWindow() != null
                && GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
        if (!shiftDown) {
            tooltip.add(Component.translatable("item." + id.getNamespace() + ".tooltip.hold_shift"));
        } else {
            tooltip.add(Component.translatable("item." + id.getNamespace() + "." + id.getPath() + ".tooltip"));
        }
    }
}
