package net.BlackCat200.Rift_Of_Souls.item.custom;

import net.BlackCat200.Rift_Of_Souls.init.ModEntities;
import net.BlackCat200.Rift_Of_Souls.entity.PlayerCloneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class CloneSpawnItem extends Item {
    public CloneSpawnItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !context.getLevel().isClientSide() && context.getLevel() instanceof ServerLevel serverLevel) {

            PlayerCloneEntity clone = new PlayerCloneEntity(ModEntities.PLAYER_CLONE.get(), serverLevel);

            clone.moveTo(context.getClickedPos().getX() + 0.5, context.getClickedPos().getY() + 1.0, context.getClickedPos().getZ() + 0.5, player.getYRot() + 180F, 0);
            clone.setPlayerProfile(player.getGameProfile());

            serverLevel.addFreshEntity(clone);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
