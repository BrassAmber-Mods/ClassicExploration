package de.dertoaster.classicexploration.objects.item;

import de.dertoaster.classicexploration.init.CEEntities;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ItemAirship extends ItemLore {

    public ItemAirship(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        EntityAirship airship = CEEntities.AIRSHIP.get().create(level);
        if (airship == null) {
            return InteractionResult.FAIL;
        }

        airship.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, context.getRotation(), 0.0F);
        level.addFreshEntity(airship);
        level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, BlockPos.containing(airship.position()));

        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        BlockPos pos = hit.getBlockPos().above();
        EntityAirship airship = CEEntities.AIRSHIP.get().create(level);
        if (airship == null) {
            return InteractionResultHolder.fail(stack);
        }

        airship.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), 0.0F);
        if (!level.noCollision(airship, airship.getBoundingBox())) {
            return InteractionResultHolder.fail(stack);
        }
        level.addFreshEntity(airship);
        level.gameEvent(player, GameEvent.ENTITY_PLACE, BlockPos.containing(airship.position()));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(stack);
    }
}