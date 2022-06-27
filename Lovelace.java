public class Lovelace {
    public static void main(String[] args) {

        inputCheck inputChecker = new inputCheck(args);
        if(!inputChecker.runNumArgCheck()) System.exit(1);

        String tree = args[0];

        if(!inputChecker.validateFile(args[1])) System.exit(1);

        OperationParser opPars = new OperationParser(args[1]);


    }
}
