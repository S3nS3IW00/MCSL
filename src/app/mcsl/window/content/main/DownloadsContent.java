package app.mcsl.window.content.main;

import app.mcsl.manager.Language;
import app.mcsl.manager.download.DownloadItem;
import app.mcsl.manager.download.DownloadManager;
import app.mcsl.manager.download.DownloadType;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.tab.TabClass;
import app.mcsl.manager.tab.TabType;
import app.mcsl.window.element.DownloadCard;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DownloadsContent implements TabClass {

    private VBox body;
    private VBox[] typeBoxes;

    public DownloadsContent() {
        DownloadManager.fetch();

        typeBoxes = new VBox[DownloadManager.getDownloadTypes().size()];

        for (int i = 0; i < typeBoxes.length; i++) {
            DownloadType type = DownloadManager.getDownloadTypes().get(i);
            Label typeTitle = new Label(type.getDisplayName());
            typeTitle.setId("underlined-title");

            HBox typeCardBox = new HBox(10);
            typeCardBox.setPadding(new Insets(5, 5, 5, 5));
            ScrollPane typeCardScroll = new ScrollPane();
            typeCardScroll.setMinHeight(230);
            typeCardScroll.setContent(typeCardBox);
            typeCardScroll.setFitToHeight(true);
            typeCardScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            VBox typeBox = new VBox(10, typeTitle, typeCardScroll);
            for (DownloadItem downloadItem : DownloadManager.getDownloadItemsByType(type)) {
                typeCardBox.getChildren().add(new DownloadCard(downloadItem));
            }
            typeBoxes[i] = typeBox;
        }

        body = new VBox();
        for (VBox vBox : typeBoxes) {
            body.getChildren().add(vBox);
        }
        body.setSpacing(20);
        body.setPadding(new Insets(10));
    }

    @Override
    public Node getContent() {
        return body;
    }

    @Override
    public TabType getType() {
        return TabType.MAIN;
    }

    @Override
    public String getTitle() {
        return Language.getText("downloads");
    }

    @Override
    public Image getIcon() {
        return FileManager.DOWNLOAD_ICON_20;
    }
}
