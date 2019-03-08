package com.sut.rsa.Controller;

import com.sut.rsa.Service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import javax.servlet.ServletContext;



@Controller
@RestController
@CrossOrigin("http://localhost:4200")
public class EncrypController {


    private void initRoot() {
        final Path rootLocation = Paths.get("file");
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

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        initRoot();

        String message = "";
        String name = file.getOriginalFilename();
        try {

            generateKeys();
            rsaEncrypt(file, ".//file//encryptionFile.txt");
            // storageService.store(file);
            // files.add(file.getOriginalFilename());

            message = "You successfully uploaded " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "FAIL to upload " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    
    private static final String DIRECTORY = ".\\file";
    private static final String DEFAULT_FILE_NAME = "encryptionFile.txt";
 
    @Autowired
    private ServletContext servletContext;
 
    @GetMapping("/downloadFile/Encryp")
    public ResponseEntity<ByteArrayResource> downloadFileencryp(
            @RequestParam(defaultValue = DEFAULT_FILE_NAME) String fileName) throws IOException {
 
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
        System.out.println("fileName: " + fileName);
        System.out.println("mediaType: " + mediaType);
 
        Path path = Paths.get(DIRECTORY + "\\" + DEFAULT_FILE_NAME);
        byte[] data = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(data);
 
        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                // Content-Type
                .contentType(mediaType) //
                // Content-Lengh
                .contentLength(data.length) //
                .body(resource);
    }



 
    private static final String DEFAULT_FILE_NAMEkey = "private.key";
 
    
 
    @GetMapping("/downloadFile/key")
    public ResponseEntity<ByteArrayResource> downloadFilekey(
            @RequestParam(defaultValue = DEFAULT_FILE_NAMEkey) String fileName) throws IOException {

        MediaType mediaType =MediaType.parseMediaType("application/octet-stream");
        System.out.println("fileName: " + fileName);
        System.out.println("mediaType: " + mediaType);
 

        Path path = Paths.get(DIRECTORY + "\\" + DEFAULT_FILE_NAMEkey);
        byte[] data = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(data);

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



    private  void generateKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.genKeyPair();
        System.out.println("pu   " + kp.getPublic());
        System.out.println("pri  " + kp.getPrivate());
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        System.out.println("keys created");
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        saveToFile("public.key", pub.getModulus(), pub.getPublicExponent());
        saveToFile("private.key", priv.getModulus(), priv.getPrivateExponent());

        

        System.out.println("keys saved");
    }


    private  void saveToFile(String fileName, BigInteger mod,
                                  BigInteger exp) throws IOException {
        ObjectOutputStream fileOut = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)));
        try {
          
            fileOut.writeObject(mod);
            fileOut.writeObject(exp);

            
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IOException("Unexpected error");
        } finally {
            fileOut.close();
            
            System.out.println("Closed writing file.");
        }
    }



    private  Key readKeyFromFile(String keyFileName) throws IOException {
        InputStream in = new FileInputStream(keyFileName);
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(
                in));
       // Exception ex=null;
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            if (keyFileName.startsWith("public")) {
                return fact.generatePublic(new RSAPublicKeySpec(m, e));
            } else {
                return fact.generatePrivate(new RSAPrivateKeySpec(m, e));
            }
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


    private  void rsaEncrypt( MultipartFile file_loc, String file_des)
            throws Exception {


            byte[] data = new byte[32];
            int i;
            System.out.println("start encyption");
            Key pubKey = readKeyFromFile("public.key");
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            FileInputStream fileIn;
            try {
            FileInputStream fileInc = (FileInputStream) file_loc.getInputStream();
                fileIn=fileInc;
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                throw e;
            }

            FileOutputStream fileOut = new FileOutputStream(file_des);
            CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
            // Read in the data from the file and encrypt it
            while ((i = fileIn.read(data)) != -1) {
                cipherOut.write(data, 0, i);
            }
            // Close the encrypted file
            System.out.println("Encode    " + cipherOut.toString());
            cipherOut.close();
            fileIn.close();
            System.out.println("encrypted file created");
            System.out.println("output    " + fileOut.getChannel());




        Path moveprivate = Files.move 
        (Paths.get(".\\private.key"),  
        Paths.get(".\\file\\private.key")); 
  
        if(moveprivate != null) 
        { 
            System.out.println("File private key renamed and moved successfully"); 
        } 
        else
        { 
            System.out.println("Failed to move the file private key"); 
        } 

        
        Path movepublic = Files.move 
        (Paths.get(".\\public.key"),  
        Paths.get(".\\file\\public.key")); 
  
        if(movepublic != null) 
        { 
            System.out.println("File public key renamed and moved successfully"); 
        } 
        else
        { 
            System.out.println("Failed to move the file public key "); 
        } 



    }



   
}