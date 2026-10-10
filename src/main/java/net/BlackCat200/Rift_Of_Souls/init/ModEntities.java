package net.BlackCat200.Rift_Of_Souls.init;
import net.BlackCat200.Rift_Of_Souls.RiftOfSoulsMod;
import net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity;
import net.BlackCat200.Rift_Of_Souls.client.renderer.PlayerCloneRenderer;
import net.BlackCat200.Rift_Of_Souls.client.renderer.PlayerCloneRenderer;
import net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = RiftOfSoulsMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RiftOfSoulsMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity>> PLAYER_CLONE =
            ENTITIES.register("player_clone", () -> EntityType.Builder.of(net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(10)
                    .build("player_clone"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

    // Автоматическая регистрация атрибутов здоровья (иначе игра вылетит при спавне)
    @SubscribeEvent
    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(PLAYER_CLONE.get(), LivingEntity.createLivingAttributes().build());
    }

    // Регистрация рендерера (сработает только на стороне клиента)
    @EventBusSubscriber(modid = RiftOfSoulsMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PLAYER_CLONE.get(), net.BlackCat200.Rift_Of_Souls.client.renderer.PlayerCloneRenderer::new);
        }
    }
}