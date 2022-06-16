package com.ally.d3.watchmen.utilities.aws;
import com.ally.d3.watchmen.utilities.WriteFile;
import com.ally.d3.watchmen.utilities.dataDriven.ReadFile;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.*;
import java.net.URISyntaxException;
import java.security.Security;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

/*
 * Copyright 2022 Ally Financial, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

public class EncryptionHelper {

    @Autowired
    ReadFile readFile;

    @Autowired
    WriteFile writeFile;

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Helper.class);


    public void pgpEncrypt(String inputFileName, String outputFileName, String publicKeyFileName, Boolean isArmor ) {

        logger.debug("Encrypt file "+inputFileName+ " and save result as a file "+outputFileName);

        Security.addProvider(new BouncyCastleProvider());

        boolean armor = isArmor;
        boolean integrityCheck = true;

        try {

            encryptFile(outputFileName, inputFileName, publicKeyFileName, armor, integrityCheck);
        }

        catch (Exception e)
        {
            logger.error("Not able to encrypt file "+e);
            throw new RuntimeException("Not able to encrypt file"+ e);
        }

    }

    private  void encryptFile(String outputFileName, String inputFileName, String publicKeyFileName,
                              boolean armor, boolean withIntegrityCheck)
            throws IOException {

        logger.debug("Encrypt file with armor = "+armor+", integrityCheck = " +withIntegrityCheck);
        //logger.debug("File content to be encrypted is: "+readFile.readFileAsString(inputFileName));

        //first - read bytes from the file

        byte[] original = readFile.readFileAsByteArray(inputFileName);

        //encrypt bytes

        byte[] encrypted;

        try {
            encrypted = encryptBytes(original, readPublicKey(publicKeyFileName), null,
                    withIntegrityCheck, armor);
        }
        catch (PGPException e)
        {
            logger.error("Not able to encrypt bytes "+e);
            throw new RuntimeException("Not able to encrypt bytes"+ e);
        }

        //write to the out file

        writeFile.bytesToFile(outputFileName,encrypted);

    }

     //Simple PGP encryption between byte[].

    private byte[] encryptBytes(byte[] clearData, PGPPublicKey encKey,
                                 String fileName,boolean withIntegrityCheck, boolean armor)
            throws IOException, PGPException {

        logger.debug("Encrypt bytes");

        if (fileName == null) {
            fileName = PGPLiteralData.CONSOLE;
        }

        ByteArrayOutputStream encOut = new ByteArrayOutputStream();

        OutputStream out = encOut;
        if (armor) {
            out = new ArmoredOutputStream(out);
        }

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        logger.debug("Generate compressed data");

        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(
                PGPCompressedDataGenerator.ZLIB);

        OutputStream cos = comData.open(bOut);

        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();

        OutputStream pOut = lData.open(cos, // the compressed output stream
                PGPLiteralData.BINARY, fileName, // "filename" to store
                clearData.length, // length of clear data
                new Date() // current time
        );
        pOut.write(clearData);

        lData.close();
        comData.close();

        logger.debug("Encrypt compressed stream");

        PGPEncryptedDataGenerator cPk  = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                .setWithIntegrityPacket(withIntegrityCheck)
                .setSecureRandom(new SecureRandom()).setProvider("BC"));
        cPk .addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BC"));

        byte[] bytes = bOut.toByteArray();
        OutputStream cOut = cPk.open(out, bytes.length);

        cOut.write(bytes); // obtain the actual bytes from the compressed stream

        cOut.close();

        out.close();

       return encOut.toByteArray();
    }


    public PGPPublicKey readPublicKey(String fileName) throws IOException, PGPException {

        logger.debug("Read publicKey from the file "+fileName);

        String fileNameWithPath = readFile.getFilePath(fileName).toString();

        InputStream keyIn = new BufferedInputStream(new FileInputStream(fileNameWithPath));
        PGPPublicKey pubKey = readPublicKey(keyIn);
        keyIn.close();
        return pubKey;
    }


    private PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException {

        logger.debug("Read publicKey from InputStream");

        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
                org.bouncycastle.openpgp.PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());
        Iterator keyRingIter = pgpPub.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPPublicKeyRing keyRing = (PGPPublicKeyRing)keyRingIter.next();
            Iterator keyIter = keyRing.getPublicKeys();
            while (keyIter.hasNext()) {
                PGPPublicKey key = (PGPPublicKey)keyIter.next();
                if (key.isEncryptionKey()) {
                    return key;
                }
            }
        }
        logger.debug("Encryption key in not found in the key ring");
        throw new RuntimeException("Encryption key in not found in key ring");
    }

}