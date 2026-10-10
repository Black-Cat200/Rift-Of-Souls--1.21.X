package net.BlackCat200.Rift_Of_Souls;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import java.util.UUID;

public class CustomPacketPayload {
    public static final ResourceLocation MOD_ID = ResourceLocation.fromNamespaceAndPath("modid", "main");

    // Пакет от Клиента к Серверу: "Я зажал кнопку"
    public record ExtractSoulPayload() implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        public static final Type<ExtractSoulPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("modid", "extract_soul"));
        public static final StreamCodec<FriendlyByteBuf, ExtractSoulPayload> CODEC = StreamCodec.unit(new ExtractSoulPayload());
        @Override public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() { return TYPE; }
    }

    // Пакет от Сервера к Клиентам: "Проиграй анимацию игрока X"
    public record PlayAnimationPayload(UUID playerUuid) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        public static final Type<PlayAnimationPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("modid", "play_animation"));
        public static final StreamCodec<FriendlyByteBuf, PlayAnimationPayload> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, PlayAnimationPayload::playerUuid,
                PlayAnimationPayload::new
        );
        @Override public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() { return TYPE; }
    }
}