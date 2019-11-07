package app.mcsl.manager.tab;

import app.mcsl.event.ServerStateChangeEvent;
import app.mcsl.event.ServerStatusChangeEvent;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.logging.Logger;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.ServerContent;
import app.mcsl.window.content.server.ServerStage;
import app.mcsl.window.content.server.ServerType;
import app.mcsl.window.element.slide.SlideItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabManager {

    private static Map<Tab, TabClass> tabContents = new HashMap<>();
    private static Map<Tab, ServerStage> detachedTabs = new HashMap<>();

    static Tab addTab(TabClass tabClass, ImageView graphic) {
        Logger.info("Adding tab with name '" + tabClass.getTitle() + "'...");

        Tab tab = new Tab();
        switch (tabClass.getType()) {
            case MAIN:
                if (Template.getMainTab() == null) {
                    tab.setText(tabClass.getTitle());
                    tab.setClosable(false);
                    tab.setGraphic(graphic);
                    Template.setMainTab(tab);
                    tab.setContent(tabClass.getContent());
                    Template.getTabPane().getTabs().add(tab);
                }
                break;
            case SERVER:
                tab.setText(tabClass.getTitle());
                tab.setGraphic(graphic);
                tab.setContent(tabClass.getContent());
                Template.getTabPane().getTabs().add(tab);
                tab.getGraphic().setStyle("-fx-effect: innershadow(gaussian, " + ((ServerContent) tabClass).getServer().getStatus().getColor() + ", 7, 1, 1, 1);");
                ServerStatusChangeEvent.addListener((server, newType) -> {
                    if (server == ((ServerContent) tabClass).getServer())
                        tab.getGraphic().setStyle("-fx-effect: innershadow(gaussian, " + newType.getColor() + ", 7, 1, 1, 1);");
                });
                ServerStateChangeEvent.addListener((server, newType) -> {
                    if (server == ((ServerContent) tabClass).getServer()) {
                        switch (newType) {
                            case DELETED:
                                Template.getTabPane().getTabs().remove(tab);
                                break;
                            case RENAMED:
                                Template.removeDragHandlers(tab);
                                tab.setGraphic(new Label(server.getName(), server.getType() == ServerType.LOCAL ? new ImageView(FileManager.SERVER_ICON) : new ImageView(FileManager.EXTERNAL_SERVER_ICON)));
                                tab.getGraphic().setStyle("-fx-effect: innershadow(gaussian, " + server.getStatus().getColor() + ", 7, 1, 1, 1);");
                                Template.addDragHandlers(tab);
                                break;
                        }
                    }
                });
                break;
        }
        tab.setOnClosed(e -> removeTab(tab));
        tabContents.put(tab, tabClass);

        return tab;
    }

    static void changeContent(TabClass changeClass, TabClass tabClass, SlideItem slideItem) {
        Logger.info("Changing '" + changeClass.getTitle() + "' tab's content to '" + tabClass.getTitle() + "'...");

        switch (changeClass.getType()) {
            case MAIN:
                if (tabClass.getIcon() != null) Template.getMainTab().setGraphic(new ImageView(tabClass.getIcon()));
                Template.getMainTab().setContent(tabClass.getContent());
                Template.getMainTab().setText(tabClass.getTitle());
                Template.getSlideMenu().selectItem(slideItem);
                Template.setCurrentTabClass(tabClass);
                TabAction.choose(Template.getMainTab());
                break;
            case SERVER:
                if (tabClass.getIcon() != null)
                    getTabByClass(changeClass).setGraphic(new ImageView(tabClass.getIcon()));
                getTabByClass(changeClass).setContent(tabClass.getContent());
                getTabByClass(changeClass).setText(tabClass.getTitle());
                break;
        }
        tabContents.replace(getTabByClass(changeClass), tabClass);
    }

    static void detachTab(TabClass tabClass) {
        Logger.info("Detaching tab '" + tabClass.getTitle() + "'...");

        Tab tab = getTabByClass(tabClass);
        if (!(tabClass instanceof ServerContent) || detachedTabs.containsKey(tab)) return;
        Template.getTabPane().getTabs().remove(tab);
        ServerStage serverStage = new ServerStage((ServerContent) tabClass);
        serverStage.build();
        serverStage.show();
        detachedTabs.put(getTabByClass(tabClass), serverStage);
    }

    static void attachTab(TabClass tabClass) {
        Logger.info("Attaching tab '" + tabClass.getTitle() + "'...");

        Tab tab = getTabByClass(tabClass);
        if (!(tabClass instanceof ServerContent) || !detachedTabs.containsKey(tab)) return;
        detachedTabs.get(tab).close();
        Template.getTabPane().getTabs().add(tab);
        detachedTabs.remove(tab);
    }

    public static boolean isDetached(Tab tab) {
        return detachedTabs.containsKey(tab);
    }

    public static ServerStage getServerStageFromTab(Tab tab) {
        return detachedTabs.get(tab);
    }

    public static List<ServerStage> getServerStages() {
        List<ServerStage> serverStages = new ArrayList<>();
        for (Tab tab : detachedTabs.keySet()) {
            serverStages.add(detachedTabs.get(tab));
        }
        return serverStages;
    }

    static void removeTab(Tab tab) {
        Logger.info("Removing tab '" + getClassByTab(tab).getTitle() + "'...");
        tabContents.remove(tab);
    }

    public static TabClass getClassByTab(Tab tab) {
        return tabContents.get(tab);
    }

    public static Tab getTabByClass(TabClass tabClass) {
        for (Tab tab : tabContents.keySet()) {
            if (tabClass == tabContents.get(tab)) {
                return tab;
            }
        }
        return null;
    }

    public static boolean isTabExists(TabClass tabClass) {
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getContent() == tabClass.getContent()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTabByTypeExists(TabClass tabClass, TabType tabType) {
        if (tabClass == null) return false;
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getType() == tabType && tabContents.get(tab).getContent() == tabClass.getContent()) {
                return true;
            }
        }
        return false;
    }

    public static TabClass getTabClassByServer(Server server) {
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getType() == TabType.SERVER && tabContents.get(tab).getContent() == server.getContent()) {
                return tabContents.get(tab);
            }
        }
        return null;
    }

    public static Tab getTabByServer(Server server) {
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getType() == TabType.SERVER && tabContents.get(tab).getContent() == server.getContent()) {
                return tab;
            }
        }
        return null;
    }

}
