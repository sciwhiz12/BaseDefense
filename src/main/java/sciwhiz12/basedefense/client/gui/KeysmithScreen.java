package sciwhiz12.basedefense.client.gui;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import sciwhiz12.basedefense.container.KeysmithContainer;
import sciwhiz12.basedefense.init.ModItems;
import sciwhiz12.basedefense.init.ModTextures;
import sciwhiz12.basedefense.net.NetworkHandler;
import sciwhiz12.basedefense.net.TextFieldChangePacket;

public class KeysmithScreen extends ContainerScreen<KeysmithContainer> implements IContainerListener {
    private TextFieldWidget nameField;
    private boolean isEnabledText = false;

    public KeysmithScreen(KeysmithContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nameField = new TextFieldWidget(this.font, i + 91, j + 28, 82, 12, "");
        this.nameField.setCanLoseFocus(false);
        this.nameField.changeFocus(true);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.nameField.setResponder(this::onTextChange);
        this.container.addListener(this);
        this.children.add(this.nameField);
        this.setFocusedDefault(this.nameField);
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        String s = this.nameField.getText();
        this.init(mc, width, height);
        this.nameField.setText(s);
    }

    @Override
    public void removed() {
        super.removed();
        this.container.removeListener(this);
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) { this.minecraft.player.closeScreen(); }

        return this.nameField.keyPressed(key, scanCode, modifiers) || this.nameField.canWrite() || super.keyPressed(
            key, scanCode, modifiers
        );
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.nameField.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8, 6, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, 73, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(ModTextures.KEYSMITH_GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
        if (this.nameField.canWrite()) {
            this.blit(relX + 88, relY + 24, 0, 166, 82, 15);
        } else {
            this.blit(relX + 88, relY + 24, 0, 181, 82, 15);
        }
    }

    private void onTextChange(String newText) {
        boolean flag1 = newText.equals(I18n.format(ModItems.KEY.getTranslationKey()));
        boolean flag2 = StringUtils.isBlank(newText);
        if (flag1 || flag2) { newText = ""; }
        container.setOutputName(newText);
        NetworkHandler.CHANNEL.sendToServer(new TextFieldChangePacket(newText));
    }

    @Override
    public void sendAllContents(Container container, NonNullList<ItemStack> itemsList) {
        this.sendSlotContents(container, 0, container.getSlot(0).getStack());
        this.sendSlotContents(container, 1, container.getSlot(1).getStack());
        this.sendSlotContents(container, 2, container.getSlot(2).getStack());
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        if (slotInd == 0) {
            if (stack.isEmpty() && isEnabledText) {
                isEnabledText = false;
                nameField.setText("");
            } else if (!stack.isEmpty() && !isEnabledText) {
                isEnabledText = true;
                nameField.setText(I18n.format(ModItems.KEY.getTranslationKey()));
            }
            this.minecraft.deferTask(() -> this.nameField.setEnabled(isEnabledText));
        } else if (slotInd == 1) {
            if (!stack.isEmpty() && isEnabledText) { nameField.setText(stack.getDisplayName().getString()); }
        }
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {}
}
