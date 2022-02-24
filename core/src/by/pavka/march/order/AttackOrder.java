package by.pavka.march.order;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class AttackOrder extends FollowOrder {
    @Override
    public SelectBox<? extends Force> createSelectBox(Force frc, Hex hex) {
        SelectBox<Force> selectForce = new SelectBox<>(frc.playScreen.game.getSkin());
        Array<Force> allies = frc.getEnemies(hex);
        selectForce.setItems(allies);
        return selectForce;
    }
}
