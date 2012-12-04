package control;

import synchronizer.ConnectInfo;
import synchronizer.SynchroManager;
import synchronizer.SynchronizerException;
import control.CFTableView.CFTableColumn;
import layout.CFHForm;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorBuilder;
import javafx.scene.control.Tab;
import javafx.scene.control.TabBuilder;
import javafx.scene.control.TabPaneBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;

public class CFSyncTextArea extends BorderPane implements CFSyncTextControl {
    private final SynchroManager manager;

    //Debug
    public CFSyncTextArea(String myport, String myname, String coport) {
        this();
        serviceForm.port.setText(myport);
        serviceForm.name.setText(myname);
        connectForm.host.setText("localhost");
        connectForm.port.setText(coport);
    }
    public CFSyncTextArea() {
        buildLayout();
        manager = new SynchroManager(this);
        textArea.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldText, String newText) {
                sendSyncText(newText);
            }
        });
    }

    protected void startService(Map<String, String> params) {
        showMessage("Starting service ...", false);
        try {
            // Start service
            Integer port = Integer.parseInt(params.get("port"));
            manager.startService(new ConnectInfo(port, params.get("name")));
            // Change display
            serviceForm.setDisableInputs(true);
            connectForm.setDisable(false);
            showMessage("Service started.", true);
            // CloseOperation
            this.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    onClosed();
                }
                
            });
        } catch (SynchronizerException ex) {
            showMessage("Failed to start service. (" + ex.getMessage() + ")", false);
        }
    }
    
    protected void onClosed() {
        if (!manager.isRunnnig()) return;
        try {
            manager.stopService();
        } catch (SynchronizerException ex) {
            showMessage("Failed to stop service. (" + ex.getMessage() + ")", false);
        }
    }

    protected void stopService() {
        showMessage("Stopping service ...", false);
        try {
            // Stop service
            manager.stopService();
            // Change display
            serviceForm.setDisableInputs(false);
            connectForm.setDisable(true);
            showMessage("Service stopped.", true);
        } catch (SynchronizerException ex) {
            showMessage("Failed to stop service. (" + ex.getMessage() + ")", false);
        }
    }
    
    protected void connect(Map<String, String> params) {
        String host = params.get("host");
        Integer port = Integer.parseInt(params.get("port"));
        showMessage("Connecting to " + host + ":" + port + ".", true);
        try {
            ConnectInfo info = new ConnectInfo(host, port);
            manager.connect(info);
            addConnection(info);
            connectForm.clearInputs();
        } catch (SynchronizerException ex) {
            showMessage("Failed to connect " + host + ":" + port + ". (" + ex.getMessage() + ")", false);
        }
    }

    protected void disconnect(ConnectInfo info) {
        String host = info.getHost();
        Integer port = info.getPort();
        showMessage("Disconnecting from " + host + ":" + port + ".", true);
        try {
            manager.disconnect(info);
            removeConnection(info);
        } catch (SynchronizerException ex) {
            showMessage("Failed to disconnect " + host + ":" + port + ". (" + ex.getMessage() + ")", false);
        } finally {removeConnection(info); }// debug
    }

    @Override
    public synchronized void addConnection(ConnectInfo info) {
        connectionTable.add(info);
        showMessage("Connected " + info.getHost() + ":" + info.getPort() + ".", true);
    }

    @Override
    public synchronized void removeConnection(ConnectInfo info) {
        connectionTable.remove(info);
        showMessage("Disconnected " + info.getHost() + ":" + info.getPort() + ".", true);
    }

    @Override
    public boolean perimitConnection(ConnectInfo info) {
        return true;
    }

    @Override
    public void receivedSyncText(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.setText(text);
            }
        });
    }

    protected void sendSyncText(String text) {
        manager.sendSyncData(text);
    }

    protected void showMessage(final String text, final boolean fadeout) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (fadeout)
                    animatedLabel.showText(text);
                else
                    animatedLabel.setText(text);
            }
        });
    }

    /**
     * UIs
     */
    private CFDetecatbleTexaArea textArea;
    private ServiceForm serviceForm;
    private ConnectForm connectForm;
    private ConnectionTable connectionTable;
    private CFAnimatedLabel animatedLabel;
    
    private void buildLayout() {
        this.setCenter(TabPaneBuilder.create()
            .tabs(
                buildTextAreaTab(),
                buildConnectionTab()
            )
            .build()
        );
        this.setBottom(VBoxBuilder.create()
            .padding(new Insets(10, 10, 10, 10))
            .spacing(5)
            .children(
                buildSeparator(),
                animatedLabel = new CFAnimatedLabel()
            )
            .build()
        );
    }

    private Tab buildTextAreaTab() {
        Tab tab = TabBuilder.create()
            .text("Text Area")
            .content(buildTextAreaContent())
            .closable(false)
            .build();
        return tab;
    }

    private Node buildTextAreaContent() {
        VBox content = VBoxBuilder.create()
            .padding(new Insets(10, 10, 0, 10))
            .spacing(5)
            .children(
                buildLabel("SyncText"),
                textArea = new CFDetecatbleTexaArea()
            )
            .build();
        VBox.setVgrow(textArea, Priority.ALWAYS);
        return content;
    }

    private Tab buildConnectionTab() {
        Tab tab = TabBuilder.create()
            .text("Connection")
            .content(buildConnectionContent())
            .closable(false)
            .build();
        return tab;
    }

    private Node buildConnectionContent() {
        BorderPane content = BorderPaneBuilder.create()
            .top(
                VBoxBuilder.create()
                    .padding(new Insets(10, 10, 5, 10))
                    .spacing(10)
                    .children(
                        buildLabel("Connection Info"),
                        serviceForm = new ServiceForm()
                    )
                .build()
            )
            .center(
                VBoxBuilder.create()
                    .padding(new Insets(10, 10, 5, 10))
                    .spacing(10)
                    .children(
                        buildSeparator(),
                        buildLabel("Connection List"),
                        connectionTable = new ConnectionTable()
                    )
                .build()
            )
            .bottom(
                VBoxBuilder.create()
                    .padding(new Insets(10, 10, 5, 10))
                    .spacing(10)
                    .children(
                        buildSeparator(),
                        connectForm = new ConnectForm()
                    )
                .build()
            )
            .build();
        return content;
    }

    protected class ServiceForm extends CFHForm {
        private final CFTextField port;
        private final CFTextField name;
        private final Button serviceSwitch;
        private final static String startString = "Start Service";
        private final static String stopString  = "Stop Service";

        public ServiceForm() {
            setPadding(new Insets(0, 0, 0, 10));
            setSpacing(15.0);
            getChildren().addAll(
                HBoxBuilder.create()
                    .spacing(5)
                    .children(
                        new Label("Port:"),
                        port = buildCFTextField("port", true,
                            "\\A\\d{4}\\d?\\b", "4-5桁の数値を入力してください")
                    )
                    .build(),
                HBoxBuilder.create()
                    .spacing(5)
                    .children(
                        new Label("Name:"),
                        name = buildCFTextField("name", true,
                            "\\A[a-zA-Z]+\\S*\\b", "英字を入力してください")
                    )
                    .build(),
                serviceSwitch = new Button(startString)
            );
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent evt) {
                    if (serviceSwitch.getText().equals(startString))
                        startService();
                    else
                        stopService();
                }
            });
        }

        private void startService() {
            CFSyncTextArea.this.startService(params());
            serviceSwitch.setText(stopString);
        }

        private void stopService() {
            CFSyncTextArea.this.stopService();
            serviceSwitch.setText(startString);
            setDisableInputs(false);
        }

        public void setDisableInputs(boolean b) {
            port.setDisable(b);
            name.setDisable(b);
        }
    }

    protected class ConnectForm extends CFHForm {
        private final CFTextField host;
        private final CFTextField port;

        public ConnectForm() {
            setPadding(new Insets(0, 0, 0, 10));
            setSpacing(15);
            getChildren().addAll(
                HBoxBuilder.create()
                    .spacing(5)
                    .children(
                        new Label("Host:"),
                        host = buildCFTextField("host", true,
                            "\\A((\\d{1,3}\\.){3}\\d{1,3})|(\\S+)\\b", "IPアドレスかホスト名を入力してください")
                    )
                    .build(),
                HBoxBuilder.create()
                    .spacing(5)
                    .children(
                        new Label("Port:"),
                        port = buildCFTextField("port", true,
                            "\\A\\d{4}\\d?\\b", "4-5桁の数値を入力してください")
                    )
                    .build(),
                ButtonBuilder.create()
                    .text("Connect")
                    .build()
            );
            setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent evt) {
                    CFSyncTextArea.this.connect(params());
                }
            });
            setDisable(true);
        }

        public void clearInputs() {
            host.clear();
            port.clear();
        }
    }

    private Separator buildSeparator() {
        return SeparatorBuilder.create()
            .styleClass("separator")
            .build();
    }

    private Label buildLabel(String text) {
        return LabelBuilder.create()
            .text(text)
            .font(Font.font("Arial Black", FontWeight.BOLD, 14))
            .build();
    }

    private CFTextField buildCFTextField(String name,
            boolean required, String format, String message) {
        CFTextField textField = new CFTextField(name);
        textField.setRequired(required);
        textField.setFormat(format);
        textField.setMessage(message);
        return textField;
    }

    protected class ConnectionTable extends CFTableView {
        private final ObservableList<ConnectionView> items = FXCollections.observableArrayList();
        
        public ConnectionTable() {
            super(ConnectionView.class);
            setItems(items);
        }

        public void add(ConnectInfo info) {
            items.add(new ConnectionView(info));
        }

        public void remove(ConnectInfo info) {
            for (ConnectionView cv : items) {
                if (cv.info == info) {
                    items.remove(cv);
                    return;
                }
            }
        }
    }

    public class ConnectionView {
        final ConnectInfo info;

        @CFTableColumn(text="Host", width=180)
        private final StringProperty  host = new SimpleStringProperty();

        @CFTableColumn(text="Port", width=150)
        private final IntegerProperty port = new SimpleIntegerProperty();

        @CFTableColumn(text="Name", width=180)
        private final StringProperty  name = new SimpleStringProperty();

        @CFTableColumn(text="Disconnect", width=150)
        private final ObjectProperty<Button> disconnect = new SimpleObjectProperty<Button>(
            ButtonBuilder.create()
                .text("Disconnect")
                .onAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent evt) {
                        disconnect.get().setDisable(true);
                        disconnect(info);
                    }
                })
                .build()
            );

        public ConnectionView(ConnectInfo info) {
            this.info = info;
            this.host.set(info.getHost());
            this.port.set(info.getPort());
            this.name.set(info.getName());
        }

        public String getHost() { return this.host.get(); }

        public void setHost(String host) { this.host.set(host); }

        public Integer getPort() { return this.port.get(); }

        public void setPort(Integer port) { this.port.set(port); }

        public String getName() { return this.name.get(); }

        public void setName(String name) { this.name.set(name); }

        public Button getDisconnect() { return this.disconnect.get(); }

        public void setDisconnect(Button disconncet) { this.disconnect.set(disconncet); }

    }
}
