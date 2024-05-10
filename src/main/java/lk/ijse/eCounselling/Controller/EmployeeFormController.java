package lk.ijse.eCounselling.Controller;

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
import lk.ijse.eCounselling.model.Employee;
import lk.ijse.eCounselling.model.User;
import lk.ijse.eCounselling.model.tm.EmployeeTm;
import lk.ijse.eCounselling.repository.EmployeeRepo;
import lk.ijse.eCounselling.repository.UserRepo;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class EmployeeFormController {
    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colContact;

    @FXML
    private TableColumn<?, ?> colDOB;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colJoinDate;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colPosition;

    @FXML
    private TableView<EmployeeTm> tblEmployee;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtContact;

    @FXML
    private DatePicker txtDOB;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
   private TextField txtPosition;

    private List<Employee> employeeList = new ArrayList<>();

    public void initialize() {
         this.employeeList = getAllEmployee();
         setCellValueFactory();
         loadCustomerTable();
    }

    private void loadCustomerTable() {
        ObservableList<EmployeeTm> EmployeeTMS = FXCollections.observableArrayList();


            for (Employee employee : employeeList) {
                EmployeeTm employeeTm=new EmployeeTm(
                        employee.getId(),
                        employee.getName(),
                        employee.getDOB(),
                        employee.getAddress(),
                        employee.getContact(),
                        employee.getPosition(),
                        employee.getJoinDate()

                );
                EmployeeTMS.add(employeeTm);

            }
        tblEmployee.setItems(EmployeeTMS);
        EmployeeTm selectedItem = (EmployeeTm) tblEmployee.getSelectionModel().getSelectedItem();
        System.out.println("selectedItem = " + selectedItem);

    }


    private List<Employee> getAllEmployee() {
        List<Employee>  employeerList = null;
        try {
            employeerList = EmployeeRepo.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employeerList;


    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colJoinDate.setCellValueFactory(new PropertyValueFactory<>("joinDate"));
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtName.getText();
        LocalDate dob = txtDOB.getValue();
        Date dateOfBirth = java.sql.Date.valueOf(dob);
        String address = txtAddress.getText();
        String contact = txtContact.getText();
        String position=txtPosition.getText();
        LocalDate joinDate = txtDate.getValue();
        Date joiningDate = java.sql.Date.valueOf(joinDate);
        //String userId = getCurrentUserId();

        Employee employee = new Employee(id, name,dateOfBirth ,address, contact,position,joiningDate);

        try {
            boolean isSaved = EmployeeRepo.save(employee);
            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer saved!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }


    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtDOB.setValue(null);
        txtAddress.setText("");
        txtContact.setText("");
        txtContact.setText("");
        txtDate.setValue(null);
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtName.getText();
        LocalDate DOB=txtDOB.getValue();
        String address = txtAddress.getText();
        String contact = txtContact.getText();
        String position=txtPosition.getText();
        LocalDate joinDate=txtDate.getValue();


        //Employee employee = new Employee(id, name,DOB, address, contact,position,joinDate);

        try {
            boolean isUpdated = EmployeeRepo.update(id,name,DOB,address,contact,position,joinDate);
           if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer updated!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
    }



    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        try {
            boolean isDeleted = EmployeeRepo.delete(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "employee deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Dashboard Form");
        stage.centerOnScreen();
    }

}









