package de.remote.agents;

public class SnakeEyesImpl implements SnakeEyes {

    private String canon;
    private String name;

    public SnakeEyesImpl() {
    };

    public SnakeEyesImpl(final String canon, final String name) {
        this.canon = canon;
        this.name = name;
    }

    @Override
    public String getCanon() {
        return this.canon;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setCanon(final String canon) {
        this.canon = canon;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
