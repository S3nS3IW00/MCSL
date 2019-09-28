package app.mcsl.windows.elements.tooltip;

import app.mcsl.managers.file.FileManager;
import javafx.scene.image.Image;

public enum TooltipType {

    DEFAULT("", FileManager.INFO_ICON_20),
    WARNING("warning-tooltip", FileManager.WARNING_ICON_20),
    ERROR("error-tooltip", FileManager.ERROR_ICON_20),
    APPLY("apply-tooltip", FileManager.SUCCESS_ICON_20);

    private String id;
    private Image icon;

    TooltipType(String id, Image icon) {
        this.id = id;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public Image getIcon() {
        return icon;
    }
}
