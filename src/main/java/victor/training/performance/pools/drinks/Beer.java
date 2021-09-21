package victor.training.performance.pools.drinks;

import lombok.Data;

@Data
public class Beer {
    private final String type ;
    private final String ice;


    public Beer(String blond) {type= blond;ice = null;}

    public Beer(String type, String ice) {
        this.type = type;
        this.ice = ice;
    }

    public Beer addIce() {
        return new Beer(type, "RECE");
    }
}
