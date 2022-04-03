package by.pavka.march.fight;

import by.pavka.march.configuration.Nation;

public class FightResult {
    Nation winner;
    int victoryLevel;
    int winnerCasualty;
    int looserCasualty;

    public FightResult() {
    }

    public FightResult(Nation winner, int victoryLevel, int winnerCasualty, int looserCasualty) {
        this.winner = winner;
        this.victoryLevel = victoryLevel;
        this.winnerCasualty = winnerCasualty;
        this.looserCasualty = looserCasualty;
    }

    public void setWinner(Nation winner) {
        this.winner = winner;
    }

    public void setVictoryLevel(int victoryLevel) {
        this.victoryLevel = victoryLevel;
    }

    public void setWinnerCasualty(int winnerCasualty) {
        this.winnerCasualty = winnerCasualty;
    }

    public void setLooserCasualty(int looserCasualty) {
        this.looserCasualty = looserCasualty;
    }
}
