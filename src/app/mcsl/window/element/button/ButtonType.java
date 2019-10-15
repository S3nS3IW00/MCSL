package app.mcsl.window.element.button;

public enum ButtonType {

    DEFAULT(""), WARNING("warning-button"), ERROR("error-button"), APPLY("apply-button"),
    ROUNDED("rounded-button"), ROUNDED_WARNING("rounded-warning-button"), ROUNDED_ERROR("rounded-error-button"), ROUNDED_APPLY("rounded-apply-button"),
    ACTION_BUTTON("action-button"), APPLY_ACTION_BUTTON("apply-action-button"), WARNING_ACTION_BUTTON("warning-action-button"), ERROR_ACTION_BUTTON("error-action-button");

    String id;

    ButtonType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
