package com.example.fp;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.*;
import static com.example.fp.EntityType.*;
import static java.lang.Math.abs;

public class PlayerComponent extends Component {
    protected boolean isPlayer = true;

    private PhysicsComponent physics;
    private Texture texture;

    private int jumps = 1;
    boolean isAttacking = false;
    boolean isFacingRight = false;

    private String charName = "";
    public int playerNumber;
    private boolean isHurt = false;

    public PlayerComponent() {};

    public PlayerComponent(String name, int playerNumber) {
        this.playerNumber = playerNumber;
        charName = name;
        texture = texture(charName.concat("/idle.gif"));
    }
    public void setCharacterTexture(String fileName) {
        loadCharTexture(charName, fileName);
    }
    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if(entity.getBottomY() >= getGameWorld().getSingleton(GROUND).getY() - 10)  {
            jumps = 1;
        }

        if( isHurt && isBlocking) {
            blockView();
        }

       checkOffscreen();
    }

    int dx = 200;
    public void walkLeft() {
        if (isDucking && physics.getVelocityY() == 0) {
            stillducking();
            loadCharTexture(charName, "duck.gif");
            return;
        }
        if (isJumping) {
            if (isFacingRight) {
                loadCharTexture(charName, "left.gif");
            } else {
                loadCharTexture(charName, "right.gif");
            }
            physics.setVelocityX(-dx);
        } else {
            if (!isPunching && !isKicking && physics.getVelocityY() == 0) {
                if (isFacingRight) {
                    isBlocking = true;
                    loadCharTexture(charName, "left.gif");
                } else {
                    loadCharTexture(charName, "right.gif");
                }

                if (entity.getX() - dx < 0 - dx) {
                    stop();
                } else {
                    physics.setVelocityX(-dx);
                }
            }
        }
    }

//        if (isFacingRight) {
//            loadCharTexture(charName, "left.gif");
//        } else {
//            loadCharTexture(charName, "right.gif");
//        }
//
//        if (entity.getX() - dx < 0 - dx) {
//            stop();
//        } else {
//            physics.setVelocityX(-dx);
//        }
//    }

    public void walkRight() {
        if (isDucking && physics.getVelocityY() == 0) {
            stillducking();
            loadCharTexture(charName, "duck.gif");
            return;
        }

        if (isJumping) {
            if (isFacingRight) {
                loadCharTexture(charName, "right.gif");
            } else {
                loadCharTexture(charName, "left.gif");
            }
            physics.setVelocityX(dx);
        } else {
            if (!isPunching && !isKicking && physics.getVelocityY() == 0) {
                if (isFacingRight) {
                    loadCharTexture(charName, "right.gif");
                } else {
                    isBlocking = true;
                    loadCharTexture(charName, "left.gif");
                }

                if (entity.getX() + dx > getAppWidth() + dx) {
                    stop();
                } else {
                    physics.setVelocityX(dx);
                }
            }
        }
    }


//    public void walkright() {
//        if (isDucking) {
//            stop();
//            loadCharTexture(charName, "duck.gif");
//            return;
//        }
//
//        if (isFacingRight) {
//            loadCharTexture(charName, "right.gif");
//        } else {
//            loadCharTexture(charName, "left.gif");
//        }
//
//        if (entity.getX() + dx > getAppWidth() + dx) {
//            stop();
//        } else {
//            physics.setVelocityX(dx);
//        }
//    }
    public void stillducking() {
        isDucking = true;
        loadCharTexture(charName, "duck.gif");
        physics.setVelocityX(0);
    }
    public void stop() {
        isRunning = false;
        isPunching = false;
        isKicking = false;
        isBlocking = false;
        loadCharTexture(charName, "idle.gif");
        physics.setVelocityX(0);

    }
    private boolean isJumping = false;
    public void jump() {
        if(jumps == 0) {
            return;
        }
//        if(abs(entity.getX()) > 0) {
//            loadCharTexture(charName, "jump2.gif");
//        } else {
//            loadCharTexture(charName, "jump1.gif");
//        }

        physics.setVelocityY(-500);
        jumps--;

        isJumping = true;
        runOnce(() -> {
            isJumping = false;
        }, Duration.seconds(0.5));

    }

    int punchType = 0;
    boolean isPunching = false;
    protected int pdamage;


    public void punch() {
        if (isDucking) {
            // Load ducking punch texture
            loadCharTexture(charName, "duck_punch.gif");
            runOnce(() -> {
                if (isDucking)
                loadCharTexture(charName, "duck.gif");
                else {
                    loadCharTexture(charName, "idle.gif");
                }
            }, Duration.seconds(1));
        } else {
            if (!isPunching && !isKicking ) {
                isPunching = true;

                //if (punchType >= 3) punchType = 0;

                // Load regular punch texture
                if (punchType == 0) {
                    pdamage = 10;
                    loadCharTexture(charName, "punch1.gif");
                } else if (punchType == 1) {
                    pdamage = 15;
                    loadCharTexture(charName, "punch2.gif");
                } else if (punchType == 2) {
                    pdamage = 20;
                    loadCharTexture(charName, "punch3.gif");
                }

                runOnce(() -> {
                    if (isPunching) {
                        loadCharTexture(charName, "idle.gif");
                        isPunching = false;
                    }
                }, Duration.seconds(1));

                //punchType++;
            }
        }
    }





