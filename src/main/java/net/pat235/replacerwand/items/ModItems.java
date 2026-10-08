package net.pat235.replacerwand.items;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pat235.replacerwand.ReplacerWand;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ReplacerWand.MOD_ID);

    public static final DeferredItem<Item> REPLACER_WAND = ITEMS.registerItem("replacer_wand",
            (properties) -> new ReplacerWandItem(properties.stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
