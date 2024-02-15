package com.arakviel.carrepair.presentation.controller.workroom;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.repository.WorkroomRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.WorkroomModelMapper;
import com.arakviel.carrepair.presentation.model.impl.AddressModel;
import com.arakviel.carrepair.presentation.model.impl.WorkroomModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class WorkroomAddEditController implements Initializable {

    @FXML
    private MFXTextField nameTextField;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private MFXButton photoButton;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label photoErrorLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private MFXFilterComboBox<AddressModel> addressComboBox;

    @FXML
    private Label addressErrorLabel;

    private WorkroomModel workroomModel;
    private WorkroomRepository workroomRepository;
    private WorkroomModelMapper workroomModelMapper;
    private ObservableList<WorkroomModel> workrooms;
    private List<AddressModel> addresses;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.workroomRepository = RepositoryFactory.getInstance().getWorkroomRepository();
        this.workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);
        initAddressComboBox();

        WorkroomModel emptyWorkroomModel = WorkroomModel.builder()
                .address(addresses.get(0))
                .name("")
                .photo(null)
                .build();
        workroomModel = emptyWorkroomModel;

        initBindings();
    }

    public void setWorkrooms(ObservableList<WorkroomModel> workrooms) {
        this.workrooms = workrooms;
    }

    public void setWorkroomModel(WorkroomModel workroomModel) {
        this.workroomModel = workroomModel.clone();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Workroom workroom = workroomModelMapper.toDomain(workroomModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Workroom newWorkroom = workroomRepository.add(workroom);
                WorkroomModel newWorkroomModel = workroomModelMapper.toModel(newWorkroom);
                workrooms.add(newWorkroomModel);
            } else {
                workroomRepository.set(workroom.getId(), workroom);
                int index = IntStream.range(0, workrooms.size())
                        .filter(i -> workrooms.get(i).getId().equals(workroomModel.getId()))
                        .findFirst()
                        .orElse(-1);
                workrooms.set(index, workroomModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetWorkroomModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = workroomRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "address", addressErrorLabel);
            UiHelpers.validateShow(validationMessages, "name", nameErrorLabel);
            UiHelpers.validateShow(validationMessages, "photo", photoErrorLabel);
            UiHelpers.validateShow(validationMessages, "description", descriptionErrorLabel);
        }
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetWorkroomModel();
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForPhoto(photoImageView);
    }

    private void initAddressComboBox() {
        var addressRepository = RepositoryFactory.getInstance().getAddressRepository();
        // addressRepository.getAllByWorkroom();
        var addressModelMapper = ModelMapperFactory.getInstance().getAddressModelMapper();
        addresses = addressRepository.getAll().stream()
                .map(addressModelMapper::toModel)
                .sorted(Comparator.comparing(AddressModel::getFullAddress))
                .toList();
        addressComboBox.getItems().addAll(addresses);
        addressComboBox.selectFirst();
        addressComboBox.setText(addresses.get(0).toString());
    }

    private void initBindings() {
        nameTextField.textProperty().bindBidirectional(workroomModel.nameProperty());
        descriptionTextArea.textProperty().bindBidirectional(workroomModel.descriptionProperty());
        addressComboBox.valueProperty().bindBidirectional(workroomModel.addressProperty());
        photoImageView.imageProperty().bindBidirectional(workroomModel.photoProperty());
        photoImageView.managedProperty().bind(workroomModel.photoProperty().isNotNull());
    }

    private void resetWorkroomModel() {
        workroomModel.setId(null);
        workroomModel.setAddressModel(addresses.get(0));
        workroomModel.setName("");
        workroomModel.setDescription("");
        workroomModel.setPhoto(null);
    }
}
