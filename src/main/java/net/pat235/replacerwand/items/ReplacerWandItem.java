package net.pat235.replacerwand.items;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;


public class ReplacerWandItem extends Item {
    public ReplacerWandItem(Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack offHandStack = context.getPlayer().getOffhandItem();
        ItemStack mainHandStack = context.getPlayer().getMainHandItem();
        if(!mainHandStack.is(ModItems.REPLACER_WAND)) return InteractionResult.FAIL;
        if( !context.getPlayer().isCreative()){
            if (context.getPlayer() instanceof ServerPlayer) {
                ((ServerPlayer)context.getPlayer()).sendSystemMessage(Component.literal("You must be in Creative mode to use this"));
            }
            return InteractionResult.CONSUME;
        }
        if(offHandStack.isEmpty() || !(offHandStack.getItem() instanceof BlockItem)) {
            if (context.getPlayer() instanceof ServerPlayer) {
                ((ServerPlayer)context.getPlayer()).sendSystemMessage(Component.literal("You must be holding a block in your offhand"));
            }
            return InteractionResult.CONSUME;
        }
            if (!(level instanceof ServerLevel)) {
                return InteractionResult.SUCCESS;
            } else {
                ServerLevel serverLevel = (ServerLevel) level;
                Block block = ((BlockItem)offHandStack.getItem()).getBlock();
                if(block.defaultBlockState().canSurvive(serverLevel, context.getClickedPos())){
                    level.setBlockAndUpdate(context.getClickedPos(), block.defaultBlockState());
                    level.playSound(null, context.getClickedPos(), block.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS);
                }
            }
            return InteractionResult.FAIL;
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
