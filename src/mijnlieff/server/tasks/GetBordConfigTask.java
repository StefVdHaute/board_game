package mijnlieff.server.tasks;

import javafx.concurrent.Task;
import javafx.stage.Stage;
import mijnlieff.StapVertaler;
import mijnlieff.bord.bordConfiguratie.BordConfigCompanion;

public class GetBordConfigTask extends Task<String> {
    private BordConfigCompanion bordConfigCompanion;
    private Stage loadingStage;

    public GetBordConfigTask(BordConfigCompanion bordConfigCompanion, Stage loadingStage){
        this.bordConfigCompanion = bordConfigCompanion;
        this.loadingStage = loadingStage;
    }

    @Override
    protected String call() {
        return StapVertaler.getServer().getAntwoord();
    }

    @Override
    protected void succeeded() {
        loadingStage.close();
        bordConfigCompanion.vertaalAntwoord(getValue());
    }

    @Override
    protected void failed() {

    }
}
