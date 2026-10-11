package net.BlackCat200.Rift_Of_Souls.client.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.Util;

public final class CloneAnimator {

    private CloneAnimator() {}

    // Метод, который будет двигать как клона, так и реального игрока!
    public static void applyBlockbenchAnimation(LivingEntity entity, PlayerModel<?> model) {
        long now = Util.getMillis();

        // Скорость проигрывания (длина вашей анимации в Блокбенче, например 2 секунды = 2000L)
        float timeFactor = (now % 2000) / 2000.0f;
        float wave = (float) Math.sin(timeFactor * Math.PI * 2);

        // ВАЖНО: Вместо сложного парсера библиотек, мы переносим ключевые точки
        // вашей Блокбенч-анимации напрямую в модель суставов.
        // Вы можете менять эти коэффициенты под движения вашей стойки в Blockbench:

        model.head.xRot = 0.05f + wave * 0.03f; // Покачивание головы

        // Пример вашей стойки из Blockbench (руки согнуты в локтях или приподняты):
        model.rightArm.xRot = -0.2f + wave * 0.1f;
        model.rightArm.zRot = 0.15f + wave * 0.02f;

        model.leftArm.xRot = -0.2f - wave * 0.1f;
        model.leftArm.zRot = -0.15f - wave * 0.02f;

        // Принудительно отключаем ванильные движения ног при пошаговой анимации
        model.rightLeg.xRot = 0; model.leftLeg.xRot = 0;
    }
}
