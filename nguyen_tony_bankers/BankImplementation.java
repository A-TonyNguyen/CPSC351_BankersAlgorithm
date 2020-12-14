// Filename: BankImplementation.java
// File Description: Definitions for Bank.java functions.
//
// Author:  Tony Nguyen
// Date:    December 10, 2020

import java.io.*;
import java.util.*;

public class BankImplementation implements Bank
{
    private int n;              // the number of threads in the system
    private int m;              // the number of resources

    private int[] available;    // the amount available of each resource
    private int[][] maximum;    // the maximum demand of each thread
    private int[][] allocation; // the amount currently allocated to each thread
    private int[][] need;       // the remaining needs of each thread
    private boolean[] released; // released customers

    // Constructor: create a new bank (with resources)
    public BankImplementation(int[] resources)
    { 
        m = resources.length;
        n = Customer.COUNT;

        available = new int[m];
        System.arraycopy(resources, 0, available, 0, m);
        maximum = new int[n][m];
        allocation = new int[n][m];
        need = new int[n][m];
        released = new boolean[n];
        Arrays.fill(released, false);
    }

    private void showAllMatrices(int[][] alloc, int[][] max, int[][] need, String msg)
    {
        System.out.print("\n\tALLOCATION\tMAXIMUM\t\tNEED\n");

        for (int i = 0; i < n; ++i)
        {
            System.out.print("\t");

            if (released[i]) // released[i] = true
            {
                System.out.print("--------\t--------\t--------\n");
            }
            else    // released[i] = false
            {
                showVector(alloc[i],"\t");
                showVector(max[i],"\t");
                showVector(need[i],"\n");
            }
        }
    }

    private void showMatrix(int[][] matrix, String title, String rowTitle)
    {
        System.out.println(title);

        for (int i = 0; i < n; ++i)
        {
            showVector(matrix[i], "");
        }
    }

    private void showVector(int[] vect, String msg)
    {
        System.out.print("[");

        for (int i = 0; i < m; ++i)
        {
            if (i + 1 < m)
                System.out.print(Integer.toString(vect[i]) + ' ');
            else
                System.out.print(Integer.toString(vect[i]));
        }

        System.out.print("]" + msg);
    }

    public void updateCustomerCount(int newCustomerCount)
    {
        n = newCustomerCount;
    }

    // invoked by a thread when it enters the system; also records max demand
    public void addCustomer(int threadNum, int[] allocated, int[] maxDemand)
    {
        for (int i = 0; i < m; ++i)
        {
            allocation[threadNum][i] = allocated[i];
            maximum[threadNum][i] = maxDemand[i];
            need[threadNum][i] = maxDemand[i] - allocated[i];
        }

        released[threadNum] = false;
    }

    public void getState() // output state for each thread
    { 
        showAllMatrices(allocation, maximum, need, "");
    }

    private boolean isSafeState(int threadNum, int[] request)
    {
        int[] currAvailable = new int[m];
        int[][] currAlloc = new int[n][m];
        int[][] currNeed = new int[n][m];

        System.arraycopy(available,0, currAvailable, 0, m);
        
        for (int i = 0; i < n; ++i)
        {
            System.arraycopy(allocation[i], 0, currAlloc[i], 0, m);
            System.arraycopy(need[i], 0, currNeed[i], 0, m);
        }

        boolean[] finish = new boolean[n];
        Arrays.fill(finish, false);

        for (int i = 0; i < m; ++i)
        {
            currAvailable[i] -= request[i];
            currAlloc[threadNum][i] += request[i];
            currNeed[threadNum][i] -= request[i];
        }

        while (true)
        {
            int index = -1;
            for (int i = 0; i < n; ++i)
            {
                boolean hasEnoughResource = true;
                
                for (int j = 0; j < m; ++j)
                {
                    if (currNeed[i][j] > currAvailable[j])
                    {
                        hasEnoughResource = false;
                        break;
                    }
                }
                if (!finish[i] && hasEnoughResource)
                {
                    index = i;
                    break;
                }
            }

            if (index > -1)
            {
                for (int i = 0; i < m; ++i)
                {
                    currAvailable[i] += currAlloc[index][i];
                    finish[index] = true;
                }
            }
            else break;
        }

        for (int i = 0; i < n; ++i)
        {
            if (!finish[i]) return false;
        }
        return true;
    }

    // make request for resources. will block until request is satisfied safely
    public synchronized boolean requestResources(int threadNum, int[] request)
    {
        for (int i = 0; i < m; ++i)     //Checking for a valid request
        {
            if (request[i] > need[threadNum][i])
            {
                request[i] = need[threadNum][i];
            }
        }

        System.out.print("\n#P"+ threadNum + " RQ:");
        showVector(request, ", Needs:");
        showVector(need[threadNum], ", Available:");
        showVector(available, " ");

        for (int i = 0; i < m; ++i)     //Checking for available resources
        {
            if (request[i] > available[i])
            {
                System.out.println("---> DENIED");
                return false;
            }
        }

        if (isSafeState(threadNum, request))
        {
            System.out.print("---> APPROVED, #P" + threadNum + " now at: ");
            
            for (int i = 0; i < m; ++i)     // Gives  the customer threadNum the resource
            {
                available[i] -= request[i];
                allocation[threadNum][i] += request[i];
                need[threadNum][i] -= request[i];
            }

            showVector(allocation[threadNum], "\nAvailable: ");
            showVector(available, "\n");
            showAllMatrices(allocation, maximum, need, "\n");

            for (int i = 0; i < m; ++i)
            {
                if (need[threadNum][i] != 0)
                {
                    //The customer is not finished
                    return false; 
                }
            }
            System.out.print("\n");

            //The customer is finished and is waiting for resources to be released.
            return true;
        }

        //If the request does not lead to a safe state, then it is denied.
        System.out.println("---> DENIED");
        return false;
    }

    //Prints if all resources for the customer has been released
    public synchronized void releaseResources(int threadNum, int[] release)
    {
        System.out.print("\n========> #P" + threadNum + " has all its resources! " + "RELEASING ALL and SHUTTING DOWN...\n");
        System.out.print("--------- #P" + threadNum + " releasing: ");

        showVector(allocation[threadNum], ", allocated = ");

        for (int i = 0; i < m; ++i)
        {
            available[i] += allocation[threadNum][i];
            allocation[threadNum][i] = 0;
        }

        showVector(allocation[threadNum], "\n");
        released[threadNum] = true;

        //Optional: use getState() to see matrix after a resources has been released
        //getState();
    }
}