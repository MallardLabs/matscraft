package com.mallardlabs.matscraft.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    protected abstract void init();

    protected TitleScreenMixin(Text title) {
        super(title);
    }
    private static String getServerUrl() {
        // Replace this with your actual server address
        String serverAddress = "localhost";
        ServerAddress address = ServerAddress.parse(serverAddress);
        System.out.println("Server address: " + address);
        return serverAddress;
    }
    // List of buttons to hide
    private static final List<String> BUTTONS_TO_HIDE = List.of("Multiplayer", "Minecraft Realms");

    /**
     * Inject into the `initWidgetsNormal` method to modify the screen after initialization.
     */
    @Inject(method = "initWidgetsNormal", at = @At("RETURN"))
    private void customizeButtons(CallbackInfo ci) {
        adjustButtonVisibilityAndPosition();
        addCustomPlayButton();
    }

    /**
     * Adjust the visibility of specific buttons and reposition the Singleplayer button.
     */
    private void adjustButtonVisibilityAndPosition() {
        for (var element : ((TitleScreen) (Object) this).children()) {
            if (element instanceof ButtonWidget button) {
                handleButtonVisibility(button);
                handleSingleplayerButtonPosition(button);
            }
        }
    }

    /**
     * Hide buttons based on their text labels.
     *
     * @param button The button to check and potentially hide.
     */
    private void handleButtonVisibility(ButtonWidget button) {
        if (BUTTONS_TO_HIDE.contains(button.getMessage().getString())) {
            button.visible = false;
        }
    }

    /**
     * Reposition the Singleplayer button to avoid overlapping with custom buttons.
     *
     * @param button The button to check and reposition.
     */
    private void handleSingleplayerButtonPosition(ButtonWidget button) {
        if ("Singleplayer".equals(button.getMessage().getString())) {
            button.setPosition(this.width / 2 - 100, this.height / 4 + 90);
        }
    }

    /**
     * Add a custom button to connect to the MatsCraft server.
     */
    private void addCustomPlayButton() {
        ServerInfo serverInfo = new ServerInfo("MatsCraft", getServerUrl(), ServerInfo.ServerType.OTHER);
        this.addDrawableChild(
                ButtonWidget.builder(Text.of("Play MatsCraft"), button -> connectToMatsCraftServer(serverInfo,getServerUrl()))
                        .dimensions(this.width / 2 - 100, this.height / 4 + 60, 200, 20)
                        .build()
        );
    }

    /**
     * Connect to the MatsCraft server when the custom button is pressed.
     *
     * @param serverInfo    The server information for MatsCraft.
     * @param serverUrl
     */
    private void connectToMatsCraftServer(ServerInfo serverInfo, String serverUrl) {
        ServerAddress serverAddress = ServerAddress.parse(serverUrl);
        ConnectScreen.connect(this, MinecraftClient.getInstance(), serverAddress, serverInfo, true, null);
    }
}
