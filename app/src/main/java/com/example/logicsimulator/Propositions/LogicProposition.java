package com.example.logicsimulator.Propositions;

import android.view.View;
import android.widget.TextView;

import com.example.logicsimulator.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogicProposition {

    TextView Error;

    public String Formula, cnf, destroyableFormula;
    public String AllowedCharacters = "()&^|!<>ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public String Symbols = "()&^|!<>";
    public String SymbolswoP = "&^|!<>";
    public String BinarySymbols = "&^|<>";
    public String Atoms = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int varCount = 0;

    public LogicProposition(String formula, TextView error) {
        super();
        this.Formula = formula;
        this.Error = error;
    }

    public boolean errorCheck() {
        if (!symbolCheck())
            return false;
        if (!parenthesisCheck())
            return false;
        if (!syntaxCheck())
            return false;

        return true;
    }

    private boolean symbolCheck() {
        destroyableFormula = Formula;

        for (int i = 0; i < destroyableFormula.length(); i++) {
            if (AllowedCharacters.indexOf(destroyableFormula.charAt(i)) == -1) {
                String ErrorMessage = "Unknown Character: " + destroyableFormula.charAt(i);
                Error.setText(ErrorMessage);
                Error.setVisibility(View.VISIBLE);
                return false;
            }
        }

        return true;
    }

    private boolean parenthesisCheck() {
        int Open, Closed;
        Open = 0;
        Closed = 0;
        destroyableFormula = Formula;

        for (int i = 0; i < destroyableFormula.length(); i++) {
            if (Objects.equals(destroyableFormula.charAt(i), '('))
                Open += 1;
            else if (Objects.equals(destroyableFormula.charAt(i), ')'))
                Closed += 1;
        }

        if (Open != Closed) {
            if (Open > Closed) {
                int difference;
                difference = Open - Closed;
                String ErrorMessage = "You are missing " + difference + " ')' parenthesis";
                Error.setText(ErrorMessage);
            } else {
                int difference;
                difference = Closed - Open;
                String ErrorMessage = "You are missing " + difference + " '(' parenthesis";
                Error.setText(ErrorMessage);
            }
            Error.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    private boolean syntaxCheck() {
        destroyableFormula = Formula;
        if (destroyableFormula.length() == 0) {
            String ErrorMessage = "Empty field";
            Error.setText(ErrorMessage);
            Error.setVisibility(View.VISIBLE);
            return false;
        } else if (destroyableFormula.length() == 1 && Atoms.indexOf(destroyableFormula.charAt(0)) == -1) {
            String ErrorMessage = "Can't have only one symbol";
            Error.setText(ErrorMessage);
            Error.setVisibility(View.VISIBLE);
            return false;
        }
        if (BinarySymbols.indexOf(destroyableFormula.charAt(0)) != -1) {
            String ErrorMessage = "Can't start with a binary symbol";
            Error.setText(ErrorMessage);
            Error.setVisibility(View.VISIBLE);
            return false;
        }
        if (SymbolswoP.indexOf(destroyableFormula.charAt(destroyableFormula.length() - 1)) != -1) {
            String ErrorMessage = "Can't end with a symbol";
            Error.setText(ErrorMessage);
            Error.setVisibility(View.VISIBLE);
            return false;
        }
        for (int i = 0; i < destroyableFormula.length() - 1; i++) {
            if (Objects.equals(destroyableFormula.charAt(i), '(')) {
                if (!Objects.equals(destroyableFormula.charAt(i + 1), '(') && Atoms.indexOf(destroyableFormula.charAt(i + 1)) == -1 && !Objects.equals(destroyableFormula.charAt(i + 1), '!')) {
                    String ErrorMessage;
                    if (Objects.equals(destroyableFormula.charAt(i + 1), ')')) {
                        ErrorMessage = "Can't have empty parenthesis '()'";
                    } else {
                        ErrorMessage = "Illegal character '" + destroyableFormula.charAt(i + 1) + "' after '('";
                    }
                    Error.setText(ErrorMessage);
                    Error.setVisibility(View.VISIBLE);
                    return false;
                }
            }

            if (Objects.equals(destroyableFormula.charAt(i), '!')) {
                if (i != 0 && (Atoms.indexOf(destroyableFormula.charAt(i - 1)) != -1 || Objects.equals(destroyableFormula.charAt(i - 1), ')'))) {
                    String ErrorMessage = "Can't have '" + destroyableFormula.charAt(i - 1) + "' before '!'";
                    Error.setText(ErrorMessage);
                    Error.setVisibility(View.VISIBLE);
                    return false;
                }
                if (Atoms.indexOf(destroyableFormula.charAt(i + 1)) == -1 && !Objects.equals(destroyableFormula.charAt(i + 1), '(')) {
                    String ErrorMessage = "Illegal symbol '" + destroyableFormula.charAt(i + 1) + "' after '!'";
                    Error.setText(ErrorMessage);
                    Error.setVisibility(View.VISIBLE);
                    return false;
                }
            }

            if (BinarySymbols.indexOf(destroyableFormula.charAt(i)) != -1) {
                if (Objects.equals(destroyableFormula.charAt(i - 1), '(') || Objects.equals(destroyableFormula.charAt(i + 1), ')') || SymbolswoP.indexOf(destroyableFormula.charAt(i - 1)) != -1 || BinarySymbols.indexOf(destroyableFormula.charAt(i + 1)) != -1) {
                    String ErrorMessage = "Illegal use of binary symbol: '" + destroyableFormula.charAt(i) + "'";
                    Error.setText(ErrorMessage);
                    Error.setVisibility(View.VISIBLE);
                    return false;
                }
            }

            if (Atoms.indexOf(destroyableFormula.charAt(i)) != -1) {
                if (i != 0 && (Atoms.indexOf(destroyableFormula.charAt(i - 1)) != -1 || Atoms.indexOf(destroyableFormula.charAt(i + 1)) != -1)) {
                    String ErrorMessage = "Can't have consecutive atoms near: '" + destroyableFormula.charAt(i) + "'";
                    Error.setText(ErrorMessage);
                    Error.setVisibility(View.VISIBLE);
                    return false;
                } else if (Atoms.indexOf(destroyableFormula.charAt(i + 1)) != -1) {
                    String ErrorMessage = "Can't have consecutive atoms near: '" + destroyableFormula.charAt(i) + "'";
                    Error.setText(ErrorMessage);
                    Error.setVisibility(View.VISIBLE);
                    return false;
                }
            }
        }
        return true;
    }

    public static String removeRedundantParens(String proposition) {
        String result;
        result = proposition.replaceAll("\\((\\p{Upper})\\)", "$1");
        if (result.charAt(0) == '(' && result.charAt(result.length() - 1) == ')') {
            result = result.substring(1, result.length() - 1);
        }
        result = result.replaceAll("\\(\\(", "(").replaceAll("\\)\\)", ")");

        return result;
    }


    public static String convertToCNF(String proposition) {
        List<String> clauses = Arrays.asList(proposition.split("\\|"));
        List<List<String>> disjunctions = new ArrayList<>();
        for (String clause : clauses) {
            List<String> literals = Arrays.asList(clause.split("\\^"));
            disjunctions.add(literals);
        }
        List<List<String>> cnf = distribute(disjunctions);
        StringBuilder sb = new StringBuilder();
        for (List<String> clause : cnf) {
            sb.append("(");
            for (String literal : clause) {
                sb.append(literal);
                sb.append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") ^ ");
        }
        sb.delete(sb.length() - 3, sb.length());
        return sb.toString();
    }

    private static List<List<String>> distribute(List<List<String>> disjunctions) {
        if (disjunctions.size() == 1) {
            return disjunctions;
        }
        List<String> first = disjunctions.get(0);
        List<List<String>> rest = distribute(disjunctions.subList(1, disjunctions.size()));
        List<List<String>> result = new ArrayList<>();
        for (String literal1 : first) {
            for (String literal2 : rest.get(0)) {
                List<String> newClause = new ArrayList<>();
                newClause.add("(" + literal1 + " | " + literal2 + ")");
                for (int i = 1; i < rest.size(); i++) {
                    newClause.addAll(rest.get(i));
                }
                result.add(newClause);
            }
        }
        return result;
    }


}


