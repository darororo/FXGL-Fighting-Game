package com.example.fp;

import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;


import static com.almasb.fxgl.dsl.FXGL.*;

public class PlayerAction {
 
    public static void setP1ControlType (int pNumber, int cType) {

        Entity player = getGameWorld().getSingleton(EntityType.PLAYER)
                .getComponent(PlayerComponent.class).playerNumber == pNumber
                ? getGameWorld().getSingleton(EntityType.PLAYER)
                : null;



        PlayerComponent pComponent = player.getComponent(PlayerComponent.class);

        if(cType == 1) {
            onKeyBuilder(KeyCode.A)
                    .onAction(() -> pComponent.walkLeft())
                    .onActionEnd(() -> pComponent.stop());
            onKeyBuilder(KeyCode.D)
                    .onAction(() -> pComponent.walkRight())
                    .onActionEnd(() -> pComponent.stop());
            onKey(KeyCode.W, () -> pComponent.jump());
            onKeyBuilder(KeyCode.S)
                    .onAction(() -> pComponent.duck())
                    .onActionEnd(() -> pComponent.unduck());

            onKeyBuilder(KeyCode.U)
                    .onActionBegin( () -> {
                        pComponent.punch();
                        player.addComponent(new AttackComponent(1, "punch"));
                    })
                    .onActionEnd( () -> player.removeComponent(AttackComponent.class));

            onKeyBuilder(KeyCode.I)
                    .onActionBegin( () -> {
                        pComponent.kick();
                        player.addComponent(new AttackComponent(1, "kick"));
                    })
                    .onActionEnd( () -> player.removeComponent(AttackComponent.class));
        }

        if(cType == 2) {
            onKeyBuilder(KeyCode.LEFT)
                    .onAction(() -> pComponent.walkLeft())
                    .onActionEnd(() -> pComponent.stop());
            onKeyBuilder(KeyCode.RIGHT)
                    .onAction(() -> pComponent.walkRight())
                    .onActionEnd(() -> pComponent.stop());
            onKey(KeyCode.UP, () -> pComponent.jump());
            onKeyBuilder(KeyCode.DOWN)
                    .onAction(() -> pComponent.duck())
                    .onActionEnd(() -> pComponent.unduck());

            onKeyBuilder(KeyCode.B)
                    .onActionBegin( () -> {
                        pComponent.punch();
                        player.addComponent(new AttackComponent(1, "punch"));
                    })
                    .onActionEnd( () -> player.removeComponent(AttackComponent.class));

            onKeyBuilder(KeyCode.N)
                    .onActionBegin( () -> {
                        pComponent.kick();
                        player.addComponent(new AttackComponent(1, "kick"));
                    })
                    .onActionEnd( () -> player.removeComponent(AttackComponent.class));
        }

    }


}
