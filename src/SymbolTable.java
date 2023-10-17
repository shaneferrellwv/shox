package edu.ufl.cise.plcsp23;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ufl.cise.plcsp23.ast.NameDef;

public class SymbolTable {
    class Chain {

        ArrayList<Pair> itemsWithThisName = new ArrayList<>();

        Chain(Pair pair) {
            itemsWithThisName.add(pair);
        }
    }

    class Pair {
        int scope;
        NameDef nameDef;

        // constructor for assigning values
        Pair(int scope, NameDef nameDef) {
            this.scope = scope;
            this.nameDef = nameDef;
        }
    }

    HashMap<String, Chain> entries = new HashMap<>();
    ArrayList<Integer> scopeStack = new ArrayList<>();
    int currentScope = 0;
    int nextScope = 0;

    SymbolTable() {
        scopeStack.add(0);
    }

    // returns true if name successfully inserted in symbol table, false if already
    // present
    public boolean insert(String name, NameDef nameDef) {
        boolean inserted = entries.putIfAbsent(name, new Chain(new Pair(currentScope, nameDef))) == null;
        if (inserted) return true;
        else {
            for (Pair i : entries.get(name).itemsWithThisName) {
                if (i.scope == currentScope)
                    return false;
            }
            entries.get(name).itemsWithThisName.add(new Pair(currentScope, nameDef));
        }
        return true;
    }

    // returns NameDef if present, or null if name not declared.
    public NameDef lookup(String name) {
        if (entries.get(name) == null) {
            return null;
        }
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            for (Pair p : entries.get(name).itemsWithThisName) {
                if (p.scope == scopeStack.get(i)) {
                    return p.nameDef;
                }
            }
        }
        return null;
    }

    // returns NameDef if present in this scope, or null if name not yet declared in this scope.
    public NameDef lookupInThisScope(String name) {
        if (entries.get(name) == null) {
            return null;
        } 
        else if (entries.get(name).itemsWithThisName.get(entries.get(name).itemsWithThisName.size() - 1).scope != currentScope) {
                    return null;
        }
        else 
            return entries.get(name).itemsWithThisName.get(entries.get(name).itemsWithThisName.size() - 1).nameDef;
    }

    public void enterScope() {
        currentScope = ++nextScope;
        scopeStack.add(currentScope);
    }

    public void exitScope() {
        currentScope = scopeStack.remove(scopeStack.size() - 1);
    }

}
