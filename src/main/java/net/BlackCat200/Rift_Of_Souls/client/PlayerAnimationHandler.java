package net.BlackCat200.Rift_Of_Souls.client;

import net.BlackCat200.Rift_Of_Souls.RiftOfSoulsMod;
import net.BlackCat200.Rift_Of_Souls.client.animation.CloneAnimator;
import net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

// Параметр bus убран согласно документации NeoForge 1.21.1 для игровых событий
@EventBusSubscriber(modid = RiftOfSoulsMod.MODID, value = Dist.CLIENT)
public class PlayerAnimationHandler {

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        var model = event.getRenderer().getModel();

        // Проверяем, есть ли рядом клон игрока в радиусе 15 блоков
        boolean isInBattle = !player.level().getEntitiesOfClass(
                PlayerCloneEntity.class,
                player.getBoundingBox().inflate(15.0)
        ).isEmpty();

        if (isInBattle) {
            // Применяем импортированные из Блокбенча углы костей на реального игрока
            CloneAnimator.applyBlockbenchAnimation(player, model);
        }
    }
}
