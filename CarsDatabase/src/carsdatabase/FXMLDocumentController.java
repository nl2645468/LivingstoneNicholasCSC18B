/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsdatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.*;
import javafx.scene.control.ButtonType;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author freez
 */
@SuppressWarnings("unchecked")
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button prevBttn;
    @FXML
    private TextField currentIndexTF;
    @FXML
    private TextField totalIndexTF;
    @FXML
    private Button nextBttn;
    @FXML
    private TextField IDTF;
    @FXML
    private TextField makeTF;
    @FXML
    private TextField modelTF;
    @FXML
    private TextField yearTF;
    @FXML
    private TextField mlgTF;
    @FXML
    private TextField modelSearchTF;
    @FXML
    private Button findBttn;
    @FXML
    private Button browseBttn;
    @FXML
    private Button insertBttn;
    @FXML
    private Button deleteBttn;
    
    CarQueries carQ = new CarQueries();    //databse query object
    List<Car> carList = new ArrayList<>();
    int carCount = 0,
            currentIndex = 1;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        carCount = carQ.getRowCount(); 
        totalIndexTF.setText(Integer.toString(carCount));
        if(carCount == 0){
            setDisableButtons(true);
            currentIndexTF.setText("0"); 
            Alert alert = new Alert(AlertType.INFORMATION, "The database is empty, you should add something!", ButtonType.CLOSE);
            alert.showAndWait();
            return; 
        }
        currentIndexTF.setText(Integer.toString(currentIndex)); 
        for(int i = 1; i <= carCount; i++){
            carList.add(carQ.getCar(i));
        }
        fillFields(carList.get(currentIndex - 1));
    }    
    
    

    @FXML
    private void prevPress(ActionEvent event) {
        if (carCount == 0)
            return; 
        currentIndex--;
         if(currentIndex < 1)
            currentIndex = carCount;
        fillFields(carList.get(currentIndex - 1));
        currentIndexTF.setText(Integer.toString(currentIndex));
    }

    @FXML
    private void nextPress(ActionEvent event) {
        if (carCount == 0)
            return;
        currentIndex++;
        if(currentIndex > carCount)
            currentIndex = 1;
        fillFields(carList.get(currentIndex - 1));
        currentIndexTF.setText(Integer.toString(currentIndex));
    }

    @FXML
    private void findPress(ActionEvent event) {
        String _model = modelSearchTF.getText();
        if(_model.isEmpty()){
            Alert alert = new Alert(AlertType.ERROR, "Uh Oh, somethings missing! Try typing in a model to search for.", ButtonType.CLOSE);
            alert.showAndWait();
            return; 
        }
        int foundIndex = 0; 
        boolean found = false; 
        for(int i = 0; i < carCount; i++){
            if(carList.get(i).getCarModel().equalsIgnoreCase(_model)){
                found = true;
                foundIndex = i;
            }
        }
        if(found){
            updateFields(foundIndex + 1);
        }
        else{
            Alert alert = new Alert(AlertType.INFORMATION, "No cars found with that model.", ButtonType.CLOSE);
            alert.showAndWait();
        }
    }
    @FXML
    private void browsePress(ActionEvent event) {
        TableView<Car> table = new TableView<Car>();    //make new table view
        
        //create table columns
        TableColumn<Car, Integer> IDColumn = new TableColumn<>("Car ID");
        TableColumn<Car, String> makeColumn = new TableColumn<>("Make");
        TableColumn<Car, String> modelColumn = new TableColumn<>("Model");
        TableColumn<Car, String> yearColumn = new TableColumn<>("Year");
        TableColumn<Car, Integer> mileageColumn = new TableColumn<>("Mileage");
        
        //fix the width of the columns to the size of the window
        IDColumn.prefWidthProperty().bind(table.widthProperty().multiply(.19));
        makeColumn.prefWidthProperty().bind(table.widthProperty().divide(5));
        modelColumn.prefWidthProperty().bind(table.widthProperty().divide(5));
        yearColumn.prefWidthProperty().bind(table.widthProperty().divide(5));
        mileageColumn.prefWidthProperty().bind(table.widthProperty().divide(5));
        
        ObservableList<Car> obsCarList = FXCollections.observableArrayList(carList);    //cast dynamic list to observable list
        
        //define data to be pulled to each column
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("CarID"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("CarMake"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("CarModel"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("CarYear"));
        mileageColumn.setCellValueFactory(new PropertyValueFactory<>("CarMileage"));
        
        table.getColumns().addAll(IDColumn, makeColumn, modelColumn, yearColumn, mileageColumn);//add columns to the table
        table.setItems(obsCarList); //fill data with table
        
        //make button
        Button refresh = new Button("");
        Image refIcon = new Image(getClass().getResourceAsStream("refreshIcon.png"));
        ImageView imgView = new ImageView(refIcon);
        imgView.setSmooth(false);
        refresh.setGraphic(imgView);
        
        
        //setup the stage and scene
        VBox root = new VBox(table, refresh);
        root.setMinWidth(400);
        root.setMaxHeight(400);
        table.prefHeightProperty().bind(root.heightProperty());
        refresh.prefHeight(26);
        refresh.prefWidthProperty().bind(root.widthProperty());
        
        EventHandler<ActionEvent> refreshPress = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                obsCarList.clear();
                obsCarList.addAll(carList);
                table.setItems(obsCarList);
            }
        };
        
        refresh.setOnAction(refreshPress);
        
        
        Stage stage = new Stage();
        stage.setTitle("Browse All Entries");
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMaxWidth(400);
        stage.show();   //show the scene
    }
    
    

    @FXML
    private void insertPress(ActionEvent event) {
        //get data from fields
        
        
        if(makeTF.getText().isEmpty() || modelTF.getText().isEmpty() || yearTF.getText().isEmpty() || mlgTF.getText().isEmpty()){
            Alert issue = new Alert(AlertType.ERROR, "Check your fields, somethings missing...", ButtonType.CLOSE);
            issue.showAndWait();
            return; 
        }
        
        if(carCount == 0){
            setDisableButtons(false);
        }
        
        String make = makeTF.getText().trim();
        String model = modelTF.getText().trim();
        String year = yearTF.getText().trim();
        int mileage = Integer.valueOf(mlgTF.getText());
        
        
        
        int ID = carQ.addCar(make, model, year, mileage);   //add car to the database, returns newly created car ID
        carList.add(new Car(ID, make, model, year, mileage));   //add car to vector array
        carCount++;     //increase the number of cars
        updateFields(carCount); //update fields with the new car
    }

    @FXML
    private void deletePress(ActionEvent event) {
        if(carCount == 0){
            Alert issue = new Alert(AlertType.ERROR, "There are no entries to delete!", ButtonType.CLOSE);
            issue.showAndWait();
            return; 
        }
        Alert confirm = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this entry?", ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Confirm Delete");
        Optional<ButtonType> yes = confirm.showAndWait();
        if(yes.isPresent() && (yes.get() == ButtonType.YES)){
            confirm.close();
            if(carQ.deleteCar(currentIndex));
                Alert success = new Alert(AlertType.INFORMATION, "Entry successfully deleted.", ButtonType.CLOSE);
                success.setTitle("Success!");
                success.showAndWait();
                carList.remove(currentIndex - 1);
                carCount--;
                if(carCount == 0){
                    IDTF.clear();
                    makeTF.clear();
                    modelTF.clear(); 
                    yearTF.clear(); 
                    mlgTF.clear(); 
                    currentIndexTF.setText("0");
                    totalIndexTF.setText("0");
                    setDisableButtons(true);
                }
                else
                    updateFields();
        }
    }
    
    private void fillFields(Car _car){
        IDTF.setText(Integer.toString(_car.getCarID()));
        makeTF.setText(_car.getCarMake());
        modelTF.setText(_car.getCarModel());
        yearTF.setText(_car.getCarYear());
        mlgTF.setText(Integer.toString(_car.getCarMileage()));
    }
    
    private void updateFields(){
        currentIndex = 1;
        currentIndexTF.setText(Integer.toString(currentIndex));
        totalIndexTF.setText(Integer.toString(carCount));
        fillFields(carList.get(currentIndex - 1));
        modelSearchTF.clear();
        prevBttn.requestFocus();
    }
    
    private void updateFields(int _index){
        currentIndex = _index;
        currentIndexTF.setText(Integer.toString(currentIndex));
        totalIndexTF.setText(Integer.toString(carCount));
        fillFields(carList.get(currentIndex - 1));
        modelSearchTF.clear();
        prevBttn.requestFocus();
    }
    
    private void setDisableButtons(boolean _disable){
        prevBttn.setDisable(_disable);
        nextBttn.setDisable(_disable);
        findBttn.setDisable(_disable);
        browseBttn.setDisable(_disable);
        deleteBttn.setDisable(_disable);
    }
}
