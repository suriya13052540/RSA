package com.sut.rsa.Controller;

import com.sut.rsa.Service.StorageService;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.servlet.ServletContext;

@Controller
@RestController
@CrossOrigin("http://localhost:4200")
public class DecrypController {

    @Autowired
    private StorageService storageService;
    private MultipartFile fileEncryp;
    private MultipartFile fileKey;
    private String fileKeyName;
    private String fileNameEncyp;



    private void initRoot() {
        final Path rootLocation = Paths.get("decryp");
        try {

            FileSystemUtils.deleteRecursively(rootLocation.toFile());
        } catch (NullPointerException e) {
            System.out.println("Eroor delete all files");
        }

        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }



    @PostMapping("/uploadEncryp")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        initRoot();

        String message = "";
        String name = file.getOriginalFilename();
        try {

                storageService.storeDecryp(file);
                fileEncryp = file;
                fileNameEncyp=file.getOriginalFilename();
                Path moveprivate = Files.move(Paths.get(".\\decryp\\"+fileNameEncyp), Paths.get(".\\"+fileNameEncyp));


            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }


    @PostMapping("/uploadkey")
    public ResponseEntity<String> handlekeyFileUpload(@RequestParam("file") MultipartFile file) {


        fileKeyName=file.getOriginalFilename();
        String message = "";
        String name = file.getOriginalFilename();
        try {

            storageService.storeDecryp(file);
            fileKey=file;

            Path moveprivate = Files.move(Paths.get(".\\decryp\\"+fileKeyName), Paths.get(".\\"+fileKeyName));

            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }



    private static final String DIRECTORY = ".\\decryp";
    private static final String DEFAULT_FILE_NAME = "DecryptionFile.txt";

    @Autowired
    private ServletContext servletContext;

    @GetMapping("/downloadFile/loadDecryption")
    public ResponseEntity<ByteArrayResource> downloadFileDecryp(
            @RequestParam(defaultValue = DEFAULT_FILE_NAME) String fileName) throws IOException {


        try {
            rsaDecrypt (".//decryp//DecryptionFile.txt");
        }
        catch (Exception e){
            System.out.println("Error Decryption  "+e.getMessage());
        }



        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
        System.out.println("fileName: " + fileName);
        System.out.println("mediaType: " + mediaType);

        Path path = Paths.get(DIRECTORY + "\\" + DEFAULT_FILE_NAME);
        byte[] data = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(data);


        Path moveprivatekey = Files.move(Paths.get(".\\"+fileKeyName), Paths.get(".\\decryp\\"+fileKeyName));
        Path moveprivate = Files.move(Paths.get(".\\"+fileNameEncyp), Paths.get(".\\decryp\\"+fileNameEncyp));
        initRoot();

        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                // Content-Type
                .contentType(mediaType) //
                // Content-Lengh
                .contentLength(data.length) //
                .body(resource);
    }




    private void rsaDecrypt( String file_des)
            throws Exception {

        int i;
        System.out.println("start decyption");
        Key priKey = readKeyFromFile();
        System.out.println("pass read key");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        FileInputStream fileIn = new FileInputStream(fileNameEncyp);
        CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
        FileOutputStream fileOut = new FileOutputStream(file_des);
        // Write data to new file
        while ((i = cipherIn.read()) != -1) {
            fileOut.write(i);
        }
        // Close the file
        fileIn.close();
        cipherIn.close();
        fileOut.close();
        System.out.println("decrypted file created");
    }

    private  Key readKeyFromFile() throws IOException {


        InputStream in = new FileInputStream(fileKeyName);
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            KeyFactory fact = KeyFactory.getInstance("RSA");

                return fact.generatePrivate(new RSAPrivateKeySpec(m, e));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //ex=e;
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
            //System.out.println(ex.getMessage());
            System.out.println("Closed reading file.");
        }
    }




}
