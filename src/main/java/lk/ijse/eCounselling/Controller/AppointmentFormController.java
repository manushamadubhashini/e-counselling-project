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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.eCounselling.Util.Regex;
import lk.ijse.eCounselling.model.*;
import lk.ijse.eCounselling.model.tm.AppointmentTm;
import lk.ijse.eCounselling.model.tm.EmployeeTm;
import lk.ijse.eCounselling.repository.*;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentFormController {

    @FXML
    private JFXButton btnAddNewItem;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXComboBox cmbTime;

    @FXML
    public JFXComboBox cmbEmployeeId;

    @FXML
    public JFXComboBox cmbPatientId;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colID;

    @FXML
    private TableColumn<?, ?> colTime;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private TableColumn<?, ?> colEmpId;

    @FXML
    private TableColumn<?, ?> colPaId;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<AppointmentTm> tblAppointment;

    @FXML
    private TextField txtDate;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtType;

    public void initialize() {
        txtId.setDisable(true);
        txtType.setDisable(true);
        txtDate.setDisable(true);
        cmbTime.setDisable(true);
        cmbEmployeeId.setDisable(true);
        cmbPatientId.setDisable(true);
        btnSave.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        txtId.setEditable(false);
        setCellValueFactory();
        loadAppointmentTable();
        getPatientId();
        getEmployeeId();
        setTime();

        tblAppointment.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDelete.setDisable(newValue == null);
            //btnUpdate.setText(newValue != null ? "Update" : "Save");
            btnUpdate.setDisable(newValue == null);

            if (newValue != null) {
                txtId.setText(newValue.getId());
                txtType.setText(newValue.getType());
                txtDate.setText(newValue.getDate());
                cmbTime.setValue(newValue.getTime());
                cmbEmployeeId.setValue(newValue.getEid());
                cmbPatientId.setValue(newValue.getPid());

                txtId.setDisable(false);
                txtType.setDisable(false);
                txtDate.setDisable(false);
                cmbTime.setDisable(false);
                cmbEmployeeId.setDisable(false);
                cmbPatientId.setDisable(false);


            }
        });
    }

    private void setTime() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        List<String> time = new ArrayList<>();
        time.add("6.30 p.m-8.30 p.m");
        time.add("7.00 a.m-9.00 a.m");
        time.add("10.00 p.m-11.00 p.m");

        for (String Time : time) {
            obList.add(Time);
        }
        cmbTime.setItems(obList);

    }

    private void getEmployeeId() {
        try {
          ArrayList<Employee> employees= EmployeeRepo.getAll();
            for (Employee e : employees) {
                cmbEmployeeId.getItems().add(e.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void getPatientId() {
        try {
        ArrayList<Patient> patients= PatientRepo.getAll();
        for (Patient p:patients){
            cmbPatientId.getItems().add(p.getId());

        }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCellValueFactory() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colEmpId.setCellValueFactory(new PropertyValueFactory<>("eid"));
        colPaId.setCellValueFactory(new PropertyValueFactory<>("pid"));
    }

    private void loadAppointmentTable(){
       tblAppointment.getItems().clear();
       try {
           ArrayList<Appointment> appointments=AppointmentRepo.getAll();
           for (Appointment a:appointments){
               tblAppointment.getItems().add(new AppointmentTm(a.getId(),a.getType(),a.getDate(),a.getTime(),a.getEid(),a.getPid()));
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
        txtDate.setText("");
        cmbTime.setValue(null);
        cmbEmployeeId.setValue(null);
        cmbPatientId.setValue(null);
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = tblAppointment.getSelectionModel().getSelectedItem().getId();

        try {
            boolean isDeleted = AppointmentRepo.delete(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "appointment deleted!").show();
            }
            tblAppointment.getItems().remove(tblAppointment.getSelectionModel().getSelectedItem());
            tblAppointment.getSelectionModel().clearSelection();
            txtId.clear();
            txtType.clear();
            txtDate.clear();
            cmbTime.setValue(null);
            cmbEmployeeId.setValue(null);
            cmbPatientId.setValue(null);
            init();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        boolean hasError = false;

        if (txtId.getText().isEmpty()) {
            txtId.setStyle("-fx-border-color: red;");
            hasError = true;
        } else {
            txtId.setStyle("");
        }
        if (txtType.getText().isEmpty()) {
            txtType.setStyle("-fx-border-color: red;");
            hasError = true;
        } else {
            txtType.setStyle("");
        }
        if (txtDate.getText().isEmpty()) {
            txtDate.setStyle("-fx-border-color: red;");
            hasError = true;
        } else {
            txtDate.setStyle("");
        }
        if (cmbTime.getValue() == null) {
            cmbTime.setStyle("-fx-border-color: red;");
            hasError = true;
        } else {
            cmbTime.setStyle("");
        }
        if (cmbEmployeeId.getValue() == null) {
            cmbEmployeeId.setStyle("-fx-border-color: red;");
            hasError = true;
        } else {
            cmbEmployeeId.setStyle("");
        }
        if (cmbPatientId.getValue() == null) {
            cmbPatientId.setStyle("-fx-border-color: red;");
            hasError = true;
        } else {
            cmbPatientId.setStyle("");
        }

        // If there is an error, show an alert and return
        if (hasError) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.show();
            return;
        }
            String id = txtId.getText();
            String type = txtType.getText();
            String date=txtDate.getText();
            String time = (String) cmbTime.getValue();
            String eid = (String) cmbEmployeeId.getValue();
            String pid = (String) cmbPatientId.getValue();

            Appointment appointment = new Appointment(id, type, date, time, eid, pid);

            try {
                boolean isSaved = AppointmentRepo.save(appointment);
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "appointment saved!").show();
                    init();
                }
                tblAppointment.getItems().add(new AppointmentTm(id,type,date,time,eid,pid));
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
    }




    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtId.getText();
        String type = txtType.getText();
        String date=txtDate.getText();
        String time= (String) cmbTime.getValue();
        String eid= (String) cmbEmployeeId.getValue();
        String pid= (String) cmbPatientId.getValue();



        try {
            boolean isUpdated = AppointmentRepo.update(id,type,date,time,eid,pid);
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "appointment updated!").show();
            }
            AppointmentTm selectedAppo=tblAppointment.getSelectionModel().getSelectedItem();
            selectedAppo.setType(type);
            selectedAppo.setDate(date);
            selectedAppo.setTime(time);
            selectedAppo.setEid(eid);
            selectedAppo.setPid(pid);
            tblAppointment.refresh();
            tblAppointment.getSelectionModel().clearSelection();
            init();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }

    public void txtAppointmentIdOnAction(ActionEvent event) {
            txtId.setStyle("-fx-border-color: green;");
            txtType.requestFocus();
    }

    public void AppointmentTypeOnAction(ActionEvent event) {
        String type=txtType.getText();
        txtType.setStyle("-fx-border-color: green");
        txtDate.requestFocus();

    }

    public void DateOnAction(ActionEvent event) {
        String date=txtDate.getText();
        txtDate.setStyle("-fx-border-color: green");
        cmbTime.requestFocus();

    }



    public void StatusOnAction(ActionEvent event) {
        String status= (String) cmbTime.getValue();
        cmbTime.setStyle("-fx-border-color: green");
        cmbEmployeeId.requestFocus();
    }

    public void EIDOnAction(ActionEvent event) {
        String eid= (String) cmbEmployeeId.getValue();
        cmbEmployeeId.setStyle("-fx-border-color: green");
        cmbPatientId.requestFocus();


    }

    public void PIDOnAction(ActionEvent event) {
        String pid= (String) cmbPatientId.getValue();
        cmbPatientId.setStyle("-fx-border-color: green");


    }
    @FXML
    void btnNewAppointmentOnAction(ActionEvent event) {
        txtId.clear();
        txtType.clear();
        txtDate.clear();
        cmbTime.setValue(null);
        cmbEmployeeId.setValue(null);
        cmbPatientId.setValue(null);
        txtId.setDisable(false);
        txtType.setDisable(false);
        txtDate.setDisable(false);
        cmbTime.setDisable(false);
        cmbEmployeeId.setDisable(false);
        cmbPatientId.setDisable(false);
        txtId.setText(generateNewId());
        txtId.setEditable(false);
        btnSave.setDisable(false);
        txtDate.setText(String.valueOf(LocalDate.now()));
        txtDate.setEditable(false);

    }

    private String generateNewId() {
        try {
            //Generate New ID
            return AppointmentRepo.generateId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new id " + e.getMessage()).show();
        }
        return "A001";

    }
    private void init(){
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);

    }

}
