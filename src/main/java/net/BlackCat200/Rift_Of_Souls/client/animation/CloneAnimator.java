package net.BlackCat200.Rift_Of_Souls.client.animation;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.Util;

public final class CloneAnimator {

    private CloneAnimator() {}

    public static class Pose {
        public float headPitch;
        public float leftArmPitch, leftArmRoll;
        public float rightArmPitch, rightArmRoll;
    }

    public static void applyTrackAnimations(LivingEntity entity, PlayerModel<?> model) {
        long now = Util.getMillis();

        // Полный цикл длится 3 секунды (3000 миллисекунд) для большей плавности
        float timeFactor = (now % 3000) / 3000.0f;
        float wave = (float) Math.sin(timeFactor * Math.PI * 2);
        // Дополнительная быстрая волна для создания естественного "микро-дыхания"
        float subtleWave = (float) Math.cos(timeFactor * Math.PI * 4);

        Pose pose = new Pose();

        // Усиленная математика движений («Боевое RPG дыхание»):
        pose.rightArmPitch = wave * 0.18f;           // Руки ходят вперед-назад сильнее
        pose.rightArmRoll = 0.15f + wave * 0.05f;    // Руки сильнее расставлены в стороны

        pose.leftArmPitch = -wave * 0.18f;          // Левая рука качается в противофазе
        pose.leftArmRoll = -0.15f - wave * 0.05f;

        pose.headPitch = 0.05f + wave * 0.04f;       // Голова плавно покачивается вверх-вниз

        // Применяем значения напрямую к костям модели Майнкрафта
        model.head.xRot = pose.headPitch;
        // Добавляем голове легкое покачивание из стороны в сторону
        model.head.yRot = subtleWave * 0.03f;

        model.rightArm.xRot = pose.rightArmPitch;
        model.rightArm.zRot = pose.rightArmRoll;
        // Легкий поворот плеч при дыхании
        model.rightArm.yRot = wave * 0.05f;

        model.leftArm.xRot = pose.leftArmPitch;
        model.leftArm.zRot = pose.leftArmRoll;
        model.leftArm.yRot = -wave * 0.05f;

        // Надежно примораживаем ноги к земле
        model.rightLeg.xRot = 0;
        model.rightLeg.yRot = 0;
        model.rightLeg.zRot = 0;

        model.leftLeg.xRot = 0;
        model.leftLeg.yRot = 0;
        model.leftLeg.zRot = 0;
    }

}