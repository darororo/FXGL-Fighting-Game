package com.example.fp;

import com.almasb.fxgl.app.scene.FXGLDefaultMenu;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.asset.AssetType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.view.KeyView;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.almasb.fxgl.dsl.FXGL.*;



public class FightMenu extends FXGLMenu {
    private final Pane menuRoot = new Pane();
    private final Pane menuContentRoot = new Pane();

    private ObjectProperty<FightButton> SelectedButton;

    VBox menuText;

    public FightMenu() {
        super(MenuType.MAIN_MENU);
        menuSelection();
    }



    protected Node createBackground(double width, double height) {
        Image backgroundImage = new Image("assets/textures/background/map10.jpg", width, height, false, true);
        ImageView backgroundView = new ImageView(backgroundImage);

        return backgroundView;
    }

    private static final Color SELECTED_COLOR = Color.WHITE;
    private static final Color NOT_SELECTED_COLOR = Color.GRAY;



    private static class FightButton extends StackPane {
        private final String name;
        private final String description;
        private final Runnable action;

        private final Text text;

        public FightButton(String name, String description, Runnable action) {
            this.name = name;
            this.description = description;
            this.action = action;

            text = getUIFactoryService().newText(name, Color.WHITE, 22.0);
            text.fillProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.strokeProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.setStrokeWidth(0.5);


            Rectangle Selector = new Rectangle(6, 17, Color.RED);
            Selector.setTranslateX(-20);
            Selector.setTranslateY(-2);
            Selector.visibleProperty().bind(focusedProperty());

            focusedProperty().addListener((observable,oldValue, isSelected) -> {
                if (isSelected) {
//                   SelectedButton.setValue(this);

                }
            });

            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

            setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    action.run();
                }
            });

            getChildren().addAll(Selector, text);
        }
    }


    static int playerTurn = 1;
    static Rectangle p1Selector;
    static Rectangle p2Selector;
    private static class CharSelectButton extends StackPane {
        private final String name;
        private final String description;
        private final Runnable action;

        private final Text text;



        public CharSelectButton(String name, String description, Runnable action) {
            this.name = name;
            this.description = description;
            this.action = action;

            p1Selector = new Rectangle(6, 17, Color.RED);
            p1Selector.setTranslateX(-20);
            p1Selector.setTranslateY(-2);

            p2Selector = new Rectangle(6, 17, Color.BLUE);;
            p2Selector.setTranslateX(100);
            p2Selector.setTranslateY(-2);
            p2Selector.visibleProperty().bind(focusedProperty());

            text = getUIFactoryService().newText(name, Color.WHITE, 22.0);
            text.fillProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.strokeProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.setStrokeWidth(0.5);

            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

            setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    if(playerTurn == 1) {
                        getChildren().add(p1Selector);
                    }
                    action.run();
                }
            });




            getChildren().addAll(text,p2Selector);
        }

    }


    private static class LineSeparator extends Pane {
        private Rectangle line = new Rectangle(400, 2);
        public LineSeparator() {
            var gradient = new LinearGradient(
                    0, 0.5, 1, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.LIGHTSKYBLUE),
                    new Stop(0.5, Color.ORANGE),
                    new Stop(1.0, Color.TRANSPARENT));

            line.setFill(gradient);

            getChildren().add(line);

        }
    }

    private void bgSelectionScreen() {
        getContentRoot().getChildren().remove(menuText);

        int height = 200;
        int width = 200;
        List<Image> bgList = new ArrayList<Image>();
        bgList.add(new Image("assets/textures/background/map1.jpg", width, height, false, true));
        bgList.add(new Image("assets/textures/background/map2.jpg", width, height, false, true));
        bgList.add(new Image("assets/textures/background/map3.jpg", width, height, false, true));
        bgList.add(new Image("assets/textures/background/map4.jpg", width, height, false, true));
        bgList.add(new Image("assets/textures/background/map5.jpg", width, height, false, true));

        List<ImageView> bgView = new ArrayList<ImageView>();
        for(Image i : bgList) {
            bgView.add(new ImageView(i));
        }

        VBox bgBox1 = new VBox(10,
                bgView.get(0)
                ,bgView.get(1)
                , bgView.get(2)
        );
        VBox bgBox2 = new VBox(10
                , bgView.get(3)
                , bgView.get(4));

        var bgHbox = new HBox(200, bgBox1, bgBox2);

        FightButton map1 = new FightButton("Cat and Girl", "map1", () ->{
            GameApp.bgName = "background/map1.jpg";
            getAudioPlayer().stopAllMusic();
            loopBGM("fight.mp3");
            resetMenu();
            fireNewGame();
        });
//        map1.setOnAction(e-> {
//            GameApp.bgName = "background/map1.jpg";
//            fireNewGame();
//        });

        FightButton map2 = new FightButton("Royal Palace", "map2", () ->{
            GameApp.bgName = "background/map2.jpg";
            getAudioPlayer().stopAllMusic();
            loopBGM("fight.mp3");
            resetMenu();
            fireNewGame();
        });


        FightButton map3 = new FightButton("Class", "map3", () ->{
            GameApp.bgName = "background/map3.jpg";
            getAudioPlayer().stopAllMusic();
            loopBGM("fight.mp3");
            resetMenu();
            fireNewGame();
        });

        FightButton map4 = new FightButton("Angkor Wat", "map4", () ->{
            GameApp.bgName = "background/map4.jpg";
            getAudioPlayer().stopAllMusic();
            loopBGM("fight.mp3");
            resetMenu();
            fireNewGame();
        });

        FightButton map5 = new FightButton("School", "map5", () ->{
            GameApp.bgName = "background/map5.jpg";
            getAudioPlayer().stopAllMusic();
            loopBGM("fight.mp3");
            resetMenu();
            fireNewGame();
        });


        VBox btnBox1 = new VBox(170, map1, map2, map3);
        VBox btnBox2 = new VBox(170, map4, map5);

        HBox btnBox = new HBox(270,btnBox1, btnBox2);


        bgHbox.setAlignment(Pos.CENTER_LEFT);
        btnBox.setTranslateX(220);
        btnBox.setTranslateY(100);

        getContentRoot().getChildren().addAll(bgHbox, btnBox);

    }

    private void gameModeSelection () {
        getContentRoot().getChildren().remove(menuText);
        FightButton btnPVP = new FightButton("PVP", "Against other player", () -> {
            charSelectionPVP();
            GameApp.gameMode = "pvp";
        });
        FightButton btnPVE = new FightButton("PVE", "Against computer", () -> {
            charSelectionPVE();
            GameApp.gameMode = "pve";
        });
        FightButton btnBack    = new FightButton("Back", "Exit To Desktop", () -> menuSelection());

        menuText = new VBox(7,
                btnPVP,
                btnPVE,
                btnBack,
                new LineSeparator());
        menuText.setAlignment(Pos.CENTER_LEFT);
        menuText.setTranslateX(85);
        menuText.setTranslateY(275);

        getContentRoot().getChildren().addAll(createBackground(getAppWidth(), getAppHeight()), menuText);
    }

    private void charSelectionPVP () {
        getContentRoot().getChildren().remove(menuText);

        CharSelectButton btnChar1 = new CharSelectButton("John", "char1", () -> {
            if(playerTurn == 1) {
                GameApp.charSelectedP1 = "char1";
                playerTurn++;
            } else {
                GameApp.charSelectedP2 = "char1";
                playerTurn = 1;
                bgSelectionScreen();
            }
        });

        CharSelectButton btnChar2 = new CharSelectButton("Jessie", "char2", () -> {
            if(playerTurn == 1) {
                GameApp.charSelectedP1 = "char2";
                playerTurn++;
            } else {
                GameApp.charSelectedP2 = "char2";
                playerTurn = 1;
                bgSelectionScreen();
            }
        });
        FightButton btnBack    = new FightButton("Back", "Exit To Desktop", () -> gameModeSelection());

        menuText = new VBox(7,
                btnChar1,
                btnChar2,
                btnBack,
                new LineSeparator());
        menuText.setAlignment(Pos.CENTER_LEFT);
        menuText.setTranslateX(275);
        menuText.setTranslateY(275);

        getContentRoot().getChildren().addAll(createBackground(getAppWidth(), getAppHeight()), menuText);
    }

    private void charSelectionPVE () {
        getContentRoot().getChildren().remove(menuText);

        CharSelectButton btnChar1 = new CharSelectButton("John", "char1", () -> {
            if(playerTurn == 1) {
                GameApp.charSelectedP1 = "char1";
                playerTurn++;
            } else {
                GameApp.charSelectedP2 = "char1";
                playerTurn = 1;
                bgSelectionScreen();
            }
        });

        CharSelectButton btnChar2 = new CharSelectButton("Jessie", "char2", () -> {
            if(playerTurn == 1) {
                GameApp.charSelectedP1 = "char2";
                playerTurn++;
            } else {
                GameApp.charSelectedP2 = "char2";
                playerTurn = 1;
                bgSelectionScreen();
            }
        });

        CharSelectButton btnChar3 = new CharSelectButton("Boss", "boss", () -> {
            if(playerTurn == 1) {
                GameApp.charSelectedP1 = "char1";
                playerTurn++;
            } else {
                GameApp.charSelectedP2 = "boss";
                playerTurn = 1;
                bgSelectionScreen();
            }
        });
        FightButton btnBack    = new FightButton("Back", "Exit To Desktop", () -> gameModeSelection());

        menuText = new VBox(7,
                btnChar1,
                btnChar2,
                btnChar3,
                btnBack,
                new LineSeparator());
        menuText.setAlignment(Pos.CENTER_LEFT);
        menuText.setTranslateX(275);
        menuText.setTranslateY(275);

        getContentRoot().getChildren().addAll(createBackground(getAppWidth(), getAppHeight()), menuText);
    }

    private void menuSelection () {
        getContentRoot().getChildren().remove(menuText);
        FightButton btnPlayGame = new FightButton("Start New Game", "Start New Game", () -> {
            gameModeSelection();

        });
        FightButton btnCon = new FightButton("Continue", "Continue To Fight", ()->{
        });
        FightButton btnOption = new FightButton("Option", "Adjust In Game Option", () -> {});
        FightButton btnQuit = new FightButton("Quit", "Exit To Desktop", () -> fireExit());

        SelectedButton = new SimpleObjectProperty<>(btnPlayGame);

        var textDescription =  getUIFactoryService().newText("", Color.LIGHTGRAY, 18);
        textDescription.textProperty().bind(
                Bindings.createStringBinding(() -> SelectedButton.get().description, SelectedButton)
        );

        menuText = new VBox(7,
                btnPlayGame,
                btnCon,
                btnOption,
                btnQuit,
                new LineSeparator());
        menuText.setAlignment(Pos.CENTER_LEFT);
        menuText.setTranslateX(85);
        menuText.setTranslateY(275);

        var p1Move1 = new KeyView(KeyCode.W, Color.GREEN, 15.0);
        var p1Move2 = new KeyView(KeyCode.S, Color.GREEN, 15.0);
        var p1Move3 = new KeyView(KeyCode.A, Color.GREEN, 15.0);
        var p1Move4 = new KeyView(KeyCode.D, Color.GREEN, 15.0);
        var p1Move5 = new KeyView(KeyCode.U, Color.GREEN, 15.0);
        var p1Move6 = new KeyView(KeyCode.I, Color.GREEN, 15.0);
        var p1Move7 = new KeyView(KeyCode.Q, Color.GREEN, 15.0);
        var p1Move8 = new KeyView(KeyCode.R, Color.GREEN, 15.0);

        var p2Move1 = new KeyView(KeyCode.UP, Color.GREEN, 15.0);
        var p2Move2 = new KeyView(KeyCode.DOWN, Color.GREEN, 15.0);
        var p2Move3 = new KeyView(KeyCode.LEFT, Color.GREEN, 15.0);
        var p2Move4 = new KeyView(KeyCode.RIGHT, Color.GREEN, 15.0);
        var p2Move5 = new KeyView(KeyCode.NUMPAD4, Color.GREEN, 15.0);
        var p2Move6 = new KeyView(KeyCode.NUMPAD5, Color.GREEN, 15.0);
        var p2Move7 = new KeyView(KeyCode.NUMPAD1, Color.GREEN, 15.0);
        var p2Move8 = new KeyView(KeyCode.NUMPAD2, Color.GREEN, 15.0);

//        var hBox = new HBox(25, getUIFactoryService().newText("BACK", 15.0), view);
//        hBox.setAlignment(Pos.BOTTOM_CENTER);
//        hBox.setTranslateX(FXGL.getAppWidth() - 150);
//        hBox.setTranslateY(550);
        var p1Man = new VBox(10, getUIFactoryService().newText("Player1",Color.LIGHTGREEN,20.0)
                , p1Move1,p1Move2,p1Move3,p1Move4, p1Move5,p1Move6,p1Move7,p1Move8);
        var p2Man = new VBox(10, getUIFactoryService().newText("Player2", Color.LIGHTGREEN,20)
                , p2Move1,p2Move2,p2Move3,p2Move4, p2Move5,p2Move6,p2Move7,p2Move8);

        var ints = new VBox(13, getUIFactoryService().newText(""),
                getUIFactoryService().newText("Jump",15),
                getUIFactoryService().newText("Duck",15),
                getUIFactoryService().newText("Left",15),
                getUIFactoryService().newText("Right",15),
                getUIFactoryService().newText("Punch",15),
                getUIFactoryService().newText("Kick",15),
                getUIFactoryService().newText("Run",15),
                getUIFactoryService().newText("Dash",15));

        var hBox = new HBox(30, ints, p1Man, p2Man);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.setTranslateX(FXGL.getAppWidth() - 300);
        hBox.setTranslateY(300);

        getContentRoot().getChildren().addAll(createBackground(getAppWidth(), getAppHeight()), menuText, hBox);
    }

    public void resetMenu() {

        getContentRoot().getChildren().clear();
        menuSelection();
    }

}