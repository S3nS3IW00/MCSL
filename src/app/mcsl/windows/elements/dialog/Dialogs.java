package app.mcsl.windows.elements.dialog;

import java.util.LinkedList;

class Dialogs {

    private static LinkedList<Dialog> dialogList = new LinkedList<>();

    static void addDialog(Dialog dialog) {
        dialogList.add(dialog);
    }

    static void removeDialog(Dialog dialog) {
        dialogList.remove(dialog);
    }

    static void showNext() {
        dialogList.get(0).show();
        dialogList.remove(dialogList.get(0));
    }

    static boolean hasNext() {
        return dialogList.size() > 0;
    }

    static boolean isExists(Dialog dialog) {
        return dialogList.contains(dialog);
    }

    static boolean canShow() {
        return dialogList.size() == 0;
    }

    static Dialog next() {
        return dialogList.get(0);
    }

}
