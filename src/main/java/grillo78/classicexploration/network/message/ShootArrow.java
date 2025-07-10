package grillo78.classicexploration.network.message;

import grillo78.classicexploration.entities.Airship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShootArrow implements Message<ShootArrow>{

    public ShootArrow() {
    }

    @Override
    public void encode(ShootArrow message, FriendlyByteBuf buffer) {
    }

    @Override
    public ShootArrow decode(FriendlyByteBuf buffer) {
        return new ShootArrow();
    }

    @Override
    public void handle(ShootArrow message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(()->{
            ServerPlayer player = supplier.get().getSender();
            if(player.getVehicle() != null && player.getVehicle() instanceof Airship){
                ItemStack arrowsStack = ((Airship)player.getVehicle()).getItemStackHandler().getStackInSlot(13);
                if(!arrowsStack.isEmpty() && arrowsStack.getItem() instanceof ArrowItem){
                    AbstractArrow arrow = ((ArrowItem) arrowsStack.getItem()).createArrow(player.level(), arrowsStack, player);
                    arrow.shoot(player.getViewVector(0).multiply(2,2,2).x,player.getViewVector(0).multiply(2,2,2).y, player.getViewVector(0).multiply(2,2,2).z, 3.0F, 1.0F);
                    player.level().addFreshEntity(arrow);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
