public class definitionPair {
    private final String variables;
    private final String[] definitions;
    definitionPair(String variable, String[] definitions){
        this.variables = variable;
        this.definitions = definitions;
    }

    public String getVariables() {
        return variables;
    }

    public String[] getDefinitions() {
        return definitions;
    }
}
