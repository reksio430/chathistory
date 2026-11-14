package pl.chathistory;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HistoryScreen extends Screen {
    private static final int LINE_H = 12;
    private int scroll = 0; // ile linii przewinięto od dołu (0 = najnowsze)

    public HistoryScreen() {
        super(Text.literal("Historia czatu i komend"));
    }

    private int visibleLines() {
        return (height - 50) / LINE_H;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer,
                "Scroll: przewijanie | Klik: kopiuj linię | ESC: zamknij (" + HistoryStore.ENTRIES.size() + " wpisów)",
                10, 24, 0xAAAAAA);

        int total = HistoryStore.ENTRIES.size();
        int vis = visibleLines();
        int end = total - scroll;
        int start = Math.max(0, end - vis);
        int y = 40;
        for (int i = start; i < end; i++) {
            HistoryStore.Entry e = HistoryStore.ENTRIES.get(i);
            ctx.drawTextWithShadow(textRenderer, textRenderer.trimToWidth(e.display(), width - 20),
                    10, y, e.command() ? 0xFFFF55 : 0xFFFFFF);
            y += LINE_H;
        }
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        int max = Math.max(0, HistoryStore.ENTRIES.size() - visibleLines());
        scroll = Math.max(0, Math.min(max, scroll + (int) Math.signum(vAmount) * 3));
        return true;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int row = (int) ((my - 40) / LINE_H);
        int total = HistoryStore.ENTRIES.size();
        int end = total - scroll;
        int start = Math.max(0, end - visibleLines());
        int idx = start + row;
        if (row >= 0 && idx < end && client != null) {
            HistoryStore.Entry e = HistoryStore.ENTRIES.get(idx);
            client.keyboard.setClipboard((e.command() ? "/" : "") + e.text());
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
