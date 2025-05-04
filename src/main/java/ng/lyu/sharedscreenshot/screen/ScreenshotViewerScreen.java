package ng.lyu.sharedscreenshot.screen;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class ScreenshotViewerScreen extends Screen {
    private final ResourceLocation location;
    private final int imageWidth;
    private final int imageHeight;

    public ScreenshotViewerScreen(byte[] imageData) {
        super(Component.literal(""));

        NativeImage image;
        try {
            image = NativeImage.read(new ByteArrayInputStream(imageData));
        } catch (IOException e) {
            throw new RuntimeException("Screenshot load error", e);
        }

        DynamicTexture texture = new DynamicTexture(image);
        this.location = Minecraft.getInstance().getTextureManager().register("sharedscreenshot/temp", texture);
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();
    }

    @Override
    protected void init() {
        super.init();
        Button.Builder builder = new Button.Builder(Component.translatable("ng.lyu.sharedscreenshot.close"), btn -> onClose());
        builder.size(50, 20);
        Button button = builder.build();
        addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        if (location != null) {
            int maxWidth = (int) (this.width * 0.85);
            int maxHeight = (int) (this.height * 0.85);

            int originalWidth = imageWidth;
            int originalHeight = imageHeight;

            float scale = Math.min((float) maxWidth / originalWidth, (float) maxHeight / originalHeight);
            int drawWidth = (int) (originalWidth * scale);
            int drawHeight = (int) (originalHeight * scale);

            int drawX = (this.width - drawWidth) / 2;
            int drawY = (this.height - drawHeight) / 2;

            graphics.fill(0, 0, this.width, this.height, 0x44000000);
            graphics.blit(
                    location,
                    drawX, drawY,
                    0, 0,
                    drawWidth, drawHeight,
                    drawWidth, drawHeight
            );
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().getTextureManager().release(location);
    }
}