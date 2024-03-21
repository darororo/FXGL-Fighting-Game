package com.example.fp;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameApp extends GameApplication {
    private static Entity player1;
    private static PlayerComponent p1Component;
    private static Entity player2;
    private static PlayerComponent p2Component;
    private Entity map;
    public static String bgName;
    public static String gameMode="";
    public static String charSelectedP1;
    public static String charSelectedP2;
    private Entity camera = new Entity();

    @Override
    protected void initInput(){
        //Player 1
        onKeyBuilder(KeyCode.A, "p1WalkLeft")
                .onAction(() -> p1Component.walkLeft())
                .onActionEnd(() -> p1Component.stop());

        onKeyBuilder(KeyCode.D, "p1WalkRight")
                .onAction(() -> p1Component.walkRight())
                .onActionEnd(() -> p1Component.stop());

        onKey(KeyCode.W, "p1Jump", () -> p1Component.jump());

        onKeyBuilder(KeyCode.S, "p1Duck")
                .onAction(() -> p1Component.duck())
                .onActionEnd(() -> p1Component.unduck());
        onKeyBuilder(KeyCode.U, "p1Punch")
                .onActionBegin( () -> {
                    p1Component.punch();
                    player1.addComponent(new AttackComponent(1, "punch"));
                })
                .onActionEnd( () -> player1.removeComponent(AttackComponent.class));

        onKeyBuilder(KeyCode.I, "p1Kick")
                .onActionBegin( () -> {
                    p1Component.kick();
                    player1.addComponent(new AttackComponent(1, "kick"));
                })
                .onActionEnd( () -> player1.removeComponent(AttackComponent.class));

        onKeyBuilder(KeyCode.R, "p1Run")
                .onAction( ()-> p1Component.run())
                .onActionEnd(() -> p1Component.stop());
        onKeyBuilder(KeyCode.Q, "p1Dash")
                .onActionBegin( () -> p1Component.dash());

        //Player 2
        onKeyBuilder(KeyCode.LEFT)
                .onAction(() -> p2Component.walkLeft())
                .onActionEnd(() -> p2Component.stop());
        onKeyBuilder(KeyCode.RIGHT)
                .onAction(() -> p2Component.walkRight())
                .onActionEnd(() -> p2Component.stop());
        onKey(KeyCode.UP, () -> p2Component.jump());
        onKeyBuilder(KeyCode.DOWN)
                .onAction(() -> p2Component.duck())
                .onActionEnd(() -> p2Component.unduck());

        onKeyBuilder(KeyCode.NUMPAD4)
                .onActionBegin( () -> {
                    p2Component.punch();
                    player2.addComponent(new AttackComponent(2, "punch"));
                })
                .onActionEnd( () -> player2.removeComponent(AttackComponent.class));

        onKeyBuilder(KeyCode.NUMPAD5)
                .onActionBegin( () -> {
                    p2Component.kick();
                    player2.addComponent(new AttackComponent(2, "kick"));
                })
                .onActionEnd( () -> player2.removeComponent(AttackComponent.class));

        onKeyBuilder(KeyCode.NUMPAD1, "p2Run")
                .onAction( ()-> p2Component.run())
                .onActionEnd(() -> p2Component.stop());
        onKeyBuilder(KeyCode.NUMPAD2, "p2Dash")
                .onActionBegin( () -> p2Component.dash());

   }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(.1);
        getSettings().setGlobalMusicVolume(.5);
        loopBGM("mainmenu.mp3");
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Fighting");
        settings.setVersion("Game");
        settings.setHeight(600);
        settings.setWidth(800);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new FightMenu();
            }
        });
    }
    @Override
    protected void initGame() {


        getGameWorld().addEntityFactory(new GameFactory());

        getWorldProperties().<Integer>addListener("hp1",
                (old, hp) -> {
                    if(hp <= 0) {
                        showGameOver("Player 2");
                    }
                });

        getWorldProperties().<Integer>addListener("hp2",
                (old, hp) -> {
                    if(hp <= 0) {
                        showGameOver("Player 1");
                    }
                });

        map = spawn("map",
                new SpawnData( 0, 0).put("mapName", bgName));

        var ground = spawn("ground", -200, 450);

        player1 = spawn("player",
                new SpawnData(100, 0)
                        .put("name", charSelectedP1)
                        .put("number", 1));
        p1Component = player1.getComponent(PlayerComponent.class);

//        var p2Mode = gameMode.equals("pvp") ? "player" : "enemy";
//        player2 = spawn(p2Mode,
//                new SpawnData(getAppWidth() - 100, 0)
//                        .put("name", charSelectedP2)
//                        .put("number", 2));

        if(gameMode.equals("pve")) {
            if(charSelectedP2.equals("boss")) {
                player2 = spawn("boss",
                        new SpawnData(getAppWidth() - 100, 0)
                                .put("number", 2));
                p2Component = player2.getComponent(BossComponent.class);
            } else {
                        player2 = spawn("enemy",
                new SpawnData(getAppWidth() - 100, 0)
                        .put("name", charSelectedP2)
                        .put("number", 2));
                p2Component = player2.getComponent(AIComponent.class);
            }
        } else {
            player2 = spawn("player",
                    new SpawnData(getAppWidth() - 100, 0)
                            .put("name", charSelectedP2)
                            .put("number", 2));
            p2Component = player2.getComponent(PlayerComponent.class);
        }




        getGameScene().getViewport().setBounds(-200, 0, 1500, (int) map.getHeight());

    }

    private void showGameOver(String winner) {
        getSettings().setGlobalSoundVolume(0.1);
        loopBGM("KO.mp3");

        // which player won
        if (winner.equals("Player 1")) {
            p2Component.setCharacterTexture("dead.gif");

            // Pause game 1 second
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                // After 1 second,
                p2Component.setCharacterTexture("dead2.gif");
                p1Component.setCharacterTexture("victory2.gif");
            }));

            timeline.play();
            p1Component.setCharacterTexture("victory.gif");

        } else {
            p1Component.setCharacterTexture("dead.gif");

            // Pause game 1 second
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                // After 1 second,
                p1Component.setCharacterTexture("dead2.gif");
                p2Component.setCharacterTexture("victory2.gif");
            }));

            timeline.play();
            p2Component.setCharacterTexture("victory.gif");

        }

        // Pause the game for an additional 1 second
        Timeline gameOverTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            getDialogService().showMessageBox(winner + " won", () -> {
                getGameController().gotoMainMenu();
                getAudioPlayer().stopAllMusic();
                loopBGM("mainmenu.mp3");
            });
        }));

        gameOverTimeline.play();
    }
