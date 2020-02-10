package mijnlieff.bord.bordConfiguratie;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mijnlieff.StapVertaler;

public class BordConfigCompanion extends StapVertaler {
    private static String[][] bordConfig = new String[][]{
            { "", "", "", "", "*", "*", "*", "*", "*", "*", "*"},
            { "", "", "", "", "*", "*", "*", "*", "*", "*", "*"},
            { "", "", "", "", "*", "*", "*", "*", "*", "*", "*"},
            { "", "", "", "", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
            { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"}
    };

    private static int grootte = 151;

    private static final int[][] bord = new int[][]{
        {0, 1},
        {0, 1}
    };

    public TextField R1;
    public TextField C1;

    public TextField R2;
    public TextField C2;

    public TextField R3;
    public TextField C3;

    public TextField R4;
    public TextField C4;

    private TextField[][] textfields;

    private int[][] coordinaten = new int[4][4];

    public BordConfigCompanion(){
        initBordconfig();
    }

    public void initialize(){
        textfields = new TextField[][]{
                {R1, R2, R3, R4},
                {C1, C2, C3, C4}
        };
    }

    public static String[][] getBordConfig(){
        return bordConfig;
    }

    private void initBordconfig(){
        bordConfig = new String[][]{
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"},
                { "*", "*", "*", "*", "*", "*", "*", "*", "*", "*", "*"}
        };
    }

    public static int getGrootte(){
        return grootte;
    }

    //Dit bepaalt de grootte die de velden zal hebben
    public static void setGrootte(String config){
        int max = 0;
        boolean kolom = true;

        for (int i = 2; i < config.length(); i += 2) {
            int value = Integer.parseInt(config.charAt(i) + "");
            if (value > max){
                max = value;
                kolom = i % 4 == 0;
            }
        }

        //afhankelijk van de grootste meegegeven coördinaat wordt een grootte meegegeven
        // (er is ook een verschil tussen kolom en rij, er passen namelijk meer kolommen op een scherm dan rijen)
        if (kolom){
            grootte = max >= 9 ? 55
                    : max >= 7 ? 69
                    : max >= 5 ? 85
                    : max >= 4 ? 115
                    : 151;
        } else {
            grootte = max >= 9 ? 69
                    : max >= 7 ? 85
                    : max >= 5 ? 115
                    : 151;
        }
    }

    public void kies() {
        StringBuilder config = new StringBuilder("X");

        if(allDigits()) {
            boolean allGood = true;

            while (notContains(coordinaten[0], 0)){
                for (int i = 0; i < coordinaten[0].length; i++){
                    coordinaten[0][i] --;
                }
            }

            while (notContains(coordinaten[1], 0)){
                for (int i = 0; i < coordinaten[1].length; i++){
                    coordinaten[1][i] --;
                }
            }

            for (int i = 0; i < textfields[0].length; i++) {
                int r = coordinaten[0][i];
                int c = coordinaten[1][i];
                config.append(" ").append(r);
                config.append(" ").append(c);

                allGood = setBordConfig(r, c);
            }

            if (allGood){
                StapVertaler.getServer().stuurCommando(config.toString());
                setGrootte(config.toString());

                Stage stage = (Stage) R1.getScene().getWindow();
                stage.close();
            } else {
                initBordconfig();
                throwAlert("Er mag geen overlapping zijn tussen borden");
            }
        } else {
            throwAlert("Er mogen alleen getallen kleiner dan\n" +
                    "10 ingevoerd worden.");
        }
    }

    private boolean notContains(int[] arr, int targetValue) {
        boolean contains = false;
        int i = 0;

        while(i < arr.length && ! contains) {
            if(arr[i] == targetValue){
                contains = true;
            }
        }
        return !contains;
    }
    
    private boolean allDigits(){
        boolean allDigits = true;
        
        int i = 0;
        String rij;
        String kolom;
        
        while (allDigits && i < textfields[0].length) {
            rij = textfields[0][i].getText();
            kolom = textfields[1][i].getText();

            if (! rij.chars().allMatch(Character::isDigit) 
                    || ! kolom.chars().allMatch(Character::isDigit) 
                    || rij.equals("") 
                    || kolom.equals("")) {
                allDigits = false;
            } else {
                coordinaten[0][i] = Integer.parseInt(rij);
                coordinaten[1][i] = Integer.parseInt(kolom);

                if(coordinaten[0][i] > 9 || coordinaten[1][i] > 9){
                    allDigits = false;
                }
            }
            
            i ++;
        }
        return allDigits;
    }

    private void throwAlert(String msg){
        Alert dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle("Mijnlieff");
        dialog.setHeaderText(msg);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));

        dialog.showAndWait();
    }

    public void cancel() {
        StapVertaler.getServer().sluitServer();
        System.exit(0);
    }

    @Override
    public void vertaalAntwoord(String stap) {
        boolean allGood = true;
        int kolom = 0;
        int rij = 0;

        setGrootte(stap);

        for (int i = 2; i < stap.length(); i += 2) {
            int value = Integer.parseInt(stap.charAt(i) + "");

            if(i % 4 == 0) {
                coordinaten[1][kolom] = value;
                kolom ++;
            } else {
                coordinaten[0][rij] = value;
                rij ++;
            }
        }
        if (notContains(coordinaten[0], 0)
                || notContains(coordinaten[1], 0)){
            allGood = false;
        }

        for (int i = 0; i < stap.length() - 2; i++) {
            int r = coordinaten[0][i];
            int c = coordinaten[1][i];

            allGood = setBordConfig(r, c);
        }

        if (! allGood){
            throwAlert("De tegenstander speelt vals,\n" +
                    "spel wordt afgesloten.");
            StapVertaler.getServer().stuurCommando("Q");
            System.exit(0);
        }
    }

    private boolean setBordConfig(int rij, int kolom) {
        boolean allGood = true;

        for (int j : bord[0]) {
            for (int k : bord[1]) {
                if (!bordConfig[rij + j][kolom + k].equals("")) {
                    bordConfig[rij + j][kolom + k] = "";
                } else {
                    allGood = false;
                }
            }
        }

        return allGood;
    }
}
