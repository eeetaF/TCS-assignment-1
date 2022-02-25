/* Gubaidullin Marat
 *  Innopolis University
 *  B21-02
 *  23.02.2022
 *  TCS Assignment 1
 * */

/*
Errors:
    E1: A state 's' is not in the set of states
    E2: Some states are disjoint
    E3: A transition 'a' is not represented in the alphabet
    E4: Initial state is not defined
    E5: Input file is malformed

Report:
    FSA is complete/incomplete

Warnings:
    W1: Accepting state is not defined
    W2: Some states are not reachable from the initial state
    W3: FSA is nondeterministic
 */
import java.nio.file.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        String string = "";
        String output = "";

        Map<String,Map<String, Integer>> trans_from_states = new HashMap<>();
        Map<String,Integer> trans_to_states = new HashMap<>();
        Map<String,List<String>> trans_states = new HashMap<>();

        String[] states_array; // line 1
        String[] alphabet; // line 2
        String initial_state; // line 3
        String[] final_states; // line 4
        List<Transition> transitions = new ArrayList<>(); // line 5

        String exception = "0"; // exception from 0 to 5
        boolean[] warning = new boolean[3]; // warning from 0 to 3

        String invalid_transition = "";
        String invalid_state = "";

        boolean fsa_is_complete = true;

        // path to "result.txt"
        Path proj_path = Path.of("result.txt");
        // creating "result.txt"
        try { Files.createFile(proj_path); } catch (Exception ignored) {}
        // reading from "fsa.txt"
        try { string = Files.readString(Path.of("fsa.txt")); } catch (Exception ignored) {}
        // inputLines contains 5 lines of input
        String[] inputLines = string.split("\n");

        // Any change of "exception" will raise an Exception.
        try {
            // checking if input is valid
            if (    !(inputLines[0].substring(0,8).equals("states=[")) ||
                    !(inputLines[0].charAt(inputLines[0].length()-1) == ']') ||
                    !(inputLines[1].substring(0,7).equals("alpha=[")) ||
                    !(inputLines[1].charAt(inputLines[1].length()-1) == ']') ||
                    !(inputLines[2].substring(0,9).equals("init.st=[")) ||
                    !(inputLines[2].charAt(inputLines[2].length()-1) == ']') ||
                    !(inputLines[3].substring(0,8).equals("fin.st=[")) ||
                    !(inputLines[3].charAt(inputLines[3].length()-1) == ']') ||
                    !(inputLines[4].substring(0,7).equals("trans=[")) ||
                    !(inputLines[4].charAt(inputLines[4].length()-1) == ']')
            ) throw new Exception("5");

            // 1st string (states array)
            //
            // removing "states=[" and "]"
            string = inputLines[0].substring(8,inputLines[0].length() - 1);
            states_array = string.split(",");
            // checking the validity of states and putting them into dictionaries
            for (String state : states_array) {
                trans_to_states.put(state, 0);
                List<String> l = new ArrayList<>();
                trans_states.put(state, l);
            }

            // 2nd string (alphabet)
            //
            // removing "alpha=[" and "]"
            string = inputLines[1].substring(7, inputLines[1].length() - 1);
            alphabet = string.split(",");

            // 3rd string (initial state)
            //
            // removing "init.st=[" and "]"
            string = inputLines[2].substring(9, inputLines[2].length() - 1);
            initial_state = string;
            // in case if initial state is not defined (E4)
            if (initial_state.equals(""))
                throw new Exception("4");
                // checking if there is no such initial state in array of states (E1)
            else {
                boolean there_is = false;
                for (String state : states_array) {
                    if (state.equals(initial_state)) {
                        there_is = true;
                        break;
                    }
                } if (!there_is) { invalid_state = initial_state; throw new Exception("1");}
            }

            // 4th string (final states)
            //
            // removing "fin.st=[" and "]"
            string = inputLines[3].substring(8,inputLines[3].length() - 1);
            final_states = string.split(",");
            // in case if final state is not defined (W1)
            if (final_states.length == 1 && final_states[0].equals(""))
                warning[0] = true;
            else {
                for (String final_state : final_states) { // for every final state
                    // checking if there is no such final state in array of states (E1)
                    boolean there_is = false;
                    for (String state : states_array) {
                        if (final_state.equals(state)) {
                            there_is = true;
                            break;
                        }
                    } if (!there_is) {invalid_state = final_state; throw new Exception("1");}
                }
            }

            // filling dictionaries
            for (String state : states_array) {
                for (String transition : alphabet) {
                    Map<String,Integer> additional_dict = new HashMap<>();
                    if (trans_from_states.get(state) != null)
                        additional_dict = trans_from_states.get(state);
                    additional_dict.put(transition, 0);
                    trans_from_states.put(state, additional_dict);
                }
            }

            // 5th string (transitions)
            //
            // removing "trans=[" and "]"
            string = inputLines[4].substring(7,inputLines[4].length() - 1);
            String[] string_transitions = string.split(",");
            // i contains "off>turn_on>on"
            for (String i : string_transitions) {
                // Tr_array contains "off", "turn_on", "on"
                String[] Tr_array = i.split(">");
                if (Tr_array.length != 3)
                    throw new Exception("5");
                transitions.add(new Transition(Tr_array[0], Tr_array[2], Tr_array[1]));
            }

            for (Transition trans: transitions) { // for every transition
                boolean to = false, from = false, cond = false;

                // for every state in states_array if equals to "to" state of trans =>
                // => no exception
                for (String state : states_array) {
                    if (state.equals(trans.to_s)) {
                        to = true;
                        break; }}

                for (String state : states_array) {
                    if (state.equals(trans.from_s)) {
                        from = true;
                        break; }}

                for (String alpha : alphabet) {
                    if (alpha.equals(trans.trans_c)) {
                        cond = true;
                        break; }}

                if (!to) {
                    invalid_state = trans.to_s;
                    throw new Exception("1"); }
                if (!from) {
                    invalid_state = trans.from_s;
                    throw new Exception("1"); }
                if (!cond) {
                    invalid_transition = trans.trans_c;
                    throw new Exception("3"); }

                List<String> temp_list = trans_states.get(trans.from_s);
                boolean temp = false;
                for (String temp_list_ : temp_list) {
                    if (temp_list_.equals(trans.to_s)) {
                        temp = true;
                        break; }}

                if (!temp) {
                    temp_list.add(trans.to_s);
                    trans_states.put(trans.from_s, temp_list);
                }

                if (!trans.from_s.equals(trans.to_s))
                    trans_to_states.put(trans.to_s, trans_to_states.get(trans.to_s) + 1);

                Map<String,Integer> add_dict;
                add_dict = trans_from_states.get(trans.from_s);
                int temp_value = add_dict.get(trans.trans_c) + 1;
                add_dict.put(trans.trans_c, temp_value);
                trans_from_states.put(trans.from_s, add_dict);
            }

            // checking if FSA in incomplete or nondeterministic
            for (Map<String, Integer> tr : trans_from_states.values()) {
                for (Integer trans : tr.values()) {
                    if (trans > 1)
                        warning[2] = true;
                    else if (trans == 0)
                        fsa_is_complete = false;
                }
            }

            // Checking if every state is reachable from the initial state
            // and there are no disjoint states
            for (String trans : trans_to_states.keySet()) {
                if (trans_to_states.get(trans) == 0 &&
                        !trans.equals(initial_state)) {
                    warning[1] = true;
                    boolean non_zero_values_exist = false;
                    for (int value : trans_from_states.get(trans).values()) {
                        if (value != 0) {
                            non_zero_values_exist = true;
                            break;
                        }
                    }
                    if (!non_zero_values_exist ||
                            ((trans_states.get(trans)).size() == 1 && trans_states.get(trans).get(0).equals(trans)))
                        throw new Exception("2");
                }

            }

            if (fsa_is_complete)  output += "FSA is complete\n";
            else output += "FSA is incomplete\n";

            if (warning[0] || warning[1] || warning[2]) {
                output += "Warning:\n";
                if (warning[0]){ output += "W1: Accepting state is not defined\n";}
                if (warning[1]){ output += "W2: Some states are not reachable from the initial state\n";}
                if (warning[2]){ output += "W3: FSA is nondeterministic\n";} }

        } catch (Exception e) { exception = e.getMessage();
            if (!exception.equals("1") && !exception.equals("2") && !exception.equals("3") && !exception.equals("4"))
                exception = "5"; }

        if (!exception.equals("0")) { try {
            output += "Error:\n";
            switch (exception) {
                case "1": { output += "E1: A state '" + invalid_state + "' is not in the set of states"; break; }
                case "2": { output += "E2: Some states are disjoint"; break; }
                case "3": { output += "E3: A transition '" + invalid_transition + "' is not represented in the alphabet"; break; }
                case "4": { output += "E4: Initial state is not defined"; break; }
                default: { output += "E5: Input file is malformed"; break; } }

        } catch (Exception ignored){}}

        // write the result (proj_path contains a directory within our project + "\result.txt")
        try {Files.writeString(proj_path, output); } catch (Exception ignored) {}
    }

    public static class Transition {
        String from_s;
        String to_s;
        String trans_c;
        public Transition(String from_s, String to_s, String trans_c) {
            this.from_s = from_s;
            this.to_s = to_s;
            this.trans_c = trans_c;
        }
    }
}