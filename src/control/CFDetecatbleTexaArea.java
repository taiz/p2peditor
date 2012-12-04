package control;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

public class CFDetecatbleTexaArea extends TextArea {
    protected final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    protected final List<Detector> detectors = new ArrayList<Detector>();

    public CFDetecatbleTexaArea() {
        this("");
    }

    public CFDetecatbleTexaArea(String text) {
        super(text);

        setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                Detector detector = new Detector();
                addDetector(detector);
                new Thread(detector).start();
            }
        });
    }

    public void addListener(ChangeListener<String> listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener<String> listener) {
        listeners.remove(listener);
    }

    protected final static int DEFAULT_INTERVAL = 1;
    private int interval = DEFAULT_INTERVAL;       // sec

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return this.interval;
    }

    protected synchronized void notifyListeners(String oldText) {
        for (ChangeListener<String> listener : listeners)
            listener.changed(this.textProperty(), oldText, this.getText());
    }

    protected synchronized void addDetector(Detector detector) {
        detectors.add(detector);
    }

    protected synchronized void clearDetector() {
        detectors.clear();
    }

    protected synchronized boolean isEndOfDetectors(Detector detector) {
        return detectors.lastIndexOf(detector) == detectors.size() - 1;
    }

    protected class Detector implements Runnable {
        private final String text;

        protected Detector() {
            this.text = getText();
        }

        @Override
        public void run() {
            try {
                Thread.sleep(interval * 1000l);
                if (isEndOfDetectors(this)) {
                    notifyListeners(this.text);
                    clearDetector();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
