package superkey;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * A text-oriented keychain to keep a list of passowords in a file
 *
 * @author 68503 Joao Gravato
 */
public class SuperKey {

    final static Scanner s = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        s.useDelimiter("\\n");
        File users = new File("Keychain.txt");
        //FileReader fr = new FileReader(users);
        //BufferedReader br = new BufferedReader (fr);

        int opcao;
        do {
            
            listarOpcoes();
            
            opcao = escolherOpcao();
                       
            switch (opcao) {
            case 1:
                System.out.println("OPCAO 1");
                criarEntrada(users);
                break;
            case 2:
                System.out.println("OPCAO 2");
                listarFicheiro(users);
                break;
            case 3://pesquisar credenciais pelo nome da aplicação
                System.out.println("OPCAO 3");
                pesquisarCredenciais(users);
                break;
            case 4:
                System.out.println("OPCAO 4");
                cifrarFicheiro();
                break;
            case 5:
                System.out.println("OPCAO 5");
                decifrarFicheiro();
                break;
            case 0:
                break;
            default:
                System.out.println("Nao existe tal opcao");
        }
            
        } while (opcao != 0);

    }

    public static int escolherOpcao() {
        int opcao;
        System.out.print("Opção? ");
        opcao = s.nextInt();
        System.out.println();
        return opcao;
    }

    public static void listarOpcoes() {
        System.out.println("1- Criar nova entrada");
        System.out.println("2- Listar keychain");
        System.out.println("3- Pesquisar credenciais p/ aplicação");
        System.out.println("4- Cifrar Ficheiro");
        System.out.println("5- Decifrar Ficheiro");
        System.out.println("0- Sair ");
    }

    public static void decifrarFicheiro() {
        try {
            File aesFile = new File("CifradoUsers.txt");
            File aesFileBis = new File("DecifradoUsers.txt");
            FileInputStream fis;
            FileOutputStream fos;
            CipherInputStream cis;
            //Creation of Secret key
            String key = "MySEcRetKeY";
            
            // AES needs exaxtly 16 chars
            int length = key.length();
            if (length > 16 && length != 16) {
                key = key.substring(0, 15);
            }
            if (length < 16 && length != 16) {
                for (int i = 0; i < 16 - length; i++) {
                    key = key + "0";
                }
            }
            
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            //Creation of Cipher objects
            Cipher decrypt = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            
            decrypt.init(Cipher.DECRYPT_MODE, secretKey);
            // Open the Encrypted file
            fis = new FileInputStream(aesFile);
            cis = new CipherInputStream(fis, decrypt);
            // Write to the Decrypted file
            fos = new FileOutputStream(aesFileBis);
            byte[] b = new byte[8];
            int i = cis.read(b);
            while (i != -1) {
                fos.write(b, 0, i);
                i = cis.read(b);
            }
            fos.close();
            cis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Decifrado");
        System.out.println();
    }

    public static void cifrarFicheiro() {
        try {
            FileInputStream fis;
            FileOutputStream fos;
            CipherInputStream cis;
            //Creation of Secret key
            String key = "MySEcRetKeY";
            
            // AES needs exaxtly 16 chars
            int length = key.length();
            if (length > 16 && length != 16) {
                key = key.substring(0, 15);
            }
            if (length < 16 && length != 16) {
                for (int i = 0; i < 16 - length; i++) {
                    key = key + "0";
                }
            }
            
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            //Creation of Cipher objects
            Cipher encrypt = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            encrypt.init(Cipher.ENCRYPT_MODE, secretKey);
            // Open the Plaintext file
            try {
                fis = new FileInputStream("Keychain.txt");
                cis = new CipherInputStream(fis, encrypt);
                // Write to the Encrypted file
                fos = new FileOutputStream("CifradoUsers.txt");
                byte[] b = new byte[8];
                int i = cis.read(b);
                while (i != -1) {
                    fos.write(b, 0, i);
                    i = cis.read(b);
                }
                
                fos.close();
                cis.close();
                fis.close();
            } catch (IOException err) {
                System.out.println("Cannot open file!");
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Ficheiro Cifrado em EncriptedUsers.txt");
        System.out.println();
    }

    public static void pesquisarCredenciais(File users) throws FileNotFoundException {
        Scanner scFile = new Scanner(users);
        System.out.println("Aplicação a procurar? ");
        String search = s.next();
        String[] credenciais;        
        imprimirCabecalhoListagem();        
        while (scFile.hasNextLine()) {
            credenciais = scFile.nextLine().split(",");
            if (credenciais[0].startsWith(search)) {
                imprimirCredenciais(credenciais);
            }           
        }
        scFile.close();
        System.out.println();
    }

    public static void listarFicheiro(File users) throws FileNotFoundException {
        Scanner scFile = new Scanner(users);       
        imprimirCabecalhoListagem();
        String[] credenciais;       
        while (scFile.hasNextLine()) {
            credenciais = scFile.nextLine().split(",");
            imprimirCredenciais(credenciais);          
        }
        scFile.close();
    }   

    public static void criarEntrada(File users) throws IOException {
        String line = "";
        System.out.println("Aplicação/categoria? ");
        String platform = s.next();
        line += platform + ",";
        System.out.println("Utilizador? ");
        String user = s.next();
        line += user + ",";
        System.out.print("Gerar password? (y/n)");
        String yn = s.next();
        if ("y".equals(yn) || "Y".equals(yn)) {
            String generatedPass = criarPassAlphaNumAleatoria();
            System.out.println("Nova Pass > " + generatedPass);
            line += generatedPass;
        } else if ("n".equals(yn) || "N".equals(yn)) {
            System.out.println("Digite Pass");
            String pass = s.next();
            line += pass;
        } else {
            System.out.println("opcao incorrecta");
        }        
        guardarNovaEntrada(users, line);
    }

    public static void guardarNovaEntrada(File users, String line) throws IOException {
        FileWriter fw = new FileWriter(users, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(line);
        bw.newLine();
        bw.close();
        
        System.out.println("Utilizador guardado.");
        System.out.println();
    }
    
    public static void imprimirCabecalhoListagem() {
        System.out.println("Aplicação       User          Password");
        System.out.println("-------------------------------------");
    }
    
    public static void imprimirCredenciais(String[] credenciais) {
        System.out.printf("%-10s  %8s  %15s", credenciais[0], credenciais[1], credenciais[2]);
        System.out.println();
    }

    public static String criarPassAlphaNumAleatoria() {
        Random rand = new Random();
        return (new BigInteger(90, rand).toString(32));
    }
}

