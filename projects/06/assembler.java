import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.HashMap;
import java.util.Scanner; // Import the Scanner class to read text files

import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;

public class assembler {
  public static void main(String[] args) {
        try {
            String inputFileName = "max/Max.asm";
            File output = new File("prog.hack");
            FileWriter writer = new FileWriter(output);
            SymbolTable symbolTable = new SymbolTable();
            //first pass
            File input1 = new File(inputFileName);
            parser parser1 = new parser(input1);
            int counter = 0;
            while(parser1.hasMoreCommands()){
                parser1.advance();
                if(parser1.commandType().equals("L_COMMAND")){
                    String symbol = parser1.symbol();
                    if(!symbolTable.contains(symbol)){
                        symbolTable.addEntry(symbol, counter);
                    }
                }else{
                    if(!parser1.commandType().equals("Not_command")){
                        counter++;
                    }
                }
            }
            //second pass
            File input2 = new File(inputFileName);
            parser parser2 = new parser(input2);
            int ramCounter = 16;
            while(parser2.hasMoreCommands()){
                parser2.advance();
                String out = "";
                switch(parser2.commandType()){
                    case "A_COMMAND":
                        String symbol = parser2.symbol();
                        out += "0";
                        if(isNumeric(symbol)){
                            out += decimalToBinary(symbol);
                        }else{
                            if(symbolTable.contains(symbol)){
                                out += decimalToBinary(String.valueOf(symbolTable.getAddress(symbol)));
                            }else{
                                symbolTable.addEntry(symbol, ramCounter);
                                out += decimalToBinary(String.valueOf(ramCounter));
                                ramCounter++;
                            }
                        }
                        writer.append(out+"\n");
                        System.out.println(symbol);
                        break;
                    case "C_COMMAND":
                        out += "111";
                        out += Code.comp(parser2.comp());
                        out += Code.dest(parser2.dest());
                        out += Code.jump(parser2.jump());
                        writer.append(out+"\n");
                        //System.out.println("comp: " + parser2.comp() + " dest: " + parser2.dest() + " jump: " + parser2.jump());
                        break;
                }    
            }
            writer.close();
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
    }

    static String decimalToBinary(String decimal){
        int num = Integer.valueOf(decimal);
        String result = Integer.toBinaryString(num);
        result = result.trim();
        int length = result.length();
        for(int i=0;i < 15-length ;i++){
            result = "0" + result;
        }
        //System.out.println(result);
        return result;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

class parser{

    Scanner myReader;
    String command;

    parser(File input){
        try{
            myReader = new Scanner(input);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    boolean hasMoreCommands(){
        if(myReader.hasNextLine()){
            return true;
        }
        myReader.close();
        return false;
    }

    void advance(){
        if(hasMoreCommands() == true){
            command = myReader.nextLine();
            String[] buffer = command.split("[//]");
            command = buffer[0];
            command = command.trim().replace(" ", "");

        }
    }

    String commandType(){
        String currentCommand = command;
        if(currentCommand.contains("@")){
            return "A_COMMAND";
        }
        else if(currentCommand.contains("(")){
            return "L_COMMAND";
        }
        else if(currentCommand.contains("=") || currentCommand.contains(";") ){
            return "C_COMMAND";
        }
        return "Not_command";
    }

    String symbol(){
        String commandType = commandType();
        String[] buffer;
        String commandCopy = command;
        if (commandType.equals("A_COMMAND")){
            buffer = commandCopy.split("[@]");
            return buffer[1];
        }
        if (commandType.equals("L_COMMAND")){
            commandCopy = commandCopy.replaceAll("[()]", "");
            return commandCopy;
        }
        return "";
    }

    String dest(){
        // D=M+1
        if(commandType().equals("C_COMMAND")){
            String commandCopy = command;
            if(commandCopy.contains("=")){
                return commandCopy.split("[=]")[0];
            }
        }
        return "null";
    }

    String comp(){
        // D=M+1
        if(commandType().equals("C_COMMAND")){
            String commandCopy = command;
            if(commandCopy.contains("=")){
                return commandCopy.split("[=]")[1];
            }
            if(commandCopy.contains(";")){
                return commandCopy.split("[;]")[0];
            }     
        }
        return "null";
    }

    String jump(){
        // 0;JMP
        if(commandType().equals("C_COMMAND")){
            String commandCopy = command;
            if(commandCopy.contains(";")){
                return commandCopy.split("[;]")[1];
            }
        }
        return "null";
    }

}

class Code{
    static String dest(String input){
        String result = "";
        switch(input){
            case "null":
                result = "000";
                break;
            case "M":
                result = "001";
                break;
            case "D":
                result = "010";
                break;
            case "MD":
                result = "011";
                break;
            case "A":
                result = "100";
                break;
            case "AM":
                result = "101";
                break;
            case "AD":
                result = "110";
                break;
            case "AMD":
                result = "111";
                break;
        }
        return result;
    }

    static String jump(String input){
        String result = "";
        switch(input){
            case "null":
                result = "000";
                break;
            case "JGT":
                result = "001";
                break;
            case "JEQ":
                result = "010";
                break;
            case "JGE":
                result = "011";
                break;
            case "JLT":
                result = "100";
                break;
            case "JNE":
                result = "101";
                break;
            case "JLE":
                result = "110";
                break;
            case "JMP":
                result = "111";
                break;
        }
        return result;
    }

    static String comp(String input){
        String result = "";       
        switch(input){
            case "0":
                result = "0101010";
                break;
            case "1":
                result = "0111111";
                break;
            case "-1":
                result = "0111010";
                break;
            case "D":
                result = "0001100";
                break;
            case "A":
                result = "0110000";
                break;
            case "!D":
                result = "0001101";
                break;
            case "!A":
                result = "0110001";
                break;
            case "-A":
                result = "0110011";
                break;
            case "-D":
                result = "0001111";
                break;
            case "D+1":
                result = "0011111";
                break;
            case "A+1":
                result = "0110111";
                break;
            case "D-1":
                result = "0001110";
                break;
            case "A-1":
                result = "0001110";
                break;
            case "D+A":
                result = "0000010";
                break;
            case "D-A":
                result = "0000010";
                break;
            case "A-D":
                result = "000111";
                break;
            case "D&A":
                result = "0000000";
                break;
            case "D|A":
                result = "0010101";
                break;
            case "M":
                result = "1110000";
                break;
            case "!M":
                result = "1110001";
                break;
            case "-M":
                result = "1110011";
                break;
            case "M+1":
                result = "1110111";
                break;
            case "M-1":
                result = "1110010";
                break;
            case "D+M":
                result = "1000010";
                break;
            case "D-M":
                result = "1010011";
                break;
            case "M-D":
                result = "1000111";
                break;
            case "D&M":
                result = "1000000";
                break;
            case "D|M":
                result = "1010101";
                break;
        }
        return result;
    }
}

class SymbolTable{
    HashMap<String,Integer> map = new HashMap<String,Integer>();

    String[][] predefined = {{"SP","0"},{"LCL","1"},{"ARG","2"},{"THIS","3"},{"THAT","4"},{"R0","0"},{"R1","1"},{"R2","2"}
        ,{"R3","3"},{"R4","4"},{"R5","5"},{"R6","6"},{"R7","7"},{"R8","8"},{"R9","9"},{"R10","10"},{"R11","11"},{"R12","12"}
        ,{"R13","13"},{"R14","14"},{"R15","15"},{"SCREEN","16384"},{"KBD","24576"}};

    SymbolTable(){    
        for(String[] ele:predefined){
            map.put(ele[0],Integer.parseInt(ele[1]));
        }
    }

    public void addEntry(String symbol,int address){      
        map.put(symbol,address);
    }

    public boolean contains(String key){
        return map.containsKey(key);
    }

    public int getAddress(String key){
        return map.get(key);
    }
}