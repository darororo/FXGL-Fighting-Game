package com.example.fp;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import static com.almasb.fxgl.dsl.FXGL.*;


public class AttackComponent extends Component {
    PhysicsComponent physics = new PhysicsComponent();
    Entity hb;
    int playerNumber;
    String attackType; // 1: punch; 2: kick; 3: special


    public AttackComponent() {
    }
    public AttackComponent(int playerNumber, String attackType) {

        this.playerNumber = playerNumber;
        this.attackType = attackType;
    }

    @Override
    public void onAdded() {

        if(attackType.equals("punch")) {
            punchHitbox();
        }

        if(attackType.equals("kick")) {
            kickHitbox();
        }

    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
    }

    @Override
    public void onRemoved() {
        getGameWorld().removeEntity(hb);
    }



    private void punchHitbox() {
        double x=0, y;
        if(GameApp.gameMode.equals("pve")) {
            if(playerNumber == 2) {
                x = entity.getComponent(AIComponent.class).isFacingRight ?
                        entity.getX() + 60 : entity.getX() - 60;
            }
            if(playerNumber == 1) {
                x = entity.getComponent(PlayerComponent.class).isFacingRight ?
                        entity.getX() + 60 : entity.getX() - 60;
            }

        } else {
            x = entity.getComponent(PlayerComponent.class).isFacingRight ?
                    entity.getX() + 60 : entity.getX() - 60;
        }


        y = entity.getY() + 10;
        hb = spawn("attack",
                new SpawnData(x, y).put("number", playerNumber));

    }

    private void kickHitbox() {
        double x=0, y, r=0;
        if(GameApp.gameMode.equals("pve")) {
            if(playerNumber == 2) {
                if(entity.getComponent(AIComponent.class).isFacingRight) {
                    x = entity.getX() + 60;
                    r = 135;
                }  else {
                    x = entity.getX() - 60;
                    r = 45;
                }

            }

            if(playerNumber == 1) {
                if(entity.getComponent(PlayerComponent.class).isFacingRight) {
                    x = entity.getX() + 60;
                    r = 135;
                } else {
                    x = entity.getX() - 60;
                    r = 45;
                }
            }

        } else {
            if(entity.getComponent(PlayerComponent.class).isFacingRight) {
                x = entity.getX() + 60;
                r = 135;

            } else {
                x = entity.getX() - 60;
                r = 45;
            }
        }

        y = entity.getY() + 30;

        hb = spawn("attack",
                new SpawnData(x, y).put("number", playerNumber));
        hb.rotateBy(r);

    }


}
