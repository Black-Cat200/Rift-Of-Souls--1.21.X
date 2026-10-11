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
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerCloneEntity entity) {
        if (entity.getPlayerProfile() != null) {
            return Minecraft.getInstance().getSkinManager().getInsecureSkin(entity.getPlayerProfile()).texture();
        }
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @Override
    public void render(PlayerCloneEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Применяем Блокбенч-анимацию на клона игрока
        CloneAnimator.applyBlockbenchAnimation(entity, this.model);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

}
