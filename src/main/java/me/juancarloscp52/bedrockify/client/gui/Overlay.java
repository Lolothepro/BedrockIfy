package me.juancarloscp52.bedrockify.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import me.juancarloscp52.bedrockify.client.features.paperDoll.PaperDoll;
import me.juancarloscp52.bedrockify.client.features.savingOverlay.SavingOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class Overlay {

    private final MinecraftClient client;
    private final PaperDoll paperDoll;
    public final SavingOverlay savingOverlay;
    private Text fps;
    private final int textPosX = 0;

    public Overlay(MinecraftClient client) {
        this.client = client;
        this.paperDoll = new PaperDoll(client);
        this.savingOverlay = new SavingOverlay();
    }

    public void renderOverlay(MatrixStack matrixStack) {
        // Only render the overlay if HUD is not hidden and debug is NOT enabled.
        if (!client.options.debugEnabled && !client.options.hudHidden){
            this.renderText(matrixStack);
            this.paperDoll.renderPaperDoll(matrixStack);
            BedrockifyClient.getInstance().reachAroundPlacement.renderIndicator(matrixStack);
            this.savingOverlay.render(matrixStack);
        }
    }

    /**
     * Renders the text components for the player position and client fps.
     */
    private void renderText(MatrixStack matrixStack) {
        fps = Text.translatable("bedrockify.hud.fps").append(" " + this.client.fpsDebugString.split(" fps")[0].trim());
        renderPositionText(matrixStack);
        renderFpsText(matrixStack);
    }

    private void renderPositionText(MatrixStack matrixStack) {
        BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        int screenBorder = settings.overlayIgnoresSafeArea ? 0 : settings.getScreenSafeArea();
        int posY = settings.getPositionHUDHeight();
        if (!settings.isShowPositionHUDEnabled())
            return;
        BlockPos blockPos = Objects.requireNonNull(this.client.getCameraEntity(), "Camera Entity cannot be null.").getBlockPos();
        MutableText position = Text.translatable("bedrockify.hud.position").append(Text.literal(" "+ blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ()));
        if(settings.getFPSHUDoption()==1)
            position.append(" ").append(fps);
        int positionWidth = client.textRenderer.getWidth(position);
        float opacity = BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false);
        RenderSystem.setShaderColor(1,1,1,1);
        fill(matrixStack, textPosX + screenBorder, posY + screenBorder, textPosX + positionWidth + 6 + screenBorder, posY + 12 + screenBorder, MathHelper.ceil((255.0D * client.options.getTextBackgroundOpacity().getValue()) * opacity)<<24);
        int alpha = (int) Math.ceil(opacity*255);
        client.textRenderer.drawWithShadow(matrixStack, position, textPosX + 3 + screenBorder, posY + 3 + screenBorder, 16777215 | ((alpha) << 24));
    }

    private void renderFpsText(MatrixStack matrixStack) {
        BedrockifyClientSettings settings = BedrockifyClient.getInstance().settings;
        int screenBorder = settings.overlayIgnoresSafeArea ? 0 : settings.getScreenSafeArea();
        int posY = settings.getPositionHUDHeight()+2;
        boolean positionEnabled = settings.isShowPositionHUDEnabled();
        if (settings.getFPSHUDoption()!=2)
            return;
        int fpsCounterWidth = client.textRenderer.getWidth(fps);
        float opacity = BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false);
        RenderSystem.setShaderColor(1,1,1,1);
        fill(matrixStack, textPosX + screenBorder, posY + (positionEnabled ? 10 : 0) + screenBorder, textPosX + fpsCounterWidth + 6 + screenBorder, posY + (positionEnabled ? 10 : 0) + 10 + screenBorder, MathHelper.ceil((255.0D * client.options.getTextBackgroundOpacity().getValue()) * opacity)<<24);
        int alpha = (int) Math.ceil(opacity*255);
        client.textRenderer.drawWithShadow(matrixStack, fps, textPosX + 3 + screenBorder, posY + 1 + (positionEnabled ? 10 : 0) + screenBorder, 16777215 | ((alpha) << 24));
    }

}
