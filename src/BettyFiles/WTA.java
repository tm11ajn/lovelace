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

package BettyFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import BettyFiles.Exceptions.SymbolUsageException;
import BettyFiles.Semiring.Semiring;
import BettyFiles.util.Hypergraph;
import BettyFiles.Exceptions.DuplicateRuleException;


public class WTA {

    /**
     * Labels mapped to their corresponding states.
     */
    private HashMap<String, State> states = new HashMap<>();
    private ArrayList<State> finalStates = new ArrayList<>();
    private RankedAlphabet rankedAlphabet = new RankedAlphabet();
    private Semiring semiring;

    private ArrayList<Rule> rules;
    private Hypergraph<State, Rule> transitionFunction;
    private State source = new State(new Symbol("DUMMY_SOURCE", 0));

    private boolean isGrammar;

    public WTA(Semiring semiring, boolean isGrammar) {
        this.semiring = semiring;
        this.isGrammar = isGrammar;
        this.transitionFunction = new Hypergraph<>();
        this.rules = new ArrayList<>();
        this.transitionFunction.addNode(source);
    }

    public WTA(Semiring semiring) {
        this(semiring, false);
    }


    public State addState(String label) throws SymbolUsageException {

        State newState = states.get(label);

        if (newState == null) {
            Symbol symbol = rankedAlphabet.addSymbol(label, 0);
            symbol.setNonterminal(true);
            newState = new State(symbol);
            states.put(label, newState);
        }

        return newState;
    }

    public boolean setFinalState(String label) throws SymbolUsageException {

        State state = states.get(label);

        if (state == null) {
            Symbol symbol = rankedAlphabet.addSymbol(label, 0);
            symbol.setNonterminal(true);
            state = new State(symbol);
            states.put(label, state);
        }

        state.setFinal();

        return finalStates.add(state);
    }

    /* Should not be necessary, and is not for v2 */
    public HashMap<String, State> getStates() {
        return states;
    }

    public ArrayList<State> getFinalStates() {
        return finalStates;
    }

    public Symbol addSymbol(String symbol, int rank)
            throws SymbolUsageException {
        return rankedAlphabet.addSymbol(symbol, rank);
    }

    public ArrayList<Symbol> getSymbols() {
        return rankedAlphabet.getSymbols();
    }

    public Semiring getSemiring() {
        return semiring;
    }

    public boolean isGrammar() {
        return isGrammar;
    }

    public LinkedList<Rule> getSourceRules() {
        return source.getOutgoing();
    }

    public void addRule(Rule rule) throws DuplicateRuleException {
        rules.add(rule);
        ArrayList<State> states = rule.getStates();
        HashMap<State, State> nonDuplicateStates = new HashMap<>();

        if (states.isEmpty()) {
            states = new ArrayList<>();
            states.add(source);
        } else {
            for (State s : states) {
                nonDuplicateStates.put(s, null);
            }
            states = new ArrayList<>(nonDuplicateStates.keySet());
        }

        transitionFunction.addEdge(rule, rule.getResultingState(), states);
    }

    public int getStateCount() {
        return transitionFunction.getNodeCount() - 1;
    }

    public int getRuleCount() {
        return transitionFunction.getEdgeCount();
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        String string = "States: ";

        for (State s : states.values()) {
            string += s + " ";
        }

        string += "\n";
        string += "Ranked alphabet: " + rankedAlphabet + "\n";
        string += "Transition function: \n" + printTransitionFunction();
        string += "Final states: ";

        for (State s : finalStates) {
            string += s + " ";
        }

        return string;
    }

    public String printTransitionFunction() {
        ArrayList<Rule> rules = getRules();
        String string = "";

        for (Rule r : rules) {
            string += r + "\n";
        }

        return string;
    }
}