//    public void punch() {
//        isPunching = true;
//
//        if (punchType >= 3) punchType = 1;
//
//        // Load regular punch texture
//        if (punchType == 0) {
//            pdamage = 10;
//            loadCharTexture(charName, "punch1.gif");
//        } else if (punchType == 1) {
//            pdamage = 15;
//            loadCharTexture(charName, "punch3.gif");
//        } else if (punchType == 2) {
//            pdamage = 20;
//            loadCharTexture(charName, "punch2.gif");
//        }
//
//        runOnce(() -> {
//            if (isPunching) {
//                loadCharTexture(charName, "idle.gif");
//                isPunching = false;
//            }
//        }, Duration.seconds(1));
//
//        punchType++;
//
//        if (isDucking) {
//            // Load ducking punch texture
//            loadCharTexture(charName, "duck_punch.gif");
//        }
//
//    }
    boolean isKicking = false;
    int kickType = 0;
    public void kick() {
        if (isDucking) {
            loadCharTexture(charName, "duck_kick.gif");
            runOnce(() -> {
                if (isKicking) {
                    loadCharTexture(charName, "duck.gif");
                    isKicking = false;
                }
            }, Duration.seconds(1));
        } else {
            isKicking = true;

            // if (kickType > 3) kickType = 1;

            if (kickType == 0) {
                pdamage = 10;
                loadCharTexture(charName, "kick1.gif");
            } else if (kickType == 1) {
                pdamage = 15;
                loadCharTexture(charName, "kick2.gif");
            } else if (kickType == 2) {
                pdamage = 20;
                loadCharTexture(charName, "kick3.gif");
            }

            runOnce(() -> {
                if (isKicking) {
                    loadCharTexture(charName, "idle.gif");
                    isKicking = false;
                    Duration.seconds(0.25);
                }
            }, Duration.seconds(1));

            //kickType++;
        }
    }


    private boolean isDucking = false;
    BoundingShape duckBox = BoundingShape.box(60,15);
    Rectangle duckBoxView = new Rectangle(60, 15, new Color(1,0,0,0.5));

    public void duck() {
        if (!isDucking && physics.getVelocityY() == 0) {
            loadCharTexture(charName, "duck.gif");

            BoundingBoxComponent bbox = getEntity().getBoundingBoxComponent();
            bbox.clearHitBoxes();
            bbox.addHitBox(new HitBox(duckBox));

            isDucking = true;
            canDash = false;
            isRunning = false;
        }
    }

    public void unduck() {
        if (isDucking) {
            boolean wasWalkingLeft = physics.getVelocityX() < 0;
            boolean wasWalkingRight = physics.getVelocityX() > 0;

            entity.getViewComponent().clearChildren();
            loadCharTexture(charName, "idle.gif");

            BoundingBoxComponent bbox = getEntity().getBoundingBoxComponent();
            bbox.clearHitBoxes();
            bbox.addHitBox(new HitBox(BoundingShape.box(60, 100)));

            isDucking = false;
            canDash = true;
            isRunning = true;

            if (wasWalkingLeft) {
                walkLeft();
            } else if (wasWalkingRight) {
                walkRight();
            }
        }
    }

    boolean isRunning = false;
    public void run() {
        if(physics.getVelocityY() == 0) {
            isRunning = true;
            loadCharTexture(charName, "run.gif");
//            runOnce(() -> {
//                if(isRunning){
//                    if(isFacingRight) {
//                        physics.setVelocityX(dx*1.2);
//                    } else {
//                        physics.setVelocityX(-dx*1.2);
//                    }
//                }
//            }, Duration.seconds(.5));

            if(isRunning){
                if(isFacingRight) {
                    physics.setVelocityX(dx*1.2);
                } else {
                    physics.setVelocityX(-dx*1.2);
                }
            }
        }
    }

    boolean canDash = true;
    public void dash() {
        if(canDash){
            setCharacterTexture("dash.png");
            canDash = false;
            physics.setVelocityX(isFacingRight? dx*2.5 : -dx*2.5);
            runOnce(() -> canDash = true, Duration.seconds(1));
            //resetDash();
        }
    }

    boolean isBlocking = false;
    private void block() {
        isBlocking = true;
    }

    private void unblock() {
        isBlocking = false;
    }

    private void blockView() {
        loadCharTexture(charName, "block.png");
    }


    public void loadCharTexture(String characterName, String fileName){
        entity.getViewComponent().clearChildren();
        texture = texture(characterName.concat("/").concat(fileName)).toAnimatedTexture(1,Duration.seconds(0.1));
        entity.getViewComponent().addChild(texture);
    }


//    private void loadAnimatedCharTexture(String characterName, String fileName){
//        LocalTimer t = newLocalTimer();
//        loadCharTexture(characterName, fileName);
//
//        if(t.elapsed(Duration.seconds(2) )) {
//            loadCharTexture(characterName, "idle.gif");
//        }
//
//
//    }


    public void setHurt() {
        isHurt = true;
    }
    public void resetHurt() {
        isHurt = false;
    }


    private void cooldown(int second) {
        LocalTimer t = newLocalTimer();
        if(t.elapsed(Duration.seconds(second))) {
            loadCharTexture(charName, "idle.gif");
        }
    }


    private void checkOffscreen() {
        BoundingBoxComponent bbox = getEntity().getBoundingBoxComponent();

        double minX = 10;
        double minY = 10;


        double maxX = getAppWidth() - bbox.getWidth();
        double maxY = getAppHeight() - bbox.getHeight();

        Point2D newPosition = new Point2D(
                Math.min(maxX, Math.max(minX, getEntity().getPosition().getX())),
                Math.min(maxY, Math.max(minY, getEntity().getPosition().getY()))
        );

        physics.overwritePosition(newPosition);
    }

}


