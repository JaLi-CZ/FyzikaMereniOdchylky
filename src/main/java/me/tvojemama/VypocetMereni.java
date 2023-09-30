package me.tvojemama;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

public class VypocetMereni {

    private long calculationStartNanos;
    public long calculationNanos;

    public double[] mereni, rozdily, absolutniRozdily;
    public int pocetMereni, validPlaces;
    public double soucetMereni = 0, prumerMereni, soucetRozdilu = 0, prumerRozdilu, soucetAbsolutnichRozdilu = 0, odchylka, odchylkaProcenta;
    public String jmenoStudenta, vysledek, velicinaZkratka, jednotka, koncovka = "";
    public String velicinaNazev4p, merenyPredmet2p, meridlo4p;

    public VypocetMereni(double[] mereni, String velicinaZkratka, String jednotka, int validPlaces) {
        calculationStartNanos = System.nanoTime();
        this.velicinaZkratka = velicinaZkratka;
        this.jednotka = jednotka;
        this.validPlaces = validPlaces;
        this.mereni = mereni;
        pocetMereni = mereni.length;
        for(double d: mereni) soucetMereni += d;
        prumerMereni = soucetMereni/pocetMereni;

        rozdily = new double[pocetMereni];
        absolutniRozdily = new double[pocetMereni];
        int i = 0;
        for(double d: mereni) {
            double rozdil = d - prumerMereni, absRozdil = Math.abs(rozdil);
            rozdily[i] = rozdil;
            absolutniRozdily[i] = absRozdil;
            soucetRozdilu += rozdil;
            soucetAbsolutnichRozdilu += absRozdil;
            i++;
        }
        prumerRozdilu = soucetRozdilu/pocetMereni;
        odchylka = soucetAbsolutnichRozdilu/pocetMereni;
        odchylkaProcenta = odchylka/prumerMereni;

        vysledek = velicinaZkratka + " = (" + prumerMereni + " ± " + odchylka + ") " + jednotka;
    }

    public VypocetMereni(double[] mereni, String velicinaZkratka, String jednotka, int validPlaces, String velicinaNazev4p,
                         String merenyPredmet2p, String meridlo4p, String jmenoStudenta) {
        this(mereni, velicinaZkratka, jednotka, validPlaces);
        this.jmenoStudenta = jmenoStudenta;
        this.merenyPredmet2p = merenyPredmet2p;
        this.velicinaNazev4p = velicinaNazev4p;
        this.meridlo4p = meridlo4p;
    }

