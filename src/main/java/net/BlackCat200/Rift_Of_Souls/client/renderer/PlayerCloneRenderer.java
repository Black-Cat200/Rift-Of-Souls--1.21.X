package net.BlackCat200.Rift_Of_Souls.client.renderer;

import net.BlackCat200.Rift_Of_Souls.client.animation.CloneAnimator;
import net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class PlayerCloneRenderer extends LivingEntityRenderer<PlayerCloneEntity, PlayerModel<PlayerCloneEntity>> {

    public PlayerCloneRenderer(EntityRendererProvider.Context context) {
        // false — стандартные руки (Стив). Если нужны тонкие руки (Алекс), поменяй на true
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerCloneEntity entity) {
        if (entity.getPlayerProfile() != null) {
            // Безопасное получение текстуры скина в 1.21.1
            return Minecraft.getInstance().getSkinManager().getInsecureSkin(entity.getPlayerProfile()).texture();
        }
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @Override
    public void render(PlayerCloneEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Вызываем расчет анимации перед тем, как отработает базовый рендер
        CloneAnimator.applyTrackAnimations(entity, this.model);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}

