package control;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBuilder;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CFTableView<S> extends TableView<S> {
    /**
     * Anntation of CFTableColumn
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CFTableColumn{
        String text();
        double width() default 30d;
    }

    /**
     * Definition of table column
     */
    private class ColumnInfo {
        String id;
        String text;
        double width;

        ColumnInfo(String id, String text, double width) {
          this.id = id;
          this.text = text;
          this.width = width;
        }
    }

    /**
     * CFTableView
     */
    private Class<S> columnDefnition;
    private Map<String, TableColumn<S,?>> tableColumns = new LinkedHashMap<String, TableColumn<S,?>>();

    public CFTableView() {}

    public CFTableView(Class<S> clazz) {
        setColumnDefinition(clazz);
    }

    public CFTableView(ObservableList<S> items) {
        setItems(items);
        if (!items.isEmpty())
            setColumnDefinition((Class<S>)items.get(0).getClass());
    }

    public Class<S> getColumnDefinition() {
        return columnDefnition;
    }

    public void setColumnDefinition(Class<S> clazz) {
        columnDefnition = clazz;

        buildTableColumns(clazz);
        getColumns().clear();
        getColumns().addAll(tableColumns.values());
    }

    protected void buildTableColumns(Class<S> clazz) {
        // Collect definitions of table column
        List<ColumnInfo> columnsInfo = getColumnsInfo(clazz);

        // Build table column
        tableColumns.clear();
        for (ColumnInfo info : columnsInfo)
            tableColumns.put(info.id, buildTableColumn(info));
    }

    protected List<ColumnInfo> getColumnsInfo(Class<S> clazz) {
        List<ColumnInfo> columnsInfo = new ArrayList<ColumnInfo>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(CFTableColumn.class)) {
              CFTableColumn annotation = (CFTableColumn)field.getAnnotation(CFTableColumn.class);
              columnsInfo.add(new ColumnInfo(field.getName(), annotation.text(), annotation.width()));
            }
        }
        return columnsInfo;
    }

    protected TableColumn<S,?> buildTableColumn(ColumnInfo info) {
        TableColumn<S,?> column = (TableColumn<S,?>)TableColumnBuilder.create()
            //.id(info.id)
            .text(info.text)
            .prefWidth(info.width)
            .cellValueFactory(new PropertyValueFactory(info.id))
            .build();
        return column;
    }
}
