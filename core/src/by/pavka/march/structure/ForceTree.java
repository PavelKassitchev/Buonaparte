package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class ForceTree extends Tree<ForceNode, Force> {
    public ForceNode resigningNode;

    public ForceTree(Skin skin) {
        super(skin);
    }

    public void prepareForResigning() {
        ForceNode rootNode = getRootNodes().get(0);
        AssignButton button = rootNode.getActor().assignButton;
        button.setResigning(false);
        button.setChecked(true);
        button.setDisabled(true);
        setReadyToResign(rootNode);
    }

    private void setReadyToResign(ForceNode node) {
        if (node.hasChildren()) {
            for (ForceNode fn : node.getChildren()) {
                fn.getActor().assignButton.setResigning(true);
                fn.getActor().assignButton.setChecked(false);
                fn.getActor().assignButton.setDisabled(false);
                setReadyToResign(fn);
            }
        }
    }

    public void prepareForAssigning(ForceNode node) {
        ForceNode rootNode = getRootNodes().get(0);
        rootNode.getActor().assignButton.setChecked(false);
        rootNode.getActor().assignButton.setDisabled(false);
        setReadyToAssign(rootNode, node);
    }

    private void setReadyToAssign(ForceNode rootNode, ForceNode node) {
        if (rootNode != node) {
            //TODO add validator
            if (rootNode.getValue() instanceof Formation && true) {
                rootNode.getActor().assignButton.setResigning(false);
            } else {
                rootNode.getActor().assignButton.setResigning(false);
                rootNode.getActor().assignButton.setChecked(true);
                rootNode.getActor().assignButton.setDisabled(true);
            }
            if (rootNode.hasChildren()) {
                for (ForceNode fn : rootNode.getChildren()) {
                    setReadyToAssign(fn, node);
                }
            }
        } else {
            for (ForceNode fn : node.getChildren()) {
                setReadyToAssign(fn, node);
            }
        }
    }

    public Array<Force> findForcesToDetach() {
        Array<Force> forcesToDetach = new Array<Force>();
        ForceNode rootNode = getRootNodes().get(0);
        setToDetach(rootNode, forcesToDetach);
        return forcesToDetach;
    }

    private void setToDetach(ForceNode rootNode, Array<Force> forcesToDetach) {
        if (rootNode.hasChildren()) {
            for (ForceNode fn : rootNode.getChildren()) {
                if (fn.getActor().checkBox.isChecked()) {
                    forcesToDetach.add(fn.getValue());
                } else {
                    setToDetach(fn, forcesToDetach);
                }
            }
        }
    }
}
