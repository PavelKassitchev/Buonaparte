package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.configuration.FormationValidator;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;
import by.pavka.march.order.DetachOrder;
import by.pavka.march.order.Order;
import by.pavka.march.order.OrderType;

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
    TextButton clearOrders;
    SelectBox<OrderType> selectBox;
    OrderType[] items;

    final OrderTable orderTable;

    public final OrderTable futureOrderTable;

    public ForceTreeTab(Skin skin, final ForceTreeWindow forceTreeWindow, final ForceTree forceTree,
                        final Force force, final Table tabTable, final ButtonGroup<ForceButton> trees) {
        super(skin);
        this.forceTreeWindow = forceTreeWindow;
        this.forceTree = forceTree;
        this.force = force;
        this.tabTable = tabTable;
        this.trees = trees;
        singleForce = new ForceButton(force, skin);
        items = OrderType.values();

//        final OrderView orderLabel = new OrderView("", getSkin());
        orderTable = new OrderTable(force, skin);
        futureOrderTable = new OrderTable(force, skin, true);
//        System.out.println("Future Order table " + futureOrderTable.getLastOrder());

        singleForce.setChecked(true);
        tabTable.add(singleForce).left();
        trees.add(singleForce);
//        force.playScreen.updateTree(forceTree, force, orderLabel);
        force.playScreen.updateTree(forceTree, force);
        table = new Table(skin);
        fillInTable();
        System.out.println("FillInTable inside Constructor");
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
                    Force group = FormationValidator.createGroup(forces, force.playScreen);
                    //TODO
                    forceTreeWindow.clearTab();
                    forceTreeWindow.addTreeTab(group, forceTree, tabTable, trees);

                }
            }
        });
        table.row();
        table.add(detach).left().padTop(12).padLeft(12);
//        table.row();

        clearOrders = new TextButton("Clear Orders", getSkin());
        clearOrders.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (Order ord : force.visualOrders) {
                    orderTable.clear();
                    if (ord.irrevocable) {
                        ord.cancel(force);
                    } else {
                        ord.revoked = true;
                    }
                }
                force.visualOrders.clear();
            }
        });
//        table.add(clearOrders);
        table.row();

        table.add(orderTable).padLeft(12).padTop(12);
        table.row();
        table.add(clearOrders);
        table.row();

        singleForce.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ForceTreeTab thisTab = ForceTreeTab.this;
                forceTreeWindow.activateTab(thisTab);
                force.playScreen.updateTree(thisTab.forceTree, thisTab.force);
//                thisTab.fillInTable();
//                System.out.println("FillInTable inside Listener");

            }
        });

        selectBox = new SelectBox<>(getSkin());
//        String[] items = {"one, two two two", "Is there anybody going to listen..."};
//        OrderType[] items = OrderType.values();
        selectBox.setItems(items);
        table.add(selectBox).left().padTop(12).padLeft(12);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                OrderType orderType = selectBox.getSelected();
                Order order = orderType.getOrder();
                force.playScreen.additiveOrder = true;
                if (order != null) {
                    futureOrderTable.addOrder(order);

                    //TODO

                    force.playScreen.treeWindow.setVisible(false);
                }
            }
        });

        table.row();
        table.add(futureOrderTable).left().padTop(12).padLeft(12);

        Force f = trees.getChecked().getForce();
        System.out.println("FORCE F = " + f.getName() + " remote head = "
                + f.remoteHeadForce + " this is NOT head = " + (f != f.playScreen.headForce));
        //TODO
        if (f.remoteHeadForce == null && f != f.playScreen.headForce) {
            Force hyperForce = f.nation == null ? ((Formation) f).subForces.get(0).findHyperForce()
                    : force.findHyperForce();
            futureOrderTable.clear();
            futureOrderTable.addOrder(new DetachOrder(hyperForce, f));
        }


        TextButton sendOrder = new TextButton("Send Order", getSkin());
        sendOrder.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Force f = trees.getChecked().getForce();
//                Force hyperForce = f.nation == null ? ((Formation) f).subForces.get(0).findHyperForce() : f.findHyperForce();
//                Force.sendOrder(f, new DetachOrder(hyperForce, f));
                long del = 0;
                for (OrderView orderView : futureOrderTable.orderViews) {
                    Order order = orderView.order;
                    if (!order.revoked) {
                        if (order instanceof DetachOrder) {
                            System.out.println("Found DetachOrder for " + f.getName());
                            DetachOrder detOrder = (DetachOrder) order;
                            detOrder.hyper.visualOrders.addFirstOrder(detOrder.detachForceOrder);
                        }
                        System.out.println("On Send Order to " + force.getName() + " " + order);
                        Force.sendOrder(force, order, del);
                        del += 50;
//                        futureOrderTable.clear();
                    }
                }


                if (f.remoteHeadForce == null && f.playScreen.headForce != f) {
//                    Force.sendOrder(f, new DetachOrder(hyperForce, f));
                    forceTreeWindow.forceTreeTabs.removeValue(ForceTreeTab.this, true);
                    ForceButton fb = trees.getChecked();
                    fb.remove();
                    trees.remove(fb);
                    trees.getButtons().get(0).setChecked(true);
                    Force force = trees.getButtons().get(0).getForce();
                    force.playScreen.updateTree(forceTree, force);
                    ForceTreeTab forceTab = forceTreeWindow.forceTreeTabs.get(0);

                    forceTab.orderTable.update();
                    forceTreeWindow.activateTab(forceTab);
//                    force.playScreen.updateTree(forceTab.forceTree, forceTab.force);
//                    forceTab.fillInTable();
                } else {
                    force.playScreen.destroyTreeWindow();
                }

            }
        });
        table.row();
        table.add(sendOrder).left().padTop(12).padLeft(12);
    }

}
