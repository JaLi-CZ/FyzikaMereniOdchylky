package me.tvojemama;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public abstract class GUI extends JFrame {

    public static final String defaultVzorec = "a";
    public static final int defaultPlatnaMista = 2;

    public static char[] promenneArr = null;
    public static String promenne = null;
    public static int varsCount = -1;

    private static final Color bg = Color.BLACK, btnBg = new Color(30, 30, 30);
    private static final Font font = new Font("", Font.PLAIN, 24);

    public static JTextField vzorecField = null;
    public static JTextField beforeField = null;
    public static JTextField afterField = null;

    private JLabel vecLabel = null;
    private final JLabel hodnotyL;
    private final JLabel vzorecL;
    private final JTextField hodnotyF;

    public GUI() {

        JPanel panel = new JPanel();
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setLayout(new GridLayout(12, 1, 5, 5));

        JPanel buttons = new JPanel();
        buttons.setBackground(bg);
        buttons.setLayout(new GridLayout(1, 3, 10, 10));

        JButton priruckaB = createButton("Příručka", Color.YELLOW, buttons);
        JButton saveDefaultB = createButton("Uložit jako výchozí", Color.GREEN, buttons);
        JButton deleteB = createButton("Smazat vše", Color.RED, buttons);
        JButton openB = createButton("Otevřít", Color.CYAN, buttons);

        JComponent[] hodnotyArr = createField("Naměřené hodnoty:", panel);
        hodnotyF = (JTextField) hodnotyArr[0];
        hodnotyL = (JLabel) hodnotyArr[1];
        JComponent[] vzorecComp = createField("Vzorec:", panel);
        JTextField vzorecHodnotyF = (JTextField) vzorecComp[0];
        vzorecField = vzorecHodnotyF;
        vzorecL = (JLabel) vzorecComp[1];
        JTextField velicinaF = (JTextField) createField("Značení veličiny (l = délka, m = hmotnost, ...):", panel)[0];
        JTextField velicina4pF = (JTextField) createField("Název veličiny (4. pád - koho co):", panel)[0];
        JTextField jednotkaF = (JTextField) createField("Značení jednotky (kg, km, cm, g/cm3, m2, ...):", panel)[0];
        JTextField vecF = (JTextField) createField("Měřená věc (2. pád - bez koho čeho/!):", panel)[0];
        JTextField meridloF = (JTextField) createField("Měřidlo (4. pád - koho co): ", panel)[0];
        JTextField autorF = (JTextField) createField("Jméno autora/autorů (oddělte čárkou nebo 'a'):", panel)[0];
        JTextField platnaMistaF = (JTextField) createField("Zaokruhlení - počet platných číslic:", panel)[0];
        JTextField prefix = (JTextField) createField("Text před závěrem:", panel)[0];
        beforeField = prefix;
        JTextField suffix = (JTextField) createField("Text po závěru:", panel)[0];
        afterField = suffix;

        panel.add(buttons);

        platnaMistaF.setText(defaultPlatnaMista+"");
        vzorecHodnotyF.setText(defaultVzorec);

        add(panel);

        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Měření odchylky - Program");
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(Main.dataFolder.getPath() + "/AppIcon.png").getImage());
        setVisible(true);


        priruckaB.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File(Main.dataFolder.getPath() + "/prirucka/prirucka.html"));
            } catch (Exception ignored) {}
        });

        vzorecField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                vzorecField.setForeground(Color.WHITE);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                vzorecField.setForeground(Color.WHITE);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                vzorecField.setForeground(Color.WHITE);
            }
        });

        deleteB.addActionListener(e -> {
            hodnotyF.setText("");
            vzorecHodnotyF.setText(defaultVzorec);
            velicinaF.setText("");
            velicina4pF.setText("");
            jednotkaF.setText("");
            vecF.setText("");
            meridloF.setText("");
            autorF.setText("");
            platnaMistaF.setText(defaultPlatnaMista+"");
            prefix.setText("");
            suffix.setText("");
        });

        hodnotyF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                double[][] values = toValues();
                setHodnotyFieldLabel(values);
                hodnotyF.setForeground(values == null ? Color.RED : Color.GREEN);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                double[][] values = toValues();
                setHodnotyFieldLabel(values);
                hodnotyF.setForeground(values == null ? Color.RED : Color.GREEN);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                double[][] values = toValues();
                setHodnotyFieldLabel(values);
                hodnotyF.setForeground(values == null ? Color.RED : Color.GREEN);
            }
        });

        velicinaF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String vel = getVelicina(velicinaF.getText());
                if(vel != null) {
                    velicina4pF.setText(vel);
                    velicinaF.setForeground(Color.GREEN);
                } else velicinaF.setForeground(Color.YELLOW);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                String vel = getVelicina(velicinaF.getText());
                if(vel != null) {
                    velicina4pF.setText(vel);
                    velicinaF.setForeground(Color.GREEN);
                } else velicinaF.setForeground(Color.YELLOW);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                String vel = getVelicina(velicinaF.getText());
                if(vel != null) {
                    velicina4pF.setText(vel);
                    velicinaF.setForeground(Color.GREEN);
                } else velicinaF.setForeground(Color.YELLOW);
            }
        });

        autorF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(autorF.getText().contains(",") || autorF.getText().contains(" a ")) autorF.setForeground(Color.GREEN);
                else autorF.setForeground(Color.WHITE);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(autorF.getText().contains(",") || autorF.getText().contains(" a ")) autorF.setForeground(Color.GREEN);
                else autorF.setForeground(Color.WHITE);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                if(autorF.getText().contains(",") || autorF.getText().contains(" a ")) autorF.setForeground(Color.GREEN);
                else autorF.setForeground(Color.WHITE);
            }
        });

        platnaMistaF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    int val;
                    val = Integer.parseInt(platnaMistaF.getText());
                    platnaMistaF.setForeground(val < 0 ? Color.RED : Color.GREEN);
                } catch (Exception ex) {
                    platnaMistaF.setForeground(Color.RED);
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    int val;
                    val = Integer.parseInt(platnaMistaF.getText());
                    platnaMistaF.setForeground(val < 0 ? Color.RED : Color.GREEN);
                } catch (Exception ex) {
                    platnaMistaF.setForeground(Color.RED);
                }
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    int val;
                    val = Integer.parseInt(platnaMistaF.getText());
                    platnaMistaF.setForeground(val < 0 ? Color.RED : Color.GREEN);
                } catch (Exception ex) {
                    platnaMistaF.setForeground(Color.RED);
                }
            }
        });

        vecF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(vecF.getText().startsWith("!")) vecLabel.setText("Měřená věc (4. pád - koho co):");
                else vecLabel.setText("Měřená věc (2. pád - bez koho čeho/!):");
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if(vecF.getText().startsWith("!")) vecLabel.setText("Měřená věc (4. pád - koho co):");
                else vecLabel.setText("Měřená věc (2. pád - bez koho čeho/!):");
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                if(vecF.getText().startsWith("!")) vecLabel.setText("Měřená věc (4. pád - koho co):");
                else vecLabel.setText("Měřená věc (2. pád - bez koho čeho/!):");
            }
        });

        openB.addActionListener(e -> {
            boolean calculating = false;
            String vzorec = null;
            try {
                double[][] values = toValues();
                if(values == null) throw new Exception("Hodnoty jsou špatně zapsané");
                double[] finalValues = new double[values.length];
                vzorec = vzorecField.getText();
                int i = 0;
                for(double[] vals: values) {
                    Param[] params = new Param[vals.length];
                    int j = 0;
                    for(double val: vals) {
                        params[j] = new Param((char) ('a'+j), val);
                        j++;
                    }
                    calculating = true;
                    finalValues[i] = Main.calculate(vzorec, params);
                    calculating = false;
                    vzorecField.setForeground(Color.WHITE);
                    i++;
                }

                int places = Integer.parseInt(platnaMistaF.getText());
                if(places < 0) throw new Exception("Nelze zakruhlit na " + places + " platných míst!");

                onEnter(new VypocetMereni(finalValues, velicinaF.getText(), jednotkaF.getText(), places, velicina4pF.getText(),
                        vecF.getText(), meridloF.getText(), autorF.getText()));
            } catch (Exception ex) {
                if(calculating) {
                    System.out.println("Vzorec '" + vzorec + "' je špatně zapsaný, buďto chybí proměnná nebo nesprávná syntaxe (" + ex.getMessage()+ ")");
                    vzorecField.setForeground(Color.RED);
                } else System.out.println("Něco se pokazilo při výpočtu (" + ex.getMessage()+ ")");
            }
        });

        saveDefaultB.addActionListener(e -> {
            String data = hodnotyF.getText() + "\n" +
                    vzorecHodnotyF.getText() + "\n" +
                    velicinaF.getText() + "\n" +
                    velicina4pF.getText() + "\n" +
                    jednotkaF.getText() + "\n" +
                    vecF.getText() + "\n" +
                    meridloF.getText() + "\n" +
                    autorF.getText() + "\n" +
                    platnaMistaF.getText() + "\n" +
                    prefix.getText() + "\n" +
                    suffix.getText() + "\n";
            Main.write(Main.defaultFile, data);
        });

        KeyListener listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) openB.doClick();
                else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
                else if(e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                    try {
                        if(Main.defaultFile.exists()){
                            String[] data = Main.read(Main.defaultFile).split("\n", -1);
                            hodnotyF.setText(data[0]);
                            vzorecHodnotyF.setText(data[1]);
                            velicinaF.setText(data[2]);
                            velicina4pF.setText(data[3]);
                            jednotkaF.setText(data[4]);
                            vecF.setText(data[5]);
                            meridloF.setText(data[6]);
                            autorF.setText(data[7]);
                            platnaMistaF.setText(data[8]);
                            prefix.setText(data[9]);
                            suffix.setText(data[10]);
                        }
                    } catch (Exception ignored) { }
                }
                else if(e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) saveDefaultB.doClick();
                else if(e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) deleteB.doClick();
                else if(e.getKeyCode() == KeyEvent.VK_F1) priruckaB.doClick();
            }
        };

        hodnotyF.addKeyListener(listener);
        vzorecHodnotyF.addKeyListener(listener);
        velicinaF.addKeyListener(listener);
        velicina4pF.addKeyListener(listener);
        jednotkaF.addKeyListener(listener);
        vecF.addKeyListener(listener);
        meridloF.addKeyListener(listener);
        autorF.addKeyListener(listener);
        platnaMistaF.addKeyListener(listener);
        openB.addKeyListener(listener);
        deleteB.addKeyListener(listener);
        saveDefaultB.addKeyListener(listener);
        prefix.addKeyListener(listener);
        suffix.addKeyListener(listener);

        try {
            if(Main.defaultFile.exists()){
                String[] data = Main.read(Main.defaultFile).split("\n");
                hodnotyF.setText(data[0]);
                vzorecHodnotyF.setText(data[1]);
                velicinaF.setText(data[2]);
                velicina4pF.setText(data[3]);
                jednotkaF.setText(data[4]);
                vecF.setText(data[5]);
                meridloF.setText(data[6]);
                autorF.setText(data[7]);
                platnaMistaF.setText(data[8]);
                prefix.setText(data[9]);
                suffix.setText(data[10]);
            }
        } catch (Exception ignored) { }
    }

    private void setHodnotyFieldLabel(double[][] values) {
        if(values == null) {
            hodnotyL.setText("Naměřené hodnoty:");
            return;
        }
        String s = values.length==0||values[0].length==1?"":", " + values[0].length;
        hodnotyL.setText("Naměřené hodnoty (" + values.length + s + "):");
    }

    private double[][] toValues() { // max 26
        try {
            String data = hodnotyF.getText().replaceAll(",", ".").replaceAll(" {2}"," ");
            String[] args = data.split(" ");
            String[][] vars = new String[args.length][];
            int maxVarsCount = -1, i = 0;
            for(String arg: args) {
                vars[i] = arg.split(";");
                int count = vars[i].length;
                if(count > maxVarsCount) maxVarsCount = count;
                i++;
            }
            if(maxVarsCount == -1) return null;
            if(maxVarsCount > 26) maxVarsCount = 26;
            double[][] values = new double[args.length][maxVarsCount];
            i = 0;
            for(String[] vals: vars) {
                int j = 0;
                for(String val: vals) if(j < 26) values[i][j++] = Double.parseDouble(val);
                while (j < maxVarsCount) values[i][j++] = 1;
                i++;
            }
            char[] arr = new char[maxVarsCount];
            StringBuilder inputVars = new StringBuilder();
            for(i=0; i<maxVarsCount; i++) {
                char c = (char) ('a'+i);
                arr[i] = c;
                inputVars.append(c);
                if(i != maxVarsCount-1) inputVars.append(";");
            }
            promenneArr = arr;
            promenne = inputVars.toString();
            varsCount = maxVarsCount;
            vzorecL.setText("Vzorec (vstupní proměnné: " + promenne + "):");
            return values;
        } catch (Exception e) {
            return null;
        }
    }

    private JComponent[] createField(String label, JPanel parent) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout(8, 8));
        p.setBackground(bg);

        JLabel l = new JLabel(label);
        JTextField f = new JTextField();

        l.setForeground(Color.WHITE);
        l.setFont(font);

        f.setCaretColor(new Color(255, 72, 0));
        f.setBorder(new LineBorder(Color.ORANGE));
        f.setBackground(btnBg);
        f.setForeground(Color.WHITE);
        f.setHorizontalAlignment(SwingConstants.CENTER);
        f.setFont(font);

        p.add(l, BorderLayout.WEST);
        p.add(f, BorderLayout.CENTER);
        parent.add(p);
        if(label.contains("věc")) vecLabel = l;
        return new JComponent[]{f, l};
    }

    private static JButton createButton(String text, Color col, JPanel panel) {
        JButton b = new JButton();
        b.setText(text);
        b.setBorder(new LineBorder(col));
        b.setBackground(btnBg);
        b.setForeground(col.darker());
        b.setFont(font);
        panel.add(b);
        return b;
    }

    public abstract void onEnter(VypocetMereni vypocetMereni);

    public String getVelicina(String zkratka) {
        String velicina4p = null;
        switch (zkratka) {
            case "L", "l" -> velicina4p = "délku";
            case "S", "s" -> velicina4p = "dráhu";
            case "t" -> velicina4p = "čas";
            case "T" -> velicina4p = "teplota";
            case "v" -> velicina4p = "rychlost";
            case "M", "m" -> velicina4p = "hmotnost";
            case "V" -> velicina4p = "objem";
            case "ρ", "Ρ", "pp", "PP" -> velicina4p = "hustotu";
            case "F", "f" -> velicina4p = "sílu";
            case "p" -> velicina4p = "tlak";
            case "I", "i" -> velicina4p = "elektrický proud";
            case "U", "u" -> velicina4p = "elektrické napětí";
            case "E", "e" -> velicina4p = "energii";
            case "Q", "q" -> velicina4p = "teplo";
            case "W", "w" -> velicina4p = "práci";
            case "P" -> velicina4p = "výkon";
        }
        return velicina4p;
    }
}
