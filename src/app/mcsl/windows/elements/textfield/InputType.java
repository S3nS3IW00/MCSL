package app.mcsl.windows.elements.textfield;

import app.mcsl.managers.Language;

public enum InputType {

    ANY(".", ""),
    ONLY_LETTERS("[a-zA-Z ]", "onlyletters"),
    ONLY_NUMBERS("[0-9]", "onlynumbers"),
    LETTERS_AND_NUMBERS("[a-zA-Z0-9 ]", "lettersandnumbers"),
    EMAIL("[a-zA-Z0-9]+@[a-zA-Z]+\\.[a-zA-Z]+", "emailtexterror");

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
