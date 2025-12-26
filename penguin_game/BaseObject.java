package penguin_game;

public class BaseObject {

    public final String type;

    public BaseObject() {
        super();
        this.type = this.getClass().getName();
    }
}
