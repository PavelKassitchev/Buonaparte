package by.pavka.march.structure;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import by.pavka.march.PlayScreen;
import by.pavka.march.military.Force;
import com.badlogic.gdx.utils.Array;

public class ForceTreeWindow extends Window {
    final PlayScreen playScreen;
    final Force force;
    final Table tabTable;
    final ForceTree tree;
    final ButtonGroup<ForceButton> trees;
    ScrollPane scrollPane;
    Array<ForceTreeTab> forceTreeTabs;

    public ForceTreeWindow(Force force, Skin skin) {
        super(force.getName(), skin);
        this.force = force;
        this.playScreen = force.playScreen;

        forceTreeTabs = new Array<>();

        playScreen.uiStage.addActor(this);
        setBounds(playScreen.uiStage.getWidth() * 0.1f, playScreen.uiStage.getHeight() * 0.1f,
                playScreen.uiStage.getWidth() * 0.8f, playScreen.uiStage.getHeight() * 0.8f);

        tabTable = new Table(skin);
        add(tabTable).left().padLeft(12);
        tabTable.setBounds(0, 0, getWidth(), getHeight() * 0.1f);
        row();

        tree = new ForceTree(skin);
        Table table = new Table(skin);
        table.add(tree).padTop(12);

        trees = new ButtonGroup();
        trees.setMinCheckCount(1);
        trees.setMaxCheckCount(1);

        final OrderView orderLabel = new OrderView(force, skin);
        addTreeTab(force, tree, tabTable, trees, orderLabel);

        ImageButton closeButton = new ImageButton(skin.getDrawable("button-close"));
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playScreen.destroyTreeWindow();
            }
        });
        getTitleTable().add(closeButton).right();
    }

    void addTreeTab(final Force force, final ForceTree tree, Table tabTable,
                    ButtonGroup<ForceButton> trees, final OrderView orderLabel) {
        ForceTreeTab forceTreeTab = new ForceTreeTab(getSkin(), this, tree, force, tabTable, trees);
        activateTab(forceTreeTab);
        forceTreeTabs.add(forceTreeTab);

    }

    void clearTab() {
        if (scrollPane != null) {
            scrollPane.remove();
        }
    }

    void activateTab(ForceTreeTab forceTreeTab) {
        clearTab();
        forceTreeTab.force.playScreen.updateTree(forceTreeTab.forceTree, forceTreeTab.force);
        scrollPane = new ScrollPane(forceTreeTab.table);
        scrollPane.setScrollingDisabled(true, false);
        row();
        add(scrollPane).left().top();
    }
}
