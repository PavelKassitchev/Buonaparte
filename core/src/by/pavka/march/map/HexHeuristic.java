package by.pavka.march.map;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class HexHeuristic implements Heuristic<Hex> {
    @Override
    public float estimate(Hex startNode, Hex endNode) {

        if (startNode == null || endNode == null) {
            return 0f;
        }
        return (Float.parseFloat(startNode.cell.getTile().getProperties().get("cost").toString())
                + Float.parseFloat(endNode.cell.getTile().getProperties().get("cost").toString())) / 2;
    }
}