    public void open() {
        try {
            final String htmlContent = toHtml();
            File file = getHtmlFile(jmenoStudenta);
            if(file == null) return;
            Main.write(file, htmlContent);
            System.out.println("Otevírám " + file.getPath() + " v prohlížeči...");
            long currNano = System.nanoTime();
            calculationNanos = currNano-calculationStartNanos;
            calculationStartNanos = currNano;
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toHtml() {
        String[] dateTime = getDateTime();
        String date = dateTime[0], time = dateTime[1];
        String content = Main.read(new File(Main.dataFolder.getPath() + "/index.html"));
        if(content == null) return null;
        if(jmenoStudenta != null) content = content.replaceAll("%STUDENT%", jmenoStudenta);
        if(date != null) content = content.replaceAll("%DATE%", date);
        if(time != null) content = content.replaceAll("%TIME%", time);
        String cislic = validPlaces == 1 ? "platnou čílici":(validPlaces >= 2 && validPlaces <= 4 ?
                "platné číslice":"platných číslic");
        content = content.replaceAll("%ZAOKRUHLENI%", validPlaces + " " + cislic);
        content = content.replaceAll("%SOUCET-MERENI%", trim(round(soucetMereni, validPlaces+8)+""));
        content = content.replaceAll("%SOUCET-ROZDILU%", trim(round(soucetRozdilu, validPlaces+8)+""));
        content = content.replaceAll("%SOUCET-ABSOLUTNICH-ROZDILU%", trim(round(soucetAbsolutnichRozdilu, validPlaces+8)+""));
        content = content.replaceAll("%PRUMER-MERENI%", trim(round(prumerMereni, validPlaces+8)+""));
        content = content.replaceAll("%PRUMER-ROZDILU%", trim(round(prumerRozdilu, validPlaces+8)+""));
        content = content.replaceAll("%PRUMER-ABSOLUTNICH-ROZDILU%", trim(round(odchylka, validPlaces+8)+""));
        if(velicinaZkratka != null) content = content.replaceAll("%ZKRATKA-VELICINY%", velicinaZkratka);
        String chyba = round(odchylka);
        int dot = chyba.indexOf(",");
        String prumer = roundOnDecimalPlaces(prumerMereni, dot == -1 ? 0 : chyba.length()-dot-1);
        content = content.replaceAll("%ODCHYLKA%", chyba);
        content = content.replaceAll("%PRUMER%", validPlaces == 0 ? "0" : prumer);
        if(jednotka != null) content = content.replaceAll("%JEDNOTKA%", jednotka);
        content = content.replaceAll("%ODCHYLKA-PROCENTA-CISLO%", round(odchylkaProcenta, validPlaces+1));
        content = content.replaceAll("%ODCHYLKA-PROCENTA%", round(odchylkaProcenta*100, validPlaces));

        String jm = jmenoStudenta.toLowerCase(), jsem = "jsem";
        if(jm.endsWith("á") || jm.contains("á ") || (jm.contains("viktor") && jm.contains("vaníček"))) koncovka = "a";
        if(jmenoStudenta.contains(",") || jmenoStudenta.contains(" a ")) {
            koncovka = "i";
            jsem = "jsme";
        }
        String vecFraze = merenyPredmet2p.startsWith("!") ? merenyPredmet2p.substring(1) : velicinaNazev4p + " " + merenyPredmet2p;
        String vecFrazeCap = vecFraze.substring(0, 1).toUpperCase() + vecFraze.substring(1);
        String hodnot = pocetMereni == 1 ? "hodnotu" : (pocetMereni >= 2 && pocetMereni <= 4 ? "hodnoty" : "hodnot");
        String jaVim = koncovka.equals("i") ? "My víme, na měření jsme lopaty" : "Já vím, na měření jsem lopata", semLopata = "";
        if(odchylkaProcenta >= 0.1) semLopata = "<br>" + jaVim + " :(";

        content = content.replaceAll("%JSEM%", jsem);
        content = content.replaceAll("%MERIDLO%", meridlo4p);
        content = content.replaceAll("%HODNOT%", pocetMereni + " " + hodnot);
        content = content.replaceAll("%VEC%", vecFraze);
        content = content.replaceAll("%VEC-FIRST-CAP%", vecFrazeCap);
        content = content.replaceAll("%KRITIKA%", semLopata);

        String vzorec = GUI.vzorecField.getText();
        if(vzorec.replaceAll(" ", "").equals(GUI.defaultVzorec)) vzorec = null;
        content = content.replaceAll("%VZOREC%", vzorec == null ? "":"Všechny naměřené vstupní hodnoty (" + GUI.promenne + ") " + jsem +
                " upravil" + koncovka + " vzorcem: finální hodnota = <u>" + vzorec + "</u>");

        String ktere = pocetMereni == 1 ? "kterou" : "které";
        String vzorec2 = ",<br>" + ktere + " " + jsem + " upravil" + koncovka + " pomocí vzorce: finální hodnota = <u>" + vzorec + "</u>";
        content = content.replaceAll("%VZOREC2%", vzorec == null ? "":vzorec2);

        content = content.replaceAll("%KONCOVKA%", koncovka);

        StringBuilder rows = new StringBuilder();
        String rowStructure = Main.read(new File(Main.dataFolder.getPath() + "/row-structure.txt"));
        int i = 0;
        for(double hodnota: mereni) {
            double rozdil = rozdily[i], absRozdil = absolutniRozdily[i];
            if(rowStructure != null) rows.append(rowStructure
                    .replaceFirst("%CISLO-MERENI%", ++i+"")
                    .replaceFirst("%HODNOTA%", trim(round(hodnota, validPlaces+6)))
                    .replaceFirst("%ROZDIL%", trim(round(rozdil, validPlaces+6)))
                    .replaceFirst("%ABSOLUTNI-ROZDIL%", trim(round(absRozdil, validPlaces+6))))
                    .append("\n");
        }

        String before = GUI.beforeField.getText();
        String after = GUI.afterField.getText();
        content = content.replaceAll("%BEFORE%", before);
        content = content.replaceAll("%AFTER%", after);

        content = content.replaceAll("%ROWS%", rows.toString());
        return content;
    }

    private String round(double num) {
        return round(num, validPlaces);
    }

    @Override
    public String toString() {
        return "Result{" +
                "mereni=" + Arrays.toString(mereni) +
                ", rozdily=" + Arrays.toString(rozdily) +
                ", absolutniRozdily=" + Arrays.toString(absolutniRozdily) +
                ", pocetMereni=" + pocetMereni +
                ", soucetMereni=" + soucetMereni +
                ", prumerMereni=" + prumerMereni +
                ", soucetRozdilu=" + soucetRozdilu +
                ", prumerRozdilu=" + prumerRozdilu +
                ", soucetAbsolutnichRozdilu=" + soucetAbsolutnichRozdilu +
                ", odchylka=" + odchylka +
                ", vysledek='" + vysledek + '\'' +
                '}';
    }

    public static String trim(String num) {
        if(num == null) return null;
        boolean neg = num.startsWith("-");
        if(neg) num = num.substring(1);
        String prefix = neg ? "-":"";
        int dot = -1, i = 0;
        StringBuilder res = new StringBuilder();
        boolean wasValid = false;
        for(char c: num.toCharArray()) {
            if(c == ',') {
                dot = i;
                break;
            }
            if(!wasValid && c >= '1' && c <= '9') wasValid = true;
            if(wasValid) res.append(c);
            i++;
        }
        wasValid = false;
        if(res.length() == 0) res = new StringBuilder("0");
        if(dot == -1) return prefix + res;
        StringBuilder end = new StringBuilder();
        for(i=num.length()-1; i >= 0; i--) {
            char c = num.charAt(i);
            if(c == ',') break;
            if(!wasValid && c >= '1' && c <= '9') wasValid = true;
            if(wasValid) end.insert(0, c);
        }
        if(end.length() == 0) return prefix + res;
        return prefix + res + "," + end;
    }
    private static String roundOnDecimalPlaces(double num, int decPlaces) {
        String res = new DecimalFormat("0." + "0".repeat(decPlaces)).format(num);
        if(res.endsWith(",")) res = res.substring(0, res.length()-1);
        return res;
    }
    public static String round(double num, int significantDigits) {
        if(significantDigits == 0) return "0";
        BigDecimal bd = new BigDecimal(num, MathContext.DECIMAL64);
        bd = bd.round(new MathContext(significantDigits, RoundingMode.HALF_UP));
        final int precision = bd.precision();
        if(precision < significantDigits) bd = bd.setScale(bd.scale() + (significantDigits-precision));
        String res = bd.toPlainString().replaceFirst("\\.", ",");
        if(res.startsWith("0,000000000") || res.startsWith("-0,000000000")) return "0";
        return res;
    }
    private static File getHtmlFile(String authors) {
        if(!Main.resultFolder.exists()) Main.resultFolder.mkdirs();
        int id = 1;
        File file;
        System.out.println(isValidFileName(authors) + ": " +authors);
        if(authors == null || authors.isEmpty() || !isValidFileName(authors))
            while ((file = getBasicFile(id)).exists()) id++;
        else while ((file = getFile(id, authors)).exists()) id++;
        return file;
    }

    private static File getBasicFile(int id) {
        return new File(Main.resultFolder.getPath() + "/" + id + ". Měřící Protokol.html");
    }

    private static File getFile(int id, String authors) {
        return new File(Main.resultFolder.getPath() + "/Měřící Protokol " + (id==1?"":"("+id+")") + " - " + authors + ".html");
    }

    private static boolean isValidFileName(String fileName) {
        File f = new File(fileName);
        try {
            if(f.createNewFile()) {
                f.delete();
                return true;
            }
        } catch (Exception ignored) { }
        return false;
    }

    private static String[] getDateTime() {
        LocalDateTime t = LocalDateTime.now();
        int year = t.getYear(), month = t.getMonthValue(), day = t.getDayOfMonth();
        int hour = t.getHour(), min = t.getMinute(), sec = t.getSecond();
        String hourS = hour < 10 ? "0":"", minS = min < 10 ? "0":"", secS = sec < 10 ? "0":"";
        String date = day + "." + month + "." + year;
        String time = hourS + hour + ":" + minS + min + ":" + secS + sec;
        return new String[]{date, time};
    }
}
