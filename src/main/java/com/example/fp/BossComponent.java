package com.example.fp;

import com.almasb.fxgl.app.services.FXGLAssetLoaderService;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;


import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.texture;

import com.example.fp.AIstate.*;

import java.util.HashMap;
import java.util.Map;

public class BossComponent extends AIComponent{
    private HashMap<String, String> attPattern = new HashMap<>(Map.of("pat", "range"));

    public BossComponent() {
        super("boss", 2);
        super.isPlayer = false;
    }

    @Override
    public void onAdded() {
        loadCharTexture("boss", "idle.png");
        if(player == null) {
            player = getGameWorld()
                    .getSingleton(e -> e.getType() == EntityType.PLAYER
                            && e.getComponent(PlayerComponent.class).isPlayer);
        }

        FXGL.run(()->{
            if(attPattern.get("pat").equals("range")) {
                shootProjectile();
            }else {
                shootFlame();
            };
        }, Duration.seconds(3));


    }

    @Override
    public void onUpdate(double tpf) {
        AIstate attack;
        if(FXGLMath.abs(entity.getX() - player.getX())  > 200 ) {
            attPattern.replace("pat","range");
        } else {
            attPattern.replace("pat", "close");
        }

        AIstate movement = FXGL.getGameWorld().getProperties().getInt("hp2") > 100 ?
                AIstate.AGGRESSIVE : AIstate.SCARED ;


        if(movement == AIstate.AGGRESSIVE) {
            moveTowards();
        } else {
            moveAway();
        }

    }


    int dx = 180;
    @Override
    public void moveTowards() {

        if(! (FXGLMath.abs(player.getX() - entity.getX()) < 150) ) {
            if (player.getX() > entity.getX()) {
                physics.setVelocityX(dx);
            } else {
                physics.setVelocityX(-dx);
            }

        } else {
            physics.setVelocityX(0);
        }
    }
    @Override
    public void moveAway() {
        if(! (FXGLMath.abs(player.getX() - entity.getX()) > 300) ) {
            if (player.getX() > entity.getX()) {
                physics.setVelocityX(-dx);
            } else {
                physics.setVelocityX(dx);
            }
        } else {
            physics.setVelocityX(0);
        }
    }

    @Override
    public void duck() {

    }

    private void shootProjectile() {
        FXGL.play("hadouken.wav");
        pdamage = 30;
        var x = entity.getX();
        var y = entity.getY() - 10;
        if(isFacingRight) {
            x = x + 70;
        } else {
            x = x - 70;
        }

        Point2D dir = isFacingRight? new Point2D(1,0) : new Point2D(-1,0);

        FXGL.getWorldProperties().setValue("Attack2", "projectile");
        var attack = spawn("projectile",
                new SpawnData(x,y)
                        .put("number", 2)
                        .put("dir", dir));

    }

    private void shootFlame() {
        FXGL.play("flame.wav");
        pdamage = 30;
        var x = entity.getX();
        var y = entity.getY() - 20;
        if(isFacingRight) {
            x = x + 150 ;
        } else {
            x = x - 150;
        }
        FXGL.getWorldProperties().setValue("Attack2", "flame");
        var attack = spawn("flame",
                new SpawnData(x,y)
                        .put("number", 2));

        FXGL.runOnce(()->{
            FXGL.getGameWorld().removeEntity(attack);
        }, Duration.seconds(1.5));
    }

}
