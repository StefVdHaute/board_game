package mijnlieff.loading;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mijnlieff.StapVertaler;

public class LoadingCompanion {
    public ImageView loadingGif;

    public void initialize(){
        loadingGif.setImage(new Image("mijnlieff/loading/rubiks.gif"));
    }

    public void cancel() {
        StapVertaler.getServer().sluitServer();
        System.exit(0);
    }
}
