package app.mcsl.windows.elements.dialog.types;

import app.mcsl.windows.elements.label.LabelColor;

public enum AlertType {

    DEFAULT(LabelColor.DEFAULT), SUCCESS(LabelColor.SUCCESS), WARNING(LabelColor.WARNING), ERROR(LabelColor.ERROR);

    LabelColor color;

    AlertType(LabelColor color) {
        this.color = color;
    }

    public LabelColor getColor() {
        return color;
    }
}
