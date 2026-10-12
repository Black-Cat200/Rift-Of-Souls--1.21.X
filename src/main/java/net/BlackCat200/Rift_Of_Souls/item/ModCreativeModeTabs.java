package net.BlackCat200.Rift_Of_Souls.item;

import net.BlackCat200.Rift_Of_Souls.RiftOfSoulsMod;
import net.BlackCat200.Rift_Of_Souls.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RiftOfSoulsMod.MODID);

//    public static final Supplier<CreativeModeTab> AZURITE_ITEMS_TAB = CREATIVE_MODE_TAB.register("azurite_items_tab",
//            () -> CreativeModeTab.builder().icon(() -> new ItemStack(net.BlackCat200.Rift_Of_Souls.item.custom.ModItems.AZURITE.get()))
//                    .title(Component.translatable("creativetab.riftofsouls.azurite_items"))
//                    .displayItems((parameters, output) -> {
////                        output.accept(net.BlackCat200.Rift_Of_Souls.item.custom.ModItems.AZURITE.get());
////                        output.accept(ModItems.RAW_AZURITE);
//e
//
//                    }).build());

    public static final Supplier<CreativeModeTab> AZURITE_BLOCK_TAB = CREATIVE_MODE_TAB.register("azurite_block_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.AZURITE_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(RiftOfSoulsMod.MODID, "azurite_items_tab"))
                    .title(Component.translatable("creativetab.riftofsouls.azurite_blocks"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.AZURITE_BLOCK);

                    }).build());

    public static final Supplier<CreativeModeTab> RIFT_OF_SOULS_TAB = CREATIVE_MODE_TAB.register("rift_of_souls_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.AZURITE_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(RiftOfSoulsMod.MODID, "azurite_block_tab"))
                    .title(Component.translatable("creativetab.riftofsouls.rift_of_souls"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.AZURITE_BLOCK);



                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

}
