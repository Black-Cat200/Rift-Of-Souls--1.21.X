package net.BlackCat200.Rift_Of_Souls.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.Collections;

public class PlayerCloneEntity extends LivingEntity {
    private GameProfile playerProfile;

    public PlayerCloneEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    public void setPlayerProfile(GameProfile profile) {
        this.playerProfile = profile;
    }

    public GameProfile getPlayerProfile() {
        return this.playerProfile;
    }

    // Клон будет статичным и не будет атаковать или ходить сам
    @Override
    public boolean isImmobile() { return true; }

    @Override
    public Iterable<ItemStack> getArmorSlots() { return Collections.emptyList(); }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) { return ItemStack.EMPTY; }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public net.minecraft.world.entity.HumanoidArm getMainArm() { return net.minecraft.world.entity.HumanoidArm.RIGHT; }

    // Сохранение и чтение профиля игрока из NBT (компонентов)
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("CloneUUID") && tag.contains("CloneName")) {
            java.util.UUID uuid = tag.getUUID("CloneUUID");
            String name = tag.getString("CloneName");
            this.playerProfile = new com.mojang.authlib.GameProfile(uuid, name);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.playerProfile != null) {
            tag.putUUID("CloneUUID", this.playerProfile.getId());
            tag.putString("CloneName", this.playerProfile.getName());
        }
    }

}