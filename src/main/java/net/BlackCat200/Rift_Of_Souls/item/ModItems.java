package net.BlackCat200.Rift_Of_Souls.item;

import net.BlackCat200.Rift_Of_Souls.RiftOfSoulsMod;
import net.BlackCat200.Rift_Of_Souls.item.custom.CloneSpawnItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(RiftOfSoulsMod.MODID);

    // Ваши ресурсы
    public static final DeferredItem<Item> AZURITE = ITEMS.registerSimpleItem("azurite", new Item.Properties());
    public static final DeferredItem<Item> RAW_AZURITE = ITEMS.registerSimpleItem("raw_azurite", new Item.Properties());

    // Предмет призыва клона (мы вернули его сюда)
    public static final DeferredItem<Item> CLONE_SPAWN_ITEM = ITEMS.register("clone_spawn_item",
            () -> new CloneSpawnItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

