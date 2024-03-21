package com.example.fp;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;
import com.example.fp.AIstate.*;

import static com.almasb.fxgl.dsl.FXGL.*;

public class AIComponent extends PlayerComponent {

    Entity player;
    //PlayerComponent aiComponent = entity.getComponent(PlayerComponent.class);
    AIstate aiState = AIstate.AGGRESSIVE;
    PhysicsComponent physics;
    private boolean isPunching = true;

    Texture texture;

    public AIComponent(String name, int playerNumber) {
        super(name, playerNumber);
        super.isPlayer = false;
    }

    @Override
    public void onAdded() {
        if(player == null) {
            player = getGameWorld()
                    .getSingleton(e -> e.getType() == EntityType.PLAYER
                            && e.getComponent(PlayerComponent.class).isPlayer);
        }

        FXGL.run(()-> attack(), Duration.seconds(2));
    }


    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);


        aiState = FXGL.getGameWorld().getProperties().getInt("hp2") > 100 ?
                AIstate.AGGRESSIVE : AIstate.SCARED ;


        if(aiState == AIstate.AGGRESSIVE) {
            moveTowards();
        } else {
            moveAway();
        }

    }

    int dx = 100;
    public void moveTowards() {
        if(! (FXGLMath.abs(player.getX() - entity.getX()) < 100) ) {
            if (player.getX() > entity.getX()) {
                walkRight();
            } else {
                walkLeft();
            }

        } else {
            if (!isPunching) {
                stop();
            }
        }
    }
    public void moveAway() {
        if(! (FXGLMath.abs(player.getX() - entity.getX()) > 300) ) {
            if (player.getX() > entity.getX()) {
                walkLeft();
            } else {
                walkRight();
            }

        } else {
            if(!isPunching){
                stop();
            }
        }
    }

    private void attack() {
        if(FXGLMath.random(0,1) < 0.5 ) {
            punch();
            AttackComponent a = new AttackComponent(2, "punch");
            entity.addComponent(a);
        } else {
            kick();
            AttackComponent a = new AttackComponent(2, "kick");
            entity.addComponent(a);
        }


        runOnce(()->{
            entity.removeComponent(AttackComponent.class);
        }, Duration.seconds(.5));
    }




}
