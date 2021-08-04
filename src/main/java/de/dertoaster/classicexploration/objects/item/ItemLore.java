package de.dertoaster.classicexploration.objects.item;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemLore extends Item {
	
	public ItemLore(Properties p_i48487_1_) {
		super(p_i48487_1_);
	}

	@Override
	public void appendHoverText(ItemStack p_77624_1_, World p_77624_2_, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
		super.appendHoverText(p_77624_1_, p_77624_2_, tooltip, p_77624_4_);
		if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) != GLFW.GLFW_PRESS) {
			tooltip.add(new StringTextComponent(TextFormatting.BLUE + I18n.get("item." + this.getRegistryName().getNamespace() + ".tooltip.hold_shift", '\n', '\n', '\n', '\n', '\n', '\n', '\n', '\n', '\n', '\n')));
		} else {
			tooltip.add(new StringTextComponent(TextFormatting.BLUE + I18n.get("item." + this.getRegistryName().getNamespace() + "." + this.getRegistryName().getPath() + ".tooltip", '\n', '\n', '\n', '\n', '\n', '\n', '\n', '\n', '\n', '\n')));
		}
	}

}
