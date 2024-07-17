import co.junwei.cpabe.Cpabe;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class CPABETest {
    private static final String PUB_FILE = "src/main/java/com/example/keys/pub_key.txt";
    private static final String MSK_FILE = "src/main/java/com/example/keys/msk_key.txt";
    private static final String PRV_FILE = "src/main/java/com/example/keys/prikey_test.txt";
    private static final String ATTRIBUTES = "attr1,attr2";
    private Cpabe cpabe;

    public CPABETest() {
        try {
            cpabe = new Cpabe();
            generateKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to generate public key and master key
    private void generateKeys() throws IOException, ClassNotFoundException {
        System.out.println("Generating public and master keys...");
        cpabe.setup(PUB_FILE, MSK_FILE);
        System.out.println("Keys generated successfully.");
        // Print the contents of the generated keys for verification
        printFileContent(PUB_FILE, "Public Key");
        printFileContent(MSK_FILE, "Master Secret Key");
    }

    // Method to test key generation
    public void testKeygen() {
        try {
            System.out.println("Testing keygen with attributes: " + ATTRIBUTES);
            cpabe.keygen(PUB_FILE, PRV_FILE, MSK_FILE, ATTRIBUTES);
            System.out.println("Key generation successful.");

            // Read the generated private key file and print its content
            String prikeyContent = new String(Files.readAllBytes(Paths.get(PRV_FILE)), StandardCharsets.UTF_8);
            System.out.println("Generated Private Key Content: ");
            System.out.println(prikeyContent);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.err.println("OutOfMemoryError: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to print the contents of a file
    private void printFileContent(String filePath, String keyType) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(filePath));
        System.out.println(keyType + " Content: " + new String(content, StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        CPABETest test = new CPABETest();
        test.testKeygen();
    }
}

