package app.mcsl.window.element.dialog.type;

import app.mcsl.window.element.label.LabelColor;

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
