package control;

import java.util.regex.Pattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;

public class CFTextField extends TextField {
    private StringProperty name = new SimpleStringProperty();
    private BooleanProperty required = new SimpleBooleanProperty(false);
    private Pattern format;
    private StringProperty message = new SimpleStringProperty();
    
    public CFTextField(String name) {
        this(name, "");
    }
    
    public CFTextField(String name, String text) {
        this.name.set(name);
        this.setText(text);
    }

    public boolean isCorrectPresence() {
        if (!required.get()) return true;
        return required.get() && (getText() != null && !getText().isEmpty());
    }

    public boolean isCorrectFormat() {
        if (format == null) return true;
        return format.matcher(getText().trim()).matches();
    }

    public String getName() { return this.name.get(); }

    public void setName(String name) { this.name.set(name); }

    public boolean getRequired() { return this.required.get(); }

    public void setRequired(boolean required) { this.required.set(required); }

    public String getFormat() { return format.toString(); }

    public void setFormat(String format) { this.format = Pattern.compile(format); }

    public String getMessage() { return this.message.get(); }

    public void setMessage(String message) { this.message.set(message); }
}
