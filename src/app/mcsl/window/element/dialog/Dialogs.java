package app.mcsl.window.element.dialog;

import java.util.LinkedList;

public class Dialogs {

    private static LinkedList<Dialog> dialogList = new LinkedList<>();

    public static void init() {
        if (Dialogs.hasNext()) Dialogs.showNext();
    }

    static void addDialog(Dialog dialog) {
        dialogList.add(dialog);
    }

    static void addDialog(Dialog dialog, int index) {
        dialogList.add(index, dialog);
    }

    static void removeDialog(Dialog dialog) {
        dialogList.remove(dialog);
    }

    static void showNext() {
        dialogList.get(0).show();
        dialogList.remove(dialogList.get(0));
    }

    public static boolean hasNext() {
        return dialogList.size() > 0;
    }

    static boolean isExists(Dialog dialog) {
        return dialogList.contains(dialog);
    }
}
