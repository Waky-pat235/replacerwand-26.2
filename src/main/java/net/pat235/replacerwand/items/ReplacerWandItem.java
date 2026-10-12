package net.pat235.replacerwand.items;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;


public class ReplacerWandItem extends Item {
    public ReplacerWandItem(Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack offHandStack = player.getOffhandItem();
        ItemStack mainHandStack = player.getMainHandItem();

        if(!mainHandStack.is(ModItems.REPLACER_WAND)) return InteractionResult.FAIL; //make sure its in the main hand
        if( !player.isCreative()){//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~only in creative mode
            if (player instanceof ServerPlayer) {
                ((ServerPlayer)player).sendSystemMessage(Component.translatable("message.replacerwand.need_creative_mode"), true);
            }
            return InteractionResult.CONSUME;
        }
        if(offHandStack.isEmpty() || !(offHandStack.getItem() instanceof BlockItem)) {//check for block in offhand
            if (player instanceof ServerPlayer) {
                ((ServerPlayer)player).sendSystemMessage(Component.translatable("message.replacerwand.need_offhand_block"), true);
            }
            return InteractionResult.CONSUME;
        }
        BlockPos pos = context.getClickedPos();
        Block block = ((BlockItem) offHandStack.getItem()).getBlock();
        if (!player.mayBuild() || !level.mayInteract(player, pos)) {//only if the player is allowed to build there
            if (player instanceof ServerPlayer) {
                ((ServerPlayer)player).sendSystemMessage(Component.translatable("message.replacerwand.not_allowed_to_build"), true);
            }
            return InteractionResult.FAIL;
        }
        if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {//only game master blocks if the player is allowed
            if (player instanceof ServerPlayer) {
                ((ServerPlayer)player).sendSystemMessage(Component.translatable("message.replacerwand.not_allowed_to_place"), true);
            }
            return InteractionResult.FAIL;
        }
        if (level.getBlockState(pos).getBlock() instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {//only game master blocks if the player is allowed
            if (player instanceof ServerPlayer) {
                ((ServerPlayer)player).sendSystemMessage(Component.translatable("message.replacerwand.not_allowed_to_replace"), true);
            }
            return InteractionResult.FAIL;
        }
        BlockState state = block.getStateForPlacement(new BlockPlaceContext(context));
        if (state == null || !state.canSurvive(level, pos)) {
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel serverLevel) {
            BlockState connected = Block.updateFromNeighbourShapes(state, serverLevel, pos);
            if (!connected.isAir()) state = connected;

            if (serverLevel.setBlock(pos, state, Block.UPDATE_ALL)) {
                BlockState placed = serverLevel.getBlockState(pos);
                placed.getBlock().setPlacedBy(serverLevel, pos, placed, context.getPlayer(), offHandStack);
                serverLevel.playSound(null, pos, placed.getSoundType().getPlaceSound(), SoundSource.BLOCKS);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Minecraft.getInstance().hasShiftDown()) {
            tooltipComponents.accept(Component.translatable("tooltip.replacerwand.replacer_wand"));
        }else{
            tooltipComponents.accept(Component.translatable("tooltip.replacerwand.shift_read_more"));
        }
    }
}
