package controller;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.Patient;
import service.PatientService;
import service.PatientServiceImpl;
import util.CustomException;
import util.DatabaseException;
import util.FormException;

public class CreateController implements Initializable {

    @FXML
    private AnchorPane anchorPane2;

    @FXML
    private Pane createPane;

    @FXML
    private JFXTextField txtPatientName;

    @FXML
    private JFXTextField txtAge;

    @FXML
    private JFXTextField txtWeight;

    @FXML
    private JFXDatePicker dateAppointment;

    @FXML
    private JFXComboBox<String> cmbGender;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnCancel;

    Logger logger;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbGender.getItems().add("Male");
        cmbGender.getItems().add("Female");
        cmbGender.setValue("value");
        try {
            validateForm();
        } catch (FormException e1) {
            e1.printStackTrace();
        }
        btnSave.setOnAction((event -> {
            try {
                PatientService patientService = new PatientServiceImpl();
                validateBeforeSave();
                patientService.create(loadPatient());
            } catch (FormException | CustomException | DatabaseException e) {
                e.printStackTrace();
            }
        }));

        btnCancel.setOnAction(event -> {
            Parent screen;
            try {
                screen = FXMLLoader.load(getClass().getResource("/view/layouts/main.fxml"));
                Scene scene = btnCancel.getScene();
                scene.setRoot(screen);
            } catch (Exception e) {
                logger.log(Level.ERROR, e);
            }
        });
    }

    /**
     * Form validation
     * 
     * @throws FormException
     */
    private void validateForm() throws FormException {
        txtPatientName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0) {
                if (!newValue.matches("[A-Z a-zÀ-Ÿà-ÿ]+") || oldValue.length() == 0) {
                    txtPatientName.setText("");
                }
            } else if (!newValue.matches("[A-Z a-zÀ-Ÿà-ÿ]+") || (newValue.length() > 50)) {
                txtPatientName.setText(oldValue);
            }
        });

        txtAge.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0) {
                if (!newValue.matches("\\d+") || oldValue.length() == 0)
                    txtAge.setText("");
            } else if (!newValue.matches("\\d+") || newValue.length() < 0)
                txtAge.setText(oldValue);
        });

        txtWeight.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() == 0) {
                if (!newValue.matches("[0-9]{1,2}[,][0-9]{1}") || oldValue.length() == 0)
                    txtWeight.setText("");
            } else if (!newValue.matches("[0-9]{1,2}[,][0-9]{1}") || newValue.length() == 0)
                txtWeight.setText(oldValue);
        });

    }

    private Patient loadPatient() {
        Patient patient = new Patient();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        patient.setPatientName(txtPatientName.getText());
        patient.setAge(Integer.parseInt(txtAge.getText()));
        patient.setGender(cmbGender.getValue());
        patient.setWeight(Double.parseDouble(txtWeight.getText()));
        try {
            Date date = sdf.parse(dateAppointment.getEditor().getText());
            patient.setVaccineDate(new java.sql.Date(date.getTime()));
        } catch (ParseException e) {
            logger.log(Level.ERROR, e);
        }

        return patient;
    }

    private void validateBeforeSave() throws FormException {
        if (txtPatientName.getText().isEmpty()) {
            throw new FormException("Inform the patient name!", "Patient Name is empty");
        }

        if (cmbGender.getEditor().getText().isEmpty()) {
            throw new FormException("Select a gender!", "Combo box is empty");
        }

        if (txtAge.getText().isEmpty()) {
            throw new FormException("Input patient age!", "Age field is empty");
        }

        if (txtWeight.getText().isEmpty()) {
            throw new FormException("Enter a Weight", "Weight field is empty");
        }

        if (dateAppointment.getEditor().getText().isEmpty()) {
            throw new FormException("Input vaccine date", "Vaccine Date is empty");
        }

        if (!dateAppointment.getEditor().getText().matches("[0-3][0-9]/[0-1][0-9]/[0-9]+")) {
            throw new FormException("try: 00/00/0000", "The date format is invalid");
        }
    }

}