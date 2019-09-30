package app.mcsl.windows.elements.textfield;

import app.mcsl.managers.Language;

public enum InputType {

    ANY(".", ""),
    ONLY_LETTERS("[\\p{L}\\s]", "onlyletters"),
    ONLY_NUMBERS("[0-9]", "onlynumbers"),
    LETTERS_AND_NUMBERS("[\\p{L}\\s0-9]", "lettersandnumbers"),
    EMAIL("[\\p{L}0-9]+@[\\p{L}]+\\.[\\p{L}]+", "emailtexterror");

    String pattern, errorString;

    InputType(String pattern, String errorString) {
        this.pattern = pattern;
        this.errorString = errorString;
    }

    public String getPattern() {
        return pattern;
    }

    public String getErrorString() {
        return Language.getText(errorString);
    }
}
