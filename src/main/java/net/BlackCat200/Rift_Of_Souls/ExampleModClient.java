package net.BlackCat200.Rift_Of_Souls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import com.mojang.blaze3d.platform.InputConstants;

import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;

@EventBusSubscriber(modid = RiftOfSoulsMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {

    private static boolean isCutsceneActive = false;
    private static double camX, camY, camZ;
    private static float camYaw, camPitch;
    private static float playerFixedYaw, playerFixedPitch;

    // ПЕРЕМЕННЫЕ ВРЕМЕНИ И ПОЗИЦИИ ИГРОКА
    private static float cutsceneTicks = 0;
    private static final float MAX_DURATION_TICKS = 100; // Длительность катсцены (100 тиков = 5 секунд)
    private static double basePlayerX, basePlayerY, basePlayerZ;

    private static final ModifierLayer<KeyframeAnimationPlayer> animationLayer = new ModifierLayer<>();
    private static boolean isLayerAdded = false;

    public static final KeyMapping ANIM_KEY = new KeyMapping(
            "key.rift_of_souls.play_anim",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            "key.categories.misc"
    );

    public ExampleModClient(ModContainer container) {
    }

    @EventBusSubscriber(modid = RiftOfSoulsMod.MODID, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(ANIM_KEY);
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RiftOfSoulsMod.LOGGER.info("HELLO FROM CLIENT SETUP");
    }

    public static void startCutscene(String animationName) {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        isCutsceneActive = true;
        cutsceneTicks = 0; // Сбрасываем таймер времени сцены в ноль

        // Запоминаем точные координаты игрока в момент старта
        basePlayerX = player.getX();
        basePlayerY = player.getY();
        basePlayerZ = player.getZ();

        playerFixedYaw = player.getYRot();
        playerFixedPitch = player.getXRot();

        // Начальная позиция камеры (прямо в глазах игрока)
        camX = basePlayerX;
        camY = basePlayerY + 1.6;
        camZ = basePlayerZ;
        camYaw = playerFixedYaw;
        camPitch = playerFixedPitch;

        mc.options.hideGui = true;
        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        mc.mouseHandler.grabMouse();

        var animation = PlayerAnimationRegistry.getAnimation(
                ResourceLocation.fromNamespaceAndPath(RiftOfSoulsMod.MODID, animationName)
        );

        if (animation != null) {
            if (!isLayerAdded) {
                AnimationStack stack = PlayerAnimationAccess.getPlayerAnimLayer(player);
                if (stack != null) {
                    stack.addAnimLayer(0, animationLayer);
                    isLayerAdded = true;
                }
            }
            animationLayer.setAnimation(new KeyframeAnimationPlayer((KeyframeAnimation) animation));
        }
    }

    public static void stopCutscene() {
        Minecraft mc = Minecraft.getInstance();
        isCutsceneActive = false;
        mc.options.hideGui = false;
        mc.options.setCameraType(CameraType.FIRST_PERSON);

        if (animationLayer != null) {
            animationLayer.setAnimation(null);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (ANIM_KEY.consumeClick() && !isCutsceneActive && mc.screen == null) {
            startCutscene("battle_idle");
        }

        if (isCutsceneActive && !mc.isPaused()) {
            mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
            mc.player.setYRot(playerFixedYaw);
            mc.player.setXRot(playerFixedPitch);
            mc.player.yRotO = playerFixedYaw;
            mc.player.xRotO = playerFixedPitch;
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        if (isCutsceneActive) {
            event.setYaw(camYaw);
            event.setPitch(camPitch);

            try {
                java.lang.reflect.Method setPositionMethod = net.minecraft.client.Camera.class.getDeclaredMethod(
                        "setPosition", double.class, double.class, double.class
                );
                setPositionMethod.setAccessible(true);
                setPositionMethod.invoke(event.getCamera(), camX, camY, camZ);
            } catch (Exception e) {
                RiftOfSoulsMod.LOGGER.error("Ошибка перемещения камеры: ", e);
            }
        }
    }

    // =========================================================================
    // БЛОК ТРУКТУРЫ РЕДАКТИРОВАНИЯ КАМЕРЫ КОДОМ
    // =========================================================================
    @SubscribeEvent
    public static void onRenderTick(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (isCutsceneActive && event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            if (mc.level != null && !mc.isPaused() && mc.player != null) {

                // Считаем время катсценыTHIRD_PERSON_BACK
                cutsceneTicks += 0.5f;

                // Если общее время (5 секунд) вышло — завершаем полет
                if (cutsceneTicks >= MAX_DURATION_TICKS) {
                    stopCutscene();
                    return;
                }

                // ЖЕСТКАЯ ФИКСАЦИЯ МОДЕЛИ ИГРОКА (ИСПРАВЛЕНИЕ РАССИНХРОНА АНИМАЦИИ)
                // Принудительно разворачиваем и тело, и голову, и шею персонажа строго
                // по направлению стартового взгляда, чтобы JSON-анимация не утягивала его на Север!
                mc.player.setYRot(playerFixedYaw);
                mc.player.setXRot(playerFixedPitch);
                mc.player.yRotO = playerFixedYaw;
                mc.player.xRotO = playerFixedPitch;

                // NeoForge-параметры рендеринга углов тела и головы со стороны:
                mc.player.yBodyRot = playerFixedYaw;
                mc.player.yBodyRotO = playerFixedYaw;
                mc.player.yHeadRot = playerFixedYaw;
                mc.player.yHeadRotO = playerFixedYaw;

                // Обнуляем скорость ходьбы, чтобы игрок не сдвигался на WASD
                mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);

                // Переводим зафиксированный угол взгляда игрока в радианы для относительного расчета камеры
                double angle = Math.toRadians(playerFixedYaw);

                // --- АБСОЛЮТНО ОТНОСИТЕЛЬНЫЙ ТАЙМЛАЙН КАМЕРЫ ---

                // СЦЕНА 1: Первые 2 секунды (0-40 тиков) — камера ПЛАВНО ОТЛЕТАЕТ НАЗАД ЗА СПИНУ
                if (cutsceneTicks < 40) {
                    double curDistanceBack = cutsceneTicks * 0.1;

                    camX = basePlayerX;
                    camZ = basePlayerZ;
                    camY = basePlayerY + 1.6;

                    camYaw = playerFixedYaw + 90;
                    camPitch = 10f;
                }

                // СЦЕНА 2: От 2 до 4 секунд (40-80 тиков) — камера ЗАСТЫВАЕТ СЗАДИ И ПЛАВНО ЛЕТИТ ВВЕРХ
                else if (cutsceneTicks >= 40 && cutsceneTicks < 80) {
                    double curDistanceBack = 4.0;

                    camX = basePlayerX -32;
                    camZ = basePlayerZ;
                    camY = basePlayerY;

                    camYaw = playerFixedYaw;
                    camPitch = 10f;
                }

                // СЦЕНА 3: Последняя 1 секунда (80-100 тиков) — камера ПЛАВНО СМЕЩАЕТСЯ К ЛЕВОМУ ПЛЕЧУ И ОБЛЕТАЕТ
                else if (cutsceneTicks >= 80 && cutsceneTicks <= 100) {
                    double curDistanceBack = 4.0;
                    double curDistanceLeft = (cutsceneTicks - 80) * 0.1;

                    camX = basePlayerX + (curDistanceBack * Math.sin(angle)) + (curDistanceLeft * Math.cos(angle));
                    camZ = basePlayerZ - (curDistanceBack * Math.cos(angle)) + (curDistanceLeft * Math.sin(angle));
                    camY = basePlayerY + 1.6 + 3.2;

                    camYaw = playerFixedYaw - ((cutsceneTicks - 80) * 1.5f);
                    camPitch = 10f;
                }

                if (mc.screen != null) {
                    stopCutscene();
                }
            }
        }
    }

}