package lk.ijse.eCounselling.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lk.ijse.eCounselling.Util.Regex;
import lk.ijse.eCounselling.model.Appointment;
import lk.ijse.eCounselling.model.Session;
import lk.ijse.eCounselling.model.tm.AppointmentTm;
import lk.ijse.eCounselling.model.tm.SessionTm;
import lk.ijse.eCounselling.repository.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionFormController  {

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnNewSession;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXComboBox cmbId;

    @FXML
    private JFXComboBox cmbPaid;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colDuration;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colPaId;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private TableColumn<?, ?> colEid;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<SessionTm> tblSession;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextField txtDuration;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtType;

    public void initialize() {
        setCellValueFactory();
        loadSessionTable();
        getEmployeeId();
        getPatientId();
        txtId.setDisable(true);
        txtType.setDisable(true);
        txtDate.setDisable(true);
        txtDuration.setDisable(true);
        cmbId.setDisable(true);
        cmbPaid.setDisable(true);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        txtId.setEditable(false);
        txtDate.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Optional styling for disabled dates
                        }
                    }
                };
            }
        });
        tblSession.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            //btnUpdate.setText(newValue != null ? "Update" : "Save");
            btnUpdate.setDisable(newValue == null);

            if (newValue != null) {
                txtId.setText(newValue.getId());
                txtType.setText(newValue.getType());
                txtDate.setValue(newValue.getDate());
                txtDuration.setText(String.valueOf(newValue.getDuration()));
                cmbId.setValue(newValue.getEid());
                cmbPaid.setValue(newValue.getPid());

                txtId.setDisable(false);
                txtType.setDisable(false);
                txtDate.setDisable(false);
                txtDuration.setDisable(false);
                cmbId.setDisable(false);
                cmbPaid.setDisable(false);


            }
        });
    }

    private void getPatientId() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = PatientRepo.getIds();
            for (String code : codeList) {
                obList.add(code);
            }

            cmbPaid.setItems(obList);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void getEmployeeId() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = EmployeeRepo.getIds();
            for (String code : codeList) {
                obList.add(code);
            }

            cmbId.setItems(obList);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colEid.setCellValueFactory(new PropertyValueFactory<>("eid"));
        colPaId.setCellValueFactory(new PropertyValueFactory<>("pid"));
    }

    private void loadSessionTable() {
        tblSession.getItems().clear();
        try {
            ArrayList<Session> sessions = SessionRepo.getAll();
            for (Session s : sessions) {
                tblSession.getItems().add(new SessionTm(s.getId(), s.getType(), s.getDate(), s.getDuration(), s.getEid(), s.getPid()));
            }
        }catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Dashboard Form");
        stage.centerOnScreen();

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtId.setText("");
        txtType.setText("");
        txtDate.setValue(null);
        txtDuration.setText("");
        cmbId.setValue(null);
        cmbPaid.setValue(null);
        tblSession.getSelectionModel().clearSelection();
        init();

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        try {
            boolean isDeleted = SessionRepo.delete(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "session deleted!").show();
            }
            tblSession.getItems().remove(tblSession.getSelectionModel().getSelectedItem());
            tblSession.getSelectionModel().clearSelection();
            txtId.clear();
            txtType.clear();
            txtDate.setValue(null);
            txtDuration.clear();
            cmbId.setValue(null);
            cmbPaid.setValue(null);
            init();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

}

    @FXML
    void btnSaveOnAction(ActionEvent event) {
           boolean hasError=false;
            // Set border color of empty text fields to red
            if (txtId.getText().isEmpty()) {
                txtId.setStyle("-fx-border-color: red;");
                hasError=true;
            } else {
                txtId.setStyle("");
            }
            if (txtType.getText().isEmpty()) {
                txtType.setStyle("-fx-border-color: red;");
                hasError=true;
            } else {
                txtType.setStyle("");
            }
            if (txtDate.getValue() == null) {
                txtDate.setStyle("-fx-border-color: red;");
                hasError=true;
            } else {
                txtDate.setStyle("");
            }
            if (txtDuration.getText().isEmpty()) {
                txtDuration.setStyle("-fx-border-color: red;");
                hasError=true;
            } else {
                txtDuration.setStyle("");
            }
            if (cmbId.getValue() == null) {
                cmbId.setStyle("-fx-border-color: red;");
                hasError=true;
            } else {
                cmbId.setStyle("");
            }
            if (cmbPaid.getValue() == null) {
                cmbPaid.setStyle("-fx-border-color: red;");
                hasError=true;
            } else {
                cmbPaid.setStyle("");
            }
            if(hasError) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields.");
                alert.show();
            }

            String id = txtId.getText();
            String type = txtType.getText();
            LocalDate date = txtDate.getValue();
            int duration;
            String eid = (String) cmbId.getValue();
            String pid = (String) cmbPaid.getValue();
            try {
                 duration = Integer.parseInt(txtDuration.getText());
            }catch (NumberFormatException e){
                txtDuration.setStyle("-fx-border-color: red");
                txtDuration.requestFocus();
                return;

            }
            Session session = new Session(id, type, date, duration, eid, pid);

           if(! type.matches("[A-Za-z ]+")){
              new Alert(Alert.AlertType.ERROR,"invalid value").show();
              txtType.setStyle("-fx-border-color: red");
              txtType.requestFocus();
              return;
           }

            if(! txtDuration.getText().matches("^(?:[1-9][0-9]?|100)$")) {
                new Alert(Alert.AlertType.ERROR,"invalid duration").show();
                txtDuration.setStyle("-fx-border-color: red");
                txtDuration.requestFocus();
                return;
            }

            try {
                boolean isSaved = SessionRepo.save(session);
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "session saved!").show();
                    init();
                }
                tblSession.getItems().add(new SessionTm(id,type,date,duration,eid,pid));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        String id = txtId.getText();
        String type = txtType.getText();
        LocalDate date = txtDate.getValue();
        int duration= Integer.parseInt(txtDuration.getText());
        String eid= (String) cmbId.getValue();
        String pid= (String) cmbPaid.getValue();


        try {
            boolean isUpdated = SessionRepo.update(id,type,date,duration,eid,pid);
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "session updated!").show();
                init();
            }
            SessionTm selectedSessions=tblSession.getSelectionModel().getSelectedItem();
            selectedSessions.setType(type);
            selectedSessions.setDate(date);
            selectedSessions.setDuration(duration);
            selectedSessions.setEid(eid);
            selectedSessions.setPid(pid);
            tblSession.refresh();
            tblSession.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

    }

    public void SessionIdOnAction(ActionEvent event) {
        String sesId = txtId.getText();
        if (Regex.isSessionId(sesId)) {
            txtId.setStyle("-fx-border-color: green;");
            txtId.requestFocus();
        } else {
            txtId.setStyle("-fx-border-color: red;");
            txtId.requestFocus();
        }

    }
    @FXML
    void btnNewSessionOnAction(ActionEvent event) {
        txtId.clear();
        txtType.clear();
        txtDate.setValue(null);
        txtDuration.clear();
        cmbId.setValue(null);
        cmbPaid.setValue(null);
        txtId.setDisable(false);
        txtType.setDisable(false);
        txtDate.setDisable(false);
        txtDuration.setDisable(false);
        cmbId.setDisable(false);
        cmbPaid.setDisable(false);
        btnSave.setDisable(false);
        txtId.setEditable(false);

       txtId.setText(generateId());
    }
    private String generateId(){
        try {
            return SessionRepo.generateId();
        }catch (SQLException e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
        return "S001";

    }
    private void init(){
        txtId.setDisable(true);
        txtType.setDisable(true);
        txtDate.setDisable(true);
        txtDuration.setDisable(true);
        cmbId.setDisable(true);
        cmbPaid.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);
    }
}

