package me.tvojemama;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Main {

    public static final File
            resultFolder = new File("vysledky"),
            dataFolder = new File("data"),
            defaultFile = new File(Main.dataFolder.getPath() + "/default.txt");

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        System.out.println("Spouštím aplikaci s uživatelským rozhraním...");
        new GUI() {
            @Override
            public void onEnter(VypocetMereni vypocetMereni) {
                vypocetMereni.open();
                long ms = vypocetMereni.calculationNanos/1_000_000;
                String msStr = ms == 1 ? "milisekundu":(ms > 1 && ms < 5 ? "milisekundy":"milisekund");
                System.out.println("Vypočítáno za " + ms + " " + msStr + "!");
            }
        };
    }

    public static String read(File file) {
        try {
            FileReader r = new FileReader(file);
            int i;
            StringBuilder b = new StringBuilder();
            while ((i = r.read()) != -1) b.append((char) i);
            return b.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void write(File file, String s) {
        try {
            FileWriter w = new FileWriter(file);
            w.write(s);
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double calculate(String example, Param[] params) {
        String[] vars = new String[params.length];
        int i = 0;
        for(Param param : params) vars[i++] = param.name+"";
        Expression exp = new ExpressionBuilder(example).variables(vars).build();
        for(Param param : params) exp.setVariable(param.name+"", param.value);
        return exp.evaluate();
    }
}
