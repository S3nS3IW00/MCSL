package app.mcsl.manager.tab;

import app.mcsl.window.Template;
import app.mcsl.window.element.slide.SlideItem;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

public class TabAction {

    public static Tab add(TabClass tabClass, ImageView graphic, boolean select) {
        Tab tab = TabManager.addTab(tabClass, graphic);
        if (select) choose(tab);
        return tab;
    }

    public static void detach(Tab tab) {
        TabManager.detachTab(TabManager.getClassByTab(tab));
    }

    public static void attach(Tab tab) {
        TabManager.attachTab(TabManager.getClassByTab(tab));
    }

    public static void changeContent(TabClass changeClass, TabClass tabClass, SlideItem slideItem) {
        TabManager.changeContent(changeClass, tabClass, slideItem);
    }

    public static void choose(TabClass tabClass) {
        if (TabManager.isTabExists(tabClass))
            Template.getTabPane().getSelectionModel().select(TabManager.getTabByClass(tabClass));
    }

    public static void choose(Tab tab) {
        if (Template.getTabPane().getTabs().contains(tab))
            Template.getTabPane().getSelectionModel().select(tab);
    }

    public static void close(Tab tab) {
        if (TabManager.isDetached(tab)) {
            TabManager.attachTab(TabManager.getClassByTab(tab));
        }
        Template.getTabPane().getTabs().remove(tab);
        TabManager.removeTab(tab);
    }

}
