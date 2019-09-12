package app.mcsl.windows.elements;

import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.events.ServerStatusChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.server.ServerAction;
import app.mcsl.managers.server.ServersManager;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.coloredtextflow.ColoredTextFlow;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelColor;
import app.mcsl.windows.elements.label.LabelType;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.IOException;

import static app.mcsl.windows.contents.server.StatusType.STOPPED;

public class ServerCard extends StackPane {

    private ImageView chooseIconView = new ImageView(FileManager.CHOOSE_ICON);
    private ImageView renameIconView = new ImageView(FileManager.RENAME_ICON);
    private ImageView deleteIconView = new ImageView(FileManager.DELETE_ICON);
    private ImageView startIconView = new ImageView(FileManager.START_ICON);
    private ImageView stopIconView = new ImageView(FileManager.STOP_ICON);

    private HBox layer;
    private FadeTransition in, out;
    private TextField renameTextField;
    private Button chooseButton, renameButton, deleteButton, startButton;
    private Label statusLine, playerCount, pingLabel;
    private ImageView graphic, favicon;
    private ColoredTextFlow motdFlow;
    private PingIndicator pingIndicator;
    private boolean showLayer = true;

    private Server server;

    public ServerCard(Server server, int height) {
        this.server = server;

        HBox.setHgrow(this, Priority.ALWAYS);
        setMaxHeight(height);
        setPrefHeight(height);
        setId("item-box");

        Label title = new Label(server.getName(), LabelType.H2);
        graphic = new ImageView(server.getType() == ServerType.LOCAL ? FileManager.SERVER_ICON : FileManager.EXTERNAL_SERVER_ICON);
        graphic.setStyle("-fx-effect: innershadow(gaussian, " + server.getStatus().getColor() + ", 7, 1, 1, 1);");
        title.setGraphic(graphic);
        title.setId("item-box-title");

        Region statusLineRegion = new Region();
        VBox.setVgrow(statusLineRegion, Priority.ALWAYS);

        statusLine = new Label("", LabelType.DEFAULT);

        motdFlow = new ColoredTextFlow(15);
        motdFlow.append(Language.getText("offlinemotd"));
        motdFlow.setTextAlignment(TextAlignment.CENTER);
        motdFlow.setPadding(new Insets(10, 100, 0, 0));

        try {
            favicon = new ImageView(FileManager.decodeBase64ToImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QA/wD/AP+gvaeTAAAAB3RJTUUH4wcECDgysB00CAAAHkNJREFUeNrdm/m7pVV15z9r7/c90z333ro1FwUUY6GICCgOIIIGHDvmkSSd5AlG0+3QUTKbbjJ0RJM8GBO72xjTaQ1m0PBEE4WYwXTUSIwkQERmCyjGogqo+Y5neN93r9U/7P2+59wif0CevvXcOufcc9797r32Wt/1Xd+1jwD82EevoLXJIYAWSD4niBMEwcwACJWiaoChagwGFZiR3o6PZhiQ5yDxz1SVMRzGD6lCKzeyDEzj+/3ZfroPVKGiKqvmnqNRiVYhXmvxetN4DwAh3d+grAxVEDGqiql7Gv0edNsCpSPriQGMnlG+9un7kKuveyUnXTwHQLESXN53aUj45A98lf8ffq785RelFSFSiZg3xcCOeORn//It+EwYLpby6NeO2aaze625XVnfeTEQTA1k4gGCEULtATS7ZQZihgKtHJB4z1AawyHRc0xoZUqexd1EhH5/BkEAoapKyrJK0zaGoxItQxpfCGZYsKmlJQ8QoSx04gEBhgMDETQo/Rmh1xZpaXt5c7tfPTc+Lpp8N3v8s8bWtwydKXrSxf1L23PuMyJsMyUgJhDdddrFa7djavEwcc30Ekl/ad6Z+rzV71syVgqn2qAAaL3AOIbp+vs1ZlBrbmqW5kvckHSBqVk20NG+fWuja4B7EXO946dotvUNIzl+X6lrByq/6629T4lntykK5urYtKnFrrP++pfNwk2nFq+TudZXTBaQxm+ssn48mR47vW/NlVM2NZsy8tTv9KYYath5Inzmq9c88NL/+pPX6H0X3y9ZcTiX7oaWdWZkF1qeac7MzBCRNAWL/5sRpkAwTO9G8ox6olnuwEUTmBg6nADWut0zEElLEYdJIKimu0KlSlCNyzDBTBpPQZIR0qCWQDI6jsXPJe8wk4kVYNdVnztv492vuOeYAVkYCzoGK8lVabk0rqqJhqpxq5ZktNOiFINMml1rFp9uEp4cYYWCCdIGv+DjYi1OqKo9A2NtMEYQ1IxMHDNZtwmDrJURXEBECKoMy6JZTAhKCGnnDZyAuDQRHzNRbRgR0GDE/TOpUiyJCFm0PljjbwCOUFWURYEgBDXmez06voWaoWJ0cjkhZqUON45/Z4nqeIkzQXZkZK/rQRknGwKEYhK7w9EwpcDAppk+OzYuUIWU+pw245eh4hCLJO9kNIKyqhCJxmzlEQAx8NEBmxATjKBN7Ik1c4UsxlazmzKJ9+R2CUx03a8S1CbuGIE4ARXQEmgnq+Y1iE6HzCQMRGIOcHH7CKqEhGKqilqcblCd3C+5ODY11anxVW09dskEcI3JZ0UkGiB+OLqhq18LJP/AdLK7pvF3ncOkRBYvMqiBb3py9c0FzE9soIU1n2mcyQRpALGO/zq2E55gqGtAnrwGjnVgXe+OYY4TPDZuWNZzbYIDdbAha+G9IR3Pyt5ljt5+FN/2hCLQfv1mZk6axUplXJU8s7zY7IA4cNMgZ9qwSBHI8kgKLIN8T4EcqCKGZBAu6mJ5zNcuM5arQdxBoG0ZPo5M5jxbugtxzMwx3LvMcO8SkrsYdqd5yjYQ4j2l3gtv9PcF2kcV9c/PNpnHNX9sk+EBcY7hQLB9JdYzbFzhS0eW55gGyhAYVYE6fMQZTupdNlpTaSoiSrqng2xZcQcD5EDLIU4wLygOnFFopMJqRkv8JGwQOr4VOXHusbFDjyjSThnqJEflgRAXn/n4d3XgVoz8SEATcE+n4QwH5iPjMx/dEy+YNyxTLFMIEVll6n3xkjDCEANpnq8PjghmcfHmoxdYDuTRA9RqF0/b4l30IANU1u2WufRSjCBG8IakusJc/K1Jgrq0IU5QZ6iP3lBnn5pLZOVqRShAKxgfDfgMJBfCALLuLK7lEFGK5Yrhc2tYaYzLEpZCmqQhXYFOjNt15EgEKpDFAKXFRRdToW3EVBYUU8ONFBuUCQ+MslOi3qOq0cCrIeJP7mCseO8SZAjZKKY5CXFwnwEKwRu+Apf4hkkNcNEC2eJdR1DxgFDev4TD0KqkvWknc+ddglZj8BnPffNeqpWjiM/AjHbt2yODCztwQRcbabNuLIKoHA9kfz9Y7xKZNAC5tlahY1AP3f1jWt+toAWuhOMvyyg2CaKGWzVm76gi+Cl02p7ZuXb0IMAeG0dj1q9T7Kga3U5GPttK3nbCVCZ4KE3c1uQsljZxIEku0zh4w+CiRVUT+p/w9hQWTyeNibeYgUramcQ9a6KoIGlc0zS8xc9aor+WcIcTEP7EUr35DCeAYFCLA2KUQXEoWilOQSVLRvBUalRBEbRxUZxAqThRXFviRBNVptLGXHIiLCRWqAYhEzSD4CSidFxjNERmaO4wBe81xinxQstysDQ/S/xBG5KRtnNiKE40Qm2AhbkuwQQ1YevCfPROVbQl6Op+UMVEmJ3LoLeAITiBTivDAK9wdFBy9M4BPhGqTd0uPhdMQINSpJK22ZlkBHXG+IkqYp1AvhLztVgEtNZ+xR9NJL/yhP52wAgm5DbEbJiosdHvtZtllVVgMCimQmE9B2iqVYFsYbZLGeKGnbKpQ+6i/QbDEUtLB3AuIvTs3Ax53iNoIHOOjbMdghqt3PPQ44c5/OAiWR7xYdOZs7TbGaqRrq6ujYBJIVPvgAalfHKUdAaJSO5TCIjRPlATL8VaGcX2bSliHDo6hA3XUmqC/kwbF/kvw1HJ2tq4waKpZa8v3oAsBCUoBIWyFHDxzSqAuQxNBqiCYVKhanHnypAoZ0xJdFwDbpUavoo0tkpFCzKRryy5ppqhuaBTpW+Toy0iOMkglhkWquTeDjQ0GACR/k5o81R1mra/vucJMIFrUCkh0zRRnPjO5FMngtl0TP9bdfjzkdBOAKvJtdOLnwBb2jGdur7Zycl42oxZk536c1NgaZMR6hlmg1FBpUIwWFyuyMRwzrE8GHP4+BrexWrwtJMW6GdtjAg2x1ZHkZYCzgnbN/URid7inZtUcWXg6NIQcXICT19fmJhBlgmdVn4CcAFkVGqsHXkUMESJBCjzDSAPhxOCoQr9XguI4Lgw12Om2yIEfX4xFF0nIbIqkrygqgJFWeG9I6SqbJJ2INRUGMFEyLMJbZ3cRJowcHVl+W84kaXc50QS5tQ4UO+3w1lAqnE0uhm4DCNvFhPrh4n3OJHkDELmHVnmk/hiE+sL66vBmlJqUnvESSxXZcKcJmNIei2gAa3KxgOwHERifc56QqDUFd10ZMTU6bysc2dVTQaNOFIl7U8NsikwqxF+vYPFsKlL+CZ1M3FEAbJWniEKzqDXzckluvS4DOsGrIJSlmHKjer7KJVro70ZxGoAHSMhViWqSiv3US8wyDNP5mVSctskTmvpjfTYyrMmC4WwnuEBBDPElHYmE9nOolpUhIncFlIqrvsaDQaJkG3odxgHowzGzi0tcgfeO9SMp55bjEUQsUlBEkLWYWMoqRZOIyycDqGIaeqZu5FiiEnEgvl+C0MIQdm6cYa5mc46Q5opToTF1REHj63gXGSWC3Ndeu2cqpbCJYarEzi6NGRpcYlxZzPfXeowN+OZ7Tq6FLREyVcPUySJfVhUlGpoEg3XGWDiHrEwcUTJSVMtL43vx0epcVgm8SumEEoslOuSQ/2jCa017WRMjdrsppnF3D6l5NSMMqSJm5AETkPFY6FE27N880CfgxtORh7bF+eysBM3WubF88KL2s8xKqt1PGA9ChvOpuJjIlunG6kRagksTboKSqXWPA9BI5OUbPJb5/gEsA0LsxhezgniHK7GF5GILbLees2fXGqdiCA4HEp7Zobb9s9wcOfZuD/7A+Q3fhWZyUH68IKruGd5C7cdmcMn0VBtYuzpn6xJR1NP1Ix2nrFlwwzeR3fcujBDt51PtMDGqMqQAcOVRxMGWBQ7XAtEqIIyHle1x7G0OmJtVCT2B61EqZ3A6rBkMCxxLqrAq8MiGlvXh0vWavGNfTM81ZrH/eYHMe+xN17Ophedw9yHf50nHrmL/NwrefKRb2FHv8sVp4zjhtRZZ4ozZPXONGyJOic7ZmdaOIk8YLbXptepDVC7dFyUDEboYKnJAuoduLpDWjFK1NaAwbjEhhNvm7FOI2GNi5KiDFHGVovuK0z6ESa0vfL3j7V58JDh/s8H0Z3bkJUAI+EtZ2znwRs/wfZPfop//fytuDMv4alnZ/jmgbu4fOew0RVliqi5aVCY9oSa/obk7kHr5zEm650JQdE6BPAE8elGmlTdlJLSY02cnAjeOZxLRkxWeB6HlFpxdnR84J7jMzz4NLhf+Xn05S/GLQfMe97Uczy6ppxZKdf99/fy/o9cy8yhu2DjWTyx+XJuPdCJ+IawWmRSBI+a4Op4DRabn5oWpo10FKcUUjkc0uJVteHfmgoWNY2FS0M7ZV2ur7GglruDrr+nTZEhJ41jEhRaVBwo57ntoQD/+R3o1W/CLQbUec7NotjUco6r244DRwIvf/OlfPCPfp0zes9CNs+TJ7+Vbz7XB63o9XK5eMsK29pD5D1Xv9aValop55y54B7KnWGGjYtKVgfjxq1rFhU7LUKv22o2aTgqGI3LuINm9LqtJgeXVWBtMG4+OxhHN6+Z2Ew3b1w+8z5iQsMLBEXwVjHM57j5blh69RXIR38NRorh2ODh9V14LMD7e1AX3qEKdPqecjzmpg//Lv9yx3NWzm+SLU/+1fIFS/+8++yCg38xd77zF77gNElS/+YNbbtWUtqqgkpZTer4hjInHpDnvmFYZRUoq9DscJ5n1MwnaK0HxNRZVkpVTaSj3Lu402p478hzHxcviTqrUmUdvvJwzpFTdiO/fQNGjmhEzjd04SmFH+nATJQgU5g5xmNl3ntOfculfLNYluLOe7TackFnr2095fbHH/zi6vGDljXSUaLD9c7UHRZJreu6g2NTaQmiIFmTM5G6yCC5cE2lJwE9Sbk0xU4tj01XafGzSt7K+PqTXZ71C7gbPox2u7iRos5xSQuOKLyuBdudsRysMSjAQub419L49KHAyrt+AM7Y5cpP/pm2Fnb+0Oisq9RWF388q+NVpyeR8nWrlTWzXBsWlCHunJdYYNSTrYI2RjCMtVSZWSqwxmU1WSzQyv0JHZr4XwhKUVTpOqOTwV1HFnj0mMN9/IPoqSchKwHNPKfn0HOwM4MLM2OQw1zfsbaiZAgtJ3yhhFsqwZxHDpbYGy+meuwRJ5+4uco2nvQj5WD5z7MTqFH8X8E7odfJm1R3ZGnA2qBAnJAl9LapM0RMGe/48jAapd7eeufV6HVzOnnW0FKdqgVCMKoqEAzaEnh4tJXbHy2RX/lF9JUXwGLAMk9f4KwcegLfkyk277jrS9/gmcef4Yd/7kc5OjR+Z1m5lzhPgmIbcrj9Idxn/iZ0LnpDVjxxx41bfvBDN2dN7idVhLVik7Yw5s34z9XVoYt+/m+pOGApBIS66dRsvZvOAvUl66s0Q2hLxaLfyG17gXe8Ha5+MxwPSOYx4IJ2zN9v7hghdxx8/Bif/+j/5tgjj7Nvzx6O/NIv8PTmefxKIOAgd7A6gl+6Qf3Gc/3ouYfvL2+/8WcO334jbtLPm1JPqGnjhBxMqyo1PoQwSYWhps06VYbWKS7xiCpo0+Ccpt7THoQGlmWWrz/eprjidcjPvAc7ViE+8otzc+gIXNmB3Azfgb133cuxRx7Fn3Ymd3/rIZ7+j+/Efes7hE2pVzYDXH+DyfGuIzfr3ffn79q8bddq75wrvb/wnFNFY2Nm80JXrvWT8zqNQGbE2jzLhHYro9vO2b55ln6vTb/bmjym50UIeBcRvt9tcdLWeTb0O2yc68US1wt57nHesbI2piwDo6KinWfs2DyL+jZ7jmcUayXujHPgzO3YILCt5Ti9Ba/qwOkZjEQoR8a555/OYmeWx/76b8lPOwfa29HP3QijAK9/Gdz4BeTP/gE762Vy3r7PLf7Y5r0fevv80jBvH8VfsHuXBMPUbPNCR6510ii3UtcFEAGqLkjyzLFxvkeelBbvPbkXMu/JMs/asGgyR6eVsWnDDHnmabfydOzFooIrsLwyJgSlrJRet8XC/AyzWcU5W5Snnlpi9W++DqeeTvv803hBCLyk47iwDWsp4mad8NBQueXi8xn5Hvblz8OOM3BnvAq+8mX4x39Cbr0T2/kyzjj8f+UsfWR0nzv7E3uqbLiqLXG1ajrhfZPKrdE8YN3hiBgCSrBJuTphhdo0KZrHFCq1tNaoNJoQNwkbltjgKMCsr/iRC5Rd8xVc98ts+cItXLjNc3ErsJZU31mB71bwW6uO48cC8v5rsJ/7eezeryErz+FefQ3u6RLX3kyuy+wcPoK6HCsLq1JTzF+w+1RJLbXNG5IHRHQ2qevxmuxEEhTBsNdpNQs2jUJkvbDRONIR74Qsc+SZp9JAUKMo42NNuFYKUPGo87TbOf1uzBBFAKeBF24qOaYtnvjyP7HDOV7y2osoSmMWuK8S/ucqDBPxsVGAyy9AXBe95U9xcxuRM15ONr+V1o6zWbQ52XDs3tGO1tInzhscHI5lIP4lZ58iwbAAmxfaRAwAyjLI2mBEWQWKIjQ6YS1TqsGoqBiVgWFRpecV4zKw0O8y1+8wN9NBRHjmyBKrgzHLa6PYTfKRBwQcS92dhN5GQm8Tzmf4YolhEbnDoAiMi5LT+iNGeZfb/vZfOXxshUtffwl3j+HjizBOWcmicADjgLviQs5yLY7fchM2uwHrbULKIbbthXK06MjKnjs+s/9IOH7/hsu8P3/3qaJqkQp35FpJGDChwvI80iIiZHmtAk8fOIjR1E7vgVCGwOqgaISOLPNR8rLI9leYaTJLi4KuDdEpBVkNxAJn9ocs533u/vpdfPfxZ7j1VZcxyDwSFEsdochNHGeUgevffBFjzXn4izchGzZjvc3iw1hl2zn5cmf7S5/ae/tNa0eeKv1Ldp8qipiabF7oyLVeUk9PTapKE42dqLW13N1u52mJjW7WpLdOK8OJQwSKMrC8Omwqyjzzqa9vmAhLzBJwqDhalPQYYA19NpzAEV3gzqXNPG07sFPO5dBt36B4YA/yutdgMy0oNB7qSE7wE13HpiJw3vdcRCDnoS/dhMxvQnubxIuFfOPJu7L5U15YPHn7n/tzTz9JqmAW1DbP+HCthkAVjNG4ksG4pKqMIgRarYxWnuG9w3tHVQWqSilDaLyi7r9HDwqpfRZV4U4rp9vOCGqMy4oQ4vUhVOTVgCwMyKsBEgqqSqlCQKqCZ3Qjf/f0Bo51XkA4+zJkbjv55h3ot7+B3fFtuPw1sNDDjQPmHFe14PUtWDJHbxzYf8VF3Kst5OabsNkFmNnisu5sVRx67Dzv2zP+nF07GgN0XRUNUCnjopLBqKJSoywDszNtOu0c7x0iwmA4JlRKWYUobmQTfl+k6nBcVogTFma7tFqeXqfFWmpcqhqhCuRhQFYNyMMaVOOoOVaBMihVEVjcdQU/9JsfZunR+zn00CPI3GaY3Ua2fRd6/7/AN25FXn0ZtnWOTUXgJ/oOBboCj5njk8sBfd1FQAv7q89Df4Fi/wNiR5+SXrfzcZf8OFVx0lDcuiFSC5NNM1NjupKp6xqtPrHGKHAmwTPiSSOmWt2RTAMrjiAexaejnnEOuQQGnZ284Puu5qpXnsl5H/sQvPUS2PsN9Jm92NzJdF/zdvzhRew/vRv2PM7bT/LMJ+2vBP64hEo87liFXfdOePc7CHferO2ZGWlvPe32a+/5yy+4SccWptXh5l8acHqRk9phfde1vma6E2NTuLG+Szt9uiPeYSKHK6aO8Rmv4LKrLuWPDhifX/TI+96BfuR61D9D9e2/JtBi5qp346sc9973Uf7jPYxaQt+MLxbwRABnhrYzeHoFbrudzgsvtXzzaaw9cvvv/8bJr8K/8PQdjSAym+u1LpGeVu5lfrZLL9HbqgqMxyVFWVGWoYl3AUbjiqWVEWuDMauDAtPoxkVRRRnNYFxUDIsKNSPPMvI8w3tPlYQUkXiWYG1YYtWYI24bL/3xa9m7+yz+5KDhMocNFE7dCm96I6weQb/2JZyfYf6St7J2zz8Txqtc+bbXcueq8dkyAqkJsXj4qV/EP/CE9l/zw36455uPhz1/9ZOs7C+z9ZUcNQkik0hi6p77eGzpDO9EuKxlq6j6VE37q5U5rD43RDz1XbelYrqSRkBpOkxJkwghYOLonfUK9r/8Ev7iALhMopzqPaxpPAT4gf8Cl17C6Ld/m+Hf3Etr20Z++L+9j2ND+JMyxaYG2OTh1z+J/OMd9P7DtVoc3ufG+x/8A06+ZE1mNmapOSo13TUFaVLetG1q5bYWOkIta1mzUJv6LKTmaIMvdaqcUn4bnXFyvSOwxE4Ov/H7OZi3oaiwTjbZH+di/+FYwF98Pu/94o3c8eGPseG8c9h91jZuOKAcMYfTgG7y8Pm/gz/4LK1LftDMd7Nq3z3D7sHv3CShQE57hWaxDxfn6b0TL3VXGKra1YniZpnO+ohAr503jQbV6B2SvKOqDJGAmuLNyJpWegTEicE0hZI1R/e8z3DVMs9++SuMd1+I7FzAVkLc9YZeCeY9bzflik7GBR+7jm5h/O0h4w51OFN0zsNdD8P1N+DPvRy3cCqjQ0+yY/m+0XsvHa1YAd8a77es184oFYJa2eu40jvLDaEoyni2J01sbVBQVlH67rQyTjtpYyNjH1sexOeJ4a2uxXRmxMMSpEIJgcGwYFxMOkUzvXaTZTIveNdmZ7di/ruf446ffpilX7gBufRF2FIA5+KBDYPvm4HXzzgOl0a3UJ5F+NNxAtq2g6PL8IFfRjaeiWx/IWE8Rp57gF3+OZ7NdlKNjsfDmVtnsJNnhV3z7rgTW1Wrm5DTQkWTnZpUOan4rKkE1wmeTRjEAxSWmn9Nem2+8THR/y2lpCIInfk2rx5/m5N/6RrsT/4SmfU4b4RSuawLP9iDZYVMYp/xj4bCmiYxNgOuux6OjHC7L0UwimMH2LJ0P/PtwHcO9+yJ0TyCpnMGTM7OTrpE64+p1KTHzMjSdwqRWr6um6nTfMHSia6AhbRCAQ3xazF1l0gtHoNVq2cSxyhLcK2cl+aHyH/vAzyx92HsAz/FBRs7vDMLjMyjwLzALSN4sARnAV3w8NHfh3+6A/fqa8Cg3H8/PPMAp84eidPQIh2+MrJ65WnxVqekRiZITYutG/vNuQEBVofjiZqr0O20GsNVmk6GWcBaPcLcjvhcHN32EXrj1egVqQe5XnKbqFDjccHYHOdtGzLztd/lgccf4cr/8avMX3AqB48GZjPHw5XwpVFEfN3o4Zavw6f/EPfyqyEEwiP/wLawj9fuDmztbKAIG3TrYATSJ8t8bI42B2CaLRerSYmkSc3223TbeQI85ejyoJmwCOR1R8diweNczGuat6n6W0ArFE+bEW0/Rskw0wmBml58TakLgRAoVDhja6B94O/5xI/u59AHf5G3/NDlrCyq/OFK/J6gm/Po/Y/Ch34DOecy06LEPfpVTpd9vOF0tY1zMzYsW77jVaoidqPy3JMNS2NxYIwrWz1lozvmhE3J/aVhcMS0V6Wzf7U8VhdBhjVqTs0jNMYDVh+e0ApEsRBig1XC8xjoJJVODGE1eBbGtn5Jt9zD537+F3j23nfifvY9YZ/33jlBF1fhug+adDarlWPfP3Qr5/YP01vbR1GdLKNAUA1g3Low3zsuTmR1bWTZY4dW7UU7Z51BEZR3m9lnRZgzs0A6/v+8A4apNT4ds+sWMMWvG27QOFlzJLvhADYZuKHE0z+WWtrjAH0/4nVbn7N/+eNPZ6t3PTgnv/YrQXfv8Lz/BuXRx7GtO/3O4UMrZ/efKm3loAwrJwaLYhbM+DYi7xtXAQF+76+/g/zc978ynnkOJscHhTmRbr/te2VR6mBUxJOXZmxb6NPptFCNYLg8KBrXxbRZhCqsDUbRvYOinVl001mIBoJkdNb20yqOYWQo2nyBoV5olAnj69FoTFGWU+ZTFsOM7Kj28bb3vT385Ece+a21k3e+W1/84sBX/s7lG+Zl28Lsb/2v79v+kY996Hds7eRdzMmKWBXW9h5cGm/oz/PGV5wtQeMJYzWQH7jifM7ZPhNbWoW6Ttac+eQ3v3AH/x5/XvGe67njU9cLYDtf+rbrDh46ekN7YcO+3bt2vOvu+/d+lXEhv3rdu+zDP/1OfvZtL29O8Qt4m3ylq/FDvr82ghlHVkvp5I5iXDIcFc1Rt60bZyMIWpSwl9bGU2lyfQisDUYRA1TR9hxh81kJBDO6a/vJkwcY+jzwqzkBGMPhmLJKX9NPMaLm6Ooy++ZfKfftWRRW1sJ533vl9856t2dlMHr0+KEj2Qu2i5YP3GyhvYCGwMnbNnHmKdtZWRtZNsUoP37znfw/PbpOoA6VeM0AAAAldEVYdGRhdGU6Y3JlYXRlADIwMTktMDctMDRUMDg6NTY6NTAtMDc6MDBvOQBrAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDE5LTA3LTA0VDA4OjU2OjUwLTA3OjAwHmS41wAAAABJRU5ErkJggg=="));
        } catch (IOException e) {
            //empty catch block
        }

        pingIndicator = new PingIndicator(4, 15, 100);
        pingIndicator.disable();

        playerCount = new Label("", LabelType.H2, LabelColor.THIRDCOLOR);

        VBox infoBox = new VBox(5, pingIndicator, playerCount);
        infoBox.setMinWidth(100);
        infoBox.setId("item-info-box");

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox detailBox = new HBox(favicon, title, region, infoBox);
        detailBox.setId("item-detail-box");

        StackPane motdStack = new StackPane(detailBox, motdFlow);
        StackPane.setAlignment(motdFlow, Pos.CENTER);

        VBox titleBox = new VBox(motdStack, statusLine);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        titleBox.setPrefHeight(height);

        renameTextField = new TextField();
        renameTextField.setPadding(new Insets(0, 0, 0, 10));
        renameTextField.setMaxWidth(100);
        renameTextField.setId("item-box-text-area");
        renameTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                requestFocus();
            }
        });
        renameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!showLayer && !newValue) {
                String newName = renameTextField.getText();

                showLayer = true;
                detailBox.getChildren().set(1, title);
                if (!newName.matches("[\\s\\p{L}0-9]+")) {
                    newName = newName.replaceAll("[^\\s\\p{L}0-9]", "");
                }
                if (newName.equalsIgnoreCase(server.getName()) || ServersManager.isExists(newName) || newName.length() < 1) {
                    return;
                }
                ServerAction.rename(server, newName);
            }
        });

        chooseButton = new Button("", ButtonType.ROUNDED);
        chooseButton.setOnAction(e -> ServerAction.choose(server));
        chooseButton.setGraphic(chooseIconView);

        renameButton = new Button("", ButtonType.ROUNDED);
        renameButton.setOnAction(e -> {
            showLayer = false;
            out.play();
            titleBox.setEffect(null);

            detailBox.getChildren().set(1, renameTextField);
            renameTextField.setText(server.getName());
            renameTextField.requestFocus();
            renameTextField.selectAll();
        });
        renameButton.setGraphic(renameIconView);

        deleteButton = new Button("", ButtonType.ROUNDED_ERROR);
        deleteButton.setOnAction(e -> ServerAction.delete(server));
        deleteButton.setGraphic(deleteIconView);

        startButton = new Button("", ButtonType.ROUNDED_APPLY);
        startButton.setOnAction(e -> {
            if (server.isRun()) {
                server.stop();
            } else {
                server.start();
            }
        });
        startButton.setGraphic(startIconView);

        HBox buttonBox = new HBox(5, chooseButton, renameButton, deleteButton, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        layer = new HBox(buttonBox);
        HBox.setHgrow(layer, Priority.ALWAYS);
        layer.setPrefHeight(height);
        layer.setAlignment(Pos.CENTER);
        layer.setOpacity(0);
        layer.setPickOnBounds(false);
        layer.setId("item-box-layer");

        in = new FadeTransition(Duration.millis(100), layer);
        in.setFromValue(0);
        in.setToValue(0.7);
        in.setCycleCount(1);
        in.setAutoReverse(true);
        in.setOnFinished(e -> layer.setPickOnBounds(true));

        out = new FadeTransition(Duration.millis(100), layer);
        out.setFromValue(0.7);
        out.setToValue(0);
        out.setCycleCount(1);
        out.setAutoReverse(true);
        out.setOnFinished(e -> layer.setPickOnBounds(false));

        layer.setOnMouseEntered(e -> {
            if (showLayer) {
                in.play();
                titleBox.setEffect(new GaussianBlur(10));
            }
        });
        layer.setOnMouseExited(e -> {
            if (showLayer) {
                out.play();
                titleBox.setEffect(null);
            }
        });

        this.getChildren().addAll(titleBox, layer);

        getStatusLine().setId(STOPPED.getId());
        getStatusLine().setText(Language.getText(STOPPED.getText()));
        startButton.setDisable(false);
        startButton.setGraphic(startIconView);
        startButton.setType(ButtonType.ROUNDED_APPLY);

        ServerStatusChangeEvent.addListener((changedServer, newStatus) -> {
            if (this.server == changedServer) {
                getStatusLine().setId(newStatus.getId());
                getStatusLine().setText(Language.getText(newStatus.getText()));
                graphic.setStyle("-fx-effect: innershadow(gaussian, " + newStatus.getColor() + ", 7, 1, 1, 1);");
                switch (newStatus) {
                    case CONNECTED:
                    case RUNNING:
                        startButton.setDisable(false);
                        startButton.setGraphic(stopIconView);
                        startButton.setType(ButtonType.ROUNDED_ERROR);
                        renameButton.setDisable(true);
                        deleteButton.setDisable(true);
                        break;
                    case PREPARING:
                        startButton.setDisable(true);
                        renameButton.setDisable(true);
                        deleteButton.setDisable(true);
                        break;
                    case CONNECTING:
                    case STARTING:
                    case STOPPING:
                        startButton.setType(ButtonType.ROUNDED_WARNING);
                        renameButton.setDisable(true);
                        deleteButton.setDisable(true);
                        break;
                    case STOPPED:
                        startButton.setDisable(false);
                        startButton.setGraphic(startIconView);
                        startButton.setType(ButtonType.ROUNDED_APPLY);
                        renameButton.setDisable(false);
                        deleteButton.setDisable(false);
                        playerCount.setText("");
                        pingIndicator.disable();
                        break;
                }
            }
        });

        ServerStateChangeEvent.addListener((server1, newType) -> {
            switch (newType) {
                case RENAMED:
                    title.setText(server.getName());
                    break;
            }
        });
    }

    public void updateInfos(String faviconBase64, String motd, int onlineplayers, int maxplayers, int latency) {
        Platform.runLater(() -> {
            try {
                if (faviconBase64 != null) {
                    favicon.setImage(FileManager.decodeBase64ToImage(faviconBase64));
                }
            } catch (IOException e) {
                //empty catch block
            }
            motdFlow.getChildren().clear();
            motdFlow.append(motd.replaceAll("\u00A7", "§"));
            playerCount.setText(onlineplayers + "/" + maxplayers);
            pingIndicator.setValue(latency);
        });
    }

    //GETTERS

    public Label getStatusLine() {
        return statusLine;
    }

    public Server getServer() {
        return server;
    }
}