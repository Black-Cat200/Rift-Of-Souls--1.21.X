package net.BlackCat200.Rift_Of_Souls;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
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

// Прямые официальные импорты playerAnimator из структуры изученного мода
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;

@EventBusSubscriber(modid = RiftOfSoulsMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {

    private static boolean isCutsceneActive = false;
    private static double camX, camY, camZ;
    private static float camYaw, camPitch;
    private static float playerFixedYaw, playerFixedPitch;

    // Официальный слой анимации игрока (как в CustomPlayerAnimation)
    private static ModifierLayer<KeyframeAnimationPlayer> animationLayer = new ModifierLayer<>();
    private static boolean isLayerAdded = false;

    // Создаем кастомную кнопку запуска (Клавиша 'G' на клавиатуре)
    public static final KeyMapping ANIM_KEY = new KeyMapping(
            "key.rift_of_souls.play_anim",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_G,
            "key.categories.misc"
    );

    public ExampleModClient(ModContainer container) {
        // Конструктор оставляем пустым
    }

    // Регистрируем нашу кнопку в системе NeoForge
    @EventBusSubscriber(modid = RiftOfSoulsMod.MODID, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(ANIM_KEY);
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RiftOfSoulsMod.LOGGER.info("HELLO FROM CLEAN CLIENT SETUP WITH KEYBIND");
    }

    public static void startCutscene(double x, double y, double z, float yaw, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        isCutsceneActive = true;
        camX = x; camY = y; camZ = z;
        camYaw = yaw; camPitch = pitch;

        playerFixedYaw = mc.player.getYRot();
        playerFixedPitch = mc.player.getXRot();

        mc.options.hideGui = true;
        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        mc.mouseHandler.grabMouse();

        // =======ОФИЦИАЛЬНЫЙ ЗАПУСК АНИМАЦИИ ИЗ СТРУКТУРЫ МОДА=======
        var animation = PlayerAnimationRegistry.getAnimation(
                ResourceLocation.fromNamespaceAndPath(RiftOfSoulsMod.MODID, "test")
        );

        if (animation != null) {
            // Подключаем слой к игроку ОДИН раз, чтобы не спамить в стек
            if (!isLayerAdded) {
                AnimationStack stack = PlayerAnimationAccess.getPlayerAnimLayer(player);
                if (stack != null) {
                    stack.addAnimLayer(0, animationLayer);
                    isLayerAdded = true;
                }
            }
            // Запускаем плеер анимации в наш постоянный слой
            animationLayer.setAnimation(new KeyframeAnimationPlayer((KeyframeAnimation) animation));
            RiftOfSoulsMod.LOGGER.info("Анимация test успешно запущена по нажатию клавиши!");
        } else {
            RiftOfSoulsMod.LOGGER.warn("Файл test.json не найден в assets/rift_of_souls/player_animations/");
        }
    }

    public static void stopCutscene() {
        Minecraft mc = Minecraft.getInstance();
        isCutsceneActive = false;
        mc.options.hideGui = false;
        mc.options.setCameraType(CameraType.FIRST_PERSON);

        // Сбрасываем текущую анимацию в слое
        if (animationLayer != null) {
            animationLayer.setAnimation(null);
        }
    }

    // Отслеживание нажатия клавиши в игре
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Если игрок нажал клавишу 'G' в мире и сцена еще не идет
        if (ANIM_KEY.consumeClick() && !isCutsceneActive && mc.screen == null) {
            var p = mc.player;
            // Активируем полет камеры и проигрывание JSON-движений
            startCutscene(p.getX() - 3, p.getY() + 1.8, p.getZ() - 3, 45, 10);
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

    @SubscribeEvent
    public static void onRenderTick(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (isCutsceneActive && event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            if (mc.level != null && !mc.isPaused()) {
                camY += 0.005;
                camYaw += 0.05f;

                if (mc.player != null) {
                    mc.player.setYRot(playerFixedYaw);
                    mc.player.setXRot(playerFixedPitch);
                    mc.player.yRotO = playerFixedYaw;
                    mc.player.xRotO = playerFixedPitch;
                    mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
                }

                if (mc.screen != null) {
                    stopCutscene();
                }
            }
        }
    }
}
