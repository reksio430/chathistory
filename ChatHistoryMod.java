package pl.chathistory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatHistoryMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("chathistory");
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        HistoryStore.load();
        LOGGER.info("Chat History zaladowany, wpisow: {}", HistoryStore.ENTRIES.size());

        ClientSendMessageEvents.CHAT.register(msg -> HistoryStore.add(false, msg));
        ClientSendMessageEvents.COMMAND.register(cmd -> HistoryStore.add(true, cmd));

        // Przywracanie historii strzałki w górę po wejściu do gry
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> client.execute(() -> {
            var entries = HistoryStore.ENTRIES;
            int from = Math.max(0, entries.size() - 100);
            for (int i = from; i < entries.size(); i++) {
                HistoryStore.Entry e = entries.get(i);
                client.inGameHud.getChatHud().addToMessageHistory((e.command() ? "/" : "") + e.text());
            }
        }));

        // Klawisz H (do zmiany w Opcje > Sterowanie > Różne)
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chathistory.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories.misc"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new HistoryScreen());
            }
        });

        // Zapasowo: komenda /historia
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("historia").executes(ctx -> {
                    var client = ctx.getSource().getClient();
                    client.send(() -> client.setScreen(new HistoryScreen()));
                    return 1;
                })));
    }
}