//    private void showGameOver(String winner) {
//        getSettings().setGlobalSoundVolume(0.1);
//        loopBGM("KO.mp3");
//
//        getDialogService().showMessageBox(winner + " won", () -> {
//            getGameController().gotoMainMenu();
//            getAudioPlayer().pauseAllMusic();
//        });
//    }

    @Override
    protected void onUpdate(double tpf) {
        //initCamera(map);
        resetPlayerOrientation();
        updateHpBarColor();
        resetCombo();
        setAttackCombo();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("hp1", 200);
        vars.put("hp2", 200);
        vars.put("combo1", 0);
        vars.put("combo2", 0);
        vars.put("Control1", 0);    // 0: WASD, 1:arrow, 2: controller or bot?
        vars.put("Control2", 1);
        vars.put("Attack1", "");
        vars.put("Attack2", "");
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 800);
        attackCollision();

    }

    private void initCamera(Entity map) {

        double distBetweenPlayers = player1.distance(player2);

        var maxX = FXGLMath.max((float) player1.getX(), (float) player2.getX());
        var minX = FXGLMath.min((float) player1.getX(), (float) player2.getX());

        double margin = 100;
        if(distBetweenPlayers < getAppWidth() - margin && distBetweenPlayers > margin) {
            camera.setX(getAppWidth()/2);
        }
        if (distBetweenPlayers > getAppWidth() - margin) {
            camera.translateX(player1.getWidth());
        }

        if (distBetweenPlayers < margin) {
            camera.translateX(-player1.getWidth());
        }

        camera.setY(getAppHeight()/2);

    }

    ProgressBar hpBar1;
    ProgressBar hpBar2;
    @Override
    protected void initUI() {
        //var text1 = getUIFactoryService().newText("", Color.GREEN, 24);
        //text1.textProperty().bind(getip("Player1").asString("Player1: [%d]"));

        //var text2 = getUIFactoryService().newText("", Color.GREEN, 24);
        //text2.textProperty().bind(getip("Player2").asString("Player2: [%d]"));

        hpBar1 = new ProgressBar();
        hpBar1.setPrefWidth(250);
        hpBar1.setPrefHeight(30);
        hpBar1.setProgress(1.0); // Set initial progress to full
        hpBar1.progressProperty().bind(getip("hp1").divide(200.0));

        hpBar2 = new ProgressBar();
        hpBar2.setPrefWidth(250);
        hpBar2.setPrefHeight(30);
        hpBar2.setProgress(1.0); // Set initial progress to full
        hpBar2.progressProperty().bind(getip("hp2").divide(200.0));
        hpBar2.setStyle("-fx-accent: green;");

        //addUINode(text1, 20, 50);
        addUINode(hpBar1, 20, 50);

        //addUINode(text2, 600, 50);
        addUINode(hpBar2, 530, 50);

        var com1 = getUIFactoryService().newText("", Color.RED, 24);
        com1.textProperty().bind(getip("combo1").asString("Combo: [%d]"));

        var com2 = getUIFactoryService().newText("", Color.RED, 24);
        com2.textProperty().bind(getip("combo2").asString("Combo: [%d]"));

        addUINode(com1, 20, 150);;
        addUINode(com2, getAppWidth()-150, 150);;

    }

    private void updateHpBarColor() {
        if( geti("hp1") /200.0 < 0.40 ) {
            hpBar1.setStyle("-fx-accent: red;");
        } else if (geti("hp1")/200.0 < 0.70) {
            hpBar1.setStyle("-fx-accent: yellow;");
        } else {
            hpBar1.setStyle("-fx-accent: green;");
        }

        if(geti("hp2")/200.0 < 0.40 ) {
            hpBar2.setStyle("-fx-accent: red;");
        } else if (geti("hp2")/200.0 < 0.70) {
            hpBar2.setStyle("-fx-accent: yellow;");
        } else {
            hpBar2.setStyle("-fx-accent: green;");
        }
    }

    private void resetPlayerOrientation() {
        if(player1.getX() < player2.getX()) {
            player1.setScaleX(1);
            player2.setScaleX(-1);
            p1Component.isFacingRight = true;
            p2Component.isFacingRight = false;
        } else {
            player1.setScaleX(-1);
            player2.setScaleX(1);
            p1Component.isFacingRight = false;
            p2Component.isFacingRight = true;
        }
    }

    boolean hitConnected1 = false;
    boolean hitConnected2 = false;
    private void attackCollision() {
        // Player and Hit collision
        onCollisionBegin(EntityType.PLAYER, EntityType.ATTACK, (p, a) -> {
            int player = p.getInt("number");
            int attack = a.getInt("number");

            if( attack != player && player == 1) {
                getGameScene().getViewport().shake(5, 0);
                if(!p.getComponent(PlayerComponent.class).isBlocking){      // p1 not blocking, receives normal damage
                    FXGL.play("hitsound2.wav");
                    if(getWorldProperties().getInt("hp1") - p2Component.pdamage < 0) {
                        set("hp1", 0);
                    } else {
                        inc("hp1", -p2Component.pdamage ); // reduce damage received
                    }
                    hitConnected2 = true;

                    inc("combo2", 1);
                    runOnce(() ->{
                        if (!p2Component.isPunching && !p2Component.isKicking){
                            hitConnected2 = false;
                        }
                    }, Duration.seconds(2));
                } else {                                                    // p1 is blocking, damage received should be 0.5x; and stop movement
                    FXGL.play("hitsound.wav");
                    p1Component.loadCharTexture("char1","block.png");
                    if(getWorldProperties().getInt("hp1") - p2Component.pdamage < 0) {
                        set("hp1", 0);
                    } else {
                        inc("hp1", -p2Component.pdamage / 2); // reduce damage received
                    }
                    hitConnected2 = true;
                   p1Component.dx = 0;
                    runOnce( () -> {
                        p1Component.dx = 200;
                        p1Component.resetHurt();
                    }, Duration.seconds(0.3) );
                }
                if(gets("Attack2").equals("projectile")) {
                    getGameWorld().removeEntity(a);
                }

            }

            if( attack != player && player == 2) {

                getGameScene().getViewport().shake(5, 0);
                if(!p2Component.isBlocking) {       // same blocking logic as p1
                    FXGL.play("hitsound2.wav");
                    if(getWorldProperties().getInt("hp2") - p1Component.pdamage < 0) {
                        set("hp2", 0);
                    } else {
                        inc("hp2", -p1Component.pdamage);
                    }

                    hitConnected1 = true;
                    inc("combo1", 1);
                    runOnce(() ->{
                        if (!p1Component.isPunching && !p1Component.isKicking){
                            hitConnected1 = false;
                        }
                    }, Duration.seconds(2));
                } else {
                    FXGL.play("hitsound.wav");
                    if(getWorldProperties().getInt("hp2") - p1Component.pdamage < 0) {
                        set("hp2", 0);
                    } else {
                        inc("hp2", -p1Component.pdamage/2);
                    }
                    hitConnected1 = true;
                    p2Component.setHurt();

                    p2Component.dx = 0;
                    runOnce( () -> {
                        p2Component.dx = 200;
                        p2Component.resetHurt();
                    }, Duration.seconds(0.3) );

                }
            }
        });

        // Parry
        onCollisionBegin(EntityType.ATTACK, EntityType.ATTACK, (a1, a2) -> {

            int n1 = a1.getInt("number");
            int n2 = a2.getInt("number");

            if( n1 != n2 ) {
                FXGL.play("hitsound.wav");
                if(p1Component.isFacingRight) {
                    player1.getComponent(PhysicsComponent.class).setVelocityX(-100);
                    player2.getComponent(PhysicsComponent.class).setVelocityX(100);
                } else {
                    player1.getComponent(PhysicsComponent.class).setVelocityX(100);
                    player2.getComponent(PhysicsComponent.class).setVelocityX(-100);
                }

                getGameScene().getViewport().shake(10, 0);

            }

        });
    }

    private void resetCombo() {
        if(!hitConnected1) {
            set("combo1",0);
        }

        if(!hitConnected2) {
            set("combo2",0);
        }
    }


    private void setAttackCombo() {
        p1Component.punchType = geti("combo1")%3;
        p1Component.kickType = geti("combo1")%3;
        p2Component.punchType = geti("combo2")%3;
        p2Component.kickType = geti("combo2")%3;

    }


    public static void main(String[] args) {
        launch(args);
    }
}