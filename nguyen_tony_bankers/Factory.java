// Filename: Factory.java
// Description: Factory class that reads from a text file and creates the bank and each bank customer.
//
// Author:  Tony Nguyen
// Date:    December 10, 2020

import java.io.*;
import java.util.*;

public class Factory
{
    public static void main(String[] args)
    {
        String filename = "infile.txt";
        
        Thread[] workers = new Thread[Customer.COUNT]; // The customers
        int threadNum = 0;

        try
        {
            File f = new File(filename);
            System.out.println(f.getAbsolutePath());
            Scanner read = new Scanner(f);

            // Reads available resources from the Bank: "infile.txt"
            String resource = read.nextLine();
            System.out.println("\nFactory " + resource + "\n");

            String[] r_tokens = resource.split(",");        // the delimiter is: "," 
            int nResources = r_tokens.length;
            int[] resources = new int[nResources];

            for (int i = 0; i < nResources; i++)
            {
                resources[i] = Integer.parseInt(r_tokens[i].trim());
            }

            Bank theBank = new BankImplementation(resources);
            int[] maxDemand = new int[nResources];
            int[] allocated = new int[nResources];

            // read Customers
            while (read.hasNextLine())
            {
                String line = read.nextLine();

                if (line.length() == 0)
                    continue;
                
                    String[] tokens = line.split(",");

                for(int i = 0; i < tokens.length / 2; ++i)
                {
                    allocated[i] = Integer.parseInt(tokens[i].trim());
                }
                for(int i = tokens.length / 2; i < tokens.length; ++i)
                {
                    maxDemand[i - nResources] = Integer.parseInt(tokens[i].trim());
                }

                workers[threadNum] = new Thread(new Customer(threadNum, maxDemand, theBank));
                theBank.addCustomer(threadNum, allocated, maxDemand);

                System.out.println("Adding customer " + threadNum + "...");

                ++threadNum;
            }

            theBank.updateCustomerCount(threadNum);
            read.close();
        }
        catch (FileNotFoundException fnfe)
        {
            throw new Error("Unable to find file \"" + filename + "\"");
        } 
        catch (IOException ioe)
        {
            throw new Error("Error processing \"" + filename + "\"");
        }   

        System.out.println("\nFACTORY: created threads"); // start the customers

        for (int i = 0; i < threadNum; i++)
        {
            workers[i].start();
        }
        System.out.println("FACTORY: started threads");
    }
}