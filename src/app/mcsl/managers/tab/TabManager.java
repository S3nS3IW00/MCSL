package app.mcsl.managers.tab;

import app.mcsl.MainClass;
import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.events.ServerStatusChangeEvent;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerContent;
import app.mcsl.windows.contents.server.ServerStage;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.elements.slide.SlideItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabManager {

    private Map<Tab, TabClass> tabContents = new HashMap<>();
    private Map<Tab, ServerStage> detachedTabs = new HashMap<>();

    Tab addTab(TabClass tabClass, ImageView graphic) {
        Logger.info("Adding tab with name '" + tabClass.getTitle() + "'...");

        Tab tab = new Tab();
        switch (tabClass.getType()) {
            case MAIN:
                if (MainClass.getTemplate().getMainTab() == null) {
                    tab.setText(tabClass.getTitle());
                    tab.setClosable(false);
                    tab.setGraphic(graphic);
                    MainClass.getTemplate().setMainTab(tab);
                    tab.setContent(tabClass.getContent());
                    MainClass.getTemplate().getTabPane().getTabs().add(tab);
                }
                break;
            case SERVER:
                tab.setText(tabClass.getTitle());
                tab.setGraphic(graphic);
                tab.setContent(tabClass.getContent());
                MainClass.getTemplate().getTabPane().getTabs().add(tab);
                tab.getGraphic().setStyle("-fx-effect: innershadow(gaussian, " + ((ServerContent) tabClass).getServer().getStatus().getColor() + ", 7, 1, 1, 1);");
                ServerStatusChangeEvent.addListener((server, newType) -> {
                    if (server == ((ServerContent) tabClass).getServer())
                        tab.getGraphic().setStyle("-fx-effect: innershadow(gaussian, " + newType.getColor() + ", 7, 1, 1, 1);");
                });
                ServerStateChangeEvent.addListener((server, newType) -> {
                    if (server == ((ServerContent) tabClass).getServer()) {
                        switch (newType) {
                            case DELETED:
                                MainClass.getTemplate().getTabPane().getTabs().remove(tab);
                                break;
                            case RENAMED:
                                MainClass.getTemplate().removeDragHandlers(tab);
                                tab.setGraphic(new Label(server.getName(), server.getType() == ServerType.LOCAL ? new ImageView(FileManager.SERVER_ICON) : new ImageView(FileManager.EXTERNAL_SERVER_ICON)));
                                tab.getGraphic().setStyle("-fx-effect: innershadow(gaussian, " + server.getStatus().getColor() + ", 7, 1, 1, 1);");
                                MainClass.getTemplate().addDragHandlers(tab);
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

    void changeContent(TabClass changeClass, TabClass tabClass, SlideItem slideItem, ImageView graphic) {
        Logger.info("Changing '" + changeClass.getTitle() + "' tab's content to '" + tabClass.getTitle() + "'...");

        switch (changeClass.getType()) {
            case MAIN:
                if (graphic != null) MainClass.getTemplate().getMainTab().setGraphic(graphic);
                MainClass.getTemplate().getMainTab().setContent(tabClass.getContent());
                MainClass.getTemplate().getMainTab().setText(tabClass.getTitle());
                MainClass.getTemplate().getSlideMenu().selectItem(slideItem);
                MainClass.getTemplate().setCurrentTabClass(tabClass);
                TabAction.choose(MainClass.getTemplate().getMainTab());
                break;
            case SERVER:
                if (graphic != null) getTabByClass(changeClass).setGraphic(graphic);
                getTabByClass(changeClass).setContent(tabClass.getContent());
                getTabByClass(changeClass).setText(tabClass.getTitle());
                break;
        }
        tabContents.replace(getTabByClass(changeClass), tabClass);
    }

    void detachTab(TabClass tabClass) {
        Logger.info("Detaching tab '" + tabClass.getTitle() + "'...");

        Tab tab = getTabByClass(tabClass);
        if (!(tabClass instanceof ServerContent) || detachedTabs.containsKey(tab)) return;
        MainClass.getTemplate().getTabPane().getTabs().remove(tab);
        ServerStage serverStage = new ServerStage((ServerContent) tabClass);
        serverStage.build();
        serverStage.show();
        detachedTabs.put(getTabByClass(tabClass), serverStage);
    }

    void attachTab(TabClass tabClass) {
        Logger.info("Attaching tab '" + tabClass.getTitle() + "'...");

        Tab tab = getTabByClass(tabClass);
        if (!(tabClass instanceof ServerContent) || !detachedTabs.containsKey(tab)) return;
        detachedTabs.get(tab).close();
        MainClass.getTemplate().getTabPane().getTabs().add(tab);
        detachedTabs.remove(tab);
    }

    public boolean isDetached(Tab tab) {
        return detachedTabs.containsKey(tab);
    }

    public ServerStage getServerStageFromTab(Tab tab) {
        return detachedTabs.get(tab);
    }

    public List<ServerStage> getServerStages() {
        List<ServerStage> serverStages = new ArrayList<>();
        for (Tab tab : detachedTabs.keySet()) {
            serverStages.add(detachedTabs.get(tab));
        }
        return serverStages;
    }

    void removeTab(Tab tab) {
        Logger.info("Removing tab '" + getClassByTab(tab).getTitle() + "'...");
        tabContents.remove(tab);
    }

    public TabClass getClassByTab(Tab tab) {
        return tabContents.get(tab);
    }

    public Tab getTabByClass(TabClass tabClass) {
        for (Tab tab : tabContents.keySet()) {
            if (tabClass == tabContents.get(tab)) {
                return tab;
            }
        }
        return null;
    }

    public boolean isTabExists(TabClass tabClass) {
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getContent() == tabClass.getContent()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTabByTypeExists(TabClass tabClass, TabType tabType) {
        if (tabClass == null) return false;
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getType() == tabType && tabContents.get(tab).getContent() == tabClass.getContent()) {
                return true;
            }
        }
        return false;
    }

    public TabClass getTabClassByServer(Server server) {
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getType() == TabType.SERVER && tabContents.get(tab).getContent() == server.getContent()) {
                return tabContents.get(tab);
            }
        }
        return null;
    }

    public Tab getTabByServer(Server server) {
        for (Tab tab : tabContents.keySet()) {
            if (tabContents.get(tab).getType() == TabType.SERVER && tabContents.get(tab).getContent() == server.getContent()) {
                return tab;
            }
        }
        return null;
    }

}
