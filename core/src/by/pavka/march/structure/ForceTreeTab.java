package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.configuration.FormationValidator;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.order.DetachOrder;

public class ForceTreeTab extends Table {
    ForceTreeWindow forceTreeWindow;
    ForceTree forceTree;
    Force force;
    Table tabTable;
    Table table;
    ScrollPane scrollPane;
    ButtonGroup<ForceButton> trees;
    ForceButton singleForce;
    TextButton detach;
    Array<OrderView> orderLabels = new Array<>();
    SelectBox<String> selectBox;

    final OrderTable orderTable;

    public ForceTreeTab(Skin skin, final ForceTreeWindow forceTreeWindow, final ForceTree forceTree,
                        final Force force, final Table tabTable, final ButtonGroup<ForceButton> trees) {
        super(skin);
        this.forceTreeWindow = forceTreeWindow;
        this.forceTree = forceTree;
        this.force = force;
        this.tabTable = tabTable;
        this.trees = trees;
        singleForce = new ForceButton(force, skin);

//        final OrderView orderLabel = new OrderView("", getSkin());
        orderTable = new OrderTable(force, skin);
        System.out.println("Order table " + force.visualOrders.first());

        singleForce.setChecked(true);
        tabTable.add(singleForce).left();
        trees.add(singleForce);
//        force.playScreen.updateTree(forceTree, force, orderLabel);
        force.playScreen.updateTree(forceTree, force);
        table = new Table(skin);
        fillInTable();
        forceTreeWindow.add(this);
    }

    public void fillInTable() {
        table.clear();
        table.add(forceTree).padTop(12);
        detach = new TextButton("Detach Checked", getSkin());
        detach.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Array<Force> forces = forceTree.findForcesToDetach();
                if (!forces.isEmpty()) {
                    for (Force frc : forces) {
                        frc.superForce.viewForces.removeValue(frc, true);
                    }
                    Force group = FormationValidator.createGroup(forces, force.playScreen.game);
                    //TODO
                    forceTreeWindow.clearTab();
                    forceTreeWindow.addTreeTab(group, forceTree, tabTable, trees, null);

                }
            }
        });
        table.row();
        table.add(detach).left().padTop(12).padLeft(12);
        table.row();

        table.add(orderTable);
        table.row();

        singleForce.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ForceTreeTab thisTab = ForceTreeTab.this;
                forceTreeWindow.activateTab(thisTab);
                force.playScreen.updateTree(thisTab.forceTree, thisTab.force);
                thisTab.fillInTable();

            }
        });

        selectBox = new SelectBox<>(getSkin());
        String[] items = {"one, two two two", "Is there anybody going to listen..."};
        selectBox.setItems(items);
        table.add(selectBox).left().padTop(12).padLeft(12);
        TextButton sendOrder = new TextButton("Send Order", getSkin());
        sendOrder.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Force f = trees.getChecked().getForce();
                Force hyperForce = f.nation == null ? ((Formation) f).subForces.get(0).findHyperForce() : f.findHyperForce();
                Force.sendOrder(f, new DetachOrder(hyperForce));
                if (f.remoteHeadForce == null) {
                    forceTreeWindow.forceTreeTabs.removeValue(ForceTreeTab.this, true);
                    ForceButton fb = trees.getChecked();
                    fb.remove();
                    trees.remove(fb);
                    trees.getButtons().get(0).setChecked(true);
//                    orderLabel.clear();
                    Force force = trees.getButtons().get(0).getForce();
                    force.playScreen.updateTree(forceTree, force);
                    ForceTreeTab forceTab = forceTreeWindow.forceTreeTabs.get(0);
//                    forceTab.fillInTable();
                    forceTab.orderTable.update();
                } else {
                    force.playScreen.destroyTreeWindow();
                }
            }
        });
        table.row();
        table.add(sendOrder).left().padTop(12);
    }

}
