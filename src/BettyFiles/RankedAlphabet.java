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


import java.util.ArrayList;
import java.util.HashMap;
import BettyFiles.Exceptions.SymbolUsageException;


public class RankedAlphabet {

    private HashMap<String, Symbol> symbols = new HashMap<>();

    public RankedAlphabet() {

    }

    public Symbol addSymbol(String symbol, int rank)
            throws SymbolUsageException {

        Symbol s = symbols.get(symbol);

        if (s == null) {
            s = new Symbol(symbol, rank);
            symbols.put(symbol, s);
        } else if (s.getRank() != rank) {
//			throw new SymbolUsageException("Rank error: The symbol " +
//					symbol + " cannot be of two different ranks (first "
//							+ s.getRank() + " then " + rank +")");

            s = new Symbol(symbol, rank);
            symbols.put(symbol, s);
        }

        return s;
    }

    public boolean hasSymbol(String symbol) {
        return symbols.containsKey(symbol);
    }

    public ArrayList<Symbol> getSymbols() {
        return new ArrayList<Symbol>(symbols.values());
    }

    @Override
    public String toString() {

        String string = "";

        for (Symbol s : symbols.values()) {
            string += s + "(" + s.getRank() + ") ";
        }

        return string;
    }

}
