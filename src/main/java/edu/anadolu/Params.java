package edu.anadolu;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "number of depots", shortName = "d", longName = "depots", defaultValue = "1")
    int getNumDepots();

    @Option(description = "number of salesmen per depot", shortName = {"s"}, longName = {"salesmen", "vehicles"}, defaultValue = "1")
    int getNumSalesmen();

    @Option(description = "use city names when displaying/printing", shortName = "v", longName = "verbose")
    boolean getVerbose();

    @Option(description = "initial city/starting point", shortName = "i", longName = "initial city", defaultValue = "45")
    int getInitialValue();

    @Option(description = "if initial city is given is it changeable", shortName = "f", longName = "fixed hub", defaultValue = "true")
    boolean getFixedHub();

    @Option(description = "Nearest neighbour method running value",shortName = "nn",longName = "NN method value",defaultValue = "true")
    boolean getNNChoise();

    @Option(helpRequest = true, description = "display help", shortName = "h")
    boolean getHelp();

}
