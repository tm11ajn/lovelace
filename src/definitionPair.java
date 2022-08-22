public class definitionPair {
    private final String variable;
    private final String[] definitions;
    definitionPair(String variable, String[] definitions){
        this.variable = variable;
        this.definitions = definitions;
    }

    public String getVariable() {
        return variable;
    }

    public String[] getDefinitions() {
        return definitions;
    }
}
