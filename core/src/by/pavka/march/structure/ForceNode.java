package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class ForceNode extends Tree.Node<ForceNode, Force, ForceView> {
    public ForceNode(final Force f, Skin skin) {
        super(new ForceView(f, skin));
        setValue(f);
        if (f instanceof Formation) {
            Formation formation = (Formation) f;
            for (Force force : formation.viewForces) {
                ForceNode fn = new ForceNode(force, skin);
                add(fn);
            }
        }

        final CheckBox box = getActor().checkBox;
        box.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (hasChildren()) {
                    if (box.isChecked()) {
                        for (ForceNode fn : getChildren()) {
                            ((ForceView)fn.getActor()).checkBox.setChecked(true);
                            ((ForceView)fn.getActor()).checkBox.setDisabled(true);
                        }
                    } else {
                        for (ForceNode fn : getChildren()) {
                            ((ForceView)fn.getActor()).checkBox.setChecked(false);
                            ((ForceView)fn.getActor()).checkBox.setDisabled(false);
                        }
                    }
                }
            }
        });
        if (f.superForce == null) {
            ((ForceView) getActor()).checkBox.setDisabled(true);
        }

        final AssignButton button = getActor().assignButton;
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Force thisForce = ForceNode.this.getValue();
                if (!button.isDisabled()) {
                    if (button.isResigning()) {
                        if (button.isChecked()) {
                            ((ForceTree)getTree()).prepareForAssigning(ForceNode.this);
                            thisForce.playScreen.resigningForce = thisForce;
                            ((ForceTree) getTree()).resigningNode = ForceNode.this;
                        } else {
                            ((ForceTree)getTree()).prepareForResigning();
                            thisForce.playScreen.resigningForce = null;
                            ((ForceTree) getTree()).resigningNode = null;
                        }
                    } else {
                        Force assignForce = thisForce.playScreen.resigningForce;
                        thisForce.playScreen.resigningForce = null;
                        assignForce.detach();
                        ((Formation)thisForce).attach(assignForce);
                        ForceTree tree = (ForceTree) getTree();
                        ForceNode assignNode = tree.resigningNode;
                        tree.remove(assignNode);
                        tree.resigningNode = null;
                        ForceNode.this.add(assignNode);
                        if (ForceNode.this.getActor().checkBox.isChecked()) {
                            assignNode.getActor().checkBox.setChecked(true);
                            assignNode.getActor().checkBox.setDisabled(true);
                        } else {
                            assignNode.getActor().checkBox.setChecked(false);
                            assignNode.getActor().checkBox.setDisabled(false);
                        }
                        getTree().getRootNodes().get(0).updateChildren();
                        ((ForceTree)getTree()).prepareForResigning();
                    }
                }
            }
        });
        if(f.superForce == null) {
            button.setResigning(false);
            button.setChecked(true);
            button.setDisabled(true);
        }
    }
}
