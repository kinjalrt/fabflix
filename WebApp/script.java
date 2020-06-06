import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class script {


    public static void main(String args[]) throws Exception {
        try {
            File myObj = new File("/home/ubuntu/tomcat/webapps/cs122b-spring20-team-80/log.txt");
            Scanner myReader = new Scanner(myObj);
            long avgTj = 0;
            long avgTs = 0;
            long count = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] arr = data.split(" ");
                avgTj += Long.parseLong(arr[0]);
                avgTs += Long.parseLong(arr[1]);
                count++;
            }
            myReader.close();
            System.out.println("tj: " + avgTj/count);
            System.out.println("ts: " + avgTs/count);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


    }
}
