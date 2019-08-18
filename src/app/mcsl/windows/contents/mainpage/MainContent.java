package app.mcsl.windows.contents.mainpage;

import app.mcsl.managers.Language;
import app.mcsl.managers.tab.TabClass;
import app.mcsl.managers.tab.TabType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelType;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class MainContent implements TabClass {

    public MainContent() {

    }

    @Override
    public Pane getContent() {
        WebView facebookView = new WebView();
        facebookView.setMaxSize(300, 500);
        //facebookView.getEngine().loadContent("<html><head><style>body{ margin: 0;}</style><body><iframe src=\"https://www.facebook.com/plugins/page.php?href=https%3A%2F%2Fwww.facebook.com%2Fmcserverlauncher&tabs=timeline&width=300&height=500&small_header=true&adapt_container_width=true&hide_cover=false&show_facepile=false&appId=107351936395222\" width=\"300\" height=\"500\" style=\"border:none;overflow:hidden\" scrolling=\"no\" frameborder=\"0\" allowTransparency=\"true\" allow=\"encrypted-media\"></iframe></body></html>");

        Label facebookTitle = new Label("Facebook", LabelType.H2);

        VBox facebookBox = new VBox(5, facebookTitle, facebookView);

        HBox body = new HBox(facebookBox);
        body.setPadding(new Insets(10));

        return body;
    }

    @Override
    public TabType getType() {
        return TabType.MAIN;
    }

    @Override
    public String getTitle() {
        return Language.getText("mainpage");
    }
}
