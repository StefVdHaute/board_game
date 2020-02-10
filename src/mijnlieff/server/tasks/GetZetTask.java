package mijnlieff.server.tasks;

import javafx.concurrent.Task;
import mijnlieff.StapVertaler;

public class GetZetTask extends Task<String> {
    private StapVertaler vertaler;

    public GetZetTask(StapVertaler vertaler, String commando){
        this.vertaler = vertaler;

        StapVertaler.getServer().stuurCommando(commando);
    }

    public GetZetTask(StapVertaler vertaler){
        this.vertaler = vertaler;
    }

    @Override
    protected String call(){
        return StapVertaler.getServer().getAntwoord();
    }

    @Override
    protected void succeeded(){
        vertaler.vertaalAntwoord(getValue());
    }

    @Override
    protected void failed(){

    }
}