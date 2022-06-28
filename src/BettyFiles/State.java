package BettyFiles;

/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */


import BettyFiles.treedata.Context;
import BettyFiles.util.Hypergraph;

public class State extends Hypergraph.Node<Rule> {
    private static Context[] bestContexts;

    public static void init(Context[] bestContexts2) {
        bestContexts = bestContexts2;
    }

    private Symbol label;
    private boolean isFinal;
    private boolean saturated;

    public State(Symbol label) {
        super();
        this.label = label;
        isFinal = false;
        saturated = false;
    }

    public Symbol getLabel() {
        return label;
    }

    public Context getBestContext() {
        return bestContexts[this.getID()];
    }

    protected void setFinal() {
        isFinal = true;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void markAsSaturated() {
        this.saturated = true;
    }

    public boolean isSaturated() {
        return saturated;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof State) {
            State s = (State) obj;

            if (s.getLabel().equals(this.label)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getID();
    }

    @Override
    public String toString() {
        return label.toString();
    }

}
