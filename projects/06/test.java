
public class test {
    public static void main(String[] args) {
        String input = " 0;JMP ";
        input = input.trim();
        input = input.replace(" ", "");
        System.out.println(input.split("[;]")[1]);

        // System.out.println(Integer.toBinaryString(2).length());
    }    
}
