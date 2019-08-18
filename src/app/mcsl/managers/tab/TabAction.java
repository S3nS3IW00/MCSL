package app.mcsl.managers.tab;

import app.mcsl.MainClass;
import app.mcsl.managers.logging.Logger;
import app.mcsl.windows.elements.slide.SlideItem;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

public class TabAction {

    public static Tab add(TabClass tabClass, ImageView graphic, boolean select) {
        Tab tab = MainClass.getTabManager().addTab(tabClass, graphic);
        if (select) choose(tab);
        return tab;
    }

    public static void detach(Tab tab) {
        MainClass.getTabManager().detachTab(MainClass.getTabManager().getClassByTab(tab));
    }

    public static void attach(Tab tab) {
        MainClass.getTabManager().attachTab(MainClass.getTabManager().getClassByTab(tab));
    }

    public static void changeContent(TabClass changeClass, TabClass tabClass, SlideItem slideItem, ImageView graphic) {
        MainClass.getTabManager().changeContent(changeClass, tabClass, slideItem, graphic);
    }

    public static void choose(TabClass tabClass) {
        if (MainClass.getTabManager().isTabExists(tabClass))
            MainClass.getTemplate().getTabPane().getSelectionModel().select(MainClass.getTabManager().getTabByClass(tabClass));
    }

    public static void choose(Tab tab) {
        if (MainClass.getTemplate().getTabPane().getTabs().contains(tab))
            MainClass.getTemplate().getTabPane().getSelectionModel().select(tab);
    }

    public static void close(Tab tab){
        if(MainClass.getTabManager().isDetached(tab)){
            MainClass.getTabManager().attachTab(MainClass.getTabManager().getClassByTab(tab));
        }
        MainClass.getTemplate().getTabPane().getTabs().remove(tab);
        MainClass.getTabManager().removeTab(tab);
    }

}
