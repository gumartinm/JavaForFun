package name.gumartinm.weather.information.model.currentweather;

import java.io.Serializable;

public class Snow implements Serializable {
    private static final long serialVersionUID = 6769716772818311879L;
    private Number three;

    public Number get3h() {
        return this.three;
    }

    public void set3h(final Number threeh) {
        this.three = threeh;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Snow [three=").append(this.three).append("]");
        return builder.toString();
    }
}
