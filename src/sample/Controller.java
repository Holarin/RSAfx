package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;

public class Controller {

    @FXML
    private TextField textField;

    @FXML
    private Button Encrypt;

    @FXML
    private Button Decrypt;

    @FXML
    private Button fileChoose;

    File read_File = new File("data/time.csv");
    File write_File_encrypted = new File("data/encrypted.csv");
    File write_File_decrypted = new File("data/decrypted.csv");

    KeyGeneration kGeneration = new KeyGeneration();

    @FXML
    private TextField userName;

    @FXML
    private Button Enter;

    private Unit currentUser = null;

    @FXML
    void initialize() throws GeneralSecurityException, IOException {


        Enter.setOnAction(event -> {
            try {
                DbHandler handler = DbHandler.getInstance();
                Unit unit = handler.getUnit(userName.getText());
                if (unit == null) {
                    kGeneration.generateRandomKeyAES();

                    kGeneration.generateKeyPair();

                    kGeneration.encryptAESKey();
                    currentUser = new Unit(userName.getText(),
                            kGeneration.getAESKey(),
                            kGeneration.getPublicKey().toString(),
                            kGeneration.getPrivateKey().toString());
                    handler.addUnit(currentUser);
                } else {
                    currentUser = unit;
                    kGeneration.setAesKey(currentUser.getAesKey());
                    kGeneration.setPrivateKey(currentUser.getPrivateKey());
                    kGeneration.setPublicKey(currentUser.getPublicKey());
                }
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        });
        Encrypt.setOnAction(event -> {
            if (currentUser != null) {
                read_File = new File(textField.getText());
                write_File_encrypted = new File(textField.getText() + "enc.txt");
                ASE_Algorithm.doEncryption(kGeneration.getAESKey(),
                        read_File, write_File_encrypted);
            }
        });

        Decrypt.setOnAction(event ->     {
            if (currentUser != null) {
                read_File = new File(textField.getText());
                write_File_decrypted = new File(textField.getText() + "dec.txt");
                ASE_Algorithm.doDecryption(kGeneration.getAESKey(),
                        write_File_encrypted, write_File_decrypted);
                try {
                    kGeneration.decryptAESKey();
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        fileChoose.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(fileChoose.getScene().getWindow());
            textField.setText(file.getAbsolutePath());
        });
    }
}
